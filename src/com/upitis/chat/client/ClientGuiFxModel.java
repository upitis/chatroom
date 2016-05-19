package com.upitis.chat.client;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by upitis on 12.05.16.
 */
public class ClientGuiFxModel {
    private ListProperty<String> users = new SimpleListProperty<>();
    private Set<String> usersList = new HashSet<>();

    private String ipAddress = "127.0.0.1";
    private int portNumber = 1234;
    private String nickName = "user_"+(int)(Math.random()*1000);

    private StringProperty messageToSend = new SimpleStringProperty();
    private StringProperty status = new SimpleStringProperty("Ожидается подключение");

    public Set<String> getUsers() {
        return usersList;
    }

    public void setUsers(Set<String> users) {
        this.users.set(FXCollections.observableArrayList(users));
    }

    public ListProperty<String> usersProperty(){
        return users;
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.setValue(status);
    }

    public void setStatus(boolean status) {
        if (status){
            this.status.setValue("Подключено");
        } else {
            this.status.setValue("Ошибка подключения");
        }
    }

    public StringProperty statusProperty(){
        return status;
    }

    public String getMessageToSend() {
        return messageToSend.get();
    }

    public StringProperty messageToSendProperty() {
        return messageToSend;
    }

    public void setMessageToSend(String messageToSend) {
        this.messageToSend.set(messageToSend);
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }
}
