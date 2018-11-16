package com.calcurie.calcurie;

import java.io.Serializable;

public class Food implements Serializable {
    private String id;
    private String name;
    private String detail;
    private int calories;
    private String img_url;
    private int qty;
    private String date;
    private String time;
    private Long total_calories;

    public Food() {}

    public Food(String id, String name, String detail, int calories, String img_url) {
        this.id = id;
        this.name = name;
        this.detail = detail;
        this.calories = calories;
        this.img_url = img_url;
        this.qty = 0;
    }

    public Food(String name, Long calories, Long qty, String date, String time ) {
        this.name = name;
        this.total_calories = calories * qty;
        this.date = date;
        this.time = time;
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

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Long getTotal_calories() {
        return total_calories;
    }

    public void setTotal_calories(Long total_calories) {
        this.total_calories = total_calories;
    }
}
