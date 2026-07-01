package com.imagenim.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.imagenim.app.data.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val apiKey: String = "",
    val saved: Boolean = false,
    val hasKey: Boolean = false
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = PreferencesManager(application)

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = SettingsUiState(
            apiKey = prefs.apiKey,
            hasKey = prefs.apiKey.isNotBlank()
        )
    }

    fun updateApiKey(key: String) {
        _uiState.value = _uiState.value.copy(apiKey = key)
    }

    fun save() {
        viewModelScope.launch {
            prefs.apiKey = _uiState.value.apiKey.trim()
            _uiState.value = _uiState.value.copy(
                saved = true,
                hasKey = prefs.apiKey.isNotBlank()
            )
        }
    }

    fun clearSaved() {
        _uiState.value = _uiState.value.copy(saved = false)
    }
}
