package com.pareshkumarsharma.gayatrievents.activities

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.pareshkumarsharma.gayatrievents.R
import com.pareshkumarsharma.gayatrievents.utilities.APICalls

class PrivacyPolicy : AppCompatActivity() {
    companion object {
        internal var Browser = 0
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)

        val wv = findViewById<WebView>(R.id.webView)
        if(Browser== 0) {
            wv.loadUrl("file:///android_res/raw/privacy_policy.htm")
        }else if(Browser == 1){
            wv.loadUrl(APICalls.HOST.split("api/")[0]+"Home/DeleteAccount")
        }
    }
}