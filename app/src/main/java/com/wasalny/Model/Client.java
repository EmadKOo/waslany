package com.wasalny.Model;

import java.io.Serializable;

public class Client implements Serializable {
    private User user;
    private String city;
    private String image;

    public Client(){}

    public Client(User user, String city) {
        this.user = user;
        this.city = city;
    }

    public Client(User user, String city, String image) {
        this.user = user;
        this.city = city;
        this.image = image;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
