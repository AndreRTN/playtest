package com.example.play_test.data.shared

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.play_test.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class Preferences(val context: Context) {

    private val onboardKey = booleanPreferencesKey("seenOnboard")
    suspend fun saveOnboard() {
        context.dataStore.edit { settings ->
            settings[onboardKey] = true
        }
    }

    fun getOnboard(): Flow<Boolean> {
        return context.dataStore.data.map { settings -> settings[onboardKey] ?: false }
    }

}