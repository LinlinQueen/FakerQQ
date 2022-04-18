package com.qqclient.service;

import java.util.HashMap;

/**
 * @Yanx
 * @Create 2022-04-17-14:35
 */

public class ManageClientConnectServerThread {
    //key 用户id value 就是线程
    private static HashMap<String, ClientConnectServerThread> hashMap = new HashMap<>();

    // 将线程加入集合中
    public static void addClientConnectServerThread(String userId,ClientConnectServerThread clientConnectServerThread){
        hashMap.put(userId,clientConnectServerThread);
    }
    // 通过userId得到对应线程
    public static ClientConnectServerThread getClientConnectServerThread(String userId){
        return hashMap.get(userId);
    }
}
