package com.meu.stock.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class AlarmReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        val noteId = intent.getLongExtra("EXTRA_NOTE_ID", -1L)
        val title = intent.getStringExtra("EXTRA_NOTE_TITLE") ?: "Lembrete"
        val content = intent.getStringExtra("EXTRA_NOTE_CONTENT") ?: "Você tem uma nova nota."

        if (noteId == -1L) return

        // **Verificação de Permissão** (Boa prática)
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // O ideal seria registrar essa falha ou lidar com ela de alguma forma
            return
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "note_reminder_channel"

        // Cria o canal de notificação (necessário para Android 8.0+)
        val channel = NotificationChannel(
            channelId,
            "Lembretes de Notas",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Canal para lembretes de notas importantes."
        }
        notificationManager.createNotificationChannel(channel)

        // Constrói a notificação com um ícone padrão
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // <- ÍCONE CORRIGIDO
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        // Mostra a notificação
        notificationManager.notify(noteId.toInt(), notification)
    }
}
