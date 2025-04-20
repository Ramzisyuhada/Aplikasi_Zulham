package com.example.aplikasi_zulham

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.aplikasi_zulham.databinding.FragmentHomeBinding
import androidx.appcompat.app.AlertDialog
import com.example.aplikasi_zulham.View.HomeFragment
import com.example.aplikasi_zulham.databinding.FragmentPembersihanUmumBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PembersihanUmum.newInstance] factory method to
 * create an instance of this fragment.
 */
class PembersihanUmum : Fragment() {


    private var _binding: FragmentPembersihanUmumBinding? = null
    private val binding get() = _binding!!


    // TODO: Rename and change types of parameters
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
        _binding = FragmentPembersihanUmumBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(com.example.aplikasi_zulham.R.id.NavButton)
        bottomNav.visibility = View.GONE
        binding.bar5.progress = 80
        val text = requireActivity().findViewById<TextView>(com.example.aplikasi_zulham.R.id.head)
        text.text = "SKOR"
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.card_review, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()


        binding.ButtonUlasanID.setOnClickListener {
            val batalbutton = dialogView.findViewById<Button>(R.id.DialogBtnClose1)
            val LanjutButton = dialogView.findViewById<Button>(R.id.LanjutBtnID)
            val dialogSucces = LayoutInflater.from(requireContext()).inflate(R.layout.alertdialog_succes_ulasan, null)
            val dialog1 = AlertDialog.Builder(requireContext())
                .setView(dialogSucces)
                .create()

            LanjutButton.setOnClickListener {

                dialog1.show()
                val buttonclose = dialogSucces.findViewById<Button>(R.id.ok)
                buttonclose.setOnClickListener {
                    dialog1.dismiss()
                    replaceFragment(HomeFragment())

                }
                dialog.dismiss()
            }
            batalbutton.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }


    }
    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.Frame, fragment)

            .addToBackStack(null)
            .commit()
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PembersihanUmum.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PembersihanUmum().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}