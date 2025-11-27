package com.example.hazim_clinic.note

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.semantics.text
import com.example.hazim_clinic.R
import com.example.hazim_clinic.model.ChildInfo
import com.example.hazim_clinic.model.Doctor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DeleteDoctorActivity: AppCompatActivity() {
    //create auth instance and initilize and create db
    private val auth = FirebaseAuth.getInstance()
    private val database= FirebaseDatabase.getInstance().reference.child("Doctor")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.delete_doctor_activity)

        val etDoctorEmail = findViewById<AutoCompleteTextView>(R.id.et_doctor_email)
        val etDoctorName = findViewById<AutoCompleteTextView>(R.id.et_doctor_name)
//        val etExperience = findViewById<EditText>(R.id.et_experience)
//        val etDoctorSpecialty = findViewById<EditText>(R.id.et_doctor_specialty)

        val doctorEmailList = mutableListOf<String>()
        val doctorNameList = mutableListOf<String>()
        val doctorEmailAdapter =
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, doctorEmailList)
        val doctorNameAdapter =
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, doctorNameList)

        etDoctorEmail.setAdapter(doctorEmailAdapter)
        etDoctorName.setAdapter(doctorNameAdapter)
        // Fetch parent emails for the dropdown
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                doctorEmailList.clear()
                for (doctorSnapshot in snapshot.children) {
                    val email = doctorSnapshot.key?.replace(",", ".")
                    if (email != null) {
                        doctorEmailList.add(email)
                    }


                doctorEmailAdapter.notifyDataSetChanged()
            }}

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@DeleteDoctorActivity,
                    "Failed to load parent emails.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        // --- End of Autocomplete Setup ---
        // --- START: NEW LOGIC ---
        // 2. Add a listener to the Parent Email field
        etDoctorEmail.setOnItemClickListener { parent, _, position, _ ->
            // Get the email that the user selected
            val selectedEmail = parent.adapter.getItem(position) as String
            // Clear previous child names and disable the name field while loading
            doctorNameList.clear()
            etDoctorName.text.clear() // Clear any previous entry in the name field
            etDoctorName.isEnabled = false

            // Fetch children for the selected parent
            fetchChildrenForParent(selectedEmail)
        }
        // --- END: NEW LOGIC ---

        findViewById<Button>(R.id.btn_update).setOnClickListener {
            val doctor_email = etDoctorEmail.text.toString().trim()
            val doctor_encoded_email = doctor_email.replace(".", ",")

            val doctor_name = etDoctorName.text.toString()



            if (doctor_encoded_email.isEmpty()) {
                Toast.makeText(this, "Enter doctor Email ID to Delete", Toast.LENGTH_SHORT).show()
                return@setOnClickListener

            }

            val doctor = Doctor(
                doctor_name, doctor_encoded_email
            )


            database.child(doctor_encoded_email).removeValue()
                .addOnSuccessListener {
                    Toast.makeText(this, "Deleted Successfully", Toast.LENGTH_SHORT).show()
                    etDoctorEmail.text.clear()
                    etDoctorName.text.clear()
//                    etExperience.text.clear()
//                    etDoctorSpecialty.text.clear()
//                    doctor.clear()
                    finish()
                }

                .addOnFailureListener {
                    Toast.makeText(this, "Delete Failed", Toast.LENGTH_SHORT).show()
                }

        }//for delete information
    }
// --- START: NEW HELPER FUNCTION ---
        /**
         * Fetches all children for a specific parent and updates the child name AutoCompleteTextView.
         */
        private fun fetchChildrenForParent(parentEmail: String) {
            val etName = findViewById<AutoCompleteTextView>(R.id.et_doctor_name)
            val childNameAdapter = etName.adapter as ArrayAdapter<String>
            val childNameList = mutableListOf<String>()

            val parentEncodedEmail = parentEmail.replace(".", ",")
            val parentNodeRef = database.child(parentEncodedEmail)

            parentNodeRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        // Assuming the child's name is stored in a field called "childName"
                        val name = childSnapshot.child("childName").getValue(String::class.java)
                        if (name != null) {
                            childNameList.add(name)
                        }
                    }

                    // Update the adapter with the new list of names
                    childNameAdapter.clear()
                    childNameAdapter.addAll(childNameList)
                    childNameAdapter.notifyDataSetChanged()

                    // Re-enable the name field now that the data is loaded
                    etName.isEnabled = true
                    Toast.makeText(this@DeleteDoctorActivity, "${childNameList.size} children found for this parent.", Toast.LENGTH_SHORT).show()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@DeleteDoctorActivity, "Failed to load child names.", Toast.LENGTH_SHORT).show()
                    // Re-enable the field even if it fails
                    etName.isEnabled = true
                }
            })
        }
        // --- END: NEW HELPER FUNCTION ---
    }
