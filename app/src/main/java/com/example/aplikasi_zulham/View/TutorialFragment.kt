package com.example.aplikasi_zulham.View

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

import com.example.aplikasi_zulham.R
import com.example.aplikasi_zulham.databinding.FragmentTambahBinding
import com.example.aplikasi_zulham.databinding.FragmentTutorialBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * A simple [Fragment] subclass.
 * Use the [TutorialFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TutorialFragment : Fragment() {
    // TODO: Rename and change types of parameters

    private var _binding: FragmentTutorialBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTutorialBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var header = requireActivity().findViewById<TextView>(R.id.head)
        val navbutton = requireActivity().findViewById<BottomNavigationView>(R.id.NavButton)
        navbutton.visibility = View.GONE
        header.text = "Aduan Singkat"
        binding.tambahaduan.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.Frame,TambahFragment()).addToBackStack(null).commit()
        }
    }

    companion object {


    }
}