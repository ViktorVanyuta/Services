package com.sakhalinec.services

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.util.Log


class MyCombinationJobServiceAndIntentService: IntentService(NAME_SERVICE) {

    /*
        Какие проблемы в этой реализации:
        2 сервиса которые делают почти одну и ту же работу, то есть дублирование кода
        нужно добавлять проверки на версию api системы android

        ЛУЧШЕ ИСПОЛЬЗОВАТЬ - JobIntentService он по сути является обьединением этих двух сервисов
    */

    // сервис создается
    override fun onCreate() {
        super.onCreate()
        log("onCreate")
        setIntentRedelivery(true)
    }

    // код внутри этого метода будет выполняться на фоновом потоке и по завершению работы сервис
    // будет автоматически остановлен
    override fun onHandleIntent(intent: Intent?) {
        log("onHandleIntent")
        val page = intent?.getIntExtra(PAGE, 0) ?: 0
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
        Log.d("SERVICE_TAG", "MyCombinationJobServiceAndIntentService: $message")
    }



    companion object {

        private const val PAGE = "page"
        private const val NAME_SERVICE = "MyCombinationJobServiceAndIntentService"

        // создание фабричного метода интента
        fun newIntent(context: Context, page: Int): Intent {
            return Intent(context, MyIntentService::class.java).apply {
                putExtra(PAGE, page)
            }
        }

    }
}