package com.example.appfunctionsdemo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appfunctionsdemo.model.Note
import com.example.appfunctionsdemo.ui.theme.CardBorder
import com.example.appfunctionsdemo.ui.theme.DeepSlate
import com.example.appfunctionsdemo.ui.theme.NeonCyan

@Composable
fun NoteCardComponent(note: Note, onDelete: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DeepSlate)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = note.title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "ID: ${note.id}",
                    color = NeonCyan.copy(alpha = 0.8f),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x1AFF5555))
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Eliminar Nota",
                    tint = Color(0xFFFF5555),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = note.content,
            color = Color.LightGray,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    }
}
