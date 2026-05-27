package com.example.appfunctionsdemo.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appfunctionsdemo.ui.theme.CardBorder
import com.example.appfunctionsdemo.ui.theme.DeepSlate
import com.example.appfunctionsdemo.ui.theme.MutedText
import com.example.appfunctionsdemo.ui.theme.NeonCyan
import com.example.appfunctionsdemo.ui.theme.ObsidianBg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteDialog(
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Nueva Nota",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título", color = MutedText) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = DeepSlate,
                        unfocusedContainerColor = DeepSlate,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Contenido", color = MutedText) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = DeepSlate,
                        unfocusedContainerColor = DeepSlate,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        onSave(title, content)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan,
                    contentColor = ObsidianBg
                )
            ) {
                Text("Guardar", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = MutedText)
            }
        },
        containerColor = DeepSlate,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.border(1.dp, CardBorder, RoundedCornerShape(24.dp))
    )
}
