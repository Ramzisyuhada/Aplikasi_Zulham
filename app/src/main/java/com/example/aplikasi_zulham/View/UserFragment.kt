package com.example.aplikasi_zulham.View

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.aplikasi_zulham.Controller.UsersController
import com.example.aplikasi_zulham.Profile

import com.example.aplikasi_zulham.R
import com.example.aplikasi_zulham.TentangAplikasi
import com.example.aplikasi_zulham.View.LoginActivity
import com.example.aplikasi_zulham.databinding.FragmentHomeBinding
import com.example.aplikasi_zulham.databinding.FragmentUserBinding
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [UserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentUserBinding.inflate(inflater,container,false)

        binding.cardprofile.setOnClickListener {

            replaceFragment(Profile())

        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val Controller = UsersController()
        val prefs = requireContext().getSharedPreferences("user_prefs", MODE_PRIVATE)
        val token = prefs.getString("token", null)
        binding.cardlogout.setOnClickListener {
            lifecycleScope.launch {
                Toast.makeText(requireContext(), token.toString(), Toast.LENGTH_SHORT).show()

               val kondisi =  Controller.Logout(token.toString())
                if (kondisi){
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()

                }else{

                    Toast.makeText(requireContext(), "Token tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.tentangaplikasi.setOnClickListener {
            replaceFragment(TentangAplikasi())
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
         * @return A new instance of fragment UserFragment.
         */
        // TODO: Rename and change types and number of parameters


    }
}