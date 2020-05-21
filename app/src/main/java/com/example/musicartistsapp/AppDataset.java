package com.example.musicartistsapp;

public class AppDataset {
    private static AppDataset instance = null;

    private String dataset;

    private AppDataset() {
    }


    public static AppDataset getInstance() {
        if (instance == null) {
            instance = new AppDataset();
        }
        return instance;
    }

    public String getDataset() {
        return dataset;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }
}