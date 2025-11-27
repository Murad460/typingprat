package com.example.hazim_clinic.note

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import androidx.cardview.widget.CardView
import android.widget.Toast
import androidx.core.view.WindowCompat
import com.example.hazim_clinic.MainActivity
import com.example.hazim_clinic.R
import com.example.hazim_clinic.model.ChildInfo
import com.example.hazim_clinic.model.Doctor
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import androidx.core.content.ContextCompat.startActivity
import com.example.hazim_clinic.model.ChildRE
import android.util.Log


class TreatmentActivity: AppCompatActivity() {
//    private val auth = FirebaseAuth.getInstance()
    private val databaseRE= FirebaseDatabase.getInstance().reference.child("childrenRE")
    private val auth = FirebaseAuth.getInstance()
    // --- FIX 1: Declare lists and adapters at the class level ---
    private var parentEmailList = mutableListOf<String>()
    private var childNameList = mutableListOf<String>()
    private lateinit var parentEmailAdapter: ArrayAdapter<String>
    private lateinit var childNameAdapter: ArrayAdapter<String>
    private lateinit var etName: AutoCompleteTextView
    private val database= FirebaseDatabase.getInstance().reference.child("children")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.treatment_plan)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars =
            true
        // Set the status bar color
        window.statusBarColor = Color.parseColor("#E7F2F1")
//        status bar code ends here
//f
//        val actvGender = findViewById<AutoCompleteTextView>(R.id.et_gender)
        val actvSession = findViewById<AutoCompleteTextView>(R.id.et_session_type)

        // --- Set up the Gender Drop-Down ---
//        val genderOptions = resources.getStringArray(R.array.gender_options)
//        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, genderOptions)
//        actvGender.setAdapter(adapter)
        // --- End of setup ---

        // --- Set up the Gender Drop-Down ---
        val SessionOptions = resources.getStringArray(R.array.session_types_options)
        val adapterSession =
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, SessionOptions)
        actvSession.setAdapter(adapterSession)
//code for update information

        val etParentEmail = findViewById<AutoCompleteTextView>(R.id.et_parent_email)
        etName = findViewById<AutoCompleteTextView>(R.id.et_name)

