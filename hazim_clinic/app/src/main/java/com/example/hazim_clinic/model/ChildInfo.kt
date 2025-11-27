package com.example.hazim_clinic.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties

data class ChildInfo(
    val parent_email:String="",
    val child_name:String="",
    val session_type:String="",
    val therapist_name:String="",
    val treatment_goal:String="",
    val planned_activities:String="",
    val progress_note:String=""


    )
