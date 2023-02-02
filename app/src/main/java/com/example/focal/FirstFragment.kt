package com.example.focal

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.focal.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userID = requireArguments().getInt("userID")

        // Set button listeners
        binding.buttonProfile.setOnClickListener{
            findNavController().navigate(R.id.action_HomeFragment_to_ProfileFragment, Bundle().apply {
                putInt("userID", userID)
            })
        }

        binding.buttonHome.setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_SquatFragment, Bundle().apply {
                putInt("userID", userID)
            })
        }

        binding.buttonGoals.setOnClickListener{
            findNavController().navigate(R.id.action_HomeFragment_to_GoalFragment, Bundle().apply {
                putInt("userID", userID)
            })
        }
        binding.buttonShoulderPress.setOnClickListener{
            findNavController().navigate(R.id.action_HomeFragment_to_shoulderPressFragment, Bundle().apply {
                putInt("userID", userID)
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}