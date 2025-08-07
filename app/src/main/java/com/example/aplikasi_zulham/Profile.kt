package com.example.aplikasi_zulham

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.aplikasi_zulham.Controller.UsersController
import com.example.aplikasi_zulham.Model.User
import com.example.aplikasi_zulham.View.HomeFragment
import com.example.aplikasi_zulham.databinding.FragmentProfileBinding
import com.example.aplikasi_zulham.databinding.FragmentUserBinding
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Profile.newInstance] factory method to
 * create an instance of this fragment.
 */
class Profile : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var email = ""
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
        _binding = FragmentProfileBinding.inflate(inflater,container,false)

        val controller = UsersController()
        val prefs = requireContext().getSharedPreferences("user_prefs", MODE_PRIVATE)
        val token = prefs.getString("token", null)
        val Id = prefs.getInt("id", -1)

        lifecycleScope.launch {
           val Json =    token?.let { controller.GetUserById(it,Id) }
            Log.d("PROFILE",Json.toString())
            val data = Json?.getJSONObject("data")
            data?.let {
                val username = it.getString("username")
                email = it.getString("email")
                Log.d("PROFILE",username)

                binding.namaEditText.setText(username)
            }
        }
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prefs = requireContext().getSharedPreferences("user_prefs", MODE_PRIVATE)

        val controller = UsersController()
        val Id = prefs.getInt("id", -1)
        val token = prefs.getString("token", null)
        binding.ProfileButton.setOnClickListener {

            lifecycleScope.launch {
                val prefs = requireContext().getSharedPreferences("user_prefs", MODE_PRIVATE)
                val token = prefs.getString("token", null)
                val isValid = Validasi()
                if (isValid) {
                    val username = binding.namaEditText.text.toString().trim()
                    val password = binding.passwordEditText.text.toString().trim()
                    val user = User(username, password,email)

                    // Pastikan token dan Id tidak null
                    if (token != null && Id != null) {
                        Log.d("IDUSER",Id.toString())
                        val isUpdate = controller.UpdateUser(token, user, Id)

                        if (isUpdate) {
                            Toast.makeText(
                                requireContext(),
                                "Berhasil update user",
                                Toast.LENGTH_SHORT
                            ).show()
                            replaceFragment(HomeFragment())
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Gagal update user",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Token atau ID tidak tersedia",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.Frame, fragment)
            .addToBackStack(null)
            .commit()
    }
    fun Validasi(): Boolean {
        val username = binding.namaEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()
        val password2 = binding.confirmPasswordEditText.text.toString().trim()

        // Validasi kosong
        if (username.isEmpty()) {
            binding.namaEditText.error = "Username tidak boleh kosong"
            return false
        }

        if (password.isEmpty()) {
            binding.passwordEditText.error = "Password tidak boleh kosong"
            return false
        }

        if (password2.isEmpty()) {
            binding.confirmPasswordEditText.error = "Konfirmasi password tidak boleh kosong"
            return false
        }

        // Validasi panjang password
        if (password.length < 6) {
            binding.passwordEditText.error = "Password minimal 6 karakter"
            return false
        }

        // Validasi konfirmasi password
        if (password != password2) {
            binding.confirmPasswordEditText.error = "Konfirmasi password tidak sama"
            return false
        }

        // Semua valid
        return true
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Profile.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Profile().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}