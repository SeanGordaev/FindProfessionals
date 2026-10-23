package com.example.findprofessionals;

import android.media.Image;

public class User {
    private String email;
    private String name;
    private String lastName;
    private String address;
    private String picture;
    private boolean status;
    private boolean gender;
    private String phone;
    private String birthday;
    private String UID;

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    private Image image;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String uid) {
        this.UID = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean getGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public String getLastName() { return lastName; }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public User()
    {

    }

    public User(String email, String name, String lastName, String address, boolean status, String phone, String birthday, String UID) {
        this.email = email;
        this.name = name;
        this.lastName = lastName;
        this.address = address;
        this.status = status;
        this.phone = phone;
        this.birthday = birthday;
        this.UID = UID;
    }
}
