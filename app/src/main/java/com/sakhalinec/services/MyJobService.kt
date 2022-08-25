package com.sakhalinec.services

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.os.Build
import android.util.Log
import kotlinx.coroutines.*

class MyJobService : JobService() {

    /*
        Такой вариант реализации работает с версии api 26
        если версия api ниже то нужна реализация комбинирование JobService и IntentService
    */

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        log("onCreate")
    }

    // метод выполняется на главном потоке, тип Boolean означает выполнение работы функции,
    // если вернуть true когда код асинхронный это значит, что мы уже могли выйти из этой функции
    // но работа сервиса еще продолжается и мы сами завершим всю работу когда это будет нужно.
    // Kод внутри метода может быть синхронным то есть обычный метод который выполняется построчно
    // в этом случае нужно вернуть false тем самым сообщив, что сервис больше не выполняется, вся
    // работа будет автоматически завершена и нам не нужно самим ничего завершать.
    override fun onStartJob(params: JobParameters?): Boolean {
        log("onStartCommand")
        // метод dequeueWork() так же доступен с версии api 26 для этого нужна проверка версий api
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            coroutineScope.launch {
                // с помощью метода dequeueWork() мы достаем из очереди все имеющиеся сервисы и
                // положим их в переменную workItem для дальнейшего использования
                var workItem = params?.dequeueWork()
                // в цикле будем получать значения страниц-сервисов до тех пор пока метод dequeueWork()
                // не вернет null, то есть до тех пор пока внутри параметров еще есть сервисы
                while (workItem != null) {
                    // получаем значение для страницы 0 тоесть это будет первый запущенный сервис
                    val page = workItem.intent.getIntExtra(PAGE, 0)
                    // выводим значение секунд и номер страницы-сервиса
                    for (i in 0 until 5) {
                        delay(1000)
                        log("Timer $i $page")
                    }
                    // метод completeWork() позволяет завершить только текущий сервис, этот метод
                    // нужно вызывать обязательно поскольку он обозначает, что запущенная работа
                    // то есть конкретный сервис который лежал в очереди завершил свою работу
                    params?.completeWork(workItem)
                    // после выполнения предыдущего сервиса из очереди, снова достаем новый объект из очереди
                    workItem = params?.dequeueWork()
                }
                // jobFinished() позволяет завершить работу сервиса, первым параметром передается объект
                // класса JobParameters он прилетает автоматически, вторым параметром нужно передать
                // true или false обозначает нужно ли запланировать выполнение сервиса заново например
                // для обновления данных в фоне в этом случае нужно вернуть true и сервис будет
                // перезапущен через какое то время, если указать false то сервис перезапущен не будет
                jobFinished(params, false)
            }
        }
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        log("onStopJob")
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
        log("onDestroy")
    }

    private fun log(message: String) {
        Log.d("SERVICE_TAG", "MyJobService: $message")
    }

    companion object {

        const val JOB_ID = 11
        //
        private const val PAGE = "page"

        // фабричный метод для создания intent-a в который кладем putExtra()
        fun newIntent(page: Int): Intent {
            return Intent().apply {
                putExtra(PAGE, page)
            }
        }
    }
}


/*
//============================= БЕЗ ИСПОЛЬЗОВАНИЯ ФУНКЦИИ ENQUEUE() ==============================//
class MyJobService: JobService() {
    // создание скоупа корутин для выполнения сервиса
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    // сервис создается
    override fun onCreate() {
        super.onCreate()
        log("onCreate")
    }

    // метод выполняется на главном потоке, тип Boolean означает выполнение работы функции,
    // если вернуть true когда код асинхронный это значит, что мы уже могли выйти из этой функции
    // но работа сервиса еще продолжается и мы сами завершим всю работу когда это будет нужно
    // код внутри метода может быть синхронным то есть обычный метод который выполняется построчно
    // в этом случае нужно вернуть false тем самым сообщив что сервис больше не выполняется, вся
    // работа будет автоматически завершена и нам не нужно самим ничего завершать.
    override fun onStartJob(p0: JobParameters?): Boolean {
        log("onStartCommand")
        coroutineScope.launch {
            // эмуляция таймера
            for (i in 0 until 100) {
                delay(1000)
                log("Timer $i")
            }
            // jobFinished() позволяет завершить работу сервиса, первым параметром передается объект
            // класса JobParameters он прилетает автоматически, вторым параметром нужно передать
            // true или false обозначает нужно ли запланировать выполнение сервиса заново например
            // для обновления данных в фоне в этом случае нужно вернуть true и сервис будет
            // перезапущен через какое то время, если указать false то сервис перезапущен не будет
            jobFinished(p0, true)
        }
        return true
    }

    // этот метод вызывается если сервис был убит системой, например у сервиса было установленно
    // ограничение, что сервис должен выполняться когда устройство заряжается или подключенно к wifi
    // если ограничения были установленны и в момент выполнения сервиса устройство было отключенно
    // например от зарядки то сервис будет остановлен и вызовится метод onStopJob()
    // ВАЖНО! если сервис был остановлен нами, функцией jobFinished() то, метод onStopJob() вызван не будет
    // если система убила сервис и мы хотим заново запланировать выполнение то, нужно в методе
    // вернуть true в противном случае false
    override fun onStopJob(p0: JobParameters?): Boolean {
        log("onStopJob")
        return true
    }

    // сервис иничтожается
    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
        log("onDestroy")
    }

    // просто для удобства
    private fun log(message: String) {
        Log.d("SERVICE_TAG", "MyJobService: $message")
    }

    companion object {
        // id сервиса для объекта jobInfo
        const val JOB_ID = 11
    }
}
*/