package com.upitis.chat.client;

import com.upitis.chat.Connection;
import com.upitis.chat.Message;
import com.upitis.chat.MessageType;

import java.io.IOException;

/**
 * Created by upitis on 13.04.16.
 */
public class ClientGuiController implements Client{
    private Connection connection;
    private volatile boolean clientConnected = false;



    private ClientGuiModel model = new ClientGuiModel();
    private GuiView view = new ClientGuiView(this);

    public static void main(String[] args) {
        ClientGuiController guiController = new ClientGuiController();
        guiController.run();
    }

    public void sendTextMessage(String text) {
        try {
            connection.send(new Message(MessageType.TEXT, text));
        } catch (IOException e) {
            clientConnected = false;
        }
    }

    public ClientSocketThread getSocketThread() {
        return new GuiSocketThread(this);
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean isClientConnected() {
        return clientConnected;
    }

    @Override
    public void setClientConnected(boolean clientConnected) {
        this.clientConnected = clientConnected;
    }

    public String getServerAddress() {
        return view.getServerAddress();
    }

    public int getServerPort() {
        return view.getServerPort();
    }

    public String getUserName() {
        return view.getUserName();
    }

    public void run() {
        ClientSocketThread socketThread = getSocketThread();
        socketThread.setDaemon(true);
        socketThread.run();
    }

    public ClientGuiModel getModel() {
        return model;
    }

    public class GuiSocketThread extends ClientSocketThread {

        public GuiSocketThread(Client client) {
            super(client);
        }

        @Override
        protected void processIncomingMessage(String message) {
            model.setNewMessage(message);
            view.refreshMessages();
        }

        @Override
        protected void informAboutAddingNewUser(String userName) {
            model.addUser(userName);
            view.refreshUsers();
        }

        @Override
        protected void informAboutDeletingNewUser(String userName) {
            model.deleteUser(userName);
            view.refreshUsers();
        }

        @Override
        protected void notifyConnectionStatusChanged(boolean clientConnected) {
            view.notifyConnectionStatusChanged(clientConnected);
        }
    }
}
