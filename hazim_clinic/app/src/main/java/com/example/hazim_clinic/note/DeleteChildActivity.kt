package com.example.hazim_clinic.note

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hazim_clinic.R
import com.example.hazim_clinic.model.ChildInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import android.widget.AutoCompleteTextView
import com.example.hazim_clinic.model.ChildRE
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class DeleteChildActivity: AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()

    private val database= FirebaseDatabase.getInstance().reference.child("childrenRE")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.delete_child_activity)
        val etParentEmail = findViewById<AutoCompleteTextView>(R.id.et_parent_email)
        val etName = findViewById<AutoCompleteTextView>(R.id.et_name)

        val parentEmailList=mutableListOf<String>()
        val childNameList=mutableListOf<String>()
        val parentEmailAdapter= ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line,parentEmailList)
        val childEmailAdapter= ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, childNameList)

        etParentEmail.setAdapter(parentEmailAdapter)
        etName.setAdapter(childEmailAdapter)

        // Fetch parent emails for the dropdown
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                parentEmailList.clear()
                for (parentSnapshot in snapshot.children) {
                    val email = parentSnapshot.key?.replace(",", ".")
                    if (email != null) {
                        parentEmailList.add(email)
                    }
                }
                parentEmailAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DeleteChildActivity, "Failed to load parent emails.", Toast.LENGTH_SHORT).show()
            }
        })
        // --- End of Autocomplete Setup ---

        findViewById<Button>(R.id.btn_update).setOnClickListener{
            val child_name=etName.text.toString()
            val parent_email = etParentEmail.text.toString().trim()
            val parent_encoded_email=parent_email.replace(".",",")

            if(parent_encoded_email.isEmpty()){
                Toast.makeText(this,"Enter Parent Email ID to delete",Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }





            database.child(parent_encoded_email).removeValue()
                .addOnSuccessListener{
                    Toast.makeText(this,"Deleted Successfully",Toast.LENGTH_SHORT).show()
                    etParentEmail.text.clear()
                    etName.text.clear()
//                    etAge
//                    etGender
//                    etGardianName
                    finish()
                }

                .addOnFailureListener{
                    Toast.makeText(this,"Delete Failed",Toast.LENGTH_SHORT).show()
                }

        }


    }//oncreate function ends here
}