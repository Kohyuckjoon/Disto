package com.example.yscdisto.ui.disto

import android.R
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.yscdisto.databinding.FragmentSaveDialogBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SaveDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SaveDialogFragment : DialogFragment() {
    private var _binding: FragmentSaveDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSaveDialogBinding.inflate(inflater, container, false)


        binding.mcSaveButton.setOnClickListener {
            val dialog = SaveCompliteFragment()
            dialog.show(parentFragmentManager, "saveComplite")
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        /* Fragment -> Dialog Fragment */
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog?.window?.setBackgroundDrawableResource(R.color.transparent)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}