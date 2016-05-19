package com.upitis.chat.client;

/**
 * Created by upitis on 11.05.16.
 */
public interface GuiView {
    String getServerAddress();
    int getServerPort();
    String getUserName();
    void notifyConnectionStatusChanged(boolean clientConnected);
    void refreshMessages();
    void refreshUsers();
}
