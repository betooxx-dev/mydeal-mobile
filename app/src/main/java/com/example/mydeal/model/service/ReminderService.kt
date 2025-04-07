package com.example.mydeal.model.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.mydeal.MainActivity
import com.example.mydeal.R
import com.example.mydeal.model.data.Transaction
import com.example.mydeal.model.repository.TransactionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class ReminderService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val transactionRepository = TransactionRepository()

    private val NOTIFICATION_ID = 1001
    private val CHANNEL_ID = "reminder_channel"
    private val CHECK_INTERVAL = TimeUnit.SECONDS.toMillis(10) // 10 segundos en lugar de 1 hora

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification("Recordatorios de MyDeal activos", "Monitoreando próximos pagos")
        startForeground(NOTIFICATION_ID, notification)

        serviceScope.launch {
            while (isActive) {
                checkUpcomingTransactions()
                delay(CHECK_INTERVAL)
            }
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Recordatorios de pagos"
            val descriptionText = "Canal para notificaciones de pagos próximos"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(title: String, content: String): android.app.Notification {
        val pendingIntent = createPendingIntent()

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun createPendingIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        return PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun checkUpcomingTransactions() {
        // Crear transacciones de prueba con la fecha actual en lugar de mañana
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = Calendar.getInstance()

        val testTransactions = listOf(
            Transaction().apply {
                id = "test1"
                description = "Notificación de prueba 1"
                amount = 199.00
                date = dateFormat.format(currentDate.time)
                isExpense = true
                category = "Prueba"
            },
            Transaction().apply {
                id = "test2"
                description = "Notificación de prueba 2"
                amount = 500.00
                date = dateFormat.format(currentDate.time)
                isExpense = true
                category = "Prueba"
            }
        )

        for (transaction in testTransactions) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Prueba: ${transaction.description}")
                .setContentText("Esta es una notificación de prueba - $${transaction.amount} MXN")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true) // La notificación desaparece al tocarla

            notificationManager.notify(transaction.id.hashCode(), notificationBuilder.build())
        }
    }

    companion object {
        fun startService(context: Context) {
            val intent = Intent(context, ReminderService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }

        fun stopService(context: Context) {
            val intent = Intent(context, ReminderService::class.java)
            context.stopService(intent)
        }
    }
}