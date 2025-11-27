package com.example.hazim_clinic.note

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.add
import androidx.compose.ui.layout.layout
//import androidx.glance.visibility
import androidx.appcompat.widget.SearchView
import com.example.hazim_clinic.R
import kotlin.text.lowercase

class ActivityDoctorsDetails: AppCompatActivity() {
    private val doctorViews = mutableListOf<DoctorViewInfo>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctors_details)
        //doctor1
        setDoctor(layout_id = R.id.doctor_1, doctor_name_id = R.id.doctor_text1,specialty_id = R.id.doctor_specialty1,rating = 4.8f,reviewCount = 120,avatarResId = R.drawable.doctor_avatar)
        //doctor2
        setDoctor(layout_id=R.id.doctor_2,doctor_name_id = R.id.doctor_text2,specialty_id = R.id.doctor_specialty2,rating = 4.8f,reviewCount = 120,avatarResId = R.drawable.doctor_avatar)
        //doctor3
        setDoctor(layout_id = R.id.doctor_3, doctor_name_id = R.id.doctor_text3,specialty_id = R.id.doctor_specialty3,rating = 4.8f,reviewCount = 120,avatarResId = R.drawable.doctor_avatar)
        //doctor4
        setDoctor(layout_id=R.id.doctor_4,doctor_name_id = R.id.doctor_text4,specialty_id = R.id.doctor_specialty4,rating = 4.8f,reviewCount = 120,avatarResId = R.drawable.doctor_avatar)
        //doctor5
        setDoctor(layout_id = R.id.doctor_5, doctor_name_id = R.id.doctor_text5,specialty_id = R.id.doctor_specialty5,rating = 4.8f,reviewCount = 120,avatarResId = R.drawable.doctor_avatar)
        //doctor6
        setDoctor(layout_id=R.id.doctor_6,doctor_name_id = R.id.doctor_text6,specialty_id = R.id.doctor_specialty6,rating = 4.8f,reviewCount = 120,avatarResId = R.drawable.doctor_avatar)
        //doctor7
        setDoctor(layout_id = R.id.doctor_7, doctor_name_id = R.id.doctor_text6,specialty_id = R.id.doctor_specialty7,rating = 4.8f,reviewCount = 120,avatarResId = R.drawable.doctor_avatar)
        //doctor8
        setDoctor(layout_id=R.id.doctor_8,doctor_name_id = R.id.doctor_text8,specialty_id = R.id.doctor_specialty8,rating = 4.8f,reviewCount = 120,avatarResId = R.drawable.doctor_avatar)

    }//override oncreate funcation ends here
    //function to set doctor
    fun setDoctor(layout_id:Int,doctor_name_id:Int,specialty_id:Int,rating:Float,reviewCount:Int,avatarResId:Int){
//        val about_doctor=findViewById<TextView>(about_id)
        val doctor_name=findViewById<TextView>(doctor_name_id).text.toString()
        val doctor_specialty=findViewById<TextView>(specialty_id).text.toString()
//        about_doctor.text="${doctor_name} is a board-certified ${doctor_specialty} with over 12 years of experience in the field of ${doctor_specialty} medicine.He specilizes. More\"\n"
        findViewById<LinearLayout>(layout_id).setOnClickListener {

            val intent=Intent(this,DoctorDetailActivity::class.java)
            intent.putExtra("doctor_name",doctor_name,
            )
            intent.putExtra("speciality",doctor_specialty,
            )
            intent.putExtra("rating",rating)
            intent.putExtra("reviewCount",reviewCount)
            intent.putExtra("doctor_avatar",avatarResId)
//            intent.putExtra("about_doctor",about_doctor.text.toString())
            startActivity(intent)
        }



        // --- PART 1: Initialize all doctors and store their info ---
        // We now call a new function that also saves the doctor's info for searching.
        initializeDoctor(R.id.doctor_1, R.id.doctor_text1, R.id.doctor_specialty1, 4.8f, 120, R.drawable.doctor_avatar)
        initializeDoctor(R.id.doctor_2, R.id.doctor_text2, R.id.doctor_specialty2, 4.8f, 120, R.drawable.doctor_avatar)
        initializeDoctor(R.id.doctor_3, R.id.doctor_text3, R.id.doctor_specialty3, 4.8f, 120, R.drawable.doctor_avatar)
        initializeDoctor(R.id.doctor_4, R.id.doctor_text4, R.id.doctor_specialty4, 4.8f, 120, R.drawable.doctor_avatar)
        initializeDoctor(R.id.doctor_5, R.id.doctor_text5, R.id.doctor_specialty5, 4.8f, 120, R.drawable.doctor_avatar)
        initializeDoctor(R.id.doctor_6, R.id.doctor_text6, R.id.doctor_specialty6, 4.8f, 120, R.drawable.doctor_avatar)
        // Note: You had a typo, doctor_7 was using doctor_text6. I corrected it.
        initializeDoctor(R.id.doctor_7, R.id.doctor_text7, R.id.doctor_specialty7, 4.8f, 120, R.drawable.doctor_avatar)
        initializeDoctor(R.id.doctor_8, R.id.doctor_text8, R.id.doctor_specialty8, 4.8f, 120, R.drawable.doctor_avatar)

        // --- PART 2: Set up the SearchView ---
        val searchView = findViewById<SearchView>(R.id.search_view)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            // This method is called when the user submits the query (e.g., presses enter)
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterDoctors(query)
                return true
            }

            // This method is called every time the text in the search view changes
            override fun onQueryTextChange(newText: String?): Boolean {
                filterDoctors(newText)
                return true
            }
        })
    }

    /**
     * Initializes a single doctor's layout, sets its click listener,
     * and stores its information for filtering.
     */
    private fun initializeDoctor(layoutId: Int, nameId: Int, specialtyId: Int, rating: Float, reviewCount: Int, avatarResId: Int) {
        val layout = findViewById<LinearLayout>(layoutId)
        val doctorName = findViewById<TextView>(nameId).text.toString()
        val doctorSpecialty = findViewById<TextView>(specialtyId).text.toString()

        // Store the layout and its text data in our list
        doctorViews.add(DoctorViewInfo(layout, doctorName, doctorSpecialty))

        // Set the click listener to open the detail activity
        layout.setOnClickListener {
            val intent = Intent(this, DoctorDetailActivity::class.java).apply {
                putExtra("doctor_name", doctorName)
                putExtra("speciality", doctorSpecialty)
                putExtra("rating", rating)
                putExtra("reviewCount", reviewCount)
                putExtra("doctor_avatar", avatarResId)
            }
            startActivity(intent)
        }
    }

    /**
     * Filters the list of doctors based on the search query.
     */
    private fun filterDoctors(query: String?) {
        val searchText = query?.trim()?.lowercase() ?: ""

        for (doctor in doctorViews) {
            val doctorName = doctor.name.lowercase()
            val doctorSpecialty = doctor.specialty.lowercase()

            // If the search text is empty, or if the name/specialty contains the search text, show the layout.
            if (searchText.isEmpty() || doctorName.contains(searchText) || doctorSpecialty.contains(searchText)) {
                doctor.layout.visibility = View.VISIBLE
            } else {
                // Otherwise, hide the layout.
                doctor.layout.visibility = View.GONE
            }
        }
    }
}

/**
 * A simple data class to hold references to a doctor's layout and searchable text.
 */
data class DoctorViewInfo(
    val layout: LinearLayout,
    val name: String,
    val specialty: String
)
