package com.imagenim.app.ui.screens

import android.util.Base64
import android.util.Base64
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.imagenim.app.viewmodel.GenerateViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateScreen(
    onBack: () -> Unit,
    viewModel: GenerateViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bild generieren") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Modell", style = MaterialTheme.typography.labelLarge)
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = uiState.selectedModel.displayName,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    uiState.models.forEach { model ->
                        DropdownMenuItem(
                            text = { Text(model.displayName) },
                            onClick = {
                                viewModel.selectModel(model)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Text("Prompt", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = uiState.prompt,
                onValueChange = { viewModel.updatePrompt(it) },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                placeholder = { Text("Eine futuristische Stadt bei Nacht, Neonlichter...") },
                maxLines = 5
            )

            uiState.error?.let { error ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            Button(
                onClick = { viewModel.generate() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Wird generiert...")
                } else {
                    Text("Generieren", style = MaterialTheme.typography.titleMedium)
                }
            }

            uiState.imageBase64?.let { base64 ->
                Text("Ergebnis", style = MaterialTheme.typography.titleMedium)
                val imageBytes = remember(base64) {
                    try {
                        Base64.decode(base64, Base64.DEFAULT)
                    } catch (e: Exception) { null }
                }
                if (imageBytes != null) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(imageBytes)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Generiertes Bild",
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.FitWrap
                        )
                    }
                    OutlinedButton(
                        onClick = { viewModel.saveImage() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Bild speichern")
                    }
                }
            }
        }
    }
}
