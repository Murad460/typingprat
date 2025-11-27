package com.example.hazim_clinic.note

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.hazim_clinic.R
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import android.widget.LinearLayout
import android.widget.Toast
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity: AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    private val doctorsDb = FirebaseDatabase.getInstance().reference.child("Doctor")
    private val parentsDb = FirebaseDatabase.getInstance().reference.child("childrenRE") // Assuming a 'parents' node

    private val db = FirebaseFirestore.getInstance() // Firestore instance
    private val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
//        AuthUI.IdpConfig.PhoneBuilder().build(),//
//        AuthUI.IdpConfig.GoogleBuilder().build(),
//        AuthUI.IdpConfig.FacebookBuilder().build(),
//        AuthUI.IdpConfig.TwitterBuilder().build(),
    )
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        this.onSignInResult(res)
    }
    /**
     * Checks the user's email against the admin, doctors, and parents databases to determine their role
     * and redirect them accordingly. If unauthorized, the user is signed out.
     */
    private fun checkIfParent(encodedEmail: String) {
        parentsDb.child(encodedEmail).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(parentSnapshot: DataSnapshot) {
                if (parentSnapshot.exists()) {
                    // Email found in the parents DB, proceed with login
                    Toast.makeText(this@LoginActivity, "Parent verified. Redirecting...", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@LoginActivity, ParentViewActivity::class.java))
                    finish()
                } else {
                    // Email not found in either DB. Deny access.
// 4. Unauthorized User: Email not found in any database.
                    Toast.makeText(this@LoginActivity, "Access Denied. Your email is not authorized.", Toast.LENGTH_LONG).show()
                    // Sign the user out as they are not permitted to use the app.
                    AuthUI.getInstance().signOut(this@LoginActivity)                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
                Toast.makeText(this@LoginActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this@LoginActivity, "Doctor verified. Redirecting...", Toast.LENGTH_SHORT).show()
                     startActivity(Intent(this@LoginActivity, DoctorViewActivity::class.java)) // TODO: Change to your Doctor Activity
                    finish()
                } else {
                    // Not a doctor, now check if they are a parent.
                    checkIfParent(encodedEmail)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@LoginActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                auth.signOut() // Sign out on error
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        if (auth.currentUser != null) {
            Toast.makeText(this, "Welcome back! Verifying role...", Toast.LENGTH_SHORT).show()
            verifyUserRoleAndRedirect(auth.currentUser?.email)
        } else {
//            findViewById<Button>(R.id.btn_login).setOnClickListener {
//                launchSignInFlow()
            }
            // Set up listeners for the three login buttons
            findViewById<LinearLayout>(R.id.as_parent_login).setOnClickListener {
               startActivity(Intent(this, ParentSignUpActivity::class.java))
//                            launchSignInFlow()
            }
            findViewById<LinearLayout>(R.id.as_doctor_login).setOnClickListener {
                     startActivity(Intent(this, DoctorAdminSignUpActivity::class.java))
            //                launchSignInFlow()
            }
            findViewById<LinearLayout>(R.id.as_admin_login).setOnClickListener {
                     startActivity(Intent(this, DoctorAdminSignUpActivity::class.java))
            //                launchSignInFlow()
            }
        }

    private fun launchSignInFlow() {

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setIsSmartLockEnabled(true) // ✅ Auto saves and retrieves credentials
            .setLogo(R.drawable.wecare_logo2)
//            .setTheme(R.style.AppTheme)
            .build()
//        signInLauncher.launch(signInIntent)
        signInLauncher.launch(signInIntent)


    }
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {

        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            Toast.makeText(this,"User successfully sign In",Toast.LENGTH_SHORT).show()
            if (auth.currentUser != null) {
                Toast.makeText(this, "Welcome back! Verifying role...", Toast.LENGTH_SHORT).show()
                verifyUserRoleAndRedirect(auth.currentUser?.email)        } else {
            val response = result.idpResponse
            if(response==null){
                Toast.makeText(this,"sign In canceled",Toast.LENGTH_SHORT).show()

            }
            else{
                Toast.makeText(this,"Sign In failed",Toast.LENGTH_SHORT).show()
            }

        }
    }
//    fun redirectBasedOnRole() {
//        val user = auth.currentUser
//        if (user != null && user.email!=null) {
//            val userEmail=user.email!!
//            if (userEmail.equals("admin@murad.com",ignoreCase = true) == true) {
//                Toast.makeText(this, "Redirecting to Admin Dashboard...", Toast.LENGTH_SHORT).show()
//                finish()
//                return@redirectBasedOnRole
////                startActivity(Intent(this, DoctorUpdateActivity::class.java))
//
//            }
//            // For other roles, check the database first
//
//        } else {
//            Toast.makeText(this, "User not Logged in ", Toast.LENGTH_SHORT).show()
//        }
//    }
}}
