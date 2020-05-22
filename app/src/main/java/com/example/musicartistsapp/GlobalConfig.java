package com.example.musicartistsapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GlobalConfig {
    private static GlobalConfig instance = null;

    private String dataset;
    private String language;
    private String backgroundColor = "White";
    private Integer fontSize = 14;
    private String fontFamily = "sans-serif";

    private final List<ConfigObserver> observers = new ArrayList<>();

    private static final Map<String, String> languageToDataset = new HashMap(); {
        languageToDataset.put("en", "artists");
        languageToDataset.put("ru", "artists_ru");
    }

    public void addObserver(ConfigObserver configObserver) {
        observers.add(configObserver);
        notifyObserver(configObserver);
    }

    public void removeObserver(ConfigObserver configObserver) {
        observers.remove(configObserver);
    }

    public void notify(ConfigObserver observer) {
        observer.updateConfig(fontFamily, fontSize, backgroundColor);
    }

    private void notifyObserver(ConfigObserver configObserver) {
        configObserver.updateConfig(fontFamily, fontSize, backgroundColor);
    }

    private void notifyObservers() {
        for (ConfigObserver observer : observers) {
            notifyObserver(observer);
        }
    }

    private GlobalConfig() {
    }

    public static GlobalConfig getInstance() {
        if (instance == null) {
            instance = new GlobalConfig();
        }
        return instance;
    }

    public String getDataset() {
        return dataset;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        dataset = languageToDataset.get(language);
        this.language = language;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Integer getFontSize() {
        return fontSize;
    }

    public void setFontSize(Integer fontSize) {
        this.fontSize = fontSize;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }


    public void updateGlobalConfig(String fontFamily, String fontSize, String backgroundColor) {
        setFontFamily(fontFamily);
        setBackgroundColor(backgroundColor);

        Matcher matcher = Pattern.compile("\\d+").matcher(fontSize);
        matcher.find();
        setFontSize(Integer.valueOf(matcher.group()));

        notifyObservers();
    }
}