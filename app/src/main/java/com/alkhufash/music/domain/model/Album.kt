package com.alkhufash.music.domain.model

data class Album(
    val id: Long,
    val name: String,
    val artist: String,
    val songCount: Int,
    val year: Int = 0
)
