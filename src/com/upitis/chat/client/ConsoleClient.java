package com.upitis.chat.client;

import com.upitis.chat.Connection;
import com.upitis.chat.ConsoleHelper;
import com.upitis.chat.Message;
import com.upitis.chat.MessageType;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by upitis on 13.04.16.
 */
public class ConsoleClient implements Client{
    protected Connection connection;
    private volatile boolean clientConnected = false;

    public static void main(String[] args) {
        ConsoleClient client = new ConsoleClient();
        client.run();
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public boolean isClientConnected() {
        return clientConnected;
    }

    public void setClientConnected(boolean clientConnected) {
        this.clientConnected = clientConnected;
    }

    public String getServerAddress() {
        ConsoleHelper.writeMessage("Введите ip-адрес подключения");
        String ipAddress = ConsoleHelper.readString();
        return ipAddress;
    }

    public int getServerPort() {
        ConsoleHelper.writeMessage("Введите порт подключения");
        int portNumber = ConsoleHelper.readInt();
        return portNumber;
    }

    public String getUserName() {
        ConsoleHelper.writeMessage("Введите ваше имя");
        String userName = ConsoleHelper.readString();
        return userName;
    }

    protected boolean shouldSentTextFromConsole() {
        return true;
    }

    public ClientSocketThread getSocketThread() {
        return new ClientSocketThread(this);
    }

    public void sendTextMessage(String text) {
        try {
            connection.send(new Message(MessageType.TEXT, text));
        } catch (IOException e) {
            ConsoleHelper.writeMessage("Произошла ошибка при отправке сообщения серверу");
            clientConnected = false;
        }
    }

    public void run() {
        ClientSocketThread socketThread = getSocketThread();
        socketThread.setDaemon(true);
        socketThread.start();
        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException e) {
                ConsoleHelper.writeMessage("Непредвиденная ошибка. Завершение программы.");
                return;
            }
        }
        if (clientConnected) {
            ConsoleHelper.writeMessage("Соединение установлено. Для выхода наберите команду 'exit'.");
        } else {
            ConsoleHelper.writeMessage("Произошла ошибка во время работы клиента.");
        }
        while (clientConnected) {
            String text = ConsoleHelper.readString();
            if ("exit".equals(text)) break;
            if (shouldSentTextFromConsole()) {
                sendTextMessage(text);
            }
        }
    }

}
