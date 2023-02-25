package com.pareshkumarsharma.gayatrievents.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import com.pareshkumarsharma.gayatrievents.R
import com.pareshkumarsharma.gayatrievents.activities.MainActivity
import com.pareshkumarsharma.gayatrievents.utilities.Database
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class PanchangNotification : Service() {

    lateinit var builder: Notification.Builder
    lateinit var notificationManager: NotificationManager

    lateinit var Td:Thread

    // In Minutes
    val THD_INTERVAL = 60
    val TMR_INTERVAL = 24*3600*1000
    val NOTFICAITON_TIME = " 06:00"

    companion object{
        var notificationId = 0
        const val CHANNEL_ID = "PARESHKUMAR_GE_PANCHANG_SERVICE"
        var Is_Running = false
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
        sendNotification("Hi Yajaman","Hellow From Pareshkumar Sharma")
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager
        Is_Running = true
        Td = Thread(Runnable {
            val d = Date()
            val td = Date(d.time+(3600*1000*24))
            val tds = SimpleDateFormat("yyyy-MM-dd").format(td)+NOTFICAITON_TIME
            val std = SimpleDateFormat("yyyy-MM-dd HH:mm").parse(tds)
            getSharedPreferences(Database.SHAREDFILE, MODE_PRIVATE).edit()
                .putLong("NOTTIME",std.time)
                .apply()

            while(true) {
                val ss = getSharedPreferences(Database.SHAREDFILE, MODE_PRIVATE).getLong("NOTTIME", System.currentTimeMillis())
                if(ss<=System.currentTimeMillis()) {
                    val tbl = Database.getPanchangFestivalOf(SimpleDateFormat("dd-MM-yyyy").format(td),SimpleDateFormat("yyyy").format(td).toInt())
                    var festivals = ""
                    for (r in tbl.Rows){
                        festivals += ","+r[1]
                    }
                    festivals = festivals.substring(1)
                    sendNotification("${tbl.Rows.size} Festival Events", festivals)
                    val d1 = Date()
                    val td1 = Date(d1.time+TMR_INTERVAL)
                    val tds1 = SimpleDateFormat("yyyy-MM-dd").format(td1)+NOTFICAITON_TIME
                    val std1 = SimpleDateFormat("yyyy-MM-dd HH:mm").parse(tds1)
                    getSharedPreferences(Database.SHAREDFILE, MODE_PRIVATE).edit()
                        .putLong("NOTTIME", std1.time)
                        .apply()
                }
                Thread.sleep(THD_INTERVAL*60*1000L)
            }
        })
        Td.start()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Is_Running = false
    }

    fun sendNotification(title:String, message:String){
        val intent = Intent(applicationContext, PanchangNotification::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                PanchangNotification.CHANNEL_ID,
                "Message Service",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.lightColor = Color.BLUE
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
            builder = Notification.Builder(this, PanchangNotification.CHANNEL_ID)
                .setContentTitle(
                    title
                ).setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher_foreground).setLargeIcon(
                    BitmapFactory.decodeResource(
                        this.resources, R.mipmap.ic_launcher_foreground
                    )
                ).setContentIntent(pendingIntent)
                .setStyle(Notification.BigTextStyle())
            notificationManager.notify(PanchangNotification.notificationId++, builder.build())
        } else {
            val n = Notification.Builder(this)
            n.setSmallIcon(R.mipmap.ic_launcher_foreground)
            n.setLargeIcon(
                BitmapFactory.decodeResource(
                    this.resources, R.mipmap.ic_launcher_foreground
                ))
            n.setContentTitle(title)
            n.setContentText(message)
            n.setStyle(Notification.BigTextStyle())
            notificationManager.notify(
                PanchangNotification.notificationId++,
                n.build()
            )
        }
    }
}