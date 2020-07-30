package com.wasalny.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class Room implements Serializable {

    String clientID;
    String driverID;
    String roomID;
    String roomTitle;
    ArrayList<Chat> chatArrayList;
    String journeyID;

    public Room() {
    }

    public Room(String clientID, String driverID, String roomID, String roomTitle, ArrayList<Chat> chatArrayList, String journeyID) {
        this.clientID = clientID;
        this.driverID = driverID;
        this.roomID = roomID;
        this.roomTitle = roomTitle;
        this.chatArrayList = chatArrayList;
        this.journeyID = journeyID;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getDriverID() {
        return driverID;
    }

    public void setDriverID(String driverID) {
        this.driverID = driverID;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getRoomTitle() {
        return roomTitle;
    }

    public void setRoomTitle(String roomTitle) {
        this.roomTitle = roomTitle;
    }

    public ArrayList<Chat> getChatArrayList() {
        return chatArrayList;
    }

    public void setChatArrayList(ArrayList<Chat> chatArrayList) {
        this.chatArrayList = chatArrayList;
    }

    public String getJourneyID() {
        return journeyID;
    }

    public void setJourneyID(String journeyID) {
        this.journeyID = journeyID;
    }
}