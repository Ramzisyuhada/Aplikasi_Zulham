package com.example.aplikasi_zulham

import android.app.Dialog
import android.content.Context.MODE_PRIVATE
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Debug
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import com.example.aplikasi_zulham.databinding.FragmentHomeBinding
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.example.aplikasi_zulham.Controller.RatingController
import com.example.aplikasi_zulham.Model.Rating
import com.example.aplikasi_zulham.View.HomeFragment
import com.example.aplikasi_zulham.databinding.FragmentPembersihanUmumBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

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
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(com.example.aplikasi_zulham.R.id.NavButton)
        bottomNav.visibility = View.GONE
        val prefs = requireContext().getSharedPreferences("user_prefs", MODE_PRIVATE)
        val token = prefs.getString("token", null)
        val Id = prefs.getInt("id", -1)
        val IdDestinasi = prefs.getInt("DestinasiID", -1)

        val Controller = RatingController()


        val dialogloading = Dialog(requireContext())
        dialogloading.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogloading.setContentView(R.layout.loading)
        dialogloading.setCancelable(false)
        dialogloading.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogloading.show()

        lifecycleScope.launch {
            var selesai = 0 ;
            val json = token?.let {
                Controller.GetRatingById(Id,IdDestinasi,it )

            }
            val JsonAllRating = token?.let {
                Controller.GetAllRating(it)
            }
            val dataArrayJson = JsonAllRating?.getJSONArray("data")
            var jumlah =dataArrayJson?.length()

            if (dataArrayJson != null) {
                if (jumlah  == 0 ){
                    dialogloading.dismiss()
                    return@launch


                }
                var count1 = 0
                var count2 = 0
                var count3 = 0
                var count4 = 0
                var count5 = 0
                var jumlahulasan = 0
                for (i in 0 until dataArrayJson.length()) {
                    val obj = dataArrayJson.getJSONObject(i)
                    val value = obj.getInt("value")
                    selesai++
                    when (value) {
                        5 -> count5++
                        4 -> count4++
                        3 -> count3++
                        2 -> count2++
                        1 -> count1++
                    }
                    jumlahulasan++
                }
                val total = count1 + count2 + count3 + count4 + count5
                if (total > 0) {
                    binding.bar5.max = 100
                    binding.bar4.max = 100
                    binding.bar3.max = 100
                    binding.bar2.max = 100
                    binding.bar1.max = 100

                    binding.bar5.progress = (count5 * 100 / total)
                    binding.bar4.progress = (count4 * 100 / total)
                    binding.bar3.progress = (count3 * 100 / total)
                    binding.bar2.progress = (count2 * 100 / total)
                    binding.bar1.progress = (count1 * 100 / total)
                    binding.JumlahUlasan.text = jumlahulasan.toInt().toString()
                    val totalNilai = count1 * 1 + count2 * 2 + count3 * 3 + count4 * 4 + count5 * 5
                    val rataRata = totalNilai.toFloat() / total

                    binding.JumlahRatingID.text = rataRata.toString()
                    binding.ratingBar.rating = rataRata

                }
                if (jumlah == total){
                    dialogloading.dismiss()
                }
            }else{
                dialogloading.dismiss()

            }


        }


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
        val prefs = requireContext().getSharedPreferences("user_prefs", MODE_PRIVATE)
        val token = prefs.getString("token", null)
        val Id = prefs.getInt("id", -1)
        val Username = prefs.getString("username",null)
        val NamaDestinasi = prefs.getString("NamaDestinasi",null)
        val IdDestinasi = prefs.getInt("DestinasiID", -1)
        val Controller = RatingController()

        /*
        * Variable Dialog View
        * */
        val NamaUserText = dialogView.findViewById<TextView>(R.id.NamaUserID)
        val NamaTempat  = dialogView.findViewById<TextView>(R.id.NamaTempatID)

        NamaUserText.text = Username
        NamaTempat.text = NamaDestinasi

        binding.ButtonUlasanID.setOnClickListener {
            val batalbutton = dialogView.findViewById<Button>(R.id.DialogBtnClose1)
            val LanjutButton = dialogView.findViewById<Button>(R.id.LanjutBtnID)
            val dialogSucces = LayoutInflater.from(requireContext()).inflate(R.layout.alertdialog_succes_ulasan, null)
            val dialog1 = AlertDialog.Builder(requireContext())
                .setView(dialogSucces)
                .create()
            var Ratingbar = dialogView.findViewById<RatingBar>(R.id.ratingBar);
            lifecycleScope.launch {
                val Object = Controller.GetRatingById(Id, IdDestinasi, token.toString())
                val dataArray = Object.getJSONArray("data")
                if (dataArray.length() > 0) {
                    Log.d("UMUM",dataArray.toString())

                    val ratingObj = dataArray.getJSONObject(0)
                    val ratingValue = ratingObj.getInt("value")
                    Ratingbar.rating = ratingValue.toFloat()
                }else{
                    LanjutButton.setOnClickListener {
                        val buttonclose = dialogSucces.findViewById<Button>(R.id.ok)
                        val ValueRating = Rating(Ratingbar.rating.toInt())
                        lifecycleScope.launch {

                            val Response = Controller.AddRating(ValueRating, token.toString());
                            if (Response){
                                dialog1.show()
                                buttonclose.setOnClickListener {



                                    dialog1.dismiss()
                                    replaceFragment(HomeFragment())

                                }
                            }else{
                                Toast.makeText(requireContext(), "Gagal", Toast.LENGTH_SHORT).show()
                            }
                        }

                        dialog.dismiss()
                    }
                }
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