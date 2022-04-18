package com.qqserver.service;

import com.qqcommon.Message;
import com.qqcommon.MessageType;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 该类的一个对象和某个客户端保持通信
 *
 * @Yanx
 * @Create 2022-04-17-15:04
 */
public class ServerConnectClientThread extends Thread {
    private Socket socket;
    private String userId;//连接服务端的用户id
    private ArrayList<Message> list = new ArrayList<>();

    public ServerConnectClientThread(Socket socket, String userId) {
        this.socket = socket;
        this.userId = userId;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {
        while (true) {
            System.out.println("服务端和客户端保持通信 : " + userId);
            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) ois.readObject();

                // 根据message类型做相应业务处理
                if (message.getMesType().equals(MessageType.MESSAGE_GET_ONLINE_FRIEND)) {
                    // 客户端要在线列表
                    /*
                     在线列表形式 100 200 300 400
                     */
                    System.out.println(message.getSender() + " 要在线用户列表");
                    String onlineUser = ManagerClientThreads.getOnlineUser();
                    //返回message
                    //构建一个message对象，返回给客户端
                    Message ms = new Message();
                    ms.setMesType(MessageType.MESSAGE_RET_ONLINE_FRIEND);
                    ms.setContent(onlineUser);
                    ms.setGetter(message.getGetter());
                    //返回给客户端
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(ms);
                } else if (message.getMesType().equals(MessageType.MESSAGE_CLIENT_EXIT)) {//客户端退出
                    System.out.println(message.getSender() + "退出系统");
                    //将客户端对应线程 从集合中移除
                    ManagerClientThreads.removeServerConnectClientThread(message.getSender());
                    socket.close();
                    //退出线程
                    break;
                } else if (message.getMesType().equals(MessageType.MESSAGE_COMM_MES)) {
                    //根据message 获取getter id
                    ServerConnectClientThread serverConnectClientThread =
                            ManagerClientThreads.getServerConnectClientThread(message.getGetter());
                    System.out.println("判断用户是否离线");
                    if (serverConnectClientThread == null) {//用户离线
                        System.out.println("离线");
                        list.add(message);
                        OfflineMessage.addMessage(message.getGetter(), list);
                        System.out.println("发送成功");
                    } else {
                        // 得到对应的socket 的对象输出流 ，将message转发
                        ObjectOutputStream oos =
                                new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                        oos.writeObject(message);//转发  客户不在线 保存在数据库 离线留言
                    }

                } else if (message.getMesType().equals(MessageType.MESSAGE_TO_ALL_MES)) {
                    // 遍历 管理线程的集合 得到所有线程的socket 将message转发
                    HashMap<String, ServerConnectClientThread> hashMap = ManagerClientThreads.getHashMap();
                    Iterator<String> iterator = hashMap.keySet().iterator();
                    while (iterator.hasNext()) {
                        //取出在线用户的id
                        String onlineUserId = iterator.next().toString();
                        if (!onlineUserId.equals(message.getSender())) {
                            // 转发message
                            ObjectOutputStream oos = new ObjectOutputStream(hashMap.get(onlineUserId).getSocket().getOutputStream());
                            oos.writeObject(message);
                        }
                    }
                } else if (message.getMesType().equals(MessageType.MESSAGE_FILE_MES)) {
                    // 根据getter id 获取对应的线程，将message对象转发
                    ServerConnectClientThread serverConnectClientThread =
                            ManagerClientThreads.getServerConnectClientThread(message.getGetter());
                    if (serverConnectClientThread == null) {//用户离线
                        System.out.println("离线");
                        list.add(message);
                        OfflineMessage.addMessage(message.getGetter(), list);
                        System.out.println("发送成功");
                    } else {
                        ObjectOutputStream oos = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                        //转发
                        oos.writeObject(message);
                    }
                } else {
                    System.out.println("其他类型的message，暂不处理");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
