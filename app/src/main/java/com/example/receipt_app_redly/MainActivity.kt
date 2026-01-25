package com.example.receipt_app_redly

import android.content.Context // Context 추가
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    // Repository를 통해 로그인 기능을 관리합니다.
    private lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // DB 및 Repository 초기화
        val db = DBHelper(this)
        authRepository = SQLiteAuthRepository(db)

        val edit_id: EditText = findViewById(R.id.editID)
        val edit_pw: EditText = findViewById(R.id.editPW)
        val loginButton: Button = findViewById(R.id.loginButton)
        val signUpButton: Button = findViewById(R.id.signUpButton)

        // 로그인 버튼 클릭 시 처리
        loginButton.setOnClickListener {
            val id = edit_id.text.toString()
            val pw = edit_pw.text.toString()

            if (id.isEmpty() || pw.isEmpty()) {
                Toast.makeText(this, "아이디와 비밀번호를 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Repository 패턴을 사용한 로그인 확인
            authRepository.login(id, pw) { success ->
                if (success) {
                    // [핵심 수정] 로그인 성공 시 SharedPreferences에 현재 아이디 저장
                    // 이렇게 저장해두면 앱의 어느 화면에서든 "누가 로그인했는지" 알 수 있습니다.
                    val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putString("current_user_id", id)
                        apply() // 비동기로 저장
                    }

                    Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()

                    // HomeActivity로 이동
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 회원가입 버튼 클릭 시 이동
        signUpButton.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}