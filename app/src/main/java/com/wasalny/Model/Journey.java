package com.wasalny.Model;

import java.io.Serializable;

public class Journey implements Serializable {
    private String clientID;
    private String journeyID;
    private String from;
    private String to;
    private String description;
    private String numOfPassengers;
    private String journeyTime;
    private String backOrNot;
    private String carType;
    private String moreConditions;
    private String expectedBudget; // the client think
    private String finalBudget;  // what they agree with { 0 is default}
    private String journeyStatus; // done or still { still is default}
    private String driverID; // { is default}

    public Journey() {
    }

    public Journey(String clientID, String journeyID, String from, String to, String description, String numOfPassengers, String journeyTime, String backOrNot, String carType, String moreConditions, String expectedBudget, String finalBudget, String journeyStatus, String driverID) {
        this.clientID = clientID;
        this.journeyID = journeyID;
        this.from = from;
        this.to = to;
        this.description = description;
        this.numOfPassengers = numOfPassengers;
        this.journeyTime = journeyTime;
        this.backOrNot = backOrNot;
        this.carType = carType;
        this.moreConditions = moreConditions;
        this.expectedBudget = expectedBudget;
        this.finalBudget = finalBudget;
        this.journeyStatus = journeyStatus;
        this.driverID = driverID;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getJourneyID() {
        return journeyID;
    }

    public void setJourneyID(String journeyID) {
        this.journeyID = journeyID;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNumOfPassengers() {
        return numOfPassengers;
    }

    public void setNumOfPassengers(String numOfPassengers) {
        this.numOfPassengers = numOfPassengers;
    }

    public String getJourneyTime() {
        return journeyTime;
    }

    public void setJourneyTime(String journeyTime) {
        this.journeyTime = journeyTime;
    }

    public String getBackOrNot() {
        return backOrNot;
    }

    public void setBackOrNot(String backOrNot) {
        this.backOrNot = backOrNot;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getMoreConditions() {
        return moreConditions;
    }

    public void setMoreConditions(String moreConditions) {
        this.moreConditions = moreConditions;
    }

    public String getExpectedBudget() {
        return expectedBudget;
    }

    public void setExpectedBudget(String expectedBudget) {
        this.expectedBudget = expectedBudget;
    }

    public String getFinalBudget() {
        return finalBudget;
    }

    public void setFinalBudget(String finalBudget) {
        this.finalBudget = finalBudget;
    }

    public String getJourneyStatus() {
        return journeyStatus;
    }

    public void setJourneyStatus(String journeyStatus) {
        this.journeyStatus = journeyStatus;
    }

    public String getDriverID() {
        return driverID;
    }

    public void setDriverID(String driverID) {
        this.driverID = driverID;
    }
}

