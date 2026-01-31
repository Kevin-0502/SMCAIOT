package com.example.smaciot.sensoresDataa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.smcaiot.R
import com.example.smcaiot.sensoresDataa.OnEstanqueClickListener
import com.squareup.picasso.Picasso


class EstanqueAdapter
    (private val estanquesList: ArrayList<Estanques>,
     private val listener: OnEstanqueClickListener) : RecyclerView.Adapter<EstanqueAdapter.EstanqueViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EstanqueViewHolder {
        val itemView= LayoutInflater.from(parent.context).inflate(R.layout.sensor_item,parent,false)
        return EstanqueViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EstanqueViewHolder, position: Int) {
        val currentItem = estanquesList[position]

        holder.MAC.text=currentItem.MAC
        holder.estanque_name.text=currentItem.nombre
        holder.ciudad.text=currentItem.ubicacion
        Picasso.get().load(currentItem.imagen).into(holder.imagen)
        /*holder.itemView.setOnClickListener {
            listener.onEstanqueClick(currentItem)
        }*/
        holder.itemView.setOnClickListener { view ->
            // Crear y mostrar el PopupMenu
            val popupMenu = PopupMenu(view.context, view)
            popupMenu.menuInflater.inflate(R.menu.menu_charts, popupMenu.menu)

            // Manejar los clics en el menú
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_diary_chart -> {
                        listener.onEstanqueClick(currentItem,1)
                        true
                    }
                    R.id.action_mensual_chart -> {
                        listener.onEstanqueClick(currentItem,2)
                        true
                    }
                    R.id.action_diary_report -> {
                        listener.onEstanqueClick(currentItem,3)
                        true
                    }
                    R.id.action_mensual_report -> {
                        listener.onEstanqueClick(currentItem,4)
                        true
                    }

                    else -> false
                }
            }
            popupMenu.show()
        }

    }

    override fun getItemCount(): Int {
        return estanquesList.size
    }


    class EstanqueViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val MAC:TextView=itemView.findViewById(R.id.mac)
        val estanque_name:TextView=itemView.findViewById(R.id.estanque_name)
        val ciudad:TextView=itemView.findViewById(R.id.city)
        val imagen:ImageView=itemView.findViewById(R.id.imageViewEstanque)
    }
}