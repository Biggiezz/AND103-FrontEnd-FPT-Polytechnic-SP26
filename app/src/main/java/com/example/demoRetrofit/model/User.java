package com.example.demoRetrofit.model;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("_id")
    private String id;
    private String userName;
    private String password;
    private int age;
    private String adress;
    private String image;
    @SerializedName("createdAt")
    private String createAt;
    @SerializedName("updatedAt")
    private String updateAt;

    public User() {
    }

    public User(String id, String userName, String password, int age, String adress, String image, String createAt, String updateAt) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.age = age;
        this.adress = adress;
        this.image = image;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }
    public User(String username, int ageInt, String address, String image) {
        this.userName = username;
        this.age = ageInt;
        this.adress = address;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return userName;
    }

    public void setUsername(String username) {
        this.userName = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", adress='" + adress + '\'' +
                ", image='" + image + '\'' +
                ", age=" + age +
                ", createAt='" + createAt + '\'' +
                ", updateAt='" + updateAt + '\'' +
                '}';
    }
}
