package com.example.focal

import android.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.example.focal.databinding.FragmentGoalBinding
import com.example.focal.ui.login.LoginFragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class GoalFragment : Fragment() {

    private var _binding: FragmentGoalBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var goalList : MutableList<Goal>
    private lateinit var database : DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGoalBinding.inflate(inflater, container, false)

        database = FirebaseDatabase.getInstance().getReference("Goals")
        database.child("U1").get().addOnSuccessListener {
            it.children.forEach {
                val goal = it.getValue(Goal::class.java)
                goalList.add(goal!!)
            }
        }
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

        _binding!!.buttonClickMe.setOnClickListener {



        }
        val fileService = FileService(requireActivity())
        val goals = fileService.getGoalsByUserID("U1")
        val fragManager = parentFragmentManager
        val trans = fragManager.beginTransaction()
        for(goal in goals){
            Log.e("Goal Fragment",goal.toString())
            trans.add(_binding!!.containerView.id,GoalTemplateFragment())
            trans.addToBackStack(null)
        }
        trans.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}