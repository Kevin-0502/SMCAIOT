package com.example.smcaiot

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smaciot.sensoresDataa.EstanqueAdapter
import com.example.smaciot.sensoresDataa.Estanques
import com.example.smcaiot.sensoresDataa.OnEstanqueClickListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity(), OnEstanqueClickListener {

    private lateinit var dbref: DatabaseReference
    private lateinit var estanqueRecyclerView: RecyclerView
    private lateinit var estanqueArrayList: ArrayList<Estanques>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        estanqueRecyclerView = findViewById(R.id.sensor_list)
        estanqueRecyclerView.layoutManager = LinearLayoutManager(this)
        estanqueRecyclerView.setHasFixedSize(true)

        estanqueArrayList = arrayListOf()
        readEstanques()
    }

    private fun readEstanques() {
        dbref = FirebaseDatabase.getInstance().getReference()
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    if (snapshot.exists()) {
                        estanqueArrayList.clear() // Limpia la lista antes de agregar nuevos elementos
                        for (estanqueSnapshot in snapshot.children) {
                            val estanque = estanqueSnapshot.getValue(Estanques::class.java)
                            if (/*estanque!!.activo == */true) {
                                estanqueArrayList.add(estanque!!)
                            }
                        }
                        // Configura el adaptador con la interfaz de clic
                        estanqueRecyclerView.adapter = EstanqueAdapter(estanqueArrayList, this@MainActivity)
                    }
                } catch (e: Exception) {
                    Log.e("Main Activity", "Error en la base ${e.message}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                try {
                    throw error.toException()
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error al leer los datos de Firebase: ${e.message}")
                }
            }
        })
    }

    override fun onEstanqueClick(estanque: Estanques, opc: Int) {

        val intent = Intent(this, ChartActivity::class.java)
        intent.putExtra("ESTANQUE", estanque)
        intent.putExtra("OPCION", opc)
        startActivity(intent)
    }
}