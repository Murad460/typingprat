package com.example.hazim_clinic.note

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.hazim_clinic.MainActivity
import com.example.hazim_clinic.R
import com.google.firebase.auth.FirebaseAuth
import kotlin.text.contains
import kotlin.text.substringBefore

class ProfileActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        if(R.id.back_img!=null){
        findViewById<ImageView>(R.id.back_img).setOnClickListener {
//            val intent_prof=Intent(this, MainActivity::class.java)
//            intent_prof.putExtra("service_activity","ProfileActivity")
//            startActivity(intent_prof)
            finish()
              }

        }
        //        to display name of email user in nav bar code
        val navEmailTextView=findViewById<TextView>(R.id.nav_email)
        val navNameTextView = findViewById<TextView>(R.id.nav_name)
        val currentuser=FirebaseAuth.getInstance().currentUser
        if (currentuser != null) {

            if(currentuser.displayName.isNullOrEmpty()){
                navNameTextView.text="${currentuser.displayName}"
                val email=currentuser.email
                if (email !=null && email.contains("@")){
                    navNameTextView.text="${email.substringBefore("@")}"
                    navEmailTextView.text=email}
            }
            else{
                val email=currentuser.email
                if (email !=null && email.contains("@"))
                    navNameTextView.text="${email.substringBefore("@")}"
                    navEmailTextView.text=email

            }
        }
        else{
            navNameTextView.text="Hello Guest"
        }
//        display code ends here

    }
}
