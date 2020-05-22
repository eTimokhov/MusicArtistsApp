package com.example.musicartistsapp

interface ConfigObserver {
    fun updateConfig(fontFamily: String?, fontSize: Int, backgroundColor: String?)
}