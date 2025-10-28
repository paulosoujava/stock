package com.meu.stock.views.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GradientButton(
    text: String, gradient: Brush,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = { },
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        modifier = modifier.height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(), // Remove o padding interno do botão
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        enabled = enabled
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = gradient,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                )
                // Aplica um alfa menor se o botão estiver desabilitado
                .then(if (!enabled) Modifier.alpha(0.5f) else Modifier),
            contentAlignment = Alignment.Center,
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(text, color = Color.White, fontSize = 16.sp)
            }
        }
    }
}
