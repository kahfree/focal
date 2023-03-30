package com.example.focal

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.example.focal.databinding.FragmentPostExerciseDashboardBinding
import com.example.focal.databinding.FragmentSquatBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PostExerciseDashboard.newInstance] factory method to
 * create an instance of this fragment.
 */
class PostExerciseDashboard : Fragment() {
    private var TAG = "PostExerciseDashboardFragment"
    private var _fragmentDashboardBinding: FragmentPostExerciseDashboardBinding? = null
    private lateinit var database : DatabaseReference
    private val fragmentDashboardBinding
        get() = _fragmentDashboardBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragmentDashboardBinding = FragmentPostExerciseDashboardBinding.inflate(inflater, container, false)
        return fragmentDashboardBinding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val FileService = FileService(requireActivity())
        val feedbackToGive = requireArguments().getSerializable("feedbackToGive") as HashMap<*, *>
        val userID = requireArguments().getString("userID").toString()
        val exercise = requireArguments().getString("exercise")
        val statOne =  String.format("%.2f", requireArguments().getFloat("maxDepth"))
        val quality = String.format("%.2f", requireArguments().getFloat("exerciseQuality"))
        val attemptFeedback = feedbackToGive.keys.joinToString(",")
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")

        val previousAttempts = fragmentDashboardBinding.textViewPreviousAttempts
        activity?.runOnUiThread {
            fragmentDashboardBinding.textViewMaxDepth.text = statOne + "°"
            fragmentDashboardBinding.textViewExerciseQuality.text = quality + "%"
            fragmentDashboardBinding.textViewFeedback.text =
                feedbackToGive.entries.joinToString("\n")
            var attemptText = ""

        }

        FocalDB.getGoals(userID, exercise!!){goals ->
            goals?.forEach {
                val goal: Goal = it!!
                if(it.title == "Max Depth"){
                    Log.e("Goal updater","In the max depth thing")
                    if (requireArguments().getFloat("maxDepth") <= goal.current!!) {
                        FocalDB.updateGoalProgress(userID, requireArguments().getFloat("maxDepth"),goal){
                            Log.e("Updated Goal",it!!)
                        }
                    }
                }else if (it.title == "Quality"){
                    Log.e("Goal updater","In the quality thing")
                    if (requireArguments().getFloat("exerciseQuality") >= goal.current!!) {
                        FocalDB.updateGoalProgress(userID, requireArguments().getFloat("maxDepth"),goal){
                            Log.e("Updated Goal",it!!)
                        }
                    }
                }
            }
        }

        val attemptTimestamp = LocalDateTime.now().format(formatter)
        val newAttempt = Attempt(exercise, statOne.toFloat(),quality.toFloat(),attemptFeedback)
        
        FocalDB.addAttempt(userID,newAttempt, attemptTimestamp){
            Log.e("Add Attempt","$it")
        }
    }
}