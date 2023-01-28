package com.example.focal.ui.login

import android.content.Context
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
import com.example.focal.databinding.FragmentLoginBinding


import com.example.focal.R
import com.mongodb.MongoClient
import com.mongodb.MongoException
import org.bson.Document
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter

class LoginFragment : Fragment() {

    private lateinit var loginViewModel: LoginViewModel
    private var _binding: FragmentLoginBinding? = null

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

        loginViewModel.loginFormState.observe(viewLifecycleOwner,
            Observer { loginFormState ->
                if (loginFormState == null) {
                    return@Observer
                }
                loginButton.isEnabled = loginFormState.isDataValid
                loginFormState.usernameError?.let {
                    usernameEditText.error = getString(it)
                }
                loginFormState.passwordError?.let {
                    passwordEditText.error = getString(it)
                }
            })

        loginViewModel.loginResult.observe(viewLifecycleOwner,
            Observer { loginResult ->
                loginResult ?: return@Observer
                loadingProgressBar.visibility = View.GONE
                loginResult.error?.let {
                    showLoginFailed(it)
                }
                loginResult.success?.let {
                    updateUiWithUser(it)
                }
            })

        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                loginViewModel.loginDataChanged(
                    usernameEditText.text.toString(),
                    passwordEditText.text.toString()
                )
            }
        }
        usernameEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.login(
                    usernameEditText.text.toString(),
                    passwordEditText.text.toString()
                )
            }
            false
        }

        loginButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE
            loginViewModel.login(
                usernameEditText.text.toString(),
                passwordEditText.text.toString()
            )
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
        val inputStream = activity?.assets?.open("users.txt")
        val text = inputStream?.bufferedReader().use { it?.readLines() }
        for(line in text!!){
            Log.e("Login Fragment",line + " test")

        }

        val fileOutputStream = requireActivity().openFileOutput("attempts.txt", Context.MODE_PRIVATE)
        val printWriter = PrintWriter(fileOutputStream)
        printWriter.println("1,1,Squat,28-01-2023 12:30:00,40.34,73.56,Knees Out!-Wider Stance!")
        printWriter.println("2,1,Squat,28-01-2023 12:34:00,36.26,77.32,Wider Stance!")
        printWriter.flush()
        printWriter.close()
        fileOutputStream.close()
        inputStream?.close()

        val usersFileInput = requireActivity().openFileInput("users.txt")
        val usersText = usersFileInput.bufferedReader().use { it.readText() }
        Log.e("usersText",usersText)
        val attemptsFileInput = requireActivity().openFileInput("attempts.txt")
        val attemptsText = attemptsFileInput.bufferedReader().use { it.readText() }
        Log.e("attemptsText",attemptsText)
        val goalsFileInput = requireActivity().openFileInput("goals.txt")
        val goalsText = goalsFileInput.bufferedReader().use { it.readText() }
        Log.e("goalsText",goalsText)
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome) + model.displayName
        // TODO : initiate successful logged in experience
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, welcome, Toast.LENGTH_LONG).show()
        findNavController().navigate(R.id.action_LoginFragment_to_HomeFragment)
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}