package com.upitis.chat;

/**
 * Created by upitis on 13.04.16.
 */
public enum MessageType {
    NAME_REQUEST,  //запрос имени
    USER_NAME,     //имя пользователя
    NAME_ACCEPTED, //имя принято
    TEXT,          //текстовое сообщение
    USER_ADDED,    //пользователь добавлен
    USER_REMOVED;  //пользователь удален
}
