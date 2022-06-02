package com.example.projektandroid

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.projektandroid.databinding.FragmentFirstBinding
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.util.Log
import com.google.android.material.snackbar.Snackbar

class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }


    private fun logInUser(view: View) {
        val user = User(
            _id = "",
            email = "",
            body = "",
            path = "",
            username = binding.inputUsername.text.toString(),
            password = binding.inputPassword.text.toString()
        )

        val retrofitBuilder = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://projekt-glz.herokuapp.com/")
            .build()
            .create(ApiInterface::class.java)


        val retrofitData = retrofitBuilder.logInUser(user)
        retrofitData.enqueue(object : Callback<User?> {
            override fun onResponse(call: Call<User?>, response: Response<User?>) {
                //Snackbar.make(view, response.message(), Snackbar.LENGTH_SHORT).show() //Successfully logged in
                    if (response.message() == "OK") {
                        Snackbar.make(view, "Successfully logged in", Snackbar.LENGTH_SHORT).show()
                        binding.inputUsername.setText("")
                        binding.inputPassword.setText("")
                        //findNavController().navigate(R.id.action_FirstFragment_to_capturingFragment)
                        findNavController().navigate(R.id.action_FirstFragment_to_faceRecognitionFragment)
                    }
            }

            override fun onFailure(call: Call<User?>, t: Throwable) {
                Log.d("FirstFragment", "On failure" + t.message)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        binding.buttonLogin.setOnClickListener {
            logInUser(view)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}