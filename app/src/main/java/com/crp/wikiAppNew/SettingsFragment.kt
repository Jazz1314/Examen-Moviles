package com.crp.wikiAppNew

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import com.crp.wikiAppNew.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicialización de SharedPreferences para almacenar la configuración
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        // Cargar la configuración actual y mostrarla en el EditText o NumberPicker
        val currentFrequentPlacesCount = sharedPreferences.getInt(KEY_FREQUENT_PLACES_COUNT, DEFAULT_FREQUENT_PLACES_COUNT)
        binding.editTextFrequentPlacesCount.setText(currentFrequentPlacesCount.toString())

        // Configurar el botón para guardar la configuración
        binding.btnSaveSettings.setOnClickListener {
            val newFrequentPlacesCount = binding.editTextFrequentPlacesCount.text.toString().toIntOrNull() ?: DEFAULT_FREQUENT_PLACES_COUNT
            saveFrequentPlacesCount(newFrequentPlacesCount)
        }
    }

    private fun saveFrequentPlacesCount(count: Int) {
        with(sharedPreferences.edit()) {
            putInt(KEY_FREQUENT_PLACES_COUNT, count)
            apply()
        }
        Toast.makeText(requireContext(), "Configuración guardada", Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val KEY_FREQUENT_PLACES_COUNT = "key_frequent_places_count"
        const val DEFAULT_FREQUENT_PLACES_COUNT = 5 // Valor por defecto si no se ha configurado antes
    }
}
