package com.upitis.chat.client;

import com.upitis.chat.Connection;
import com.upitis.chat.ConsoleHelper;
import com.upitis.chat.Message;
import com.upitis.chat.MessageType;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by upitis on 12.05.16.
 */
public class ClientSocketThread extends Thread {
    private Client client;

    public ClientSocketThread(Client client){
        this.client = client;
    }

    protected void processIncomingMessage(String message) {
        ConsoleHelper.writeMessage(message);
    }

    protected void informAboutAddingNewUser(String userName) {
        ConsoleHelper.writeMessage(userName + " присоединился к чату.");
    }

    protected void informAboutDeletingNewUser(String userName) {
        ConsoleHelper.writeMessage(userName + " покинул чат.");
    }

    protected void notifyConnectionStatusChanged(boolean clientConnected) {
        client.setClientConnected(clientConnected);
        synchronized (client) {
            client.notify();
        }
    }

    protected void clientHandshake() throws IOException, ClassNotFoundException {
        while (true) {
            Message msg = client.getConnection().receive();
            switch (msg.getType()) {
                case NAME_REQUEST:
                    client.getConnection().send(new Message(MessageType.USER_NAME, client.getUserName()));
                    break;
                case NAME_ACCEPTED:
                    notifyConnectionStatusChanged(true);
                    return;
                default:
                    throw new IOException("Unexpected MessageType");

            }
        }
    }

    protected void clientMainLoop() throws IOException, ClassNotFoundException {
        while (true) {
            Message msg = client.getConnection().receive();
            switch (msg.getType()) {
                case TEXT:
                    processIncomingMessage(msg.getData());
                    break;
                case USER_ADDED:
                    informAboutAddingNewUser(msg.getData());
                    break;
                case USER_REMOVED:
                    informAboutDeletingNewUser(msg.getData());
                    break;
                default:
                    throw new IOException("Unexpected MessageType");
            }
        }
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(client.getServerAddress(), client.getServerPort());
            client.setConnection(new Connection(socket));
            clientHandshake();
            clientMainLoop();
        } catch (IOException ex) {
            notifyConnectionStatusChanged(false);
        } catch (ClassNotFoundException ex) {
            notifyConnectionStatusChanged(false);
        }
    }
}
