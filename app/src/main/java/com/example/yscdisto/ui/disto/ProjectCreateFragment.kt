package com.example.yscdisto.ui.disto

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.yscdisto.databinding.FragmentProjectCreateBinding
import com.example.yscdisto.data.database.AppDatabase
import com.example.yscdisto.data.model.ProjectCreate
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProjectCreateFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProjectCreateFragment : Fragment() {

    private var _binding: FragmentProjectCreateBinding?= null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProjectCreateBinding.inflate(inflater, container, false)
        // fragment_project_create.xml 레이아웃을 화면에 표시
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mcCreateButton.setOnClickListener {
            val name = binding.etProjectName.text.toString().trim()
            val locationName = binding.etLocationName.text.toString().trim()
            val sheetNumber = binding.etSheetNumber.text.toString().trim()
            val memo = binding.etProjectCreateMemo.text.toString().trim()

            //필수 항목 모두 입력
            if (name.isEmpty() || locationName.isEmpty() || sheetNumber.isEmpty()) {
                Toast.makeText(requireContext(), "필수 항목을 모두 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //DB 저장
            lifecycleScope.launch {
                val project = ProjectCreate(
                    name = name,
                    location = locationName,
                    sheetNumber = sheetNumber,
                    memo = if (memo.isEmpty()) null else memo
                )

                val db = AppDatabase.getDatabase(requireContext())
                db.projectDao().insertProject(project)

                Toast.makeText(requireContext(), "프로젝트가 생성되었습니다.", Toast.LENGTH_SHORT).show()

                parentFragmentManager.popBackStack() //이전 화면으로 이동
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // 메모리 누수 방지
    }
}