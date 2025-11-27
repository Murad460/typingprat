package com.example.hazim_clinic.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties

data class ChildRE (
    val child_name:String="",
    val child_age:Int=0,
    val child_gender:String="",
    val parent_email:String="",
    val guardian_name: String=""
)