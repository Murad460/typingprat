package com.example.hazim_clinic.note

import androidx.lifecycle.ViewModel
import com.example.hazim_clinic.model.ChildInfo
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class NoteViewModel: ViewModel() {
    private val firebasedatabase = FirebaseDatabase.getInstance().reference
    private val firestore= FirebaseStorage.getInstance()
    var note=mutableListOf<ChildInfo>()
        private set

}