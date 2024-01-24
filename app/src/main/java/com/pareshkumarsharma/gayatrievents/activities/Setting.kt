package com.pareshkumarsharma.gayatrievents.activities

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.pareshkumarsharma.gayatrievents.R

internal class Setting : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

//        if (getSharedPreferences(Database.SHAREDFILE, MODE_PRIVATE).getInt("LLUType", 0) == 2) {
//            findViewById<Button>(R.id.btnChangeRequest_UserType).visibility = View.GONE
//        }

//        findViewById<Button>(R.id.btnChangeRequest_UserType).setOnClickListener {
//            val UserOldType =
//                getSharedPreferences(Database.SHAREDFILE, MODE_PRIVATE).getInt("LLUType", 0)
//            if (UserOldType < 1) {
//                Toast.makeText(
//                    applicationContext,
//                    "You can't change the user type",
//                    Toast.LENGTH_LONG
//                ).show()
//                return@setOnClickListener
//            } else if (UserOldType != 1) {
//                Toast.makeText(
//                    applicationContext,
//                    "You are already changed your user type",
//                    Toast.LENGTH_LONG
//                ).show()
//                return@setOnClickListener
//            } else if (UserOldType == 1) {
//                Thread(Runnable {
//                    APICalls.setContext(this)
//                    APICalls.cookies = mapOf<String, String>(
//                        Pair(
//                            "token",
//                            getSharedPreferences(
//                                Database.SHAREDFILE,
//                                MODE_PRIVATE
//                            ).getString("token", "").toString()
//                        ),
//                        Pair(
//                            "expires",
//                            getSharedPreferences(
//                                Database.SHAREDFILE,
//                                MODE_PRIVATE
//                            ).getString("expires", "").toString()
//                        )
//                    )
//                    if (APICalls.requestUserTypeChange(
//                            UserTypeChangeRequestModel(
//                                UserOldType,
//                                2
//                            )
//                        )
//                    ) {
//                        runOnUiThread(Runnable {
//                            Toast.makeText(
//                                applicationContext,
//                                APICalls.lastCallMessage,
//                                Toast.LENGTH_LONG
//                            ).show()
//                        })
//                    } else {
//                        runOnUiThread {
//                            Toast.makeText(
//                                applicationContext,
//                                APICalls.lastCallMessage,
//                                Toast.LENGTH_LONG
//                            ).show()
//                        }
//                    }
//                }).start()
//            }
//        }

        findViewById<TextView>(R.id.tv_link_privacyPolice).setOnClickListener {
            PrivacyPolicy.Browser = 0
            startActivity(Intent(this, PrivacyPolicy::class.java))
        }

        findViewById<TextView>(R.id.btn_link_yajamanRegistration).setOnClickListener {
            PrivacyPolicy.Browser = 0
            startActivity(Intent(this, YajamanRegistration::class.java))
        }

        findViewById<TextView>(R.id.btn_link_yajamanAppointment).setOnClickListener {
            PrivacyPolicy.Browser = 0
            startActivity(Intent(this, YajamanAppointment::class.java))
        }

//        findViewById<TextView>(R.id.tv_link_deleteUser).setOnClickListener {
//            PrivacyPolicy.Browser = 1
//            startActivity(Intent(this, PrivacyPolicy::class.java))
//        }
    }
}