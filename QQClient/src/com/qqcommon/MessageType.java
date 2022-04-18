package com.qqcommon;

/**
 * @Yanx
 * @Create 2022-04-17-12:00
 */
public interface MessageType {
    public String MESSAGE_LOGIN_SUCCEED = "1";//登录成功
    public String MESSAGE_LOGIN_FAIL = "2";//登录失败
    public String MESSAGE_COMM_MES = "3";//普通信息包
    public String MESSAGE_GET_ONLINE_FRIEND = "4";//在线用户列表
    public String MESSAGE_RET_ONLINE_FRIEND = "5";//返回在线用户列表
    public String MESSAGE_CLIENT_EXIT = "6";//客户端请求退出
    public String MESSAGE_TO_ALL_MES = "7";//群发消息
    public String MESSAGE_FILE_MES = "8";//发送文件
}
