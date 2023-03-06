package com.example.focal

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.focal.databinding.FragmentGoalBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class GoalFragment : Fragment() {

    private var _binding: FragmentGoalBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var goalList : MutableList<Goal> = mutableListOf()
    private lateinit var database : DatabaseReference
    private lateinit var userID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userID = requireArguments().getString("userID")!!


        database = FirebaseDatabase.getInstance().getReference("Goals")
        database.child(userID).get().addOnSuccessListener {
            it.children.forEach {
                it.children.forEach {
                    val goal = it.getValue(Goal::class.java)
                    Log.e("Goal converted", goal.toString())
                    goalList.add(goal!!)
                }
            }
        }
    }
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

        binding.buttonAddGoal.setOnClickListener {
            findNavController().navigate(com.example.focal.R.id.action_GoalFragment_to_addGoalFragment, Bundle().apply {
                putString("userID", userID)
            })
        }
        val fileService = FileService(requireActivity())
        val userID = requireArguments().getString("userID")!!
        val goals = fileService.getGoalsByUserID(userID)
        val fragManager = parentFragmentManager
        val trans = fragManager.beginTransaction()
        var index= 0
        GlobalScope.launch {
        suspend {
            delay(100)
            Log.e("GoalList",goalList.count().toString())
            for(goal in goalList){
                Log.e("Goal Fragment",binding.goalContainer.id.toString())
                trans.add(binding.goalContainer.id,GoalTemplateFragment(goal))
                trans.addToBackStack(null)
            }
            trans.commit()
        }.invoke()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}