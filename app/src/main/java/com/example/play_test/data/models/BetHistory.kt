package com.example.play_test.data.models

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BetHistory(

    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @NonNull val uid: Int,
    @NonNull val value: Double,
    @NonNull val win: Boolean,
    @NonNull val period: String,
    @NonNull val oddId: String,
    @NonNull val odd: Double
)
