package com.example.appfunctionsdemo.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appfunctionsdemo.ui.theme.CardBorder
import com.example.appfunctionsdemo.ui.theme.DeepSlate
import com.example.appfunctionsdemo.ui.theme.MutedText
import com.example.appfunctionsdemo.ui.theme.NeonCyan
import com.example.appfunctionsdemo.ui.theme.NeonGreen
import com.example.appfunctionsdemo.ui.theme.NeonPurple

enum class AdbCommandTab { LIST, CREATE, SEARCH }

@Composable
fun AdbTestingConsole(modifier: Modifier = Modifier) {
    var activeTab by remember { mutableStateOf(AdbCommandTab.LIST) }
    val context = LocalContext.current

    val commandText = when (activeTab) {
        AdbCommandTab.LIST -> {
            "adb shell cmd app_function list-app-functions | grep com.example.appfunctionsdemo"
        }

        AdbCommandTab.CREATE -> {
            "adb shell 'cmd app_function execute-app-function --package com.example.appfunctionsdemo --function \"com.example.appfunctionsdemo.functions.NoteFunctions#createNote\" --parameters \"{\\\"title\\\":\\\"Nota desde ADB\\\",\\\"content\\\":\\\"Esta nota fue registrada headlessly a traves de AppFunctions!\\\"}\"'"
        }

        AdbCommandTab.SEARCH -> {
            "adb shell 'cmd app_function execute-app-function --package com.example.appfunctionsdemo --function \"com.example.appfunctionsdemo.functions.NoteFunctions#getNotes\" --parameters \"{\\\"query\\\":\\\"\\\"}\"'"
        }
    }

    val commandExplanation = when (activeTab) {
        AdbCommandTab.LIST -> "Muestra en formato JSON las AppFunctions indexadas por el sistema de Android 16."
        AdbCommandTab.CREATE -> "Llama en segundo plano (headless) a la función 'createNote' insertando una nueva nota con sincronización multi-proceso reactiva."
        AdbCommandTab.SEARCH -> "Invoca de forma headless la función 'getNotes' retornando la persistencia de las notas guardadas."
    }

    fun copyToClipboard() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("ADB Command", commandText)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Comando copiado al portapapeles", Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0A0A10))
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = "ADB Console",
                tint = NeonGreen,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Consola de Pruebas ADB (AppFunctions)",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Simula llamadas de agentes inteligentes (como Gemini) directamente en local a través del servicio de consola de Android 16:",
            color = MutedText,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AdbTabButton(
                label = "Listar",
                isSelected = activeTab == AdbCommandTab.LIST,
                onClick = { activeTab = AdbCommandTab.LIST }
            )
            AdbTabButton(
                label = "Crear Nota",
                isSelected = activeTab == AdbCommandTab.CREATE,
                onClick = { activeTab = AdbCommandTab.CREATE }
            )
            AdbTabButton(
                label = "Buscar",
                isSelected = activeTab == AdbCommandTab.SEARCH,
                onClick = { activeTab = AdbCommandTab.SEARCH }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Terminal Block
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF141420))
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "terminal",
                    color = MutedText,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
                Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = "Copiar Comando",
                    tint = NeonCyan,
                    modifier = Modifier
                        .size(16.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { copyToClipboard() }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = commandText,
                color = NeonGreen,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                lineHeight = 16.sp,
                modifier = Modifier.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { copyToClipboard() }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Acción: $commandExplanation",
            color = MutedText,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun AdbTabButton(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) NeonPurple else DeepSlate)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else MutedText,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
