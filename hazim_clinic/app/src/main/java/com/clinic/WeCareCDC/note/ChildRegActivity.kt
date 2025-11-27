package com.example.hazim_clinic.note

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hazim_clinic.R
import com.example.hazim_clinic.model.ChildInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import android.widget.AutoCompleteTextView
import com.example.hazim_clinic.model.ChildRE

class ChildRegActivity: AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()

    private val database= FirebaseDatabase.getInstance().reference.child("childrenRE")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.child_reg_activity)
        val etParentEmail = findViewById<EditText>(R.id.et_parent_email)
        val etName = findViewById<EditText>(R.id.et_name)
        val etAge = findViewById<EditText>(R.id.et_age)
        val etGender = findViewById<AutoCompleteTextView>(R.id.et_gender)
        val etGardianName = findViewById<EditText>(R.id.et_gardian_name)
//for gender dropdown

        // ... find other views ...
        val actvGender = findViewById<AutoCompleteTextView>(R.id.et_gender)

        // --- Set up the Gender Drop-Down ---
        val genderOptions = resources.getStringArray(R.array.gender_options)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, genderOptions)
        actvGender.setAdapter(adapter)
        // --- End of setup ---
        findViewById<Button>(R.id.btn_update).setOnClickListener{
            val child_name=etName.text.toString()
            val parent_email = etParentEmail.text.toString().trim()
            val parent_encoded_email=parent_email.replace(".",",")
            val age_text = etAge.text.toString()
            val child_gender = actvGender.text.toString()
            val gardian_name = etGardianName.text.toString()


            if(parent_encoded_email.isEmpty()){
                Toast.makeText(this,"Enter Parent Email ID",Toast.LENGTH_SHORT).show()
                return@setOnClickListener

            }

            if(age_text.isEmpty()){
                Toast.makeText(this,"Enter Child Age",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val child_age=age_text.toInt()
            val child= ChildRE(
                child_name , child_age,child_gender,parent_encoded_email,  gardian_name
            )


            database.child(parent_encoded_email).child(child_name).setValue(child)
                .addOnSuccessListener{
                    Toast.makeText(this,"Updated Successfully",Toast.LENGTH_SHORT).show()
                    finish()}

                .addOnFailureListener{
                    Toast.makeText(this,"Update Failed",Toast.LENGTH_SHORT).show()
                }
//            val intent=Intent(this, MainActivity::class.java)
//            startActivity(intent)
//            finish()
        }


    }//oncreate function ends here
}