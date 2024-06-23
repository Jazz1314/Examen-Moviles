package com.crp.wikiAppNew.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load

import com.crp.wikiAppNew.databinding.WikiSearchItemBinding
import com.crp.wikiAppNew.datamodel.Page
import java.net.URLEncoder

class WikiAdapter(private val list: List<Page>, val adapterOnClick: (String) -> Unit) :
    RecyclerView.Adapter<WikiAdapter.WikiView>() {

    inner class WikiView(private val binding: WikiSearchItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(page: Page) {
            binding.itemTitle.text = page.title
            binding.itemDesc.text = page.extract
            binding.itemLogo.load(page.thumbnail?.source)

            binding.root.setOnClickListener {
                val encodedTitle = URLEncoder.encode(page.title, "UTF-8")
                val formattedTitle = encodedTitle.replace("+", "_")
                val url = "https://en.wikipedia.org/wiki/$formattedTitle"
                adapterOnClick(url)
            }
        }
    }


    override fun getItemCount() = list.size


    override fun onBindViewHolder(holder: WikiView, position: Int) = holder.bind(list[position])

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = WikiView(
        WikiSearchItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

}