package com.example.yscdisto.ui.disto

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yscdisto.databinding.FragmentProjectSelectBinding
import com.example.yscdisto.data.database.AppDatabase
import com.example.yscdisto.data.model.ProjectCreate
import com.example.yscdisto.ui.adapter.ProjectAdapter
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
    private val projectList = mutableListOf<ProjectCreate>()
    private lateinit var projectAdapter: ProjectAdapter

    interface OnProjectSelectedListener {
        fun onProjectSelected(project: ProjectCreate)
    }

    private var listener: OnProjectSelectedListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnProjectSelectedListener) {
            listener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProjectSelectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //RecyclerView 셋팅
        projectAdapter = ProjectAdapter(projectList) { projectCreate ->
            Log.e("khj", "선택된 프로젝트(Fragment) : $projectCreate")
            listener?.onProjectSelected(projectCreate)
        }

        binding.rcProjectList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = projectAdapter
        }

        lifecycleScope.launch {
            val projects = AppDatabase.getDatabase(requireContext()).projectDao().getAllProjects()

            /* 프로젝트 리스트 확인 */
            projects.forEach {
                Log.e("DB_SELECT_LIST", it.toString())
            }

            /* Adapter에 데이터 넣기 */
            projectList.clear()
            projectList.addAll(projects)
            projectAdapter.notifyDataSetChanged()

            Log.e("khj", "로그 찍기")
            /* 프로젝트 리스트 존재 여부에 따라 View 처리 */
            if (projects.isEmpty()) {
                binding.rcProjectList.visibility = View.GONE
                binding.mcNoData.visibility = View.VISIBLE
                Log.e("khj", "데이터가 존재하지 않음")
            } else {
                binding.rcProjectList.visibility = View.VISIBLE
                binding.mcNoData.visibility = View.GONE
                Log.e("khj", "데이터 존재")
            }

        }

        /* 테스트 코드 */
//        binding.rcProjectList.setOnClickListener {
//            lifecycleScope.launch {
//                val projects = AppDatabase.getDatabase(requireContext()).projectDao().getAllProjects()
//                projects.forEach {
//                    Log.e("DB_TEST", it.toString())
//                }
//            }
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // 메모리 누수 방지
    }
}