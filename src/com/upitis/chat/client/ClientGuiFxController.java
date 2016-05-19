package com.upitis.chat.client;

import com.upitis.chat.Connection;
import com.upitis.chat.Message;
import com.upitis.chat.MessageType;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Created by upitis on 11.05.16.
 */
public class ClientGuiFxController implements Initializable {
    private Connection connection;
    private ClientGuiFxModel model = new ClientGuiFxModel();
    private ClientGuiFxView view;
    Properties properties = new Properties();

    @FXML
    private TextFlow messagesTextArea;
    @FXML
    protected TextArea inputTextArea;
    @FXML
    protected ListView usersListView;
    @FXML
    private Label statusLabel;
    @FXML
    private ScrollPane msgScrollPane;

    @FXML
    public void handleKeyReleased(KeyEvent keyEvent){
        if (keyEvent.getCode()== KeyCode.ENTER){
            inputTextArea.clear();
        }
    }

    @FXML
    private void handleOnKeyPress(KeyEvent keyEvent){
        String msg = inputTextArea.getText();
        if (keyEvent.getCode()== KeyCode.ENTER){
            try {
                String selectedUser = (String)usersListView.getSelectionModel().getSelectedItem();
                if (selectedUser != null && !selectedUser.equals("All")) {
                    msg += "@"+selectedUser;
                } else {
                    msg += "@All";
                }

                connection.send(new Message(MessageType.TEXT, msg));
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputTextArea.clear();
        }
    }

    public ClientGuiFxController() {

    }

    public void setView(ClientGuiFxView view) {
        this.view = view;
    }

    private String getUserName(){
        TextInputDialog dialog = new TextInputDialog(model.getNickName());
        dialog.setTitle("Ник");
        dialog.setHeaderText("Ник");
        dialog.setContentText("Введите псевдоним (ник) для чата:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            properties.setProperty("nickname",result.get());
            return result.get();
        }
        return "UKI";
    }

    private String getServerAddress(){
        TextInputDialog dialog = new TextInputDialog(model.getIpAddress());
        dialog.setTitle("IP");
        dialog.setHeaderText("Адрес подключения");
        dialog.setContentText("Введите ip-адрес подключения:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            properties.setProperty("ip",result.get());
            return  result.get();
        }
        return "127.0.0.1";
    }

    private int getServerPort(){
        TextInputDialog dialog = new TextInputDialog(String.valueOf(model.getPortNumber()));
        dialog.setTitle("Port");
        dialog.setHeaderText("TCP-порт подключения");
        dialog.setContentText("Введите порт, на котором сервер принимает подключения:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            properties.setProperty("port",result.get());
            return Integer.parseInt(result.get());
        }
        return 1234;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        statusLabel.textProperty().bind(model.statusProperty());
        usersListView.setItems(model.usersProperty());


        ClassLoader myCL = ClientGuiFxController.class.getClassLoader();
        try {
            properties.load(myCL.getResourceAsStream("com/upitis/chat/client/client_config.properties"));
            model.setIpAddress(properties.getProperty("ip"));
            model.setPortNumber(Integer.parseInt(properties.getProperty("port")));
            model.setNickName(properties.getProperty("nickname"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        model.setIpAddress(getServerAddress());
        model.setPortNumber(getServerPort());
        model.setNickName(getUserName());

        model.getUsers().add("All");
        model.setUsers(model.getUsers());
    }

    public class SocketThread extends Task<Void> {

        protected void processIncomingMessage(final String message) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    String userName = message.substring(0,message.indexOf(':')+1);
                    String msg = message.substring(message.indexOf(':')+1);

                    Text userNameText = new Text(userName);
                    Text msgText = new Text(msg);
                    Text eof = new Text("\n");

                    userNameText.setFill(Color.RED);

                    messagesTextArea.getChildren().addAll(userNameText,msgText,eof);
                    msgScrollPane.setVvalue(1.0 );
                }
            });
        }

        protected void informAboutAddingNewUser(String userName) {
            model.getUsers().add(userName);
            model.setUsers(model.getUsers());
        }

        protected void informAboutDeletingNewUser(String userName) {
            model.getUsers().remove(userName);
            model.setUsers(model.getUsers());
        }

        protected void clientHandshake() throws IOException, ClassNotFoundException {
            while (true) {
                Message msg = connection.receive();
                switch (msg.getType()) {
                    case NAME_REQUEST:
                        connection.send(new Message(MessageType.USER_NAME, model.getNickName()));
                        break;
                    case NAME_ACCEPTED:
                        model.setStatus(true);
                        return;
                    default:
                        throw new IOException("Unexpected MessageType");

                }
            }
        }

        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            while (true) {
                Message msg = connection.receive();
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
        protected Void call() throws Exception {
            try {
                Socket socket = new Socket(model.getIpAddress(), model.getPortNumber());
                connection = new Connection(socket);
                model.setStatus(true);
                clientHandshake();
                clientMainLoop();
            } catch (IOException ex) {
                model.setStatus(ex.getMessage());
            } catch (ClassNotFoundException ex) {
                model.setStatus(ex.getMessage());
            }
            return null;
        }

    }


}
