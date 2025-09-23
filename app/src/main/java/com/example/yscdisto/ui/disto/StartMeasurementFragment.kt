package com.example.yscdisto.ui.disto

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
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

        val floatingToolbar = binding.llMeasureControll

        // 플로우팅 버튼 드래그를 위한 변수
        var dX = 0f
        var dY = 0f

        /* floating*/
        floatingToolbar.setOnTouchListener { v, event ->
            when (event.action) {
                //손가락을 눌렀을 때 기준 좌표 저장
                MotionEvent.ACTION_DOWN -> {
                    dX = v.x - event.rawX
                    dY = v.y - event.rawY
                    true
                }

                // 움직일 때 View 위치 갱신
                MotionEvent.ACTION_MOVE -> {
                    v.animate()
                        .x(event.rawX + dX)
                        .y(event.rawY + dY)
                        .setDuration(0)
                        .start()
                    true
                }
                MotionEvent.ACTION_UP -> {
                    v.performClick() // 접근성 이벤트 보장
                    true
                }

                else -> false
            }
        }

        binding.mcMeasureResult.setOnClickListener {
            val dialog = SaveDialogFragment()
            dialog.show(parentFragmentManager, "saveDialog")
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}