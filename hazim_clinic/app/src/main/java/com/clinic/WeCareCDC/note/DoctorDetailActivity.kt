package com.example.hazim_clinic.note

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.hazim_clinic.MainActivity
import com.example.hazim_clinic.R

class DoctorDetailActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_detail)




        val doctorNameTextView: TextView = findViewById(R.id.doctor_name_detail)
        val specialityDetailTextView: TextView = findViewById(R.id.specialty_detail)
        val ratingDetailTextView: TextView = findViewById(R.id.rating_detail)
        val doctorAvatarImageView: ImageView = findViewById(R.id.doctor_avatar_detail)
        val ratingClick=findViewById<TextView>(R.id.rating_click)
        val reviewsClick=findViewById<LinearLayout>(R.id.reviews_click)
        val reviewsClickText=findViewById<TextView>(R.id.reviews_click2)

        val doctorName=intent.getStringExtra("doctor_name")
        val speciality=intent.getStringExtra("speciality")
        val rating=intent.getFloatExtra("rating",0.0f)
        val reviewCount = intent.getIntExtra("REVIEW_COUNT", 0)
        val doctorAvatar=intent.getIntExtra("doctor_avatar",R.drawable.doctor_avatar)


        doctorNameTextView.text=doctorName
        specialityDetailTextView.text=speciality
        ratingDetailTextView.text="Rating $rating($reviewCount reviews)"
        doctorAvatarImageView.setImageResource(doctorAvatar)
                val about_doctor=findViewById<TextView>(R.id.about_doctor)
        about_doctor.text="${doctorNameTextView.text} is a board-certified ${specialityDetailTextView.text} with over 12 years of experience in the field of ${specialityDetailTextView.text} medicine.He specilizes. More\"\n"


        findViewById<ImageView>(R.id.back_img).setOnClickListener {
//            val intent_detail= Intent(this, MainActivity::class.java)
//            startActivity(intent_detail)
            finish()
        }
        // ================== NEW RATING LOGIC ==================
        // A unique key to identify this specific doctor for rating purposes.
        val doctorRatingKey = "rated_${doctorName?.replace(" ", "_")}"

        // Use SharedPreferences to store whether the user has rated this doctor.
        val sharedPrefs = getSharedPreferences("DoctorRatings", Context.MODE_PRIVATE)
        val hasUserRated = sharedPrefs.getBoolean(doctorRatingKey, false)

        if (hasUserRated) {
            reviewsClickText.text = "You've already rated"
            reviewsClick.isEnabled = false // Disable the button if already rated
        } else {
            reviewsClick.setOnClickListener {
                // --- Simulate adding a new rating ---
                // For a real app, you would get the new rating from a dialog (e.g., 4.5 stars).
                // Here, we'll just use a fixed value like 5.0 for demonstration.
                val newRatingFromUser = 5.0f

                // Calculate the new average rating
                // Calculate the new average rating
                val totalRatingSum = (rating * reviewCount) + newRatingFromUser
                val newReviewCount = reviewCount + 1
                val newAverageRating = totalRatingSum / newReviewCount

                // Update the UI immediately to give feedback to the user
                ratingDetailTextView.text = "Rating %.1f ($newReviewCount reviews)".format(newAverageRating)
                Toast.makeText(this, "Thank you for your review!", Toast.LENGTH_SHORT).show()

                // --- Save the state to prevent re-rating ---
                // Mark this doctor as rated by this user on this device.
                with(sharedPrefs.edit()) {
                    putBoolean(doctorRatingKey, true)
                    apply() // apply() saves the data in the background
                }

                // Disable the button after rating
                it.isEnabled = false
                (it as TextView).text = "Thanks for rating!"
            }
        }

    }//oncreate function ends here


}