package com.example.bankapp

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.bankapp.databinding.ActivityMainBinding

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
        startTimer()
    }

    private fun getBlikCodeFromApi(): String {
        val url = "$HOST_NAME/api/blik?sessionToken=$token";
        var blik = ""

        val request = StringRequest(
            Request.Method.GET,
            url,
            { response ->
                Log.e("myMessage", response)
                setBlikCodeToElement(response.toString())
            },
            {
                    response -> Log.e("myMessage", response.toString())
                blik = response.toString()
                Log.e("myMessage", blik)
            }
        )

        val queue = Volley.newRequestQueue(this)
        queue.add(request)
        return blik
    }

    private fun setBlikCodeToElement(blikCode: String) {
        Log.e("showBlikCode", "Show")
        binding.BlikCode.visibility = View.VISIBLE
        binding.BlikCode.text = blikCode
    }

    private fun startTimer(){
        binding.Time.visibility = View.VISIBLE
        binding.Time.text = "1:59"

        val timerTextView = binding.Time

        val timer = object : CountDownTimer(120000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 60000
                val seconds = (millisUntilFinished % 60000) / 1000

                timerTextView.text = "$minutes:$seconds"
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
