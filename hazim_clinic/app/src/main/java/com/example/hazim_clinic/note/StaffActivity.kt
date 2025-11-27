package com.example.hazim_clinic.note

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.hazim_clinic.R
import android.widget.Spinner
import android.widget.AdapterView

import android.view.View
import android.widget.Toast
import androidx.cardview.widget.CardView

class StaffActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.staff_activity)
findViewById<CardView>(R.id.add_child).setOnClickListener {
    startActivity(Intent(this, ChildRegActivity::class.java))
}
findViewById<CardView>(R.id.add_doctors).setOnClickListener {
startActivity(Intent(this, AddDoctorActivity::class.java))
}
        findViewById<CardView>(R.id.delete_doctor).setOnClickListener {
            startActivity(Intent(this, DeleteDoctorActivity::class.java))
        }

        findViewById<CardView>(R.id.delete_child).setOnClickListener {
            startActivity(Intent(this, DeleteChildActivity::class.java))
        }

    }// oncreate function ends here
}
