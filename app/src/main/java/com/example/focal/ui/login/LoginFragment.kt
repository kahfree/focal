package com.example.focal.ui.login

import android.content.Context
import android.opengl.Visibility
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.focal.FileService
import com.example.focal.databinding.FragmentLoginBinding


import com.example.focal.R
import com.example.focal.User
import com.example.focal.UserClass
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mongodb.MongoClient
import com.mongodb.MongoException
import org.bson.Document
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter

class LoginFragment : Fragment() {

    private lateinit var loginViewModel: LoginViewModel
    private var _binding: FragmentLoginBinding? = null

    private lateinit var userToLogin: User

    private lateinit var database : DatabaseReference
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        (activity as AppCompatActivity).actionBar?.title = "Login"
        val usernameEditText = binding.username
        val passwordEditText = binding.password
        val loginButton = binding.login
        val loadingProgressBar = binding.loading

        loginButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE
            if(login())
                updateUiWithUser(userToLogin)
            else
                showLoginFailed()

        }

        binding.buttonRegister.setOnClickListener {
            findNavController().navigate(R.id.action_LoginFragment_to_RegisterFragment)
        }

//        var mongoClient: MongoClient? = null
//        try {
//            mongoClient = MongoClient("127.0.0.1", 27017)
//            Log.e("Login Fragment","Connection to the db!")
//            val database = mongoClient.getDatabase("focal")
//            Log.e("Login Fragment","Got the database")
//            val collection = database.getCollection("focal")
//            Log.e("Login Fragment","Got the collection")
//            val documents = collection.listIndexes()
//            Log.e("Login Fragment","Found some documents")
//            Log.e("Login Fragment",documents.joinToString("\n"))
//            mongoClient.close()
//            Log.e("Login Fragment","Closed client connection")
//        } catch (e: MongoException) {
//            e.printStackTrace()
//        } finally {
//            mongoClient!!.close()
//        }
//        val inputStream = activity?.assets?.open("users.txt")
//        val text = inputStream?.bufferedReader().use { it?.readLines() }
//        for(line in text!!){
//            Log.e("Login Fragment",line + " test")
//
//        }
//        inputStream?.close()
//
//        val fileOutputStream = requireActivity().openFileOutput("attempts.txt", Context.MODE_PRIVATE)
//        val printWriter = PrintWriter(fileOutputStream)
//        printWriter.println("1,1,Squat,28-01-2023 12:30:00,40.34,73.56,Knees Out!-Wider Stance!")
//        printWriter.println("2,1,Squat,28-01-2023 12:34:00,36.26,77.32,Wider Stance!")
//        printWriter.flush()
//        printWriter.close()
//        fileOutputStream.close()
//
//
//        val usersFileInput = requireActivity().openFileInput("users.txt")
//        val usersText = usersFileInput.bufferedReader().use { it.readText() }
//        Log.e("usersText",usersText)
//        val attemptsFileInput = requireActivity().openFileInput("attempts.txt")
//        val attemptsText = attemptsFileInput.bufferedReader().use { it.readText() }
//        Log.e("attemptsText",attemptsText)
//        val goalsFileInput = requireActivity().openFileInput("goals.txt")
//        val goalsText = goalsFileInput.bufferedReader().use { it.readText() }
//        Log.e("goalsText",goalsText)
//        usersFileInput.close()
//        attemptsFileInput.close()
//        goalsFileInput.close()
        FileService(requireActivity()).resetGoals()
        FileService(requireActivity()).logGoals()
        FileService(requireActivity()).resetUsers()
        FileService(requireActivity()).logUsers()
        FileService(requireActivity()).resetAttempts()
        FileService(requireActivity()).logAttempts()

        database = FirebaseDatabase.getInstance().getReference("Users")
//        val userClass = UserClass("John","Doe","john.doe@gmail.com","johndoe24")
//        database.child("JohnDoe").setValue(userClass).addOnSuccessListener {
//            Log.e("Firebase","User successfully added to Firebase DB!")
//        }.addOnFailureListener {
//            Log.e("Firebase","Failure with Firebase DB :/")
//        }
        database.child("JohnDoe").get().addOnSuccessListener {
            Log.e("Firebase", "Got value ${it.value}")
        }
//        FileService(requireActivity()).resetUsers()
    }

    private fun login(): Boolean{
        val users = FileService(requireActivity()).getUsers()
        for(user in users){
            if(user.email == binding.username.text.toString() && user.password == binding.password.text.toString())
            {
                userToLogin = user
                return true
            }
        }
        return false
    }
    private fun updateUiWithUser(user: User) {
        val welcome = "Welcome ${user.firstname}!"
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, welcome, Toast.LENGTH_LONG).show()
        findNavController().navigate(R.id.action_LoginFragment_to_HomeFragment, Bundle().apply {
            putInt("userID", user.userID)
        })
    }

    private fun showLoginFailed() {
        binding.loading.visibility = View.INVISIBLE
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, "Login failed", Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}