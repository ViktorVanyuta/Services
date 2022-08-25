package com.sakhalinec.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.Message
import android.util.Log
import kotlinx.coroutines.*

class MyService: Service() {

    /*
      Сервис - это один из основных компонентов Android системы, всего компонентов 4
      ( Activity, Service, Broadcast Receivers, Content Providers ) все они должны быть зарегестрированны
      в Android Manifest-e
    */
    /*
       Сервисы предназначены для выполнения задач в фоне
       Чтобы создать сервис нужно унаследоваться от класса Service
       Жизненый цикл сервисов:
       onCreate(сервис создан), onStartCommand(сервис выполняет работу), onDestroy(сервис уничтожен)
       По умолчанию код внутри сервиса выполняется на главном потоке и нам нужно самим заботиться о
       том, чтобы не блокировать главный поток
       Чтобы запустить сервис, нужно вызвать метод startService в активити и передать intent
       в качестве параметра
       При выполнении сервиса начиная с 8-й версии android-a мы обязаны получить разрешение на выполнение
       сервиса у пользователя, иначе сервис не будет пересоздан после его первого завершения работы
    */

    // создание скоупа корутин для выполнения сервиса
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    // сервис создается
    override fun onCreate() {
        super.onCreate()
        log("onCreate")
    }

    // сервис делает какую то работу, этот метод возвращает тип Int оно означает какое поведение
    // будет у сервиса на android-e, мы можем вернуть одно из 3-х значений -
    // - ( START_STICKY, START_NON_STICKY, START_REDELIVER_INTENT )
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log("onStartCommand")
        val start = intent?.getIntExtra(EXTRA_START, 0) ?: 0
        coroutineScope.launch {
            // эмуляция таймера
            for (i in start until start + 100) {
                delay(1000)
                log("Timer $i")
            }
        }
        return START_REDELIVER_INTENT
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
        Log.d("SERVICE_TAG", "MyService: $message")
    }

    companion object {

        private const val EXTRA_START = "start"

        // создание фабричного метода интента
        fun newIntent(context: Context, start: Int): Intent {
            return Intent(context, MyService::class.java).apply {
                putExtra(EXTRA_START, start)
            }

        }
    }

}