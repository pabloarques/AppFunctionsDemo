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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appfunctionsdemo.ui.components.AdbTestingConsole
import com.example.appfunctionsdemo.ui.components.AddNoteDialog
import com.example.appfunctionsdemo.ui.components.NoteCardComponent
import com.example.appfunctionsdemo.ui.components.SearchBarComponent
import com.example.appfunctionsdemo.ui.components.StatItem
import com.example.appfunctionsdemo.ui.theme.CardBorder
import com.example.appfunctionsdemo.ui.theme.MutedText
import com.example.appfunctionsdemo.ui.theme.NeonCyan
import com.example.appfunctionsdemo.ui.theme.NeonGreen
import com.example.appfunctionsdemo.ui.theme.NeonPurple
import com.example.appfunctionsdemo.ui.theme.ObsidianBg
import com.example.appfunctionsdemo.ui.viewmodel.NoteViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDashboardScreen(
    viewModel: NoteViewModel = koinViewModel()
) {
    val notesList by viewModel.notesList.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val showAddDialog by viewModel.showAddDialog.collectAsState()
    val filteredNotes by viewModel.filteredNotes.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.setShowAddDialog(true) },
                containerColor = Color.Transparent,
                contentColor = Color.White,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(NeonCyan, NeonPurple)
                        )
                    )
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
                HeaderSection(notesCount = notesList.size)
            }

            // Search Bar Section
            item {
                SearchBarComponent(
                    query = searchQuery,
                    onQueryChange = { viewModel.onSearchQueryChange(it) }
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
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${filteredNotes.size} notas encontradas",
                        color = MutedText,
                        fontSize = 12.sp
                    )
                }
            }

            // Notes List or Empty State
            if (filteredNotes.isEmpty()) {
                item {
                    EmptyStateComponent()
                }
            } else {
                items(filteredNotes, key = { it.id }) { note ->
                    NoteCardComponent(
                        note = note,
                        onDelete = {
                            viewModel.deleteNote(note.id)
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp)) // Extra space for FAB and bottom
            }
        }
    }

    // Add Note Dialog
    if (showAddDialog) {
        AddNoteDialog(
            onDismiss = { viewModel.setShowAddDialog(false) },
            onSave = { title, content ->
                viewModel.addNote(title, content)
                viewModel.setShowAddDialog(false)
            }
        )
    }
}

@Composable
fun HeaderSection(notesCount: Int) {
    Column(
        modifier = Modifier
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
                    color = Color.White,
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
                        color = Color.White,
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
fun EmptyStateComponent() {
    Column(
        modifier = Modifier
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