//        val etAge = findViewById<EditText>(R.id.et_age)
//        val etGender = findViewById<AutoCompleteTextView>(R.id.et_gender)
//        val etSessionType = findViewById<AutoCompleteTextView>(R.id.et_session_type)
        val etTherapistName = findViewById<EditText>(R.id.et_therapist_name)
        val etTreatmentGoal = findViewById<EditText>(R.id.et_treatment_goal)
        val etPlannedActivities = findViewById<EditText>(R.id.et_planned_activities)
        val etProgressNote = findViewById<EditText>(R.id.et_progress_note)

        parentEmailAdapter =
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, parentEmailList)
        etParentEmail.setAdapter(parentEmailAdapter)


        childNameAdapter =
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, childNameList)
        etName.setAdapter(childNameAdapter)

        findViewById<Button>(R.id.btn_update).setOnClickListener {

            val child_name = etName.text.toString()
            if(child_name.isEmpty()) {
                Toast.makeText(this, "Enter Child Name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // This is the crucial fix
            }
            val parent_email = etParentEmail.text.toString().trim()
            val parent_encoded_email = parent_email.replace(".", ",")
            if(parent_encoded_email.isEmpty()) {
                Toast.makeText(this, "Enter Parent Email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // This is the crucial fix
            }
//            val age_text = etAge.text.toString()
//            val child_gender = actvGender.text.toString()
            val session_type = actvSession.text.toString()
            if(session_type.isEmpty()) {
                Toast.makeText(this, "Enter Session Type", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // This is the crucial fix
            }
            val therapist_name = etTherapistName.text.toString()
            if(therapist_name.isEmpty()) {
                Toast.makeText(this, "Enter Therapist Name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // This is the crucial fix
            }
            val treatment_goal = etTreatmentGoal.text.toString()
            val planned_activities = etPlannedActivities.text.toString()
            val progress_note = etProgressNote.text.toString()


            if (parent_encoded_email.isEmpty()) {
                Toast.makeText(this, "Enter Parent Email ID", Toast.LENGTH_SHORT).show()
                return@setOnClickListener

            }

//            if(age_text.isEmpty()){
//                Toast.makeText(this,"Enter Child Age",Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//            val child_age=age_text.toInt()
            val child = ChildInfo(
                parent_encoded_email,
                child_name,
                session_type,
                therapist_name,
                treatment_goal,
                planned_activities,
                progress_note
            )



            database.child(parent_encoded_email).child(child_name).setValue(child)
                .addOnSuccessListener {
                    Toast.makeText(this, "Updated Successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }

                .addOnFailureListener {
                    Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show()
                }
//            val intent=Intent(this, MainActivity::class.java)
//            startActivity(intent)
//            finish()
        }
//code to signout from the app
        findViewById<CardView>(R.id.card2).setOnClickListener {
            startActivity(Intent(this, StaffActivity::class.java))
        }
        //    code for going to home


        // Fetch parent emails for the dropdown
        databaseRE.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                parentEmailList.clear()
                for (doctorSnapshot in snapshot.children) {
                    val email = doctorSnapshot.key?.replace(",", ".")


                    if (email != null) {
                        parentEmailList.add(email)
                    }

                }
                    parentEmailAdapter.notifyDataSetChanged()



            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@TreatmentActivity,
                    "Failed to load parent emails.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        // ----------------------------------------------------------
// 1. Parent-email AutoCompleteTextView
// ----------------------------------------------------------
        etParentEmail.setOnItemClickListener { _, _, position, _ ->
            val selectedParentEmail = parentEmailList[position]

            // ----- 1. Show the selected e-mail as the first child -----
            childNameList.clear()                     // start fresh
            childNameList.add(selectedParentEmail)    // <-- the trick
            childNameAdapter.notifyDataSetChanged()

            etName.setText("")                        // clear any previous name
//            etName.hint = "Loading children…"

            // ----- 2. Load the real children for this parent -----
            val encodedEmail = selectedParentEmail.replace(".", ",")   // Firebase-friendly key
            fetchChildrenForParent(encodedEmail) { children ->
                // `children` is a List<String> returned from your DB call
                // Remove the temporary e-mail entry if you don't want it duplicated
                childNameList.remove(selectedParentEmail)

                // Add the real children (skip if already present)
                children.forEach { child ->
                    if (!childNameList.contains(child)) {
                        childNameList.add(child)

                    }
                    if(!childNameList.isNotEmpty()){
                        etName.setText(childNameList[0])

                    }
                }

                // Refresh UI on the main thread
                etName.post {
                    childNameAdapter.notifyDataSetChanged()

//                    etName.hint = "Select child"
                }
            }
        }
        }
    }
        // ----------------------------------------------------------
// 2. Helper: fetchChildrenForParent (Firebase example)
// ----------------------------------------------------------
        private fun fetchChildrenForParent(encodedEmail: String, onComplete: (List<String>) -> Unit) {
            // Example with Firebase Realtime Database
            val ref = FirebaseDatabase.getInstance()
                .getReference("parents/$encodedEmail/children")   // adjust path to your schema

            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<String>()
                    for (childSnap in snapshot.children) {
                        val name = childSnap.getValue(String::class.java) ?: continue
                        list.add(name)
                    }
                    onComplete(list)
                }

                override fun onCancelled(error: DatabaseError) {
                    onComplete(emptyList())
                    Log.e("fetchChildren", "Failed: ${error.message}")
                }
            })
        }
//        etParentEmail.setOnItemClickListener { _, _, position, _ ->
//            val parent_email = parentEmailList[position]
//            childNameList.clear()
//            childNameAdapter.notifyDataSetChanged()
//            etName.setText("") // Clear previous name
////            etName.hint = "Loading children..."
////            etName.isEnabled = true
//            val encode_emaili=parent_email.replace(".",",")
//            fetchChildrenForParent(encode_emaili)
//        }
//        }
//
//    // 3. Load ONLY the children that belong to the selected parent
//// ---------------------------------------------------------------------
//    private fun fetchChildrenForParent(parentEncoded: String) {
//        val parentRef = databaseRE.child(parentEncoded)
//
//        parentRef.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snap: DataSnapshot) {
//                childNameList.clear()
//
//                for (childNode in snap.children) {
//                    val name = childNode.child("child_name")
//                        .getValue(String::class.java)
//                    if (!name.isNullOrEmpty()) {
//                        childNameList.add(name)
//                    }
//                }
//
//                // ---- UI update ------------------------------------------------
//                childNameAdapter.notifyDataSetChanged()
//                etName.isEnabled = true
//                if (childNameList.isEmpty()) {
//                    etName.setText("No children found")
//                } else {
//                    // Optionally, you can set the first child's name as the default
//                    etName.setText(childNameList[0])
//                }// <<< RE-ENABLE HERE
////                etName.text = if (childNameList.isEmpty())
////                    "No children" else childNameList[0]/
//
//                Toast.makeText(
//                    this@TreatmentActivity,
//                    "Loaded ${childNameList.size} child(ren)",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                etName.isEnabled = true                     // <<< RE-ENABLE ON ERROR
//                etName.hint = "Error loading"
//                Toast.makeText(
//                    this@TreatmentActivity,
//                    "Error: ${error.message}",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//        })
//    }
//
//            /**
//             * Fetches all children for a specific parent and updates the child name AutoCompleteTextView.
//             */
////            private fun fetchChildrenForParent(parentEmail: String) {
//////                Log.d("DEBUG", "fetchChildrenForParent called with: $parentEmail")  // ADD THIS
////                val etName = findViewById<AutoCompleteTextView>(R.id.et_name)
//////                etName.isEnabled = false // Disable field while loading
//////                val parentEncodedE = parentEmail.replace(".", ",")
//////                Log.d("DEBUG", "Encoded email: $parentEncodedE")  // ADD THIS
////
////
////                databaseRE.child(parentEmail)
////                    .addListenerForSingleValueEvent(object : ValueEventListener {
////                    override fun onDataChange(snapshot: DataSnapshot) {
////                        Log.d("FIREBASE", "Exists: ${snapshot.exists()}, Children: ${snapshot.childrenCount}")
////
////                        childNameList.clear()
////                            if(snapshot.exists()) {
//////
////                                for (childSnapshot in snapshot.children) {
////                                    // Assuming the child's name is stored in a field called "childName"
////                                    val name = childSnapshot.child("child_name")
////                                        .getValue(String::class.java)
////                                    if (name != null && name.isNotEmpty()) {
////                                        val name1 = name.replace(",", ".")
////                                        childNameList.add(name1)
////                                    }
////                                }
////
//                                // Update the adapter with the new list of names
////                        childNameAdapter.addAll(childNameList)
////                        etName.hint = "Select child name"
////                        etName.isEnabled = true
//                                childNameAdapter.notifyDataSetChanged()
//                                etName.isEnabled = true
//                                etName.hint = if (childNameList.isEmpty()) "No children" else "Select child"
//                                // Re-enable the name field now that the data is loaded
//                                if (childNameList.isNotEmpty()) {
////                                etName.setText(childNameList[0])
////                            etName.setSelection(etName.text.length)
//                                    Toast.makeText(
//                                        this@TreatmentActivity,
//                                        "${childNameList.size} children found for this parent.",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                } else {
//                                    Toast.makeText(
//                                        this@TreatmentActivity,
//                                        "Failed  to load children.",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                }
//                            }else{
//                                Toast.makeText(
//                                    this@TreatmentActivity,
//                                    "No children found for this parent.",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//                        Toast.makeText(
//                            this@TreatmentActivity,
//                            "Failed to load child names.",
//                            Toast.LENGTH_SHORT
//                        ).show()
////                        / Always re-enable the UI, even on failure
//                        childNameList.clear()
//                        childNameAdapter.notifyDataSetChanged()
//
//
//                    }
//                })
//            }
// --- This is the final closing brace for the TreatmentActivity class ---

