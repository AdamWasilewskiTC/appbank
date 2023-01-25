package com.example.bankapp

import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.bankapp.databinding.ActivityMainBinding
import java.time.Instant
import java.time.ZoneOffset

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var token: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        token = intent.getStringExtra("Token").toString();
        binding.Blik.setOnClickListener{
            showBlikCode()
        }
    }

    private fun showBlikCode() {
        binding.Blik.isEnabled = false
        binding.Blik.isClickable = false
        getBlikCodeFromApi()
    }

    private fun getBlikCodeFromApi() {
        val url = "$HOST_NAME/api/blik?sessionToken=$token";

        val request = StringRequest(
            Request.Method.GET,
            url,
            { response ->
                Log.e("myMessage", response)
                val str = response.toString().split(" ")
                setBlikCodeToElement(str[1])
                startTimer(str[0].toLong() - Instant.now().toEpochMilli())
            },
            {
                response -> Log.e("myMessage", response.toString())
                Log.e("myMessage", response.toString())
            }
        )

        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    private fun setBlikCodeToElement(blikCode: String) {
        Log.e("showBlikCode", "Show")
        binding.BlikCode.visibility = View.VISIBLE
        binding.BlikCode.text = blikCode
    }

    private fun startTimer(milliseconds: Long){
        binding.Time.visibility = View.VISIBLE
        binding.Time.text = "01:59"

        val timerTextView = binding.Time

        val timer = object : CountDownTimer(milliseconds, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                    val minutes = millisUntilFinished / 60000
                    val seconds = (millisUntilFinished % 60000) / 1000
                    timerTextView.text = "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
            }
            override fun onFinish() {
                timerTextView.text = "Time's up!"
                binding.BlikCode.visibility = View.GONE
                binding.BlikCode.text = ""
                binding.Blik.isEnabled = true
                binding.Blik.isClickable = true
            }
    }
        timer.start()
    }
}
