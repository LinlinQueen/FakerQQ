package com.qqserver.service;

import java.util.HashMap;
import java.util.Iterator;

/**
 * 管理和客户端 通信的线程
 * @Yanx
 * @Create 2022-04-17-15:11
 */
public class ManagerClientThreads {
    private static HashMap<String,ServerConnectClientThread> hashMap = new HashMap<>();

    // 返回
    public static HashMap<String, ServerConnectClientThread> getHashMap() {
        return hashMap;
    }

    //添加线程到集合
    public static void addClientThread(String userId, ServerConnectClientThread serverConnectClientThread){
        hashMap.put(userId,serverConnectClientThread);
    }
    // 根据userId返回 ServerConnectClientThread线程
    public static ServerConnectClientThread getServerConnectClientThread(String userId){
        return hashMap.get(userId);
    }

    //移除线程
    public static void removeServerConnectClientThread(String userId){
        hashMap.remove(userId);
    }
    public static String getOnlineUser(){
        //集合遍历， 遍历HashMap的key
        Iterator<String> iterator = hashMap.keySet().iterator();
        String onlineUserList ="";
        while (iterator.hasNext()){
            onlineUserList+=iterator.next()+" ";
        }
        return onlineUserList;
    }
}
