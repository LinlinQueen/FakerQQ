package com.qqclient.service;
import com.qqcommon.Message;
import com.qqcommon.MessageType;
import com.qqcommon.User;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * @Yanx
 * @Create 2022-04-17-14:15
 */
public class UserClientService {
    private User user = new User();
    private Socket socket;

    public boolean checkUser(String userId, String pwd) {
        boolean b = false;
        user.setUserId(userId);
        user.setPassword(pwd);
        //连接服务端 发送user对象
        try {
            socket = new Socket(InetAddress.getLocalHost(), 9999);
            // 得到ObjectOutputStream
            ObjectOutputStream oot = new ObjectOutputStream(socket.getOutputStream());
            oot.writeObject(user);//发送user对象

            // 读取从服务器回复的Message对象
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message message = (Message) ois.readObject();

            if (message.getMesType().equals(MessageType.MESSAGE_LOGIN_SUCCEED)) {//登录成功

                //创建一个和服务器端保持通信的线程 -> 创建一个类ClientConnectServiceThread
                ClientConnectServerThread clientConnectServerThread = new ClientConnectServerThread(socket);

                //启动客户端线程
                clientConnectServerThread.start();

                // 线程放入集合管理
                ManageClientConnectServerThread.addClientConnectServerThread(userId, clientConnectServerThread);

                b = true;
            } else {
                //登录失败不能创建线程
                socket.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    // 向服务器端请求在线用户列表
    public void onlineFriendList(){
        //发送message，类型 MESSAGE_GET_ONLINE_FRIEND
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_GET_ONLINE_FRIEND);
        message.setSender(user.getUserId());
        //发送给服务器
        try {
            // 从管理线程中通过 userId 得到线程对象
            ClientConnectServerThread clientConnectServerThread =
                    ManageClientConnectServerThread.getClientConnectServerThread(user.getUserId());
            //通过线程得到关联的socket
            Socket socket = clientConnectServerThread.getSocket();
            // 得到当前线程的Socket 对应的 ObjectOutputStream对象
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);//发送一个Message对象，向服务器要求在线用户列表
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 退出客户端 给服务端发送退出系统的message对象
    public void logout(){
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_CLIENT_EXIT);
        message.setSender(user.getUserId());// 指定哪个客户端退出
        //发送message
        try {
            //ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectOutputStream oos = new ObjectOutputStream
                    (ManageClientConnectServerThread.getClientConnectServerThread(user.getUserId())
                            .getSocket().getOutputStream());
            oos.writeObject(message);
            System.out.println(user.getUserId()+"退出系统");
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
