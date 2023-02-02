package com.example.focal

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.example.focal.databinding.FragmentPostExerciseDashboardBinding
import com.example.focal.databinding.FragmentSquatBinding
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
        val userID = requireArguments().getInt("userID")
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
            for(attempt in FileService.getAttemptsByUserIDAndExercise(userID,exercise!!)){

                attemptText += attempt.display() + "\n\n"
            }
            previousAttempts.text = attemptText
        }

        FileService.logGoals()
        val goalList = FileService.readGoals()
        if(goalList[0].current > requireArguments().getFloat("maxDepth")) {
            FileService.updateGoalProgress(requireArguments().getFloat("maxDepth"))
            if(requireArguments().getFloat("maxDepth") <= goalList[0].goal)
                FileService.updateGoalStatus("Complete")
//            else if (LocalDate.now() >= LocalDate.parse(goalList[0].deadline))
//                FileService.updateGoalStatus("Expired")
        }

        val newAttempt = Attempt(FileService.getNumRowsAttempts()+1,userID,exercise!!,
            LocalDateTime.now().format(formatter),statOne.toFloat(),quality.toFloat(),attemptFeedback)
        FileService.addAttempt(newAttempt)
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