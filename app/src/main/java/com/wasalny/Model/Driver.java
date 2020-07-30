package com.wasalny.Model;

import java.io.Serializable;

public class Driver implements Serializable {
    User user;
    String cityDriver;
    String carModelNumber;
    String driverLicence;
    String userIdentity;
    String imageLink;

    public Driver() {
    }

    public Driver(User user, String cityDriver, String carModelNumber, String driverLicence, String userIdentity, String imageLink) {
        this.user = user;
        this.cityDriver = cityDriver;
        this.carModelNumber = carModelNumber;
        this.driverLicence = driverLicence;
        this.userIdentity = userIdentity;
        this.imageLink = imageLink;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCityDriver() {
        return cityDriver;
    }

    public void setCityDriver(String cityDriver) {
        this.cityDriver = cityDriver;
    }

    public String getCarModelNumber() {
        return carModelNumber;
    }

    public void setCarModelNumber(String carModelNumber) {
        this.carModelNumber = carModelNumber;
    }

    public String getDriverLicence() {
        return driverLicence;
    }

    public void setDriverLicence(String driverLicence) {
        this.driverLicence = driverLicence;
    }

    public String getUserIdentity() {
        return userIdentity;
    }

    public void setUserIdentity(String userIdentity) {
        this.userIdentity = userIdentity;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }
}