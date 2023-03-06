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
        var feedbackToGive = requireArguments().getSerializable("feedbackToGive") as HashMap<String, String>
        val userID = requireArguments().getString("userID")
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
//            for(attempt in FileService.getAttemptsByUserIDAndExercise(userID,exercise!!)){
//
//                attemptText += attempt.display() + "\n\n"
//            }
//            previousAttempts.text = attemptText
        }

//        FileService.logGoals()
//        val goalList = FileService.readGoals()
        val databaseGoal = FirebaseDatabase.getInstance().getReference("Goals").child(userID.toString()).child(exercise!!)
        databaseGoal.get().addOnSuccessListener {
            it.children.forEach {
                var goal: Goal = it.getValue(Goal::class.java)!!
                Log.e("Goal updater","${it.key}")
                if(it.key!! == "Max Depth"){
                    Log.e("Goal updater","In the max depth thing")
                    if (requireArguments().getFloat("maxDepth") <= goal?.current!!) {
                        goal.current = requireArguments().getFloat("maxDepth")
                        val updatedGoal: Goal = Goal(
                            goal.goalID,
                            goal.userID,
                            goal.exercise,
                            goal.goal,
                            requireArguments().getFloat("maxDepth"),
                            goal.deadline,
                            goal.title,
                            goal.status
                        )
                        Log.e("Goal to Update", updatedGoal.toString())
                        databaseGoal.child("Max Depth").setValue(updatedGoal).addOnSuccessListener {
                            Log.e("Goal Updated", "Updated max depth goal")
                        }
                    }
                }else if (it.key!! == "Quality"){
                    Log.e("Goal updater","In the quality thing")
                    if (requireArguments().getFloat("exerciseQuality") >= goal?.current!!) {
                        goal.current = requireArguments().getFloat("exerciseQuality")
                        val updatedGoal: Goal = Goal(
                            goal.goalID,
                            goal.userID,
                            goal.exercise,
                            goal.goal,
                            requireArguments().getFloat("exerciseQuality"),
                            goal.deadline,
                            goal.title,
                            goal.status
                        )
                        Log.e("Goal to Update", updatedGoal.toString())
                        databaseGoal.child("Quality").setValue(updatedGoal).addOnSuccessListener {
                            Log.e("Goal Updated", "Updated exercise quality goal")
                        }
                    }
                }
            }
        }
//        if(goalList[0].current!! > requireArguments().getFloat("maxDepth")) {
//            FileService.updateGoalProgress(requireArguments().getFloat("maxDepth"))
//            if(requireArguments().getFloat("maxDepth") <= goalList[0].goal!!)
//                FileService.updateGoalStatus("Complete")
////            else if (LocalDate.now() >= LocalDate.parse(goalList[0].deadline))
////                FileService.updateGoalStatus("Expired")
//        }
        val attemptTimestamp = LocalDateTime.now().format(formatter)
        val newAttempt = Attempt(exercise!!, statOne.toFloat(),quality.toFloat(),attemptFeedback)
//        FileService.addAttempt(newAttempt)
        database = FirebaseDatabase.getInstance().getReference("Attempts").child(userID.toString())
        database.child(attemptTimestamp).setValue(newAttempt).addOnSuccessListener {
            Log.e("Firebase", "Attempts have been logged")
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PostExerciseDashboard.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PostExerciseDashboard().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}