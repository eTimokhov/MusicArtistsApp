package com.example.musicartistsapp

import java.util.*
import java.util.regex.Pattern

class GlobalConfig private constructor() {
    var dataset: String? = null
    private var backgroundColor = "White"
    private var fontSize = 14
    private var fontFamily = "sans-serif"
    private val observers: MutableList<ConfigObserver> = ArrayList()
    fun addObserver(configObserver: ConfigObserver) {
        observers.add(configObserver)
        notifyObserver(configObserver)
    }

    private fun notifyObserver(configObserver: ConfigObserver) {
        configObserver.updateConfig(fontFamily, fontSize, backgroundColor)
    }

    private fun notifyObservers() {
        for (observer in observers) {
            notifyObserver(observer)
        }
    }

    fun updateGlobalConfig(fontFamily: String, fontSize: String?, backgroundColor: String) {
        this.fontFamily = fontFamily
        this.backgroundColor = backgroundColor
        val matcher = Pattern.compile("\\d+").matcher(fontSize)
        matcher.find()
        this.fontSize = Integer.valueOf(matcher.group())
        notifyObservers()
    }

    companion object {
        @kotlin.jvm.JvmStatic
        var instance: GlobalConfig? = null
            get() {
                if (field == null) {
                    field = GlobalConfig()
                }
                return field
            }
            private set
        private val languageToDataset: MutableMap<String?, String?> = HashMap()
    }

    init {
        languageToDataset["en"] = "artists"
        languageToDataset["ru"] = "artists_ru"
    }
}