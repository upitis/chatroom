package com.upitis.chat;

import com.upitis.chat.client.ConsoleClient;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by upitis on 13.04.16.
 */
public class Server {
    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();


    private static class Handler extends Thread{
        private Socket socket;

        public Handler(Socket socket){
            this.socket = socket;
        }

        private String serverHandshake(Connection connection) throws IOException,ClassNotFoundException{
            while (true) {
                connection.send(new Message(MessageType.NAME_REQUEST));
                Message msg = connection.receive();
                if (msg!=null && msg.getType() == MessageType.USER_NAME){
                    String userName = msg.getData();
                    if (userName != null && !userName.isEmpty() && !connectionMap.containsKey(userName)) {
                        connectionMap.put(msg.getData(), connection);
                        connection.send(new Message(MessageType.NAME_ACCEPTED));
                        return userName;
                    }
                }
            }
        }

        private void sendListOfUsers(Connection connection, String userName) throws IOException{
            for (String user: connectionMap.keySet()){
                if (!user.equals(userName))
                    connection.send(new Message(MessageType.USER_ADDED,user));
            }
        }

        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException{
            while (true){
                Message msg = connection.receive();
                if (msg!=null && msg.getType() == MessageType.TEXT){
                    String msgString = msg.getData();
                    // Private msgs have to end with @username
                    if (msgString.contains("@")){
                        String forUser = msgString.substring(msgString.lastIndexOf("@")+1);

                        msgString = msgString.substring(0, msgString.lastIndexOf("@"));
                        if (forUser.equals("All")){
                            sendBroadcastMessage(new Message(MessageType.TEXT, userName + ": " + msgString));
                        } else {
                            msgString = userName + "(for " + forUser + "): " + msgString;
                            if (connectionMap.containsKey(forUser)) {
                                sendPrivateMessage(new Message(MessageType.TEXT, msgString), forUser);
                                sendPrivateMessage(new Message(MessageType.TEXT, msgString), userName);
                            }
                        }
                    } else {
                        sendBroadcastMessage(new Message(MessageType.TEXT, userName + ": " + msgString));
                    }
                }
                else{
                    ConsoleHelper.writeMessage("Ошибка: Сообщение от "+userName+" не ТЕКСТ.");
                }
            }
        }

        @Override
        public void run() {
            ConsoleHelper.writeMessage("Установлено новое соединение с адресом: " + socket.getRemoteSocketAddress());
            String userName = null;
            Connection connection = null;
            try {
                connection = new Connection(socket);
                userName = serverHandshake(connection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, userName));
                sendListOfUsers(connection, userName);
                serverMainLoop(connection, userName);
            }
            catch (IOException ex){
                try {
                    connection.close();
                } catch (IOException e) { }
                ConsoleHelper.writeMessage("Произошла ошибка при обмене с сервером");
            }
            catch (ClassNotFoundException ex){
                try {
                    connection.close();
                } catch (IOException e) { }
                ConsoleHelper.writeMessage("Произошла ошибка при обмене с сервером");
            }
            finally {
                if (userName!=null) {
                    connectionMap.remove(userName);
                    sendBroadcastMessage(new Message(MessageType.USER_REMOVED,userName));
                }
                ConsoleHelper.writeMessage("Соединение с адресом: " + socket.getRemoteSocketAddress() + " закрыто!");
            }


        }
    }

    public static void sendBroadcastMessage(Message message){
        for (Map.Entry<String,Connection> entry: connectionMap.entrySet()){
            try {
                entry.getValue().send(message);
            } catch (IOException e) {
                ConsoleHelper.writeMessage("Проблемы с отправкой сообщения для " + entry.getKey());
            }
        }
    }

    public static void sendPrivateMessage(Message message, String userName){
        try {
            connectionMap.get(userName).send(message);
        } catch (IOException ex) {
            ConsoleHelper.writeMessage("Проблемы с отправкой сообщения для " + userName);
        }
    }

    public static void main(String[] args){
        int portNumber;
        try {
            portNumber = Integer.parseInt(args[0]);
        } catch (Exception ex) {
            ConsoleHelper.writeMessage("Задайте порт сервера.");
            portNumber = ConsoleHelper.readInt();
        }
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(portNumber);
            ConsoleHelper.writeMessage("Сервер слушает на порту "+portNumber+"...");
            while (true){
                Socket clientSocket = serverSocket.accept();
                new Handler(clientSocket).start();
            }
        } catch (Exception e) {
            ConsoleHelper.writeMessage("Возникли c сервером");
            try {
                serverSocket.close();
            } catch (IOException e1) {}
        }

    }
}
