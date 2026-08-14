package com.bhartiyestudent.studytimer
import android.app.*
import android.content.*
import android.os.*
class StudyTimerService:Service(){
 private val channel="study_timer"
 override fun onCreate(){
  super.onCreate()
  if(Build.VERSION.SDK_INT>=26){
   val nm=getSystemService(NotificationManager::class.java)
   nm.createNotificationChannel(NotificationChannel(channel,"Study Timer",NotificationManager.IMPORTANCE_LOW))
   val n=Notification.Builder(this,channel).setContentTitle("Study Timer").setContentText("Study session is running").setSmallIcon(android.R.drawable.ic_lock_idle_alarm).setOngoing(true).build()
   startForeground(101,n)
  }
 }
 override fun onStartCommand(i:Intent?,f:Int,s:Int):Int{ return START_STICKY }
 override fun onBind(i:Intent?)=null
}
