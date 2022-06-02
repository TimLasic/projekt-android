package com.example.projektandroid

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.projektandroid.databinding.FragmentSecondBinding
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    private fun addUser(view: View) {
        val user = User(
            _id = "",
            body = "",
            path = "",
            username = binding.inputUsername.text.toString(),
            password = binding.inputPassword.text.toString(),
            email = binding.inputEmail.text.toString()
        )

        val retrofitBuilder = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://projekt-glz.herokuapp.com/")
            .build()
            .create(ApiInterface::class.java)


        val retrofitData = retrofitBuilder.addUser(user)
        retrofitData.enqueue(object : Callback<User?> {
            override fun onResponse(call: Call<User?>, response: Response<User?>) {
                Snackbar.make(view, response.message(), Snackbar.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_SecondFragment_to_faceRecognitionFragment)
            }

            override fun onFailure(call: Call<User?>, t: Throwable) {
                Log.d("FirstFragment", "On failure" + t.message)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonLogin.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
        binding.buttonCreateLogin.setOnClickListener {
            if (isDataValid()) {
                addUser(view)
                binding.inputUsername.setText("")
                binding.inputPassword.setText("")
                binding.inputEmail.setText("")
            }
        }
    }

    fun isDataValid(): Boolean {
        var ok = true
        if (TextUtils.isEmpty(binding.inputUsername.text.toString())) {
            binding.inputUsername.setError("Empty username")
            ok = false
        }
        if (TextUtils.isEmpty(binding.inputPassword.text.toString())) {
            binding.inputPassword.setError("Empty password")
            ok = false
        }
        if (TextUtils.isEmpty(binding.inputEmail.text.toString())) {
            binding.inputEmail.setError("Empty email")
            ok = false
        }
        return ok
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}