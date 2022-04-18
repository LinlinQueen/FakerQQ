package com.qqserver.service;

import com.qqcommon.Message;
import com.qqcommon.MessageType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Yanx
 * @Create 2022-04-18-12:37
 */
public class OfflineMessage {
    //key 用户id value 就是线程
    private static ConcurrentHashMap<String, ArrayList<Message>> hashMap = new ConcurrentHashMap<>();

    // 将线程加入集合中
    public static void addMessage(String userId,ArrayList<Message> messages){
        hashMap.put(userId,messages);
    }
    // 通过userId得到对应线程
    public static List<Message> getMessageByUserId(String userId){
        return hashMap.get(userId);
    }
    public static void removeMessageById(String id){
        hashMap.remove(id);
    }
}
