package com.example.hazim_clinic.note

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hazim_clinic.R
import com.example.hazim_clinic.model.ChildInfo
import com.example.hazim_clinic.model.Doctor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AddDoctorActivity: AppCompatActivity() {
    //create auth instance and initilize and create db
    private val auth = FirebaseAuth.getInstance()
   private val database= FirebaseDatabase.getInstance().reference.child("Doctor")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_doctor_activity)

        val etDoctorEmail = findViewById<EditText>(R.id.et_doctor_email)
        val etDoctorName = findViewById<EditText>(R.id.et_doctor_name)
        val etExperience = findViewById<EditText>(R.id.et_experience)
//        val etGender = findViewById<AutoCompleteTextView>(R.id.et_gender)
//        val etSessionType = findViewById<AutoCompleteTextView>(R.id.et_session_type)
        val etDoctorSpecialty = findViewById<EditText>(R.id.et_doctor_specialty)



        findViewById<Button>(R.id.btn_update).setOnClickListener{
            val doctor_email=etDoctorEmail.text.toString().trim()
            val doctor_encoded_email=doctor_email.replace(".",",")
            val doctor_name = etDoctorName.text.toString()
            val experience = etExperience.text.toString()
            val doctor_specialty = etDoctorSpecialty.text.toString()



            if(doctor_encoded_email.isEmpty()){
                Toast.makeText(this,"Enter doctor Email ID",Toast.LENGTH_SHORT).show()
                return@setOnClickListener

            }

            if(experience.isEmpty()){
                Toast.makeText(this,"Enter doctor Experience",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val child_age=experience.toInt()
            val doctor= Doctor(
                doctor_name,doctor_specialty,experience,doctor_encoded_email)


            database.child(doctor_encoded_email).setValue(doctor)
                .addOnSuccessListener{
                    Toast.makeText(this,"Updated Successfully",Toast.LENGTH_SHORT).show()
                    finish()}

                .addOnFailureListener{
                    Toast.makeText(this,"Update Failed",Toast.LENGTH_SHORT).show()
                }

        }//for update or delete information

    }}