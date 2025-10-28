package com.meu.stock.repositories

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.meu.stock.contracts.IAlarmScheduler
import com.meu.stock.model.Note
import com.meu.stock.service.AlarmReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class AlarmSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : IAlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ScheduleExactAlarm")
    override fun schedule(note: Note) {
        // Precisamos do ID da nota para criar um alarme único
        if (note.id == null || note.reminderDate.isNullOrBlank() || note.reminderTime.isNullOrBlank()) {
            return
        }

        // 1. Converte a data e hora do lembrete para milissegundos
        val dateTimeString = "${note.reminderDate} ${note.reminderTime}"
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.getDefault())
        val localDateTime = LocalDateTime.parse(dateTimeString, formatter)
        val triggerAtMillis = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()


        // 2. Cria a Intent que será disparada quando o alarme tocar
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("EXTRA_NOTE_ID", note.id)
            putExtra("EXTRA_NOTE_TITLE", note.title)
            putExtra("EXTRA_NOTE_CONTENT", note.content)
        }

        // 3. Agenda o alarme
        // O note.id.toInt() garante que cada alarme tenha um PendingIntent único
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            PendingIntent.getBroadcast(
                context,
                note.id.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    override fun cancel(note: Note) {
        // Cancela um alarme que já foi agendado para esta nota
        if (note.id == null) return

        val intent = Intent(context, AlarmReceiver::class.java)
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                note.id.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}
