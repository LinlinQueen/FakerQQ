package com.qqclient.service;

import com.qqcommon.Message;
import com.qqcommon.MessageType;

import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * @Yanx
 * @Create 2022-04-17-14:25
 */
public class ClientConnectServerThread extends Thread{
    //该线程持有Socket
    private Socket socket;

    public Socket getSocket() {
        return socket;
    }

    //构造器可以接收socket对象
    public ClientConnectServerThread(Socket socket){
        this.socket = socket;
    }
    @Override
    public void run(){
        //Thread需要在后台和服务器通信，
        while (true){
            System.out.println("客户端线程等待从服务器发送的消息");
            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                //服务器没有发送Message对象 线程会阻塞在这里
                Message message = (Message)ois.readObject();
                // 判读message类型，然后做相应的业务处理
                // 如果读取到的是服务端返回的在线用户列表
                if(message.getMesType().equals(MessageType.MESSAGE_RET_ONLINE_FRIEND)){
                    // 取出在线列表信息
                    String[] onlineUsers = message.getContent().split(" ");
                    System.out.println("\n===========在线用户列表========");
                    for (int i = 0; i < onlineUsers.length; i++) {
                        System.out.println("用户:"+onlineUsers[i]);
                    }
                }else if(message.getMesType().equals(MessageType.MESSAGE_COMM_MES)){//普通的聊天消息
                    System.out.println("\n"+message.getSender()+"在"+ message.getSendTime()+"对"+message.getGetter()
                    +"说: "+message.getContent());
                }else if(message.getMesType().equals(MessageType.MESSAGE_TO_ALL_MES)){
                    // 显示在客户端控制台
                    System.out.println("\n"+message.getSender()+" 对大家说"+message.getContent());
                }else if (message.getMesType().equals(MessageType.MESSAGE_FILE_MES)){//文件消息
                    System.out.println("\n"+message.getSender()+" 给 "+ message.getGetter()+" 发文件:"
                    +message.getSrc()+" 到我的电脑目录" +message.getDest());

                    // 取出message 的文件字节数组，通过文件输出流写出到磁盘
                    FileOutputStream fileOutputStream = new FileOutputStream(message.getDest());
                    fileOutputStream.write(message.getFileBytes());
                    fileOutputStream.close();
                    System.out.println("保存文件成功");

                }
                else{
                    System.out.println("其他类型message");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
