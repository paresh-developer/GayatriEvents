package com.pareshkumarsharma.gayatrievents.activities

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.pareshkumarsharma.gayatrievents.R

class YajamanRegistration : AppCompatActivity() {
    companion object {
        internal var Browser = 0
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)

        val wv = findViewById<WebView>(R.id.webView)
        wv.settings.javaScriptEnabled = true
        if(Browser== 0) {
            wv.loadUrl("https://docs.google.com/forms/d/e/1FAIpQLSdsF1y7LwPT1Sx9FxOKDioUK79Edd6tL2a8AVtkuLP-14aTrA/viewform?usp=pp_url")
        }
    }
}