package com.example.projektandroid

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
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
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.io.File
import java.io.IOException

class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    private lateinit var intent : Intent

    private lateinit var app : MyApplication

    private lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        app = activity?.application as MyApplication

        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                //val f: File = File(currentPhotoPath)
                //imageView.setImageURI(Uri.fromFile(f))
                var file : File = File(currentPhotoPath)
                Log.d("PENIS", file.toString())
                upload(file)
            }
        }

        dispatchTakePictureIntent()
    }

    private fun upload(originalFile : File) {

        var userId = RequestBody.create(MultipartBody.FORM, app.userID)

        var fileUri = Uri.fromFile(originalFile)

        var filePart = RequestBody.create(
            MediaType.parse(app.contentResolver.getType(fileUri).toString()),
            originalFile
        )

        var file = MultipartBody.Part.createFormData("image", originalFile.name, filePart)

        val retrofitBuilder = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://projekt-glz.herokuapp.com/")

        var retrofit = retrofitBuilder.build()

        var client = retrofit.create(ApiInterface::class.java)

        var call = client.upload(file, userId)
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                response: Response<ResponseBody?>
            ) {
                var call1 = client.getPyscript(User("", "", app.username, "", "", ""))
                call1.enqueue(object : Callback<ResponseBody?> {
                    override fun onResponse(
                        call: Call<ResponseBody?>,
                        response: Response<ResponseBody?>
                    ) {
                        response.body()?.let {
                            if ("Unlocked" in it.string()) {
                                app.unlocked = true
                                findNavController().navigate(R.id.action_FirstFragment_to_capturingFragment)
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                        Log.d("CapturingFragment", "On failure, pyscript: " + t.message)
                        Toast.makeText(
                            requireContext(),
                            "Not recognized",
                            Toast.LENGTH_LONG
                        ).show()
                        dispatchTakePictureIntent()
                        activityResultLauncher.launch(intent)
                    }
                })


                Toast.makeText(requireContext(), "yeah!", Toast.LENGTH_SHORT).show()
                Log.d("YEAH!", "fileCreated")
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Log.d("CapturingFragment", "On failure, Image: " + t.message)
            }
        })
    }

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


        Log.d("USERNAME: ", user.username)
        val retrofitData = retrofitBuilder.logInUser(user)
        retrofitData.enqueue(object : Callback<User?> {
            override fun onResponse(call: Call<User?>, response: Response<User?>) {
                //Snackbar.make(view, response.message(), Snackbar.LENGTH_SHORT).show() //Successfully logged in
                if (response.message() == "OK") {
                    Snackbar.make(view, "Successfully logged in", Snackbar.LENGTH_SHORT).show()
                    binding.inputUsername.setText("")
                    binding.inputPassword.setText("")

                    app.username = response.body()!!.username
                    app.userID = response.body()!!._id
                    Log.d("login USERNAME:", app.username)
                    Log.d("login id:", app.userID)
                    //findNavController().navigate(R.id.action_FirstFragment_to_capturingFragment)
                    //findNavController().navigate(R.id.action_FirstFragment_to_faceRecognitionFragment)
                    activityResultLauncher.launch(intent)
                }
            }

            override fun onFailure(call: Call<User?>, t: Throwable) {
                Log.d("FirstFragment", "On failure" + t.message)
            }
        })
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val imageFileName = "JPEG_0_"
        val storageDir: File? = app.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
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