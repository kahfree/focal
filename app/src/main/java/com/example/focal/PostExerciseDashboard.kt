package com.example.focal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.focal.databinding.FragmentPostExerciseDashboardBinding
import com.example.focal.databinding.FragmentSquatBinding
import java.time.LocalTime

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var feedbackString = requireArguments().getStringArray("feedbackList")?.joinToString("\n")
        var feedbackToGive = requireArguments().getSerializable("feedbackToGive") as HashMap<String, String>

        activity?.runOnUiThread {
            fragmentDashboardBinding.textViewMaxDepth.text =
                requireArguments().getFloat("maxDepth").toString()
            fragmentDashboardBinding.textViewExerciseQuality.text =
                requireArguments().getFloat("exerciseQuality").toString()
            fragmentDashboardBinding.textViewFeedback.text =
                feedbackToGive.entries.joinToString("\n")
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