package com.example.aplikasi_zulham.View

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.example.aplikasi_zulham.R
import com.example.aplikasi_zulham.databinding.FragmentAdminBinding

/**
 * A simple [Fragment] subclass.
 * Use the [Admin.newInstance] factory method to
 * create an instance of this fragment.
 */
class Admin : Fragment() {


    private var _binding : FragmentAdminBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAdminBinding.inflate(inflater,container,false)


        return _binding!!.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    companion object {
        private const val TAG = "Admin"
    }
}