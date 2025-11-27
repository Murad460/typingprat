package com.example.hazim_clinic.note

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.hazim_clinic.R
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.hazim_clinic.MainActivity
import com.example.hazim_clinic.model.ChildInfo
import com.firebase.ui.auth.AuthUI
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import android.widget.LinearLayout
import androidx.compose.ui.input.key.key
import androidx.compose.ui.semantics.text
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.hazim_clinic.model.ChildRE
import com.google.firebase.auth.FirebaseAuth
import kotlin.text.contains
import kotlin.text.substringBefore

class ParentViewActivity: AppCompatActivity() {

    // References to the correct database nodes
    private val treatmentPlanDb = FirebaseDatabase.getInstance().reference.child("children")
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val childREDb = FirebaseDatabase.getInstance().reference.child("childrenRE")


    private lateinit var  childrenContainer: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parent_view)
        childrenContainer = findViewById(R.id.card_container)
        val parentEmail = currentUser?.email
        if (parentEmail == null) {
            Toast.makeText(this, "User not login", Toast.LENGTH_SHORT).show()
            return
        }
        val parentEmail_encode = parentEmail.replace(".", ",")


        val img_view = findViewById<ImageView>(R.id.home_icon)

        img_view.setOnClickListener {
            val imgIntent = Intent(this, MainActivity::class.java)
            startActivity(imgIntent)
        }
//code for signout or logout fromt he app
        findViewById<ImageView>(R.id.you_icon_parent).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        findViewById<ImageView>(R.id.setting_icon_parent).setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
        //        to display name of email user in nav bar code
        val navNameTextView = findViewById<TextView>(R.id.nav_name)
        val currentuser = FirebaseAuth.getInstance().currentUser
        if (currentuser != null) {

            if (currentuser.displayName.isNullOrEmpty()) {
                navNameTextView.text = "${currentuser.displayName}"
            } else {
                val email = currentuser.email
                if (email != null && email.contains("@"))
                    navNameTextView.text = "${email.substringBefore("@")}"

            }
        } else {
            navNameTextView.text = "Hello Guest"
        }
//        }:
//        display code ends here
        // This part was missing. It fetches the list of children for the logged-in parent.
        childREDb.child(parentEmail_encode).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Clear previous views if any, to avoid duplicates
                childrenContainer.removeAllViews()
                if (!snapshot.exists()) {
                    Toast.makeText(this@ParentViewActivity, "No children found for this account.", Toast.LENGTH_LONG).show()
                    return
                }
                for (childSnapshot in snapshot.children) {
                    val childInfoRE = childSnapshot.getValue(ChildRE::class.java)
                    if (childInfoRE != null) {
                        // For each child found, fetch their treatment plan
                        // The ChildInfo parameter is not used in your fetch function, so we can pass a dummy object.
                        fetchTreatmentPlanForChild(childInfoRE, ChildInfo())
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ParentViewActivity, "Failed to load children list: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
        // *** END: ADDED CODE ***

    }//oncreate function ends here

    private fun fetchTreatmentPlanForChild(childInfo: ChildRE,childtreatement: ChildInfo) {
        // Assuming the treatment plan is stored under the parent's email and then the child's ID/name
        // You might need to adjust the path based on your exact database structure
        val parentEmail = currentUser?.email
        if (parentEmail == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val parentEmailEncode = parentEmail.replace(".", ",")
        val childKey =
            childInfo.child_name // Or child_id if you have one. This MUST match the key in the "children" node.

        //db names initilizae
        val childRE = childREDb.child(parentEmailEncode).child(childKey)

        val treatmentPlan = treatmentPlanDb.child(parentEmailEncode).child(childKey)
//        initilizeation ends here

        childRE.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val planTextRE = snapshot.getValue(ChildRE::class.java)
                //new neset database access
                treatmentPlan.addListenerForSingleValueEvent(object : ValueEventListener {
                    var planTexttreatement: ChildInfo? = null

                    override fun onDataChange(snapshot: DataSnapshot) {

                        val planTexttreatement = snapshot.getValue(ChildInfo::class.java)
//                    val treatmentPlan = planText?.guardian_name ?: "No gardian name  set"

                        // Add the card view with updated info
                        addChildCardToView(

                            planTextRE ?: ChildRE(
                                child_name = "Not defined",
                                child_age = 0,
                                child_gender = "Not defined",
                                parent_email = "Not defined",
                                guardian_name = "Not defined"
                            ),
                            planTexttreatement ?: ChildInfo(
                                session_type = "Not defined",
                                therapist_name = "Not defined",
                                treatment_goal = "Not defined",
                                planned_activities = "Not defined",
                                progress_note = "Not defined"
                            ),
                            "Plan not available"
                        )
                    }

                    override fun onCancelled(error: DatabaseError) {
                        addChildCardToView(
                            ChildRE(child_name = "Unknown"),

                            planTexttreatement ?: ChildInfo(),
                            "Plan not available"
                        )
                        Toast.makeText(
                            this@ParentViewActivity,
                            "Could not load treatment plan for this child",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@ParentViewActivity,
                    "Failed to load child info",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
    private fun addChildCardToView(child_detail: ChildRE,child_treatment:ChildInfo,treatmentPlan:String) {
        val inflater = LayoutInflater.from(this)
        // Ensure this is the correct layout file for your card
        val childCardView = inflater.inflate(R.layout.child_view_test_activity, childrenContainer, false)

        // Find the TextViews inside the NEWLY inflated card
        val childNameTextView = childCardView.findViewById<TextView>(R.id.child_name)
        val therapistNameTextView = childCardView.findViewById<TextView>(R.id.name_therapist)
        val childAgeTextView = childCardView.findViewById<TextView>(R.id.child_age) // **ADD THIS TEXTVIEW TO YOUR LAYOUT**
        val childSessionTextView = childCardView.findViewById<TextView>(R.id.child_session) // **ADD THIS TEXTVIEW TO YOUR LAYOUT**
        val treatmentGoalTextView = childCardView.findViewById<TextView>(R.id.treatment_goal) // **ADD THIS TEXTVIEW TO YOUR LAYOUT**
        val PlannedActivitiesTextView = childCardView.findViewById<TextView>(R.id.planned_activities) // **ADD THIS TEXTVIEW TO YOUR LAYOUT**
        val progressNoteTextView = childCardView.findViewById<TextView>(R.id.progress_note) // **ADD THIS TEXTVIEW TO YOUR LAYOUT**
        val treatmentPlanTextView = childCardView.findViewById<TextView>(R.id.treatment_plan_text) // **ADD THIS TEXTVIEW TO YOUR LAYOUT**

        // Set the data for this specific child
        childNameTextView.text = child_detail.child_name
        therapistNameTextView.text = "${child_treatment.therapist_name}"
        childAgeTextView.text = "Age: ${child_detail.child_age}"
        childSessionTextView.text = "Session Type: ${child_treatment.session_type}"
        treatmentGoalTextView.text = "Goal: ${child_treatment.treatment_goal}"
        progressNoteTextView.text = "Note: ${child_treatment.progress_note}"
        PlannedActivitiesTextView.text = "Activities: ${child_treatment.planned_activities}"

        // Display the treatment plan if it was found
//        if (plan != null) {
        treatmentPlanTextView.text = "Plan: $treatmentPlan"//        } else {
//            treatmentPlanTextView.text = "Plan: Not available"
//        }

        // Add the new card to the on-screen container
        childrenContainer.addView(childCardView)
    }
    } //main function ends here




