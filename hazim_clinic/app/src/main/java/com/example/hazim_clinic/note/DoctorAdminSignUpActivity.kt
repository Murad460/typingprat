package com.example.hazim_clinic.note

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.hazim_clinic.MainActivity // Assuming this is your main screen after login/signup
import com.example.hazim_clinic.R
import com.firebase.ui.auth.AuthUI

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import androidx.credentials.CredentialManager
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.CreateCredentialCancellationException
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch



class DoctorAdminSignUpActivity : AppCompatActivity() {

    // It's good practice to define a TAG for logging
    private val TAG = "SignUpActivity"
    private val auth = FirebaseAuth.getInstance()
    private val doctorsDb = FirebaseDatabase.getInstance().reference.child("Doctor")
    private val parentsDb = FirebaseDatabase.getInstance().reference.child("childrenRE") // Assuming a 'parents' node

    private val db = FirebaseFirestore.getInstance() // Firestore instance
    private var currentPasswordForSave: String? = null

    // 3. Create an ActivityResultLauncher to handle the Smart Lock save dialog
    private val savePasswordLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                Toast.makeText(this, "Credentials saved!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Credentials not saved.", Toast.LENGTH_SHORT).show()
            }
            // Whether saved or not, navigate to the main activity
            navigateToMain()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // You can reuse your login layout if it has the same fields
        setContentView(R.layout.signup_activity)

        // Initialize Firebase Auth




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
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish() // finish SignUpActivity
    }
    private fun saveCredentialWithCredentialManager(email: String, password: String) {
        // launch on lifecycleScope because createCredential uses suspend-style behavior
        lifecycleScope.launch {
            val credentialManager = CredentialManager.create(this@DoctorAdminSignUpActivity)
            val request = CreatePasswordRequest(email, password)

            try {
                // This will display the system-provided "Save password" UI (may be a bottom sheet)
                credentialManager.createCredential( this@DoctorAdminSignUpActivity,request)
                Toast.makeText(this@DoctorAdminSignUpActivity, "Credentials saved securely", Toast.LENGTH_SHORT).show()
            } catch (e: CreateCredentialCancellationException) {
                // User cancelled save
                Toast.makeText(this@DoctorAdminSignUpActivity, "Save cancelled", Toast.LENGTH_SHORT).show()
            } catch (e: CreateCredentialException) {
                // Save not available or failed (autofill conflicts, provider missing, etc.)
                Log.w(TAG, "Credential save failed: ${e.message}")
                Toast.makeText(this@DoctorAdminSignUpActivity, "Could not save credentials", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error saving credential", e)
            } finally {
                // clear in-memory password for safety if you're storing it temporarily
                currentPasswordForSave = null
            }
        }
    }

    private fun createUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign up success
//                    saveCredentialsToSmartLock(email)
                    saveCredentialWithCredentialManager(email,password)

                    verifyUserRoleAndRedirect(email)

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
//    //to check if the user is parent
    private fun checkIfParent(encodedEmail: String) {
        parentsDb.child(encodedEmail).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(parentSnapshot: DataSnapshot) {
                if (parentSnapshot.exists()) {
                    // Email found in the parents DB, proceed with login
                    Toast.makeText(this@DoctorAdminSignUpActivity, "Parent verified. Redirecting...", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@DoctorAdminSignUpActivity, ParentViewActivity::class.java))
                    finish()
                } else {
                    // Email not found in either DB. Deny access.
// 4. Unauthorized User: Email not found in any database.
                    Toast.makeText(this@DoctorAdminSignUpActivity, "Access Denied. Your email is not authorized.", Toast.LENGTH_LONG).show()
                    // Sign the user out as they are not permitted to use the app.
                    AuthUI.getInstance().signOut(this@DoctorAdminSignUpActivity)                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
                Toast.makeText(this@DoctorAdminSignUpActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                auth.signOut() // Sign out on error
            }
        })
    }

    //to verify the user is admin  and redirect to its page
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
                    Toast.makeText(this@DoctorAdminSignUpActivity, "Doctor verified. Redirecting...", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@DoctorAdminSignUpActivity, DoctorViewActivity::class.java)) // TODO: Change to your Doctor Activity
                    finish()
                } else {
                    // Not a doctor, now check if they are a parent.
                    Toast.makeText(this@DoctorAdminSignUpActivity, "Access Denied. Your email is not authorized.", Toast.LENGTH_LONG).show()
//                    checkIfParent(encodedEmail)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DoctorAdminSignUpActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                auth.signOut() // Sign out on error
            }
        })
    }}


