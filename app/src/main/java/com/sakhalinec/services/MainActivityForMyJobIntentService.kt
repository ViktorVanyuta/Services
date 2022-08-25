package com.sakhalinec.services

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sakhalinec.services.databinding.ActivityMainBinding

class MainActivityForMyJobIntentService : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var page = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // вот так просто запускается JobIntentService
        binding.jobIntentService.setOnClickListener {
            MyJobIntentService.enqueue(this, page++)
        }
    }


}
