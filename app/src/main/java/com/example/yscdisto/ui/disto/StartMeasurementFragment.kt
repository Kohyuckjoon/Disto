package com.example.yscdisto.ui.disto

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import ch.leica.sdk.Devices.Device
import com.example.yscdisto.databinding.FragmentStartMeasurementBinding


/**
 * A simple [Fragment] subclass.
 * Use the [StartMeasurementFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StartMeasurementFragment : Fragment(){
    private var _binding : FragmentStartMeasurementBinding? = null
    private val binding get() = _binding!!


    private var isMeasuring = false
    private val measureHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

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
            dialog.show(parentFragmentManager, "saveComplite")
        }

        binding.mcAutoBtn.setOnClickListener { v ->
            onClickSurveyToggle()
        }



//        binding.mcAutoBtn.setOnClickListener {
//            val startMeasureCommand = byteArrayOf(0x02, 0x52, 0x03)
//
//            if (distManager?.isDistoConnected() == true) {
//                val success = distManager?.sendMeasurementCommand(startMeasureCommand)
//                if (success == true) {
//                    Toast.makeText(context, "Disto D5로 측정 명령을 전송했습니다.", Toast.LENGTH_SHORT).show()
//                } else {
//                    Toast.makeText(context, "명령 전송에 실패했습니다.", Toast.LENGTH_SHORT).show()
//                }
//            } else {
//                Toast.makeText(context, "Disto D5 장비가 연결되지 않았습니다.", Toast.LENGTH_LONG).show()
//            }
//        }

        return binding.root
    }

    private fun onClickSurveyToggle() {
        if (!isMeasuring) {
            startMeasuring()
        } else {
            stopMeasuring(true)
        }
    }

    fun startMeasuring() {
//        isMeasuring = true
//        lastDistance = Double.NaN
//        trendingUp = false
//
//        maxDistance = Double.NEGATIVE_INFINITY
//        maxDistanceUnit = ""
//        maxAngle = Double.NEGATIVE_INFINITY

        if (binding != null) {
            binding.tvDistance.setText("test");
        }
    }

    private fun showToast(msg: String?) {
        if (!isAdded()) return
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    private fun stopMeasuring(showToast: Boolean) {
        if (!isMeasuring) return
        isMeasuring = false
        measureHandler.removeCallbacksAndMessages(null)

        if (showToast) showToast("측정을 중지했습니다.")
    }
}