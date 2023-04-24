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
import com.example.focal.databinding.FragmentAddGoalBinding
import com.example.focal.models.Goal
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class AddGoalFragment : Fragment() {

    private var _binding: FragmentAddGoalBinding? = null

    private val binding get() = _binding!!
    private lateinit var userID: String

    private lateinit var database : DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddGoalBinding.inflate(inflater, container, false)
        userID = requireArguments().getString("userID")!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = FirebaseDatabase.getInstance().getReference("Goals")
        binding.buttonAddGoal.setOnClickListener {
            findNavController().navigate(R.id.action_addGoalFragment_to_GoalFragment, Bundle().apply {
                val exercise: String = binding.exercise.selectedItem.toString()
                val title: String = binding.title.selectedItem.toString()
                val goal: Float = binding.goal.text.toString().toFloat()
                val deadline: String = binding.deadline.text.toString()
                val current = if(title.toString() == "Max Depth") 180f else 0f
                val newGoal: Goal = Goal("G3",userID,exercise,goal,current,deadline,title,"Ongoing")
                FocalDB.addGoal(newGoal) { Log.e("Add Goal", "Goal has been added successfully") }

                putString("userID", userID)
            })
            Toast.makeText(requireContext(),"Goal has been added!",Toast.LENGTH_SHORT).show()
        }
    }

}