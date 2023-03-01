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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterFragment : Fragment() {
    private var _fragmentRegisterBinding: FragmentRegisterBinding? = null

    private val fragmentRegisterBinding
        get() = _fragmentRegisterBinding!!

    private var numUsers: Int = 0

    private lateinit var database : DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _fragmentRegisterBinding = FragmentRegisterBinding.inflate(inflater, container, false)
        database = FirebaseDatabase.getInstance().getReference("Users")
        database.get().addOnSuccessListener { numUsers = it.children.count() }
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

            database.child(newUser.userID!!).setValue(newUser).addOnSuccessListener {
                Log.e("Firebase", "User has been registered")
            }


//            FileService(requireActivity()).addUser(newUser!!)
            //Navigate to login screen
            Toast.makeText(requireContext(), "Registration was successful", Toast.LENGTH_SHORT)
            findNavController().navigate(R.id.action_RegisterFragment_to_LoginFragment)

//                Toast.makeText(requireContext(), "Failed to Register", Toast.LENGTH_SHORT)

        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RegisterFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegisterFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}