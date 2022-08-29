package com.example.play_test.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Balance(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val value: Double = 0.0,
)
