package com.sakhalinec.services

import android.content.Context
import android.util.Log
import androidx.work.*

class MyWorker(
    context: Context,
    private val workerParameters: WorkerParameters
): Worker(context, workerParameters) {

    /*
        При создании класса Worker-а необходимо унаследоваться от класса Worker и передать в конструктор
        параметры Context и WorkerParameters

        В этом классе необходимо переопределить только один метод doWork() в котором будет выполняться
        вся работа сервиса, этот метод выполняется в другом потоке не в main-e

        Ничего не нужно регестроравать в AndroidManifest-e
    */

    // вся работа в этом методе выполняется в фоновом потоке и нам не нужно об этом беспокоиться
    // метод doWork() должен вернуть объект Result и он примает одно из трех значений:
    // return Result.retry() - метод завершился с исключением, в этом случае он будет перезапущен
    // return Result.failure() - метод завершился с исключением, а в этом случае не будет перезапущен
    // return Result.success() - значит все прошло успешно и сервис завершил свою работу
    override fun doWork(): Result {
        log("doWork")
        // workerParameters.inputData - передаются различные параметры, возвращает класс Data он похож
        // на Bundle тем, что так же хранит объекты парами ключ и значение
        val page = workerParameters.inputData.getInt(PAGE, 0)
        for (i in 0 until 5) {
            Thread.sleep(1000)
            log("Timer $i $page")
        }
        return Result.success()
    }

    // просто для удобства
    private fun log(message: String) {
        Log.d("SERVICE_TAG", "MyWorker: $message")
    }

    companion object {

        private const val PAGE = "page"
        const val WORK_NAME = "work name"

        // фабричный метод который возвращает экземпляр OneTimeWorkRequest для метода enqueueUniqueWork()
        // в MainActivity
        fun makeRequest(page: Int): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<MyWorker>()

                // все параметры в WorkRequest передаются при помощи объекта Data который создается
                // при помощи метода workDataOf() куда передается объект Pair в котором хранятся
                // объекты парами ключ-значение
                .setInputData(workDataOf(PAGE to page))

                // устанавливается ограничения для этого используется метод setConstraints()
                // который принимает объект Constraints
                .setConstraints(makeConstraints())
                .build()
        }

        private fun makeConstraints(): Constraints {
            return Constraints.Builder()
                // ограничение на работу сервиса при подключении к wifi
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .build()
        }

    }

}