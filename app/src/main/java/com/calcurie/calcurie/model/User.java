package com.calcurie.calcurie.model;

import android.provider.BaseColumns;

public class User {
    private String id;
    private String name;
    private String gender;
    private int age;
    private float weight;
    private float height;
    private int activityLevel;
    private String imageUrl = "";

    public static final String DATABASE_NAME = "calcurie_users.db";
    public static final int DATABASE_VERSION = 2;
    public static final String TABLE = "user";

    public User(){

    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public User(String id, String name, String gender, int age, float weight, float height, int activityLevel, String imageUrl) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.activityLevel = activityLevel;
        this.imageUrl = imageUrl;
    }

    public class Column {
        public static final String ID = BaseColumns._ID;
        public static final String NAME = "name";
        public static final String GENDER = "gender";
        public static final String AGE = "age";
        public static final String WEIGHT = "weight";
        public static final String HEIGHT = "height";
        public static final String ACTIVITY_LEVEL = "activity_level";
        public static final String IMAGE_URL = "image_url";
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public int getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(int activityLevel) {
        this.activityLevel = activityLevel;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
