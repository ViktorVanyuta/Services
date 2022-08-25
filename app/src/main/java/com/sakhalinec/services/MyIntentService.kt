package com.sakhalinec.services

import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*

class MyIntentService: IntentService(NAME_SERVICE) {

    /*
        Работает на фоновом потоке, метод onHandleIntent() запускается в фоне
        Не нужно ручка делать остановку сервиса, все делается за нас

        При нескольких вызовах сервиса все они будут выполняться по очереди, при этом метод onCreate()
        будет вызван только один раз при старте первого сервиса, далее каждый новый старт сервиса
        будет вызван методом onHandleIntent() и после выполнения всех вызванных сервисов будет
        вызван метод onDestroy() и сервисы будут убиты.

        IntentService - запускать его можно как обычный сервис с помощью startService() так и
        через ContextCompat.startForegroundService()

        В IntentService мы ничего не возвращаем, в отличии от onStartCommand(): Int
        который возвращает значение например START_STICKY, START_REDELIVER_INTENT, START_NOT_STICKY

        Если нужно поведение как у START_STICKY или START_NOT_STICKY то можно ипользовать метод:
        setIntentRedelivery(true) - аналог START_REDELIVER_INTENT (false) - аналог START_NOT_STICKY
    */

    // сервис создается
    override fun onCreate() {
        super.onCreate()
        log("onCreate")
        setIntentRedelivery(true)
        createNotificationChannel()

        //в функцию startForeground() первым параметром нужно передать id уведомления и этот id не
        // должен быть равен 0 можно использовать любое другое значение. Вторым параметром нужно
        // передать само уведомление, в нашем случае функцию создания уведомления
        startForeground(NOTIFICATION_ID, createNotification())
    }

    // код внутри этого метода будет выполняться на фоновом потоке и по завершению работы сервис
    // будет автоматически остановлен
    override fun onHandleIntent(p0: Intent?) {
        log("onHandleIntent")
        for (i in 0 until 5) {
            Thread.sleep(1000)
            log("Timer $i")
        }
    }

    // сервис иничтожается
    override fun onDestroy() {
        super.onDestroy()
        log("onDestroy")
    }


    // просто для удобства
    private fun log(message: String) {
        Log.d("SERVICE_TAG", "MyIntentService: $message")
    }

    // создание канала уведомлений
    private fun createNotificationChannel() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    // создание уведомления
    private fun createNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("Title")
        .setContentText("Text")
        .setSmallIcon(R.drawable.ic_launcher_background)
        .build()



    companion object {

        private const val CHANNEL_ID = "channel_foreground_id"
        private const val CHANNEL_NAME = "channel_foreground_name"
        private const val NOTIFICATION_ID = 1
        private const val NAME_SERVICE = "MyIntentService"

        // создание фабричного метода интента
        fun newIntent(context: Context): Intent {
            return Intent(context, MyIntentService::class.java)
        }

    }
}




