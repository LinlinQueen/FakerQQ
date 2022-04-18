package com.qqclient.service;

import com.qqcommon.Message;
import com.qqcommon.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Yanx
 * @Create 2022-04-17-18:16
 * 提供和消息相关的服务方法
 */
public class MessageClientService {
    private SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");

    /**
     *
     * @param content 内容
     * @param senderId 发送着
     */
    public void sendMessageToAll(String content,String senderId){
        // 构建message
        Message message = new Message();
        message.setSender(senderId);
        message.setContent(content);
        message.setMesType(MessageType.MESSAGE_TO_ALL_MES);//群发消息类型
        String format = sdf.format(new Date());
        message.setSendTime(format);//发送时间设置到message对象
        System.out.println(senderId+ "对大家说"+content);
        //发送给服务器
        try {
            ObjectOutputStream oos =
                    new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId)
                            .getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param content  内容
     * @param senderId 发送用户id
     * @param getterId 接收用户id
     */
    public void sentMessageToOne(String content,String senderId, String getterId){
        // 构建message
        Message message = new Message();
        message.setSender(senderId);
        message.setGetter(getterId);
        message.setContent(content);
        message.setMesType(MessageType.MESSAGE_COMM_MES);
        String format = sdf.format(new Date());
        message.setSendTime(format);//发送时间设置到message对象
        System.out.println(senderId+ "对"+ getterId+"说"+content);
        //发送给服务器
        try {
            ObjectOutputStream oos =
                    new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId)
                            .getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
