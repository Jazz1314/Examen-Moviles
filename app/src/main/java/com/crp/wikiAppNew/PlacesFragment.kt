package com.crp.wikiAppNew

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.crp.wikiAppNew.adapter.PlaceAdapter
import com.crp.wikiAppNew.dao.PlaceDao
import com.crp.wikiAppNew.database.AppDatabase
import com.crp.wikiAppNew.databinding.FragmentPlacesBinding
import com.crp.wikiAppNew.datamodel.PlaceEntity
import com.crp.wikiAppNew.viewmodel.PlacesView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URLEncoder

class PlacesFragment : Fragment() {

    private var _binding: FragmentPlacesBinding? = null
    private val binding get() = _binding!!

    private lateinit var placeViewModel: PlacesView
    private lateinit var adapter: PlaceAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlacesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        placeViewModel = ViewModelProvider(this).get(PlacesView::class.java)
        adapter = PlaceAdapter(
            requireContext(),
            mutableListOf(),
            { place ->
                placeViewModel.deletePlace(place)
            }, { place ->
                val bundle = Bundle()

                val encodedTitle = URLEncoder.encode(place.wikipediaTitle, "UTF-8")
                val formattedTitle = encodedTitle.replace("+", "_")
                val wikipediaUrl = "https://es.wikipedia.org/wiki/$formattedTitle"
                val TAG = "someFunction"
                Log.e(TAG, "onViewCreated: $wikipediaUrl", )
                bundle.putString("articleTitle", wikipediaUrl)
                openArticle(wikipediaUrl)

            }
        )
        binding.listPlaces.adapter = adapter

        placeViewModel.places.observe(viewLifecycleOwner) { places ->
            if (places != null) {
                adapter.updateList(places)
            }
        }
    }
    private fun openArticle(url: String) {
        val articleFragment = WebViewPlaceFragment.newInstance(url)
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, articleFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
