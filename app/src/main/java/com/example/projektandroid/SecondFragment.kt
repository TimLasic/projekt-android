package com.example.projektandroid

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import com.example.projektandroid.databinding.FragmentSecondBinding
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    private lateinit var intent : Intent

    private lateinit var app : MyApplication

    private lateinit var currentPhotoPath: String

    private var counterP : Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        app = activity?.application as MyApplication

        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                //val f: File = File(currentPhotoPath)
                //imageView.setImageURI(Uri.fromFile(f))
            }
        }
    }

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
                //findNavController().navigate(R.id.action_SecondFragment_to_faceRecognitionFragment)

                /*repeat(10) {
                    dispatchTakePictureIntent()
                    activityResultLauncher.launch(intent)
                }*/

                counterP = 0

                findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
            }

            override fun onFailure(call: Call<User?>, t: Throwable) {
                Log.d("FirstFragment", "On failure" + t.message)
            }
        })
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        //val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + counterP.toString() + "_"
        val storageDir: File? = app.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.absolutePath
        return image
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun dispatchTakePictureIntent() {
        intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(app.packageManager)?.also {
                // Create the File where the photo should go
                //val photoFile : File = File(createImageFile().toURI())
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        app,
                        "com.example.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                }
            }
        }
        counterP += 1
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