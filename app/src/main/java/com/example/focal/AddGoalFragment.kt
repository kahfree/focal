package com.example.focal

import android.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.focal.databinding.FragmentAddGoalBinding
import com.example.focal.databinding.FragmentGoalBinding
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
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddGoalBinding.inflate(inflater, container, false)
        userID = requireArguments().getString("userID")!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = FirebaseDatabase.getInstance().getReference("Goals")
        binding.buttonAddGoal.setOnClickListener {
            findNavController().navigate(com.example.focal.R.id.action_addGoalFragment_to_GoalFragment, Bundle().apply {
                val exercise: String = binding.exercise.selectedItem.toString()
                val title: String = binding.title.selectedItem.toString()
                val goal: Float = binding.goal.text.toString().toFloat()
                val deadline: String = binding.deadline.text.toString()
                val newGoal: Goal = Goal("G3",userID,exercise,goal,0f,deadline,title,"Ongoing")
                database.child(userID).child(exercise).child(title).setValue(newGoal)
                putString("userID", userID)
            })
            Toast.makeText(requireContext(),"Goal has been added!",Toast.LENGTH_SHORT).show()
        }
    }

}