package com.example.yscdisto

interface DistoCommandManager {
    /**
     * Disto D5 장비에 측정 시작 명령을 전송합니다.
     * @param commandData 전송할 명령 바이트 배열 (Disto D5 문서 참조)
     * @return 명령 전송 성공 여부
     */
    fun sendMeasurementCommand(commandData: ByteArray): Boolean

    // 향후 연결 상태 등을 확인할 메서드도 여기에 추가 가능
    fun isDistoConnected(): Boolean
}