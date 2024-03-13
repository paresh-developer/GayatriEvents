package com.pareshkumarsharma.gayatrievents.activities

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.pareshkumarsharma.gayatrievents.R

class PrivacyPolicy : AppCompatActivity() {
    companion object {
        internal var Browser = 0
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)

        val wv = findViewById<WebView>(R.id.webView)
        if(Browser== 0) {
            try {
                wv.loadUrl("https://paresh98000.wordpress.com/privacy-policy/")
            }
            catch (ex : Exception){
                wv.loadUrl("file:///android_res/raw/privacy_policy.htm")
                //wv.loadUrl(APICalls.HOST.split("api/")[0]+"Home/DeleteAccount")
            }
        }
    }
}