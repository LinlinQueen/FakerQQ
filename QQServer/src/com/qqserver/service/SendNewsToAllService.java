package com.qqserver.service;

import com.qqcommon.Message;
import com.qqcommon.MessageType;
import com.utils.Utility;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Yanx
 * @Create 2022-04-18-0:09
 */
public class SendNewsToAllService implements Runnable {
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");

    @Override
    public void run() {
        while (true) {
            System.out.println("输入服务器要推送的新闻/消息[输入exit表示退出推送服务线程]");
            String news = Utility.readString(1000);
            if ("exit".equals(news)) {
                break;
            }
            // 构建消息 群发消息
            Message message = new Message();
            message.setSender("服务器");
            message.setMesType(MessageType.MESSAGE_TO_ALL_MES);
            message.setContent(news);
            String time = simpleDateFormat.format(new Date());
            message.setSendTime(time);
            System.out.println("服务器在" + time + "推送消息给所有人 说" + news);

            // 遍历当前所有的通信线程，得到socket 发送消息
            HashMap<String, ServerConnectClientThread> hashMap = ManagerClientThreads.getHashMap();
            Iterator<String> iterator = hashMap.keySet().iterator();
            while (iterator.hasNext()) {
                String onlineUserList = iterator.next().toString();
                ServerConnectClientThread serverConnectClientThread = hashMap.get(onlineUserList);
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                    oos.writeObject(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
