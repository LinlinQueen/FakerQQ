package com.qqclient.service;

import com.qqcommon.Message;
import com.qqcommon.MessageType;

import java.io.*;

/**
 * 文件传输
 * @Yanx
 * @Create 2022-04-17-23:28
 */
public class FileClientService {
    /**
     *
     * @param src 源文件
     * @param dest 传输到对方哪里
     * @param senderId 发送id
     * @param getterId 接收id
     */
    public void sentFileToOne(String src,String dest,String senderId,String getterId){
        Message message =new Message();
        message.setMesType(MessageType.MESSAGE_FILE_MES);
        message.setSender(senderId);
        message.setGetter(getterId);
        message.setSrc(src);
        message.setDest(dest);
        //读取文件
        FileInputStream fileInputStream = null;
        byte[] fileBytes = new byte[(int)new File(src).length()];
        try {
            fileInputStream = new FileInputStream(src);
            fileInputStream.read(fileBytes);//将src 文件读入到程序的字节数组
            // 将文件对应的字节数组设置到 message
            message.setFileBytes(fileBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //关闭
            if(fileInputStream != null){
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //提示信息
        System.out.println("\n"+senderId+" 给 "+getterId+" 发送文件:"+src+" 到对方的电脑目录 "+dest);
        //发送
        try {
            ObjectOutputStream oos = new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId)
                    .getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
