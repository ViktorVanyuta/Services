package com.sakhalinec.services


import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.app.job.JobWorkItem
import android.content.ComponentName
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.sakhalinec.services.databinding.ActivityMainBinding

class MainActivityForCombinationJobServiceAndIntentService : AppCompatActivity() {

    /*
        Какие проблемы в этой реализации:
        2 сервиса которые делают почти одну и ту же работу, то есть дублирование кода
        нужно добавлять проверки на версию api системы android

        ЛУЧШЕ ИСПОЛЬЗОВАТЬ - JobIntentService он по сути является обьединением этих двух сервисов
    */

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var page = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        // запуск сервиса jobScheduler с использованием функции enqueue()
        binding.jobScheduler.setOnClickListener {
            // указываем какой сервис нужен, передавая контекст и класс сервиса
            val componentName = ComponentName(this, MyJobService::class.java)

            // в JobInfo устанавливаются все ограничения, создается при помощи Builder() в который
            // нужно передать id jobService-a и componentName
            val jobInfo = JobInfo.Builder(MyJobService.JOB_ID, componentName)
                // означает что сервис будет работать только когда устройство заряжается
                .setRequiresCharging(true)
                // означает что сервис будет работать только когда устройство подключенно к wifi
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .build()

            // планирование сервиса для запуска сервиса
            val jobScheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler

            // метод enqueue() доступен с версии api 26 для этого нужна данная проверка
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // создаем intent и передаем нужный параметр. в данном случае page++ типа счетчик
                // страниц для которых будет запускаться сервис
                val intent = MyJobService.newIntent(page++)
                // метод enqueue() кладет все созданные им сервисы в очередь, чтобы его запустить
                // ему нужно передать jobInfo и JobWorkItem(intent)
                jobScheduler.enqueue(jobInfo, JobWorkItem(intent))
            }
            // в блоке else запускаем IntentService если по ходу проверки выяснили что api ниже 26
            else {
                startService(MyCombinationJobServiceAndIntentService.newIntent(this, page++))
            }
        }

    }

}
