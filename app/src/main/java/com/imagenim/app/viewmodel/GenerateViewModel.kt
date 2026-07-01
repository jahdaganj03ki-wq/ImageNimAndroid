package com.imagenim.app.viewmodel

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.imagenim.app.data.GenerateRequest
import com.imagenim.app.data.ModelCatalog
import com.imagenim.app.data.ModelInfo
import com.imagenim.app.data.NIMApiService
import com.imagenim.app.data.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class GenerateUiState(
    val models: List<ModelInfo> = ModelCatalog.generationModels,
    val selectedModel: ModelInfo = ModelCatalog.generationModels[0],
    val prompt: String = "",
    val isLoading: Boolean = false,
    val imageBase64: String? = null,
    val error: String? = null,
    val hasApiKey: Boolean = false
)

class GenerateViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = PreferencesManager(application)

    private val _uiState = MutableStateFlow(GenerateUiState())
    val uiState: StateFlow<GenerateUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = _uiState.value.copy(hasApiKey = prefs.apiKey.isNotBlank())
    }

    fun updatePrompt(text: String) {
        _uiState.value = _uiState.value.copy(prompt = text, error = null)
    }

    fun selectModel(model: ModelInfo) {
        _uiState.value = _uiState.value.copy(selectedModel = model, error = null)
    }

    fun generate() {
        val state = _uiState.value
        if (!state.hasApiKey) {
            _uiState.value = state.copy(error = "Bitte API-Key in den Einstellungen hinterlegen")
            return
        }
        if (state.prompt.isBlank()) {
            _uiState.value = state.copy(error = "Bitte einen Prompt eingeben")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null, imageBase64 = null)

            val service = NIMApiService(prefs.apiKey)
            val request = GenerateRequest(prompt = state.prompt)

            val result = withContext(Dispatchers.IO) {
                service.generateImage(state.selectedModel, request)
            }

            result.fold(
                onSuccess = { response ->
                    val base64 = response.artifacts?.firstOrNull()?.base64
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        imageBase64 = base64
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Unbekannter Fehler"
                    )
                }
            )
        }
    }

    fun saveImage() {
        val base64 = _uiState.value.imageBase64 ?: return
        viewModelScope.launch {
            val context = getApplication<Application>()
            withContext(Dispatchers.IO) {
                try {
                    val imageBytes = Base64.decode(base64, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                    val filename = "ImageNim_${System.currentTimeMillis()}.png"
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val values = ContentValues().apply {
                            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/ImageNim")
                        }
                        val uri = context.contentResolver.insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
                        )
                        uri?.let {
                            context.contentResolver.openOutputStream(it)?.use { stream ->
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                            }
                        }
                    }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Bild gespeichert", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Fehler beim Speichern: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
