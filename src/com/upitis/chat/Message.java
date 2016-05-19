package com.upitis.chat;

import java.io.Serializable;

/**
 * Created by upitis on 13.04.16.
 */
public class Message implements Serializable {
    private final MessageType type;
    private final String data;

    public Message(MessageType type) {
        this.type = type;
        this.data = null;
    }

    public Message(MessageType type, String data) {
        this.type = type;
        this.data = data;
    }

    public MessageType getType() {
        return type;
    }

    public String getData() {
        return data;
    }
}
