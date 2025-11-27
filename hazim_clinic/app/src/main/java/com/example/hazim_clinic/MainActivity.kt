package com.example.hazim_clinic

import android.content.Intent
import android.os.Bundle
import com.example.hazim_clinic.note.DoctorAdminSignUpActivity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.hazim_clinic.note.DoctorDetailActivity
import com.example.hazim_clinic.note.LoginActivity
import com.example.hazim_clinic.note.ProfileActivity
import com.example.hazim_clinic.note.SettingActivity
import com.google.firebase.auth.FirebaseAuth
import android.widget.TextView
import com.example.hazim_clinic.model.Doctor
import com.example.hazim_clinic.note.ActivityDoctorsDetails
import com.example.hazim_clinic.note.DoctorAdapter
import com.example.hazim_clinic.note.OurServicesActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    // 1. Define doctorList and doctorAdapter as class properties
    private val doctorList: MutableList<Doctor> = mutableListOf()
    private lateinit var doctorAdapter: DoctorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

//doctor1
        setDoctor(layout_id = R.id.doctor_1, doctor_name_id = R.id.doctor_text1,specialty_id = R.id.doctor_specialty1,rating = 4.8f,reviewCount = 120,avatarResId = R.drawable.doctor_avatar)
//doctor2
        setDoctor(layout_id=R.id.doctor_2,doctor_name_id = R.id.doctor_text2,specialty_id = R.id.doctor_specialty2,rating = 4.8f,reviewCount = 120,avatarResId = R.drawable.doctor_avatar)

        findViewById<ImageView>(R.id.more_icon).setOnClickListener {
            startActivity(Intent(this,DoctorAdminSignUpActivity::class.java))
        }

        findViewById<ImageView>(R.id.doctor_icon).setOnClickListener{
            val user = FirebaseAuth.getInstance().currentUser
//            if (user != null) {
//                startActivity(Intent(this,LoginActivity::class.java))
////                if (user.email?.endsWith("@doctor.com") == true) {
////                    startActivity(Intent(this, DoctorViewActivity::class.java))
////
////                } else {
////                    startActivity(Intent(this, ParentViewActivity::class.java))
////                }
////                finish()
//            } else {
                Toast.makeText(this, "Please Logged in first", Toast.LENGTH_SHORT).show()

                startActivity(Intent(this, LoginActivity::class.java))

//            }
        }
        findViewById<ImageView>(R.id.profile_icon).setOnClickListener{
            val intentprofile=Intent(this, ProfileActivity::class.java)
            intentprofile.putExtra("service_activity","MainActivity")
            startActivity(intentprofile)
//            finish()
        }
        findViewById<ImageView>(R.id.setting_icon).setOnClickListener {
            val intent_setting=Intent(this, SettingActivity::class.java)
            intent_setting.putExtra("service_activity","MainActivity")
            startActivity(intent_setting)
        //            finish()
        }
//        to display name of email user in nav bar code
        val navNameTextView = findViewById<TextView>(R.id.nav_name)
        val currentuser=FirebaseAuth.getInstance().currentUser
        if (currentuser != null) {

        if(currentuser.displayName.isNullOrEmpty()){
            navNameTextView.text="Hello ${currentuser.displayName}"}
        else{
            val email=currentuser.email
            if (email !=null && email.contains("@"))
                navNameTextView.text="Hello ${email.substringBefore("@")}"

            }
        }
        else{
            navNameTextView.text="Hello Guest"
        }
//        }:
//        display code ends here

        findViewById<LinearLayout>(R.id.our_services2).setOnClickListener {
            startActivity(Intent(this, OurServicesActivity::class.java))}

        findViewById<LinearLayout>(R.id.our_services3).setOnClickListener {
            startActivity(Intent(this, ActivityDoctorsDetails::class.java))}
//for current date code
        val dateTextView = findViewById<TextView>(R.id.date_doctor)
        val current_date=Date()
        val dateFormat=SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val formattedDate=dateFormat.format(current_date)
        dateTextView.text=formattedDate




    }//on create funciton ends here

fun setDoctor(layout_id:Int,doctor_name_id:Int,specialty_id:Int,rating:Float,reviewCount:Int,avatarResId:Int){
    findViewById<LinearLayout>(layout_id).setOnClickListener {

            val intent=Intent(this,DoctorDetailActivity::class.java)
            intent.putExtra("doctor_name",findViewById<TextView>(doctor_name_id).text.toString(),
            )
            intent.putExtra("speciality",findViewById<TextView>(specialty_id).text.toString(),
            )
            intent.putExtra("rating",rating)
            intent.putExtra("reviewCount",reviewCount)
            intent.putExtra("doctor_avatar",avatarResId)
            startActivity(intent)
    }}
}