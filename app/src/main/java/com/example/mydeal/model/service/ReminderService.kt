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
    private val CHECK_INTERVAL = TimeUnit.HOURS.toMillis(1)

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
        val today = Calendar.getInstance()
        val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 1) }
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val upcomingTransactions = listOf(
            Transaction().apply {
                id = "upcoming1"
                description = "Pago de Netflix"
                amount = 199.00
                date = dateFormat.format(tomorrow.time)
                isExpense = true
                category = "Suscripciones"
            },
            Transaction().apply {
                id = "upcoming2"
                description = "Pago de servicios"
                amount = 1200.00
                date = dateFormat.format(tomorrow.time)
                isExpense = true
                category = "Servicios"
            }
        )

        for (transaction in upcomingTransactions) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Próximo pago: ${transaction.description}")
                .setContentText("Tienes un pago de ${transaction.amount} MXN programado para mañana")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

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