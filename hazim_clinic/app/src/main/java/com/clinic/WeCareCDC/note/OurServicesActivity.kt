package com.example.hazim_clinic.note

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.hazim_clinic.R

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.text
//import androidx.glance.visibility
import com.example.hazim_clinic.MainActivity
import kotlin.text.lowercase

class OurServicesActivity: AppCompatActivity() {
    private val serviceViews = mutableListOf<ServiceViewInfo>()

    override fun onCreate (savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ourservices)
        findViewById<ImageView>(R.id.back_img).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }


                // --- PART 1: Initialize all service cards and store their info ---
                // This function registers each service card with our search system.
                initializeServiceCard(R.id.service_occupational_therapy, R.id.service_name_1)
                initializeServiceCard(R.id.service_speech_therapy, R.id.service_name_2)
                initializeServiceCard(R.id.service_behavior_therapy, R.id.service_name_3)
                initializeServiceCard(R.id.service_special_therapy, R.id.service_name_4)
                initializeServiceCard(R.id.service_physiotherapy, R.id.service_name_5)
                initializeServiceCard(R.id.service_parentConselling, R.id.service_name_6)
                initializeServiceCard(R.id.psychological_asses, R.id.service_name_7)
        // Add more calls here if you add more services

                // --- PART 2: Set up the SearchView ---
                val searchView = findViewById<SearchView>(R.id.search_view_services)
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                    // This method is called when the user submits the query (e.g., presses enter)
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        filterServices(query)
                        return false // Let the system handle default actions
                    }

                    // This method is called every time the text in the search view changes
                    override fun onQueryTextChange(newText: String?): Boolean {
                        filterServices(newText)
                        return true
                    }
                })
            }

            /**
             * Initializes a single service card, finds its name, and stores its
             * information for filtering.
             */
            private fun initializeServiceCard(layoutId: Int, nameId: Int) {
                val layout = findViewById<LinearLayout>(layoutId)
                val serviceName = findViewById<TextView>(nameId).text.toString()

                // Store the layout and its text data in our list.
                serviceViews.add(ServiceViewInfo(layout, serviceName))

                // You can also set a click listener here if needed.
                // layout.setOnClickListener {
                //     // Handle click, e.g., open a detail screen for this service
                // }
            }

            /**
             * Filters the list of service cards based on the search query.
             */
            private fun filterServices(query: String?) {
                // Use lowercase for a case-insensitive search.
                val searchText = query?.trim()?.lowercase() ?: ""

                for (service in serviceViews) {
                    val serviceName = service.name.lowercase()

                    // If the search text is empty, or if the service name contains the search text, show it.
                    if (searchText.isEmpty() || serviceName.contains(searchText)) {
                        service.layout.visibility = View.VISIBLE
                    } else {
                        // Otherwise, hide the layout.
                        service.layout.visibility = View.GONE
                    }
                }
            }
        }

        /**
         * A simple data class to hold a reference to a service card's layout and its name.
         */
        data class ServiceViewInfo(
            val layout: LinearLayout,
            val name: String
        )

