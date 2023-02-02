package com.example.focal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.focal.databinding.FragmentGoalBinding
import com.example.focal.databinding.FragmentPostExerciseDashboardBinding
import com.example.focal.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private var _fragmentProfileBinding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val fragmentProfileBinding get() = _fragmentProfileBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _fragmentProfileBinding = FragmentProfileBinding.inflate(inflater, container, false)
        return fragmentProfileBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userID = requireArguments().getInt("userID")
        val userText = fragmentProfileBinding.textViewUserProfile
        val userAttempts = fragmentProfileBinding.textViewUserAttempts
        val FileService = FileService(requireActivity())
        val user = FileService.getUserByID(userID)
        val attempts = FileService.getAttemptsByUserID(userID).map { it -> it.display() }.joinToString("\n\n")

        requireActivity().runOnUiThread {
            userText.text = user.toString()
            userAttempts.text = attempts
        }
    }
}