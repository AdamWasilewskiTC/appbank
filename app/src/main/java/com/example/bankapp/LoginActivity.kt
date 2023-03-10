package com.example.bankapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.bankapp.databinding.ActivityLoginBinding
import org.json.JSONObject

@RequiresApi(26)
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var queue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        queue = Volley.newRequestQueue(this)

        binding.login.setOnClickListener {
            sendUserDataApi()
        }
    }
    private fun sendUserDataApi() {
        val userName = binding.pesel.text
        val userPass = binding.password.text
        val requestBodyObject = JSONObject()
        requestBodyObject.put("pesel", userName)
        requestBodyObject.put("passwd", userPass)
        val requestBody = requestBodyObject.toString()
        val url = "$HOST_NAME/api/sessions"

        val request = object : StringRequest(
            Method.POST,
            url,
            { response: String ->
                binding.login.isClickable = true
                if(response == "") {
                    val toast = Toast.makeText(this, "Login or password incorrect!", Toast.LENGTH_SHORT)
                    toast.show()
                } else {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("Token", response)

                    startActivity(intent)
                }
            },
            { error: VolleyError ->
                error.printStackTrace()
                binding.login.isClickable = true
            }
        ){
            override fun getBody(): ByteArray{
                return requestBody.toByteArray()
            }

            override fun getBodyContentType(): String {
                return "application/json"

            }
        }
        queue.add(request)
        binding.login.isClickable = false
    }
}