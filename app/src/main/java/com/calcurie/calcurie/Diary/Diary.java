package com.calcurie.calcurie.Diary;

public class Diary {
    int image;
    String name;
    int calories;
    int amount;

    public Diary(String name, int calories) {
        this.name = name;
        this.calories = calories;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}