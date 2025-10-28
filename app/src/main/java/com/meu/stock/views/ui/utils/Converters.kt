package com.meu.stock.views.ui.utils


import androidx.room.TypeConverter
import java.util.Date

/**
 * Conversores de tipo para o Room Database.
 * Ensina o Room a salvar e ler tipos de dados que ele não conhece nativamente.
 */
class Converters {

    /**
     * Converte um Long (timestamp em milissegundos) para um objeto Date.
     * O Room usará esta função ao LER do banco de dados.
     * @param value O timestamp armazenado no banco.
     * @return O objeto Date correspondente, ou null se o valor for nulo.
     */
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    /**
     * Converte um objeto Date para um Long (timestamp em milissegundos).
     * O Room usará esta função ao ESCREVER no banco de dados.
     * @param date O objeto Date a ser convertido.
     * @return O timestamp em milissegundos, ou null se a data for nula.
     */
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
