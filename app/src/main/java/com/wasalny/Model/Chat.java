package com.wasalny.Model;

import java.io.Serializable;

public class Chat implements Serializable {

    String message;
    int flag; // 0 for driver & 1 for client
    String time;

    public Chat() {
    }

    public Chat(String message, int flag, String time) {
        this.message = message;
        this.flag = flag;
        this.time = time;    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}