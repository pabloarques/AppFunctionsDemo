package com.example.appfunctionsdemo.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.appfunctionsdemo.ui.theme.CardBorder
import com.example.appfunctionsdemo.ui.theme.DeepSlate
import com.example.appfunctionsdemo.ui.theme.MutedText
import com.example.appfunctionsdemo.ui.theme.NeonCyan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarComponent(query: String, onQueryChange: (String) -> Unit) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Buscar notas...", color = MutedText) },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Buscar", tint = MutedText) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = DeepSlate,
            unfocusedContainerColor = DeepSlate,
            disabledContainerColor = DeepSlate,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedIndicatorColor = NeonCyan,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
    )
}
