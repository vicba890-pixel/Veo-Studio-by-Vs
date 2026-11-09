package com.example.ui

import java.io.Serializable

enum class AutoSaveState {
    IDLE,
    SAVING,
    SAVED,
    ERROR
}

data class AutoSaveStatus(
    val state: AutoSaveState = AutoSaveState.IDLE,
    val lastSavedTimestamp: Long = System.currentTimeMillis(),
    val saveVersion: Long = 1L,
    val lastSavedReason: String = "Inicio de sesión",
    val errorMessage: String? = null
) : Serializable
