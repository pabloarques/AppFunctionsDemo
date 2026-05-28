package com.example.appfunctionsdemo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appfunctionsdemo.model.Note
import com.example.appfunctionsdemo.ui.components.AdbTestingConsole
import com.example.appfunctionsdemo.ui.components.AddNoteDialog
import com.example.appfunctionsdemo.ui.components.NoteCardComponent
import com.example.appfunctionsdemo.ui.components.SearchBarComponent
import com.example.appfunctionsdemo.ui.components.StatItem
import com.example.appfunctionsdemo.ui.theme.AppTheme
import com.example.appfunctionsdemo.ui.theme.CardBorder
import com.example.appfunctionsdemo.ui.theme.MutedText
import com.example.appfunctionsdemo.ui.theme.NeonCyan
import com.example.appfunctionsdemo.ui.theme.NeonGreen
import com.example.appfunctionsdemo.ui.theme.NeonPurple
import com.example.appfunctionsdemo.ui.theme.ObsidianBg
import com.example.appfunctionsdemo.ui.viewmodel.NoteDashboardUiState
import com.example.appfunctionsdemo.ui.viewmodel.NoteIntent
import com.example.appfunctionsdemo.ui.viewmodel.NoteSideEffect
import com.example.appfunctionsdemo.ui.viewmodel.NoteViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NoteDashboardScreen(
    viewModel: NoteViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is NoteSideEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
                is NoteSideEffect.ShowError -> snackbarHostState.showSnackbar("⚠️ ${effect.message}")
            }
        }
    }

    NoteDashboardContent(
        uiState = uiState,
        onIntent = viewModel::processIntent,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoteDashboardContent(
    uiState: NoteDashboardUiState,
    onIntent: (NoteIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onIntent(NoteIntent.ShowAddDialog) },
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(colors = listOf(NeonCyan, NeonPurple)))
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Añadir Nota")
            }
        },
        containerColor = ObsidianBg
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                HeaderSection(notesCount = uiState.allNotes.size)
            }

            // Search Bar Section
            item {
                SearchBarComponent(
                    query = uiState.searchQuery,
                    onQueryChange = { onIntent(NoteIntent.SearchQueryChanged(it)) }
                )
            }

            // Interactive Testing & ADB commands
            item {
                AdbTestingConsole()
            }

            // Note List Title
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mis Notas Guardadas",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${uiState.filteredNotes.size} notas encontradas",
                        color = MutedText,
                        fontSize = 12.sp
                    )
                }
            }

            // Notes List or Empty State
            if (uiState.filteredNotes.isEmpty() && !uiState.isLoading) {
                item {
                    EmptyStateComponent()
                }
            } else {
                items(uiState.filteredNotes, key = { it.id }) { note ->
                    NoteCardComponent(
                        note = note,
                        onDelete = { onIntent(NoteIntent.DeleteNote(note.id)) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Add Note Dialog
    if (uiState.showAddDialog) {
        AddNoteDialog(
            onDismiss = { onIntent(NoteIntent.HideAddDialog) },
            onSave = { title, content ->
                onIntent(NoteIntent.AddNote(title, content))
            }
        )
    }
}

@Composable
private fun HeaderSection(
    notesCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1E1035), Color(0xFF0F0C20))
                )
            )
            .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "AppFunctions",
                    color = NeonCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Notes Agent Demo",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x1FFF0087))
                    .border(1.dp, Color(0x4DFF0087), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFFF0087))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Android 16 Dev",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = CardBorder)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatItem(label = "Total Notas", value = notesCount.toString(), color = NeonCyan)
            StatItem(label = "AppFunctions", value = "3 Activas", color = NeonPurple)
            StatItem(label = "Execution", value = "Headless", color = NeonGreen)
        }
    }
}

@Composable
private fun EmptyStateComponent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No hay notas disponibles",
            color = MutedText,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Usa el botón + o ejecuta comandos ADB para añadir notas.",
            color = MutedText.copy(alpha = 0.6f),
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NoteDashboardContentPreview() {
    AppTheme {
        NoteDashboardContent(
            uiState = NoteDashboardUiState(
                allNotes = listOf(
                    Note("1", "Nota de ejemplo 1", "Este es el contenido de la nota 1"),
                    Note("2", "Nota de ejemplo 2", "Este es el contenido de la nota 2")
                ),
                filteredNotes = listOf(
                    Note("1", "Nota de ejemplo 1", "Este es el contenido de la nota 1"),
                    Note("2", "Nota de ejemplo 2", "Este es el contenido de la nota 2")
                ),
                searchQuery = "",
                showAddDialog = false,
                isLoading = false
            ),
            onIntent = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}
