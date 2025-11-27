package com.example.hazim_clinic.note

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.text
import com.example.hazim_clinic.R
import com.google.firebase.Firebase
import android.widget.Button
import android.widget.TextView
import com.example.hazim_clinic.MainActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

class SignInActivity: AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    private val doctorsDb = FirebaseDatabase.getInstance().reference.child("Doctor")
    private val parentsDb = FirebaseDatabase.getInstance().reference.child("childrenRE") // Assuming a 'parents' node

    private val db = FirebaseFirestore.getInstance()
// ...
// Initialize Firebase Auth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sigin_activity)
    val EditTextEmail=findViewById<TextInputEditText>(R.id.email_signin)
    val EditTextPassword=findViewById<TextInputEditText>(R.id.password_signin)
    val button=findViewById<Button>(R.id.btn_signin)
    val textForgotPassword = findViewById<TextView>(R.id.text_forgot_password) // Find the new TextView
    val email = EditTextEmail.text.toString()
    val password = EditTextPassword.text.toString()



// If we get here, both are valid
//    signIn(email, password)

    button.setOnClickListener {
        // Read the current text when the button is clicked
        val email = EditTextEmail.text.toString()
        val password = EditTextPassword.text.toString()

        if (email.isBlank()) {
            EditTextEmail.error = "Enter email"
            return@setOnClickListener // Stop execution
        }
        if (password.isBlank()) {
            EditTextPassword.error = "Enter password"
            return@setOnClickListener // Stop execution
        }

        // Now validate and sign in
        if (email.isNotBlank() && password.isNotBlank()) {
            signIn(email, password)
        } else {
            Toast.makeText(this, "Email and password cannot be empty", Toast.LENGTH_SHORT).show()

        }// over ride function ends here
    }
    // --- ⭐️ Set OnClick Listener for Forgot Password ⭐️ ---
    textForgotPassword.setOnClickListener {
        val email = EditTextEmail.text.toString().trim()
        if (email.isBlank()) {
            EditTextEmail.error = "Please enter your email to reset password"
            Toast.makeText(this, "Enter your email first", Toast.LENGTH_SHORT).show()
            return@setOnClickListener
        }

        // Send password reset email via Firebase
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Password reset email sent to $email", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Failed to send reset email: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }}

        private fun signIn(email: String, password: String) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        // For example, you might want to start a new activity
                        Toast.makeText(baseContext, "Authentication successful.", Toast.LENGTH_SHORT).show()
                        verifyUserRoleAndRedirect(email)
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            baseContext,
                            "Authentication failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        } // signIn function ends here

    // ✅ CORRECT: Placed outside signIn, inside the class


    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))        }
    }




    private fun Signout(){
        Firebase.auth.signOut()
    }
    private fun checkIfParent(encodedEmail: String) {
        parentsDb.child(encodedEmail).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(parentSnapshot: DataSnapshot) {
                if (parentSnapshot.exists()) {
                    // Email found in the parents DB, proceed with login
                    Toast.makeText(this@SignInActivity, "Parent verified. Redirecting...", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@SignInActivity, ParentViewActivity::class.java))
                    finish()
                } else {
                    // Email not found in either DB. Deny access.
// 4. Unauthorized User: Email not found in any database.
                    Toast.makeText(this@SignInActivity, "Access Denied. Your email is not authorized.", Toast.LENGTH_LONG).show()
                    // Sign the user out as they are not permitted to use the app.
                    AuthUI.getInstance().signOut(this@SignInActivity)                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
                Toast.makeText(this@SignInActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                auth.signOut() // Sign out on error
            }
        })
    }
    private fun verifyUserRoleAndRedirect(email: String?) {
        if (email == null) {
            Toast.makeText(this, "Could not get user email. Please try again.", Toast.LENGTH_LONG).show()
            auth.signOut() // Sign out because we can't verify an empty email
            return
        }

        // 1. Check for Admin role (Hardcoded)
        if (email.equals("admin@murad.com", ignoreCase = true)||email.equals("adminwecarecdc@gmail.com", ignoreCase = true)) {
            Toast.makeText(this, "Admin login successful. Redirecting...", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, AdminActivity::class.java)) // TODO: Change to your Admin Activity
            finish()
            return
        }
        val encodedEmail = email.replace(".", ",")

        // 2. Check if the user is a Doctor
        doctorsDb.child(encodedEmail).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // It's a doctor. Redirect to doctor's view.
                    Toast.makeText(this@SignInActivity, "Doctor verified. Redirecting...", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@SignInActivity, DoctorViewActivity::class.java)) // TODO: Change to your Doctor Activity
                    finish()
                } else {
                    // Not a doctor, now check if they are a parent.
                    checkIfParent(encodedEmail)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SignInActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                auth.signOut() // Sign out on error
            }
        })
    }

}
