package com.sakhalinec.services

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService


class MyJobIntentService: JobIntentService() {

    /*
        Под капотом использует два вида сервисов: JobService если запущен на устройстве с api выше 26
        и IntentService если запущен на устройстве с api ниже 26

        Реализация такая же как и у intentService-a с одним отличием, для того чтобы запустить сервис
        JobIntentService необходимо вызвать статический метод enqueueWork() в который нужно
        передать несколько параметров: контекст, имя класса сервиса, id джобы, интент.

        НЕОБХОДИМЫЕ РАЗРЕШЕНИЯ В МАНИФЕСТЕ:
        <uses-permission android:name="android.permission.WAKE_LOCK" />
        <service android:name=".MyJobIntentService"
            android:permission="android.permission.BIND_JOB_SERVICE"/>

        В этом сервисе мы не можем накладывать какие то ограничения, как это можно делать
        в JobScheduler-e то есть например выполнять сервис когда устройство заряжается...
    */

    // сервис создается
    override fun onCreate() {
        super.onCreate()
        log("onCreate")

    }

    // код внутри этого метода будет выполняться на фоновом потоке и по завершению работы сервис
    // будет автоматически остановлен
    override fun onHandleWork(intent: Intent) {
        log("onHandleWork")
        val page = intent.getIntExtra(PAGE, 0)
        for (i in 0 until 5) {
            Thread.sleep(1000)
            log("Timer $i $page")
        }
    }

    // сервис уничтожается
    override fun onDestroy() {
        super.onDestroy()
        log("onDestroy")
    }

    // просто для удобства
    private fun log(message: String) {
        Log.d("SERVICE_TAG", "MyJobIntentService: $message")
    }

    companion object {

        private const val PAGE = "page"
        private const val JOB_ID = 123

        // тот самый обязательный метод для запуска сервиса
        fun enqueue(context: Context, page: Int){
            JobIntentService.enqueueWork(
                context,
                MyJobIntentService::class.java,
                JOB_ID,
                newIntent(context, page)
            )
        }

        // создание фабричного метода интента
        private fun newIntent(context: Context, page: Int): Intent {
            return Intent(context, MyIntentService::class.java).apply {
                putExtra(PAGE, page)
            }
        }

    }
}