package com.example.aplikasi_zulham.View

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.aplikasi_zulham.Ppt

import com.example.aplikasi_zulham.R
import com.example.aplikasi_zulham.Video
import com.example.aplikasi_zulham.databinding.FragmentEcoTourismeGuideBinding
import com.example.aplikasi_zulham.databinding.FragmentHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * A simple [Fragment] subclass.
 * Use the [EcoTourismeGuide.newInstance] factory method to
 * create an instance of this fragment.
 */
class EcoTourismeGuide : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentEcoTourismeGuideBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEcoTourismeGuideBinding.inflate(inflater, container, false)
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(com.example.aplikasi_zulham.R.id.NavButton)

        bottomNav.visibility = View.GONE
        binding.PptButtonID.setOnClickListener {
            replaceFragment(Ppt())
        }
        binding.VideoButtonID.setOnClickListener {
            replaceFragment(Video())
        }
        return binding.root
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.Frame, fragment)

            .addToBackStack(null)
            .commit()
    }

    companion object {


    }
}