package com.example.bankapp

import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.bankapp.databinding.ActivityMainBinding
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.collections.ArrayDeque

@RequiresApi(26)
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var token: String
    private lateinit var queue: RequestQueue
    private lateinit var paymentQueue: ArrayDeque<PaymentModel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        token = intent.getStringExtra("Token").toString()
        queue = Volley.newRequestQueue(this)
        paymentQueue = ArrayDeque()
        binding.Blik.setOnClickListener{
            showBlikCode()
        }
        binding.yesBtn.setOnClickListener{confirmPayment()}
        binding.noBtn.setOnClickListener{rejectPayment()}
    }

    private fun showBlikCode() {
        binding.Blik.isEnabled = false
        binding.Blik.isClickable = false
        getBlikCodeFromApi()
    }

    private fun nextPopup(){
        paymentQueue.removeFirst()
        val payment: PaymentModel? = paymentQueue.firstOrNull()
        if(payment == null)
            binding.popup.visibility = View.GONE
        else
            binding.popupInfo.text = payment.toString()
    }

    private class ConfirmRequest(op: String, val uuid: String, val token: String, listener: Response.Listener<String>): StringRequest(
        Method.POST,
        "$HOST_NAME/api/transactions/$op",
        listener,
        { err ->
            error(err)
        }
    ) {
        override fun getBody(): ByteArray{
            return "{\"paymentRequestId\": \"$uuid\", \"sessionToken\":\"$token\"}".toByteArray()
        }

        override fun getBodyContentType(): String {
            return "application/json"

        }
    }

    private fun confirmPayment(){
        val payment: PaymentModel? = paymentQueue.firstOrNull()
        val req = ConfirmRequest("confirm", payment!!.uuid, token){
            nextPopup()
        }
        queue.add(req)
    }
    private fun rejectPayment(){
        val payment: PaymentModel? = paymentQueue.firstOrNull()
        val req = ConfirmRequest("reject", payment!!.uuid, token){
            nextPopup()
        }
        queue.add(req)
    }

    private fun showConfirmPopup() {
        if(binding.popup.visibility == View.GONE) {
            val payment: PaymentModel? = paymentQueue.firstOrNull()
            if (payment == null) {
                binding.popup.visibility = View.GONE
            } else {
                binding.popup.visibility = View.VISIBLE
                binding.popupInfo.text = payment.toString()
            }
        }
    }

    private fun getPaymentDetails(id: String){
        val req = StringRequest(
            "$HOST_NAME/api/transactions?id=$id",
            { res ->
                val json = JSONObject(res)
                paymentQueue.add(
                    PaymentModel(
                        json.getString("id"),
                        json.getDouble("amount").toFloat(),
                        json.getString("destination")
                    )
                )
                showConfirmPopup()
            },
            { err ->
                error(err)
            }
        )
        queue.add(req)
    }

    private fun subscribeToBlikCode(blikCode: String){
        Thread {
            val url = URL("$HOST_NAME/api/transactions/subscribe?blikCode=$blikCode")
            with(url.openConnection() as HttpURLConnection) {
                this.requestMethod = "GET"
                var buffer = ""
                this.inputStream.bufferedReader().forEachLine { line ->
                    if (line == "") {
                        val hashMap = mutableMapOf<String, String>()
                        buffer
                            .split("\n")
                            .map{it.split(":")}
                            .forEach { arr ->
                                hashMap[arr[0]] = arr[1]
                            }
                        if(hashMap["event"]!! == "payment/request")
                            getPaymentDetails(hashMap["data"]!!)
                        buffer = ""
                    } else {
                        buffer +=
                            if(buffer == "")
                                line
                            else
                                "\n" + line
                    }
                }
            }
        }.start()
    }

    private fun getBlikCodeFromApi() {
        val url = "$HOST_NAME/api/blik?sessionToken=$token"

        val request = StringRequest(
            Request.Method.GET,
            url,
            { response ->
                Log.e("myMessage", response)
                val str = response.toString().split(" ")
                setBlikCodeToElement(str[1])
                subscribeToBlikCode(str[1])
                startTimer(str[0].toLong())
            },
            {
                response -> Log.e("myMessage", response.toString())
                Log.e("myMessage", response.toString())
            }
        )


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
