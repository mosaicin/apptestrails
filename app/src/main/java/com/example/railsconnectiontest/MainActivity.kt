package com.example.railsconnectiontest

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import java.net.URL
import android.util.Log

class MainActivity : AppCompatActivity() {
    private lateinit var resultTextView: TextView
    private lateinit var connectButton: Button
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            resultTextView = findViewById(R.id.resultTextView)
            connectButton = findViewById(R.id.connectButton)
            webView = findViewById(R.id.webview)

            // Инициализация WebView
            webView.webViewClient = WebViewClient()
            webView.settings.javaScriptEnabled = true

            // Загрузка сохраненного URL
            val sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE)
            val localHostUrl: String? = sharedPref.getString("localHostUrl", "http://192.168.114.255:3000")
            webView.loadUrl(localHostUrl ?: "http://192.168.114.129:3000")

            // Добавление обработчика для кнопки
            connectButton.setOnClickListener {
                testConnection()
                // Сохранение URL
                with(sharedPref.edit()) {
                    putString("localHostUrl", "http://192.168.114.129:3000")
                    apply()
                }
                // Загрузка URL в WebView
                loadWebContent()
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error in onCreate: ${e.message}", e)
        }
    }
    private fun testConnection() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    try {
                        val url = URL("http://192.168.114.129:3000")
                        val connection = url.openConnection()
                        connection.connectTimeout = 10000
                        connection.connect()
                        "Подключение успешно"
                    } catch (e: Exception) {
                        Log.e("MainActivity", "Error in connection: ${e.message}", e)
                        "Ошибка подключения: ${e.message}"
                    }
                }
                resultTextView.text = result
            } catch (e: Exception) {
                Log.e("MainActivity", "Error in testConnection: ${e.message}", e)
                resultTextView.text = "Произошла ошибка: ${e.message}"
            }
        }
    }

    private fun loadWebContent() {
        try {
            webView.loadUrl("http://192.168.114.129:3000")
        } catch (e: Exception) {
            Log.e("MainActivity", "Error in loading URL: ${e.message}", e)
            resultTextView.text = "Ошибка загрузки веб-контента: ${e.message}"
        }
    }
}
