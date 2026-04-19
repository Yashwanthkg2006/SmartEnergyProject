package com.scem.energy

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.*

class DataActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var chart: LineChart
    private val entries = ArrayList<Entry>()
    private var index = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data)

        val tvVoltage = findViewById<TextView>(R.id.tvVoltage)
        val tvCurrent = findViewById<TextView>(R.id.tvCurrent)
        val tvPower = findViewById<TextView>(R.id.tvPower)
        val btnBack = findViewById<Button>(R.id.btnBack)

        chart = findViewById(R.id.chart)

        database = FirebaseDatabase.getInstance().getReference("energy")

        btnBack.setOnClickListener { finish() }

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val voltage = snapshot.child("voltage").getValue(Double::class.java) ?: 0.0
                val current = snapshot.child("current").getValue(Double::class.java) ?: 0.0
                val power = snapshot.child("power").getValue(Double::class.java) ?: 0.0

                tvVoltage.text = "Voltage: $voltage V"
                tvCurrent.text = "Current: $current A"
                tvPower.text = "Power: $power W"

                // GRAPH UPDATE
                entries.add(Entry(index++, power.toFloat()))

                val dataSet = LineDataSet(entries, "Power Usage")
                val lineData = LineData(dataSet)
                chart.data = lineData
                chart.invalidate()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}