package com.example.receipt_app_redly

interface AuthRepository {
    fun login(id: String, password: String, callback: (Boolean) -> Unit)
    fun signup(id: String, password: String, callback: (Boolean) -> Unit)
}