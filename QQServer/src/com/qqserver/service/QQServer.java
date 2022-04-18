package com.qqserver.service;

import com.qqcommon.Message;
import com.qqcommon.MessageType;
import com.qqcommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Yanx
 * @Create 2022-04-17-14:50
 * 监听9999 等待客服端连接 ，保持通信
 */
public class QQServer {
    private ServerSocket serverSocket =null;

    // 创建集合模拟数据库
    private static ConcurrentHashMap<String,User> validUsers = new ConcurrentHashMap<>();
    static {//静态代码块 初始化validUsers
        validUsers.put("100",new User("100","123456"));
        validUsers.put("200",new User("200","123456"));
        validUsers.put("300",new User("300","123456"));
        validUsers.put("400",new User("400","123456"));
        validUsers.put("500",new User("500","123456"));
    }

    //验证用户是否有效
    private boolean checkUser(String userId,String password){
        User user = validUsers.get(userId);
        if(user == null ){
            return false;
        }
        if(!user.getPassword().equals(password)){// 密码不正确
            return false;
        }
        return true;
    }

    public QQServer(){
        System.out.println("服务器在9999端口监听");
        new Thread(new SendNewsToAllService()).start();
        try{
            serverSocket = new ServerSocket(9999);
            while (true){// 和客户端建立连接后 ，继续监听
                Socket socket = serverSocket.accept();
                // 得到socket对象关联的输入流
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

                // 得到socket关联的对象输出流
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

                User user = (User)ois.readObject();
                //创建Message对象
                Message message = new Message();
                // 验证
                if(checkUser(user.getUserId(),user.getPassword())){
                    message.setMesType(MessageType.MESSAGE_LOGIN_SUCCEED);
                    // 将message 对象回复
                    oos.writeObject(message);
                    // 创建一个线程 和客户端保持通信
                    ServerConnectClientThread serverConnectClientThread
                            = new ServerConnectClientThread(socket, user.getUserId());
                    //启动线程
                    serverConnectClientThread.start();
                    // 把线程放入集合管理
                    ManagerClientThreads.addClientThread(user.getUserId(),serverConnectClientThread);
                    //===============================离线消息======================
                    ServerConnectClientThread serverConnectClientThread1 =
                            ManagerClientThreads.getServerConnectClientThread(user.getUserId());
                    List<Message> l = OfflineMessage.getMessageByUserId(user.getUserId());
                    //登录成功先检查 Offline里面是否有消息 有消息就发送
                    if (l !=null ) {
                        System.out.println("集合里面有东西");
                        List<Message> messageList = OfflineMessage.getMessageByUserId(user.getUserId());
                        for (Message ms : messageList) {
                            System.out.println("循环");
                            ObjectOutputStream oos1 =
                                    new ObjectOutputStream(serverConnectClientThread1.getSocket().getOutputStream());
                            oos1.writeObject(ms);
                        }
                        OfflineMessage.removeMessageById(user.getUserId());
                    }
                    //===============================
                }else {//登录失败
                    message.setMesType(MessageType.MESSAGE_LOGIN_FAIL);
                    oos.writeObject(message);
                    //关闭socket
                    socket.close();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            // 服务端退出while循环 服务器不在监听 关闭ServerSocket
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
