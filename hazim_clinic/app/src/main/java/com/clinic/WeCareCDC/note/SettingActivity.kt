package com.example.hazim_clinic.note

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hazim_clinic.MainActivity
import com.example.hazim_clinic.R
import com.firebase.ui.auth.AuthUI
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class SettingActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        findViewById<ConstraintLayout>(R.id.logout_cons).setOnClickListener {
            Signout()
        }
        findViewById<ImageView>(R.id.back_img_setting).setOnClickListener {
//            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }
    private fun Signout(){
        Firebase.auth.signOut()
//            if(task.isSuccessful){
                startActivity(Intent(this, MainActivity::class.java))
//                finish()
//            }
//            else{
//                Toast.makeText(this, "Sign Out Failed", Toast.LENGTH_SHORT).show()


    }
//    private fun signout() {
//
//        AuthUI.getInstance()
//            .signOut(this)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    startActivity(Intent(this, MainActivity::class.java))
//                    finish()
//                } else {
//                    Toast.makeText(this, "Sign Out Failed", Toast.LENGTH_SHORT).show()
//                }    }   }

}



