package com.example.receipt_app_redly

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val edit_id: EditText = findViewById(R.id.sign_idEdit)
        val edit_pw: EditText = findViewById(R.id.sign_pwEdit)
        val btnSignUp: Button = findViewById(R.id.btnSignUp)
        val btnLogin: Button = findViewById(R.id.btnLogin)

        // 통합된 DBHelper 선언
        val db = DBHelper(this)

        // 회원가입 버튼 클릭 시
        btnSignUp.setOnClickListener {
            val id = edit_id.text.toString()
            val pw = edit_pw.text.toString()

            if (id.isEmpty() || pw.isEmpty()) {
                Toast.makeText(this, "아이디와 비밀번호를 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // DBHelper의 signup 함수를 직접 호출하여 회원가입 처리
            val success = db.signup(id, pw)

            if (success) {
                Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                // 회원가입 완료 후 로그인 화면으로 돌아가기
                finish()
            } else {
                Toast.makeText(this, "이미 존재하는 아이디입니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // 로그인 화면으로 돌아가기 버튼
        btnLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}