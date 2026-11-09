package com.example.findprofessionals;

public class User {
    private String uid;

    private String email;
    private String name;
    private String lastName;
    private String address;
    private boolean status;
    private boolean gender;
    private String phone;
    private String birthday;
    private String description;
    private int score;
    private int scoreTimes;
    private String tags;
    private String dataStartOfJob;

    public int getScoreTimes() {
        return scoreTimes;
    }
    public void setScoreTimes(int scoreTimes) {
        this.scoreTimes = scoreTimes;
    }


    public String getTags() {
        return tags;
    }
    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getDataStartOfJob() {
        return dataStartOfJob;
    }
    public void setDataStartOfJob(String dataStartOfJob) {
        this.dataStartOfJob = dataStartOfJob;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

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

    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public boolean getGender() {
        return gender;
    }

    public void setGender(boolean isMale) {
        this.gender = isMale;
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
        this.uid = UID;
    }
}
