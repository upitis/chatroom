package com.upitis.chat.client;

import com.upitis.chat.Connection;

/**
 * Created by upitis on 12.05.16.
 */
public interface Client {
    Connection getConnection();
    void setConnection(Connection connection);
    boolean isClientConnected();
    void setClientConnected(boolean clientConnected);
    String getServerAddress();
    int getServerPort();
    String getUserName();
    void run();
    ClientSocketThread getSocketThread();
    void sendTextMessage(String text);
}
