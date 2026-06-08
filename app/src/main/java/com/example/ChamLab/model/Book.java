package com.example.ChamLab.model;

import com.google.gson.annotations.SerializedName;

public class Book {
    @SerializedName("_id")
    private String id;

    @SerializedName("tensach")
    private String tenSach;

    @SerializedName("tacgia")
    private String tacGia;

    private int namXb;

    public Book(String id, String tenSach, String tacGia, int namXb) {
        this.id = id;
        this.tenSach = tenSach;
        this.tacGia = tacGia;
        this.namXb = namXb;
    }

    public Book(String tenSach, String tacGia, String namXb) {
        this.tenSach = tenSach;
        this.tacGia = tacGia;
        this.namXb = Integer.parseInt(namXb);
    }

    public Book(String tenSach, String tacGia, int namXbInt) {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenSach() {
        return tenSach;
    }

    public void setTenSach(String tenSach) {
        this.tenSach = tenSach;
    }

    public String getTacGia() {
        return tacGia;
    }

    public void setTacGia(String tacGia) {
        this.tacGia = tacGia;
    }

    public int getNamXb() {
        return namXb;
    }

    public void setNamXb(int namXb) {
        this.namXb = namXb;
    }
}
