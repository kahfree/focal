package com.example.focal.fragments.goals

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.focal.FocalDB
import com.example.focal.R
import com.example.focal.databinding.FragmentGoalTemplateBinding
import com.example.focal.models.Goal


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class GoalTemplateFragment(goal: Goal) : Fragment() {

    private var _binding: FragmentGoalTemplateBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private var goal1: Goal = goal
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGoalTemplateBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.complete.setOnClickListener {
            FocalDB.removeGoal(goal1) { Log.e("Remove Goal", "$it") }
            findNavController().navigate(R.id.action_GoalFragment_to_HomeFragment, Bundle().apply {
                putString("userID", goal1.userID)
            })
            Toast.makeText(requireContext(),"Goal has been completed!",Toast.LENGTH_SHORT).show()
        }

        Log.e("Goal Template",goal1.toString())
        val pb = binding.progressBarGoal
        var progress = 0
        if(goal1.title!! == "Max Depth") {
            progress = ((goal1.goal!! / goal1.current!!) * 100).toInt()
        }else {
            progress = ((goal1.current!! / goal1.goal!!) * 100).toInt()
        }
        pb.max = 100
        pb.progress = progress
        binding.textViewGoalPercentage.text = progress.toString() + "%"
        binding.textViewGoalMetric2.text = goal1.current.toString() + " -> " + goal1.goal.toString()
        binding.textViewGoalTitle.text = goal1.title
        binding.textViewGoalDeadline2.text = goal1.deadline
        binding.textViewGoalStatus.text = goal1.status
        binding.infoText.text = goal1.exercise
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}