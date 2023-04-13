package com.example.focal.fragments.misc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.focal.R
import com.example.focal.databinding.FragmentFirstBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userID = requireArguments().getString("userID")

        binding.cardSquat.setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_SquatFragment, Bundle().apply {
                putString("userID", userID)
            })
        }
        binding.cardShoulderPress.setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_shoulderPressFragment, Bundle().apply {
                putString("userID", userID)
            })
        }
        binding.cardBicepCurl.setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_bicepCurlFragment, Bundle().apply {
                putString("userID", userID)
            })
        }
        binding.cardProfile.setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_ProfileFragment, Bundle().apply {
                putString("userID", userID)
            })
        }
        binding.cardGoals.setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_GoalFragment, Bundle().apply {
                putString("userID", userID)
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}