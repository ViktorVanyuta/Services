package com.sakhalinec.services

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

class MyForegroundService: Service() {

    /*
        Главный недостаток ForegroundService в том, что если его вызвать несколько раз то он
        будет при каждом вызове запускать метод onStartCommand() и таким образом эти несколько
        сервисов будут работать паралельно друг другу выполняя одну и ту же работу.
        Ну и по умолчанию он выполняется на Main потоке.
    */

    // создание скоупа корутин для выполнения сервиса
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    // сервис создается
    override fun onCreate() {
        super.onCreate()
        log("onCreate")
        createNotificationChannel()

        //в функцию startForeground() первым параметром нужно передать id уведомления и этот id не
        // должен быть равен 0 можно использовать любое другое значение. Вторым параметром нужно
        // передать само уведомление, в нашем случае функцию создания уведомления
        startForeground(NOTIFICATION_ID, createNotification())
    }

    // сервис делает какую то работу, этот метод возвращает тип Int оно означает какое поведение
    // будет у сервиса на android-e, мы можем вернуть одно из 3-х значений -
    // - ( START_STICKY, START_NON_STICKY, START_REDELIVER_INTENT )
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log("onStartCommand")
        coroutineScope.launch {
            // эмуляция таймера
            for (i in 0 until 5) {
                delay(1000)
                log("Timer $i")
            }
            // stopSelf() данная фукнкция позволяет останавливать сервис, зачем это нужно - если работа
            // сервиса не долгая и он быстро справляется со свой задачей то, иногда сервис сам
            // не убивается вызовом метода onDestroy() и нам нужно самим его останавливать.
            stopSelf()
        }
        return START_STICKY
        //return super.onStartCommand(intent, flags, startId)

        // return START_STICKY - означает, что если система убьет наш сервис то он будет пересоздан

        // return START_NON_STICKY - означает, что если система убьет наш сервис то его перезапускать не нужно

        // return START_REDELIVER_INTENT - работает по тому же принципу что и START_STICKY но с
        // одним отличием, если система убьет сервис то он будет перезапущен с переданным в параметрах
        // интентом, то есть тот интент который был передан изначально точно также прилетит в метод onStartCommand()

    }

    // сервис иничтожается
    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
        log("onDestroy")
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    // просто для удобства
    private fun log(message: String) {
        Log.d("SERVICE_TAG", "MyForegroundService: $message")
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

        // создание фабричного метода интента
        fun newIntent(context: Context): Intent {
            return Intent(context, MyForegroundService::class.java)
        }

    }
}




