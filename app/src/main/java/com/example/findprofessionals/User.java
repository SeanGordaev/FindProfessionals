package com.example.findprofessionals;

public class User {
    private String email;
    private String name;
    private String lastName;
    private String address;
    private String picture;
    private String status;
    private String gender;
    private String phone;
    private String birthday;
    private String code;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLastName() {
        return lastName;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User()
    {

    }

    public User(String email, String name, String lastName, String address, String picture, String status, String gender, String phone, String birthday, String code) {
        this.email = email;
        this.name = name;
        this.lastName = lastName;
        this.address = address;
        this.picture = picture;
        this.status = status;
        this.gender = gender;
        this.phone = phone;
        this.birthday = birthday;
        this.code = code;
    }
}
