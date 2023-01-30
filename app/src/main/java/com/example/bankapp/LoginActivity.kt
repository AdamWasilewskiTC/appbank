package com.example.bankapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.bankapp.databinding.ActivityLoginBinding
import org.json.JSONArray
import org.json.JSONObject

@RequiresApi(26)
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.login.setOnClickListener {
            sendUserDataApi()
        }
    }
        private fun sendUserDataApi() {
            val userName = binding.pesel.text;
            val userPass = binding.password.text;
            val requestBodyObject = JSONObject()
            requestBodyObject.put("pesel", userName)
            requestBodyObject.put("passwd", userPass)
            val requestBody = requestBodyObject.toString()

            val url = "$HOST_NAME/api/sessions"

            val request = object : StringRequest(
                Method.POST,
                url,
                { response: String ->
                    if(response == "")
                        throw RuntimeException("BladLogowania.exe")

                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("Token",response);

                    startActivity(intent)
                },
                { error: VolleyError ->
                    error.printStackTrace();
                }
            ){
                override fun getBody(): ByteArray{
                    return requestBody.toByteArray()
                }

                override fun getBodyContentType(): String {
                    return "application/json"

                }
            }
            val queue = Volley.newRequestQueue(this)
            queue.add(request)
        }



}