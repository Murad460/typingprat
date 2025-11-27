package com.example.hazim_clinic.note


import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hazim_clinic.R
import com.example.hazim_clinic.model.Doctor

class DoctorAdapter(private val doctors: List<Doctor>) :
    RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder>() {

    class DoctorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val doctorNameTextView: TextView = itemView.findViewById(R.id.doctor_name_detail)
        val specialtyTextView: TextView = itemView.findViewById(R.id.specialty_detail)
        val doctorAvatarImageView: ImageView = itemView.findViewById(R.id.doctor_avatar_detail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_doctor_detail, parent, false)
        return DoctorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        val doctor = doctors[position]
        holder.doctorNameTextView.text = doctor.name
        holder.specialtyTextView.text = doctor.specialty

        // You can map the avatarId to a drawable resource
        val avatarResId = when (doctor.avatarId) {
            "doctor_avatar_2" -> R.drawable.doctor_avatar // Example
            else -> R.drawable.doctor_avatar
        }
        holder.doctorAvatarImageView.setImageResource(avatarResId)


        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DoctorDetailActivity::class.java).apply {
                putExtra("doctor_name", doctor.name)
                putExtra("speciality", doctor.specialty)
                putExtra("rating", doctor.rating)
//                putExtra("REVIEW_COUNT", doctor.reviewCount)
                putExtra("doctor_avatar", avatarResId)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = doctors.size
}
