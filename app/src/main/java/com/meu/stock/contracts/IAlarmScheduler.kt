package com.meu.stock.contracts


import com.meu.stock.model.Note

/**
 * Define o contrato para um agendador de alarmes.
 * Esta interface desacopla a lógica de negócio (nos ViewModels)
 * da implementação específica do AlarmManager do Android.
 */
interface IAlarmScheduler {
    /**
     * Agenda um lembrete para uma nota específica.
     * @param note O objeto da nota contendo os detalhes do lembrete.
     */
    fun schedule(note: Note)

    /**
     * Cancela um lembrete previamente agendado para uma nota.
     * @param note O objeto da nota cujo alarme deve ser cancelado.
     */
    fun cancel(note: Note)
}