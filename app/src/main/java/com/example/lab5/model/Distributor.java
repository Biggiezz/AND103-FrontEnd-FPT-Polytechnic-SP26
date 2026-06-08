package com.example.lab5.model;

import com.google.gson.annotations.SerializedName;

public class Distributor {

    @SerializedName("_id")
    private String id;
    private String name;

    @SerializedName(value = "createAt", alternate = "createdAt")
    private String createAt;

    @SerializedName(value = "updateAt", alternate = "updatedAt")
    private String updateAt;

    public Distributor() {
    }

    public Distributor(String id, String name, String createAt, String updateAt) {
        this.id = id;
        this.name = name;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
