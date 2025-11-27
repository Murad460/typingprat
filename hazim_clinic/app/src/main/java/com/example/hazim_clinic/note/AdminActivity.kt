package com.example.hazim_clinic.note

import android.content.Intent
import android.graphics.Color
import android.widget.ImageView
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.WindowCompat
import com.example.hazim_clinic.MainActivity
import com.example.hazim_clinic.R
import java.text.SimpleDateFormat // <-- Import for date formatting
import java.util.Date // <-- Import for getting the current date
import java.util.Locale
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import kotlin.text.contains
import kotlin.text.substringBefore

class AdminActivity: AppCompatActivity() {
    //    private val auth = FirebaseAuth.getInstance()
//
//    private val database= FirebaseDatabase.getInstance().reference.child("children")
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_view)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true
        // Set the status bar color
        window.statusBarColor = Color.parseColor("#E7F2F1")

//code to signout from the app
        findViewById<CardView>(R.id.card2).setOnClickListener {
            startActivity(Intent(this, StaffActivity::class.java))}

    findViewById<CardView>(R.id.staff_card).setOnClickListener {
        startActivity(Intent(this, StaffActivity::class.java))}

        findViewById<CardView>(R.id.card_treatment).setOnClickListener {
            startActivity(Intent(this, TreatmentActivity::class.java))}
        //    code for going to home
        val img_view2=findViewById<ImageView>(R.id.home_icon_doctor)
        img_view2.setOnClickListener {
            val imgIntent= Intent(this, MainActivity::class.java)
            startActivity(imgIntent)
            finish()
        }
        findViewById<ImageView>(R.id.profile_icon).setOnClickListener{
            val intentprofile=Intent(this, ProfileActivity::class.java)
            intentprofile.putExtra("service_activity","DoctorUpdateActivity")
            startActivity(intentprofile)


        }

        findViewById<ImageView>(R.id.doctor_setting_icon).setOnClickListener {
            val intent_setting=Intent(this, SettingActivity::class.java)
            intent_setting.putExtra("service_activity","DoctorUpdateActivity")
            startActivity(intent_setting)
        }
        findViewById<ImageView>(R.id.parent_activity).setOnClickListener {
            val intent_setting=Intent(this, ParentViewActivity::class.java)
            intent_setting.putExtra("service_activity","DoctorUpdateActivity")
            startActivity(intent_setting)
        }
//for current date code
        val dateTextView = findViewById<TextView>(R.id.date_doctor)
        val current_date=Date()
        val dateFormat=SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val formattedDate=dateFormat.format(current_date)
        dateTextView.text=formattedDate
        //        to display name of email user in nav bar code
        val navNameTextView = findViewById<TextView>(R.id.nav_name)
        val currentuser=FirebaseAuth.getInstance().currentUser
        if (currentuser != null) {

            if(currentuser.displayName.isNullOrEmpty()){
                navNameTextView.text="${currentuser.displayName}"}
            else{
                val email=currentuser.email
                if (email !=null && email.contains("@"))
                    navNameTextView.text="${email.substringBefore("@")}"

            }
        }
        else{
            navNameTextView.text="Hello Guest"
        }
//        }:
//        display code ends here

    }//        oncreate function ends here

//    signout code ends here

}