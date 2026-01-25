package com.example.receipt_app_redly

class SQLiteAuthRepository(private val dbHelper: DBHelper) : AuthRepository {
    // 로그인 기능 구현 (DBHelper를 통해 ID, PW 확인)
    override fun login(id: String, password: String, callback: (Boolean) -> Unit) {
        val result = dbHelper.login(id, password)
        callback(result)
    }

    // 회원가입 기능 구현 (DBHelper를 통해 DB에 저장)
    override fun signup(id: String, password: String, callback: (Boolean) -> Unit) {
        val result = dbHelper.signup(id, password)
        callback(result)
    }
}