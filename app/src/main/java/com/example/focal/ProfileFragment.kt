package com.example.focal

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.focal.databinding.FragmentGoalBinding
import com.example.focal.databinding.FragmentPostExerciseDashboardBinding
import com.example.focal.databinding.FragmentProfileBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private var _fragmentProfileBinding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val fragmentProfileBinding get() = _fragmentProfileBinding!!
    private var attemptList : MutableList<Attempt?> = mutableListOf()
    private var user : User? = null
    private lateinit var userID: String

    private lateinit var database : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userID = requireArguments().getString("userID")!!

        FocalDB.getUserByID(userID){ u ->
            if(u != null)
                user = u
            else
                Log.e("User Profile","User object returned null")
        }
        FocalDB.getAttempts(userID){attempts ->
            if(attempts != null)
                attemptList = attempts
            else
                Log.e("User Profile","Failed to get attempts")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _fragmentProfileBinding = FragmentProfileBinding.inflate(inflater, container, false)
        return fragmentProfileBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("ProfileViewCreated","In here")
        val userText = fragmentProfileBinding.textViewUserProfile
        val userAttempts = fragmentProfileBinding.textViewUserAttempts
        val FileService = FileService(requireActivity())

        Log.e("UI Thread","Inside the UI thread")
        GlobalScope.launch {
            Log.e("GlobalScope","Inside the Coroutine")
            Log.e("Suspend","Inside the suspend")
            delay(100)
            Log.e("AttemptList", attemptList.count().toString())
            Log.e("User", user.toString())
            requireActivity().runOnUiThread {
            userText.text = user.toString()
            userAttempts.text = attemptList.map { it -> it?.display() }.joinToString("\n\n")
            }
        }

    }
}