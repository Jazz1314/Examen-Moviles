package com.crp.wikiAppNew.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.crp.wikiAppNew.R
import com.crp.wikiAppNew.datamodel.PlaceEntity

class TopPlaceAdapter(
    private val context: Context,
    private var places: List<PlaceEntity>,
    private val onItemClick: (PlaceEntity) -> Unit
) : RecyclerView.Adapter<TopPlaceAdapter.TopPlaceViewHolder>() {

    inner class TopPlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val placeName: TextView = itemView.findViewById(R.id.itemTitlePlace)
        private val articleLogo: ImageView = itemView.findViewById(R.id.itemLogoPlace)
        private val placeDesc: TextView = itemView.findViewById(R.id.itemArticleDesc)
        private val visitCount: TextView = itemView.findViewById(R.id.itemVisitCount)

        fun bind(place: PlaceEntity) {
            placeName.text = place.placeName
            placeDesc.text = place.wikiDescription
            visitCount.text = place.visitCount.toString()

            // Load image from thumbnailUrl into articleLogo ImageView
            articleLogo.load(place.thumbnailUrl) {
                crossfade(true)
                error(R.drawable.grey_app_icon)
            }

            // Handle item click
            itemView.setOnClickListener {
                onItemClick(place)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopPlaceViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.most_visited_item, parent, false)
        return TopPlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: TopPlaceViewHolder, position: Int) {
        holder.bind(places[position])
    }

    override fun getItemCount(): Int = places.size

    fun updateList(newPlaces: List<PlaceEntity>) {
        Log.d("TopPlaceAdapter", "Updating list with ${newPlaces.size} items")
        places = newPlaces
        notifyDataSetChanged()
    }
}
