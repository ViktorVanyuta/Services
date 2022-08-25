package com.sakhalinec.services


import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.app.job.JobWorkItem
import android.content.ComponentName
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.sakhalinec.services.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var page = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.simpleService.setOnClickListener {
            stopService(MyForegroundService.newIntent(this))
            startService(MyService.newIntent(this, 25))
        }

        binding.foregroundService.setOnClickListener {
            ContextCompat.startForegroundService(
                this,
                MyForegroundService.newIntent(this))
        }

        binding.intentService.setOnClickListener {
            ContextCompat.startForegroundService(
                this,
                MyIntentService.newIntent(this))
        }

        binding.jobScheduler.setOnClickListener {
            val componentName = ComponentName(this, MyJobService::class.java)
            val jobInfo = JobInfo.Builder(MyJobService.JOB_ID, componentName)
                .setRequiresCharging(true)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .build()

            val jobScheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val intent = MyJobService.newIntent(page++)
                jobScheduler.enqueue(jobInfo, JobWorkItem(intent))
            } else {
                startService(MyCombinationJobServiceAndIntentService.newIntent(this, page++))
            }
        }

        binding.jobIntentService.setOnClickListener {
            MyJobIntentService.enqueue(this, page++)
        }

        // запуск Worker-a на выполнение
        binding.workManager.setOnClickListener {

            // первым делом нужно получить экземпляр workManager-a и необходимо передать контекст приложения,
            // а не контекст activity чтобы не получилось так, что activity умерла а сервис продолжает работать
            val workManager = WorkManager.getInstance(applicationContext)

            // когда получили экземпляр класса workManager можно вызывать метод enqueue() или enqueueUniqueWork()
            // метод enqueueUniqueWork() - этот метод примает имя Worker-a и мы можем сами указать
            // что делать если была попытка запустить Worker который уже запущен
            // метод enqueue() - если были запущены несколько Worker-ов то, все Worker-ы начнут выполнение
            workManager.enqueueUniqueWork(

                // первым параметром нужно передать имя Worker-a
                MyWorker.WORK_NAME,

                // вторым параметром указывается что делать если пытаемся запустить работу которая уже запущена
                // ExistingWorkPolicy.APPEND_OR_REPLACE - новый Worker будет положен в очередь, в случае
                // ошибки будет создана новая цепочка очереди сервисов
                // ExistingWorkPolicy.APPEND - новый Worker будет положен в очередь, если была ошибка
                // то эта ошибка будет распространяться на все последующие сервисы в очереди
                // ExistingWorkPolicy.KEEP - существующий Worker продолжит выполнение а новый Worker проигнорируется
                // ExistingWorkPolicy.REPLACE - существующий Worker будет заменен новым
                ExistingWorkPolicy.APPEND,

                // третьим параметром указывается oneTimeWorkRequest - создаем его в классе Worker-a и
                // там указываем какого поведения хотим добиться при выполнении сервиса,
                // передаются все необходимые параметры и ограничения
                MyWorker.makeRequest(page++)
            )
        }


    }

}
