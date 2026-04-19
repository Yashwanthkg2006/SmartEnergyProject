package com.scem.energy

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class LabActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)

        val tvRoom = findViewById<TextView>(R.id.tvRoom)

        // 🔥 Firebase path for LAB
        database = FirebaseDatabase.getInstance().getReference("energy/lab")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val power = snapshot.child("power").getValue(Double::class.java) ?: 0.0

                val status = when {
                    power > 400 -> "⚠ High Usage"
                    power > 200 -> "⚡ Moderate Usage"
                    else -> "👍 Good Saving"
                }

                tvRoom.text = " Lab\nPower: $power W\n$status"

                if (power > 400) tvRoom.setTextColor(Color.RED)
                else if (power > 200) tvRoom.setTextColor(Color.YELLOW)
                else tvRoom.setTextColor(Color.GREEN)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}