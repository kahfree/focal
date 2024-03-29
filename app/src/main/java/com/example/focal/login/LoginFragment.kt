package com.example.focal.login

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.focal.*
import com.example.focal.databinding.FragmentLoginBinding
import com.example.focal.models.User

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null

    private lateinit var userToLogin : User
    private  var listOfUsers : MutableList<User?> = mutableListOf()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).actionBar?.title = "Login"
        val loginButton = binding.login
        val loadingProgressBar = binding.loading

        FocalDB.getUsers { users ->
            if(users != null)
                listOfUsers = users
            else
                Log.e("Login","Failed to get list of users")
        }
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

    }

    @SuppressLint("NewApi")
    private fun login(): Boolean{

        for(user in listOfUsers){
            if(user?.email == binding.username.text.toString() && user.password == binding.password.text.toString())
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
            putString("userID", user.userID!!)
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