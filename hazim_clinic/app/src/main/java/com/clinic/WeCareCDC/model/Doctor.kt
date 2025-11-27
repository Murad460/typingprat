package com.example.hazim_clinic.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Doctor (
        val name:String="",
        val specialty:String="",
        val experience:String="",
        val email:String="",
        val rating:Float=0.0f,
        val avatarId:String="default_avatar"


)