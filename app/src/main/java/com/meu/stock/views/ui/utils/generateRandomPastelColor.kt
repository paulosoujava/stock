package com.meu.stock.views.ui.utils


import androidx.compose.ui.graphics.Color
import kotlin.random.Random

/**
 * Gera uma cor aleatória em tom pastel.
 * Cores pastéis são criadas misturando uma cor pura com branco,
 * o que resulta em cores mais suaves e com baixa saturação.
 * Esta função garante que os componentes de cor não sejam muito escuros
 * e tenham uma variação agradável.
 */
fun generateRandomPastelColor(): Color {
    val red = (Random.nextInt(128) + 128) // Intervalo de 128 a 255
    val green = (Random.nextInt(128) + 128) // Intervalo de 128 a 255
    val blue = (Random.nextInt(128) + 128) // Intervalo de 128 a 255
    return Color(red, green, blue)
}
