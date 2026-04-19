package com.scem.energy

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class BillActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill)

        val tvEnergy = findViewById<TextView>(R.id.tvEnergy)
        val tvBill = findViewById<TextView>(R.id.tvBill)
        val btnBack = findViewById<Button>(R.id.btnBack)

        database = FirebaseDatabase.getInstance().getReference("energy")

        btnBack.setOnClickListener { finish() }

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val energy = snapshot.child("energyUsed").getValue(Double::class.java) ?: 0.0

                val bill = energy * 5   // ₹5 per unit

                tvEnergy.text = "Energy Used: $energy kWh"
                tvBill.text = "Estimated Bill: ₹$bill"
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}