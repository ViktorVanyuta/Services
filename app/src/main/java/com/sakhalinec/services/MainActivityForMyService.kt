package com.sakhalinec.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.sakhalinec.services.databinding.ActivityMainBinding

class MainActivityForMyService : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // при нажатии на кнопку будет запущен сервис
        binding.simpleService.setOnClickListener {
            // старт сервиса MyService
            startService(MyService.newIntent(this, 25))
        }

    }

    // реализация уведомлений с проверкой версий android api
    private fun showNotification() {
        // создание notificationManager-a для возможности показывать уведомления
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // создание канала уведомлений с проверкой версий api системы android, данная проверка нужна для того
        // чтобы в зависимости от версий андроид системы вызывался код для создания канала уведомлений
        // то есть если текущая версия больше или равна версии android 8 Oreo api 26 то в этом случае
        // нам нужно создать notificationChannel, тут создали сам объект теперь его нужно создать с
        // помощью notificationManager-a
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                // NotificationManager константы этого класса отвечают за приоритет, уведомления со звуком
                // или они могут быть показанны поверх других окон и т.д.
                NotificationManager.IMPORTANCE_DEFAULT
            )
            // создаем канал уведомлений
            notificationManager.createNotificationChannel(notificationChannel)
        }
        // создание уведомления
        // NotificationCompat внутри этого класса уже реализованна проверка версий android api
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Title")
            .setContentText("Text")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .build()

        // теперь нужно показать уведомление пользователю
        // id в параметрах нужен для количества отображаемых уведомлений
        notificationManager.notify(1, notification)
    }

    companion object {
        private const val CHANNEL_ID = "channel_id"

        // имя канала нужно для того чтобы пользователь мог блокировать уведомления,
        // поэтому желательно давать нормальные имена для этих каналов
        private const val CHANNEL_NAME = "channel_name"
    }

}

    // такая реализация работает до 8-й версии андроида до api 26 версии
//    private fun showNotification() {
//        // создание уведомления
//        val notification = Notification.Builder(this)
//            .setContentTitle("Title")
//            .setContentText("Text")
//            .setSmallIcon(R.drawable.ic_launcher_background)
//            .build()
//
//        // теперь нужно показать уведомление пользователю
//        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//        // id в параметрах нужен для количества отображаемых уведомлений
//        notificationManager.notify(1, notification)
//    }

