package com.example.aplikasi_zulham

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.aplikasi_zulham.View.Aduan
import com.example.aplikasi_zulham.databinding.FragmentListtAduanBinding
import com.example.aplikasi_zulham.databinding.FragmentTambahBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ListtAduan.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListAduan : Fragment() {
    // TODO: Rename and change types of parameters
    private var _binding: FragmentListtAduanBinding? = null
    private val binding get() = _binding!!

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListtAduanBinding.inflate(inflater,container,false)


        for (i in 1..5) {
            val view = inflater.inflate(R.layout.card_aduan, binding.cardContainer, false)
            view.findViewById<ImageView>(R.id.IconAduanID).setImageResource(R.drawable.tahura1)
            binding.cardContainer.addView(view)
            view.setOnClickListener {
                replaceFragment(Aduan())
            }
        }

        return binding.root
    }
    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.Frame, fragment)

            .addToBackStack(null)
            .commit()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(com.example.aplikasi_zulham.R.id.NavButton)
        val text = requireActivity().findViewById<TextView>(com.example.aplikasi_zulham.R.id.head)
        text.text = "Aduan Terbaru"
        bottomNav.visibility = View.GONE
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ListtAduan.
         */
        // TODO: Rename and change types and number of parameters

    }
}