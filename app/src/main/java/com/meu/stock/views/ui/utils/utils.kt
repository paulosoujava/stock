package com.meu.stock.views.ui.utils

import java.text.SimpleDateFormat
import java.util.Date


/* Função de extensão auxiliar para formatar um objeto Date para uma string legível.
* Exemplo: "25 de out de 2025, 19:30"
*/
fun Date.toFormattedString(): String {
    val format = SimpleDateFormat("dd 'de' MMM 'de' yyyy, HH:mm",
        java.util.Locale("pt", "BR")
    )
    return format.format(this)
}

