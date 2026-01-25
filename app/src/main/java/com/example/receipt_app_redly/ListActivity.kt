package com.example.receipt_app_redly

import android.content.Context // Context 추가
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        // 1. Intent에서 카테고리 정보 가져오기
        val categoryId = intent.getIntExtra("CATEGORY_ID", -1)
        val categoryName = intent.getStringExtra("CATEGORY_NAME") ?: "목록"

        // 2. XML 뷰 연결
        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewList)

        // 3. 상단 제목 설정
        tvTitle.text = "$categoryName 내역"

        // 4. 리사이클러뷰 레이아웃 매니저 설정
        recyclerView.layoutManager = LinearLayoutManager(this)

        // [수정] 5-1. SharedPreferences에서 로그인된 유저 ID 가져오기
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val currentUserId = sharedPref.getString("current_user_id", "") ?: ""

        // [수정] 5-2. DB에서 해당 카테고리와 유저 ID에 맞는 영수증 리스트 가져오기
        val db = DBHelper(this)

        // DBHelper에서 수정한 대로 categoryId와 currentUserId 두 개를 인자로 전달합니다.
        val receiptList = db.getReceiptsByCategory(currentUserId, categoryId)

        // 6. 어댑터 연결
        recyclerView.adapter = ReceiptListAdapter(receiptList)
    }
}