package com.example.musicartistsapp

data class ArtistModel(
        var name: String? = null,
        var description: String? = null,
        var country: String? = null,
        var imagePath: String? = null,
        var videoPath: String? = null,
        var genres: List<String>? = null
)