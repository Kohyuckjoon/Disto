package com.example.yscdisto.ui.disto

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.yscdisto.R
import com.example.yscdisto.databinding.FragmentStartMeasurementBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [StartMeasurementFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StartMeasurementFragment : Fragment() {
    private var _binding : FragmentStartMeasurementBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStartMeasurementBinding.inflate(inflater, container, false)

        val floatingToolbar = binding.ftMeasureBtn

        /* floating*/
        floatingToolbar.setOnClickListener {

        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}