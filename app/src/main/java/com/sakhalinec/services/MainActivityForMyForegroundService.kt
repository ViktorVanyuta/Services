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

class MainActivityForMyForegroundService : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // при нажатии на кнопку будет запущен сервис
        binding.simpleService.setOnClickListener {
            // для остановки сервиса из любого другого места программы нужно использовать
            // stopService() в который нужно передать интент сервиса который хотим остановить
            stopService(MyForegroundService.newIntent(this))
            // старт сервиса MyService
            startService(MyService.newIntent(this, 25))
        }

        // для работы на устройствах с версией api 29 и выше нужно в манифест добавить разрешение
        // <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
        binding.foregroundService.setOnClickListener {
            // старт сервиса MyForegroundService, вызвав метод startForegroundService() из
            // класса ContextCompat позволяет не делать ручками проверку версий android api
            // все проверки будут сделаны за нас, то есть если версия api >= 26 то будет вызван
            // метод startForegroundService(), а если api ниже 26 то будет вызван startService()
            // ВАЖНО при вызове этого метода мы обязуемся в течении 5 секунд запустить метод
            // startForeground() который долны вызвать в классе самого сервиса
            ContextCompat.startForegroundService(
                this,
                MyForegroundService.newIntent(this))
        }

    }


}
