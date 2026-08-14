package com.bhartiyestudent.studytimer
import android.Manifest
import android.app.*
import android.content.*
import android.os.*
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

data class Subject(val id:String,val name:String,val icon:String,val target:Long)

class MainActivity:AppCompatActivity(){
 private val subs=listOf(
  Subject("maths","Maths","🧮",9600),
  Subject("reasoning","Reasoning","🧠",7200),
  Subject("gkgs","GK / GS","📚",7200),
  Subject("mpgk","MP GK","🇮🇳",3600))
 private val prefs by lazy{getSharedPreferences("study",0)}
 private val handler=Handler(Looper.getMainLooper())
 private var active:String?=null
 private lateinit var root:LinearLayout
 override fun onCreate(b:Bundle?){super.onCreate(b);setContentView(R.layout.activity_main);root=findViewById(R.id.root)
  if(Build.VERSION.SDK_INT>=33)requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS),5)
  active=prefs.getString("active",null); draw()
  handler.post(object:Runnable{override fun run(){draw();handler.postDelayed(this,1000)}})
 }
 private fun elapsed(id:String):Long{
  val e=prefs.getLong("e_$id",0); if(prefs.getString("active",null)==id){val st=prefs.getLong("st_$id",0);return e+(System.currentTimeMillis()-st)/1000};return e
 }
 private fun fmt(x:Long):String=String.format(Locale.US,"%02d:%02d:%02d",x/3600,(x%3600)/60,x%60)
 private fun draw(){
  root.removeAllViews()
  val title=TextView(this);title.text="📚  आज की पढ़ाई";title.textSize=28f;title.setPadding(0,10,0,5);root.addView(title)
  val total=subs.sumOf{elapsed(it.id)};val target=subs.sumOf{it.target}
  val sum=TextView(this);sum.text="कुल ${fmt(total)} / ${fmt(target)}   •   ${(total*100/target)}%";sum.textSize=18f;root.addView(sum)
  subs.forEach{s->
   val box=LinearLayout(this);box.orientation=LinearLayout.VERTICAL;box.setPadding(18,18,18,18)
   val tv=TextView(this);tv.text="${s.icon}  ${s.name}  — Target ${fmt(s.target)}\n${fmt(elapsed(s.id))}";tv.textSize=20f;box.addView(tv)
   val bar=ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);bar.max=100;bar.progress=minOf(100,(elapsed(s.id)*100/s.target).toInt());box.addView(bar)
   val row=LinearLayout(this);row.gravity=Gravity.CENTER_VERTICAL
   val btn=Button(this);val running=prefs.getString("active",null)==s.id;btn.text=if(running)"⏸ Pause" else if(elapsed(s.id)>=s.target)"✓ Completed" else "▶ Start"
   btn.setOnClickListener{if(running)pause(s.id) else start(s.id)};row.addView(btn)
   val reset=Button(this);reset.text="Reset";reset.setOnClickListener{prefs.edit().remove("e_${s.id}").remove("st_${s.id}").apply();draw()};row.addView(reset)
   box.addView(row);root.addView(box)
  }
  val note=TextView(this);note.text="🔒 Screen lock/display off के दौरान भी foreground service elapsed time track करेगा.";note.setPadding(8,20,8,8);root.addView(note)
 }
 private fun start(id:String){
  if(prefs.getString("active",null)!=null){Toast.makeText(this,"पहले चल रहा timer Pause करें",Toast.LENGTH_SHORT).show();return}
  prefs.edit().putString("active",id).putLong("st_$id",System.currentTimeMillis()).apply()
  ContextCompatCompat.start(this);draw()
 }
 private fun pause(id:String){prefs.edit().putLong("e_$id",elapsed(id)).remove("st_$id").remove("active").apply();stopService(Intent(this,StudyTimerService::class.java));draw()}
 object ContextCompatCompat{fun start(c:Context){if(Build.VERSION.SDK_INT>=26)c.startForegroundService(Intent(c,StudyTimerService::class.java))else c.startService(Intent(c,StudyTimerService::class.java))}}
}
