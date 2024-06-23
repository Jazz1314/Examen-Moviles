package com.crp.wikiAppNew

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.crp.wikiAppNew.adapter.PlaceAdapter
import com.crp.wikiAppNew.adapter.TopPlaceAdapter
import com.crp.wikiAppNew.databinding.FragmentMostVisitedBinding
import com.crp.wikiAppNew.databinding.FragmentPlacesBinding
import com.crp.wikiAppNew.viewmodel.PlacesView
import java.net.URLEncoder

class MostVisitedFragment : Fragment() {

    private var _binding: FragmentMostVisitedBinding? = null
    private val binding get() = _binding!!

    private lateinit var placeViewModel: PlacesView
    private lateinit var adapter: TopPlaceAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMostVisitedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        placeViewModel = ViewModelProvider(this).get(PlacesView::class.java)
        adapter = TopPlaceAdapter(requireContext(), mutableListOf()) { place ->
            // Handle click action here, e.g., open Wikipedia article
        }

        binding.listMostVisited.layoutManager = LinearLayoutManager(requireContext())
        binding.listMostVisited.adapter = adapter


        placeViewModel.getTopVisitedPlaces(requireContext()) { places ->
            // Debug log to check data
            Log.d("MostVisitedFragment", "Fetched places: $places")
            adapter.updateList(places)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
