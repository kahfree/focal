package com.example.focal

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.focal.databinding.FragmentRegisterBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking


class RegisterFragment : Fragment() {
    private var _fragmentRegisterBinding: FragmentRegisterBinding? = null

    private val fragmentRegisterBinding
        get() = _fragmentRegisterBinding!!

    private var numUsers: Int = 0
    private lateinit var database : DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _fragmentRegisterBinding = FragmentRegisterBinding.inflate(inflater, container, false)
        database = FirebaseDatabase.getInstance().getReference("Users")
        database.get().addOnSuccessListener { numUsers = it.children.count() }
//        runBlocking {
//            numUsers = FocalDB.getNumUsers()
//            delay(500)
//        }

        return fragmentRegisterBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentRegisterBinding.buttonRegister.setOnClickListener {
            //Create user and add to system
            Log.e("Register Fragment", "Pressed the fucking button!")
            val newUser = User("U" +
                    (numUsers+1),fragmentRegisterBinding.firstname.text.toString(),
                fragmentRegisterBinding.lastname.text.toString(),
                fragmentRegisterBinding.email.text.toString(),
                fragmentRegisterBinding.password.text.toString())

            FocalDB.addUser(newUser)

            //Navigate to login screen
            Toast.makeText(requireContext(), "Registration was successful", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_RegisterFragment_to_LoginFragment)

        }

    }

}