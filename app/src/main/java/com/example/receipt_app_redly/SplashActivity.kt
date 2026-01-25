package com.example.receipt_app_redly

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.receipt_app_redly.R // R 클래스 임포트 추가 확인
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        lifecycleScope.launch {
            delay(5000) // 5초 대기
            // 로그인 화면(MainActivity)으로 전환
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            // 스플래시 화면을 종료하여 뒤로가기 시 다시 나타나지 않게 함
            finish()
        }
    }
}