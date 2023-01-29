package com.example.focal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBar
import androidx.navigation.fragment.findNavController
import com.example.focal.databinding.FragmentGoalBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class GoalFragment : Fragment() {

    private var _binding: FragmentGoalBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGoalBinding.inflate(inflater, container, false)
        return binding.root
//        // Inflate the layout for this fragment
//        val view = inflater.inflate(R.layout.fragment_goal, container, false)
//
//        // Get a reference to the LinearLayout
//
//        // Create a new Button
//        val button = Button(requireContext())
//        button.text = "Button added programmatically"
//
//        // Add the Button to the LinearLayout
//        view.addView(button)
//
//        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val goalList = FileService(requireActivity()).readGoals()
        FileService(requireActivity()).logGoals()
        val pb = binding.progressBar1
        pb.max = 100 - goalList[0].goal.toInt()
        pb.progress = 100 - goalList[0].current.toInt()
        binding.textViewGoal1Body.text = goalList[0].current.toString() + "\n/\n" + goalList[0].goal.toString()
        binding.textViewGoal1Title.text = goalList[0].title
        binding.textViewGoal1Deadline.text = goalList[0].deadline
        binding.textViewGoal1Status.text = goalList[0].status
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}