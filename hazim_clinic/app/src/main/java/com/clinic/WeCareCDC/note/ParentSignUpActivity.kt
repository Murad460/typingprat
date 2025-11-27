package com.example.hazim_clinic.note

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hazim_clinic.R
import com.firebase.ui.auth.AuthUI
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

class ParentSignUpActivity: AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    private val doctorsDb = FirebaseDatabase.getInstance().reference.child("Doctor")
    private val parentsDb = FirebaseDatabase.getInstance().reference.child("childrenRE") // Assuming a 'parents' node

    private val db = FirebaseFirestore.getInstance() // Firestore instance
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // You can reuse your login layout if it has the same fields
        setContentView(R.layout.signup_activity)

        // Get references to your UI elements from the XML layout
        val editTextEmail = findViewById<TextInputEditText>(R.id.email_signup)
        val editTextPassword = findViewById<TextInputEditText>(R.id.password_signup)
        val buttonSignUp = findViewById<Button>(R.id.btn_signup) // Assuming you reuse the button
        val text_to_sigin=findViewById<TextView>(R.id.text_go_to_signin)
        // You might want to change the button's text for clarity
        buttonSignUp.text = "Sign Up"
        findViewById<TextView>(R.id.text_go_to_signin).setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
        // Set a click listener on the button
        buttonSignUp.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            // 1. Validate Input
            if (email.isBlank()) {
                editTextEmail.error = "Email cannot be empty"
                return@setOnClickListener // Stop the function
            }
            if (password.length < 6) {
                editTextPassword.error = "Password must be at least 6 characters"
                return@setOnClickListener // Stop the function
            }

            // 2. Call Firebase to create the user
            //  4. Store the password before creating the user
//            currentPasswordForSave = password
            createUser(email, password)
        }
        text_to_sigin.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }
    private fun createUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign up success
//                    saveCredentialsToSmartLock(email)
                    val encodedEmail = email.replace(".", ",")

                    checkIfParent(encodedEmail)
//                    Toast.makeText(
//                        baseContext,
//                        "User created",
//                        Toast.LENGTH_LONG).show()
//                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    // If sign up fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed: ${task.exception?.message}",
                        Toast.LENGTH_LONG, // Show a longer message to display the error
                    ).show()
//                    currentPasswordForSave = null                }
                }
            }}
    /**
     * Checks the user's email against the admin, doctors, and parents databases to determine their role
     * and redirect them accordingly. If unauthorized, the user is signed out.
     */
    private fun checkIfParent(encodedEmail: String) {
        if (encodedEmail == null) {
            Toast.makeText(this, "Could not get user email. Please try again.", Toast.LENGTH_LONG).show()
            auth.signOut() // Sign out because we can't verify an empty email
            return
        }
        parentsDb.child(encodedEmail).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(parentSnapshot: DataSnapshot) {
                if (parentSnapshot.exists()) {
                    // Email found in the parents DB, proceed with login
                    Toast.makeText(this@ParentSignUpActivity, "Parent verified. Redirecting...", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@ParentSignUpActivity, ParentViewActivity::class.java))
                    finish()
                } else {
                    // Email not found in either DB. Deny access.
// 4. Unauthorized User: Email not found in any database.
                    Toast.makeText(this@ParentSignUpActivity, "Access Denied. Your email is not authorized.", Toast.LENGTH_LONG).show()
                    // Sign the user out as they are not permitted to use the app.
                    AuthUI.getInstance().signOut(this@ParentSignUpActivity)                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
                Toast.makeText(this@ParentSignUpActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                auth.signOut() // Sign out on error
            }
        })
    }
//    private fun verifyUserRoleAndRedirect(email: String?) {
//        if (email == null) {
//            Toast.makeText(this, "Could not get user email. Please try again.", Toast.LENGTH_LONG).show()
//            auth.signOut() // Sign out because we can't verify an empty email
//            return
//        }
//
//        // 1. Check for Admin role (Hardcoded)
//        if (email.equals("admin@murad.com", ignoreCase = true)||email.equals("adminwecarecdc@gmail.com", ignoreCase = true)) {
//            Toast.makeText(this, "Admin login successful. Redirecting...", Toast.LENGTH_SHORT).show()
//            startActivity(Intent(this, AdminActivity::class.java)) // TODO: Change to your Admin Activity
//            finish()
//            return
//        }
//        val encodedEmail = email.replace(".", ",")
//
//        // 2. Check if the user is a Doctor
//        doctorsDb.child(encodedEmail).addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (snapshot.exists()) {
//                    // It's a doctor. Redirect to doctor's view.
//                    Toast.makeText(this@SignUpActivity, "Doctor verified. Redirecting...", Toast.LENGTH_SHORT).show()
//                    startActivity(Intent(this@SignUpActivity, DoctorViewActivity::class.java)) // TODO: Change to your Doctor Activity
//                    finish()
//                } else {
//                    // Not a doctor, now check if they are a parent.
//                    checkIfParent(encodedEmail)
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(this@SignUpActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
//                auth.signOut() // Sign out on error
//            }
//        })
//    }

}