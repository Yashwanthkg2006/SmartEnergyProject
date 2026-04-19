package com.scem.energy

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private val channelId = "energy_alert"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 🔔 Permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
        }

        // Buttons
        val btnOn = findViewById<Button>(R.id.btnOn)
        val btnOff = findViewById<Button>(R.id.btnOff)
        val btnBill = findViewById<Button>(R.id.btnBill)
        val btnData = findViewById<Button>(R.id.btnData)

        val btnClassroom = findViewById<Button>(R.id.btnClassroom)
        val btnLab = findViewById<Button>(R.id.btnLab)
        val btnLibrary = findViewById<Button>(R.id.btnLibrary)

        // Text
        val tvStatus = findViewById<TextView>(R.id.tvStatus)
        val tvSuggestion = findViewById<TextView>(R.id.tvSuggestion)

        database = FirebaseDatabase.getInstance().getReference("energy")

        createNotificationChannel()

        // Relay ON
        btnOn.setOnClickListener {
            database.child("relay").setValue("ON")
        }

        // Relay OFF
        btnOff.setOnClickListener {
            database.child("relay").setValue("OFF")
        }

        // Navigation
        btnBill.setOnClickListener {
            startActivity(Intent(this, BillActivity::class.java))
        }

        btnData.setOnClickListener {
            startActivity(Intent(this, DataActivity::class.java))
        }

        btnClassroom.setOnClickListener {
            startActivity(Intent(this, ClassroomActivity::class.java))
        }

        btnLab.setOnClickListener {
            startActivity(Intent(this, LabActivity::class.java))
        }

        btnLibrary.setOnClickListener {
            startActivity(Intent(this, LibraryActivity::class.java))
        }

        // Firebase AI logic
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val relay = snapshot.child("relay").getValue(String::class.java) ?: "OFF"
                val power = snapshot.child("power").getValue(Double::class.java) ?: 0.0

                tvStatus.text = "Status: $relay"

                when {
                    power > 500 -> {
                        tvSuggestion.setTextColor(Color.RED)
                        tvSuggestion.text = "⚠ High power! Auto OFF"

                        database.child("relay").setValue("OFF")

                        showNotification("Auto OFF", "High power detected")
                    }

                    power > 100 -> {
                        tvSuggestion.setTextColor(Color.YELLOW)
                        tvSuggestion.text = "Moderate usage ⚡"
                    }

                    else -> {
                        tvSuggestion.setTextColor(Color.GREEN)
                        tvSuggestion.text = "Good saving 👍"
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Energy Alerts",
                NotificationManager.IMPORTANCE_HIGH
            )
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    private fun showNotification(title: String, message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) return
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        NotificationManagerCompat.from(this).notify(1, builder.build())
    }
}