package com.example.findprofessionals;

public class Request {
    private String RID;

    private String fullName;
    private String phone;

    private String title;

    private String category;

    private String description;

    private String location;
    public Request(String RID, String fullName, String phone, String title, String category, String description, String location) {
        this.RID = RID;
        this.fullName = fullName;
        this.phone = phone;
        this.title = title;
        this.category = category;
        this.description = description;
        this.location = location;
    }
    public String getPhone() {
        return phone;

    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getRID() {
        return RID;
    }

    public void setRID(String RID) {
        this.RID = RID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
