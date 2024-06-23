package com.crp.wikiAppNew.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import coil.load
import com.crp.wikiAppNew.datamodel.PlaceEntity
import com.crp.wikiAppNew.R
import com.crp.wikiAppNew.dao.PlaceDao
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PlaceAdapter(
    private val context: Context,
    private var places: List<PlaceEntity>,
    private var onDeleteClickListener: ((PlaceEntity) -> Unit),
    private val onEditClickListener: (PlaceEntity) -> Unit
) : BaseAdapter() {
    private lateinit var placeDao: PlaceDao
    fun updateList(newPlaces: List<PlaceEntity>) {
        places = newPlaces
        notifyDataSetChanged()
    }

    override fun getCount(): Int = places.size

    override fun getItem(position: Int): PlaceEntity = places[position]

    override fun getItemId(position: Int): Long = position.toLong()


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view =LayoutInflater.from(context).inflate(R.layout.places_item, parent, false)



        val placeName = view.findViewById<TextView>(R.id.itemPlaceName)
        val articleLogo = view.findViewById<ImageView>(R.id.itemLogoPlace)
        val articleTitle = view.findViewById<TextView>(R.id.itemTitlePlace)
        val placeDesc = view.findViewById<TextView>(R.id.itemArticleDesc)
        val date = view.findViewById<TextView>(R.id.itemFecha)

        val place = getItem(position)
        articleTitle.text = place.placeName
        placeName.text = place.wikipediaTitle
        placeDesc.text = place.wikiDescription

        articleLogo.load(place.thumbnailUrl){
            crossfade(true)
            error(R.drawable.grey_app_icon)
        }
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formattedDate = dateFormat.format(Date(place.timestamp))

        date.text = formattedDate

        view.setOnClickListener {
            onEditClickListener(place)
        }

        return view
    }

    private fun showDeleteConfirmationDialog(place: PlaceEntity) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Confirmar eliminación")
        builder.setMessage("¿Está seguro que desea eliminar este movimiento?")

        builder.setPositiveButton("Eliminar") { dialog, _ ->
            onDeleteClickListener.invoke(place)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }


}
