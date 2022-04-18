package com.qqclient.view;

import com.qqclient.service.FileClientService;
import com.qqclient.service.MessageClientService;
import com.qqclient.service.UserClientService;
import com.qqclient.utils.Utility;

/**
 * @Yanx
 * @Create 2022-04-17-12:04
 */
public class QQView {
    private boolean loop = true;// 控制是否显示菜单
    private String key = "";//接收用户键盘输入
    private UserClientService userClientService = new UserClientService();// 对象用于登录服务、注册用户
    private MessageClientService messageClientService = new MessageClientService();// 用户私聊 群聊
    private FileClientService fileClientService = new FileClientService();// 用户传输文件

    //显示主菜单
    private void mainMenu() {
        while (loop) {
            System.out.println("=============欢迎登录===========");
            System.out.println("\t\t 1 登录系统");
            System.out.println("\t\t 9 退出系统");
            System.out.print("输入你的选择：");

            key = Utility.readString(1);
            switch (key) {
                case "1":
                    System.out.print("输入账号:");
                    String userId = Utility.readString(50);
                    System.out.print("输入密码:");
                    String password = Utility.readString(50);
                    //到服务端验证是否合法
                    if (userClientService.checkUser(userId,password)) {//登录成功
                        System.out.println("=============欢迎(用户" + userId + "登录成功)===========");
                        //进入二级菜单
                        while (loop) {
                            System.out.println("\n===========网络通信二级菜单(用户" + userId + ")=============");
                            System.out.println("\t\t 1 显示在线用户列表");
                            System.out.println("\t\t 2 群发消息");
                            System.out.println("\t\t 3 私聊消息");
                            System.out.println("\t\t 4 发送文件");
                            System.out.println("\t\t 9 退出系统");
                            System.out.println("输入你的选择:");
                            key = Utility.readString(1);
                            switch (key) {
                                case "1":
                                    // 获取在线用户列表
                                    userClientService.onlineFriendList();
                                    //System.out.println("显示用户列表");
                                    break;
                                case "2":
                                    System.out.println("输入想对大家说的话:");
                                    String s = Utility.readString(100);
                                    // 将消息封装为message对象
                                    messageClientService.sendMessageToAll(s,userId);
                                    break;
                                case "3":
                                    System.out.print("请输入想聊天的用户号(在线)");
                                    String getterId = Utility.readString(50);
                                    System.out.print("请输入想说的话:");
                                    String content = Utility.readString(100);
                                    messageClientService.sentMessageToOne(content,userId,getterId);
                                    break;
                                case "4":
                                    System.out.print("输入用户 给他发送文件(在线):");
                                    getterId = Utility.readString(50);
                                    System.out.print("输入发送文件的路径(形式 D:\\xx.jpg):");
                                    String src =Utility.readString(100);
                                    System.out.print("输入把文件发送到对方的路径(形式 D:\\xx.jpg):");
                                    String dest = Utility.readString(100);
                                    fileClientService.sentFileToOne(src,dest,userId,getterId);
                                    break;

                                case "9":
                                    // 调用方法 给服务器发送一个退出系统的message
                                    userClientService.logout();
                                    loop = false;
                                    break;
                            }

                        }
                    } else {
                        System.out.println("==========登录失败===========");
                    }
                    break;
                case "9":
                    System.out.println("退出系统");
                    loop = false;
                    break;
            }
        }

    }

    public static void main(String[] args) {
        new QQView().mainMenu();
        System.out.println("客户端退出系统");
    }
}
