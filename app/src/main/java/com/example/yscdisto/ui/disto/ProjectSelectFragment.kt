package com.example.yscdisto.ui.disto

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.yscdisto.R
import com.example.yscdisto.databinding.FragmentProjectCreateBinding
import com.example.yscdisto.databinding.FragmentProjectSelectBinding
import com.example.yscdisto.ui.AppDatabase
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProjectSelectFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProjectSelectFragment : Fragment() {
    private var _binding: FragmentProjectSelectBinding?= null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProjectSelectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.projectView.setOnClickListener {
            lifecycleScope.launch {
                val projects = AppDatabase.getDatabase(requireContext()).projectDao().getAllProjects()
                projects.forEach {
                    Log.e("DB_TEST", it.toString())
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // 메모리 누수 방지
    }
}