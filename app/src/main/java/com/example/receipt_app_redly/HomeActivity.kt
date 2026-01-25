package com.example.receipt_app_redly

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeActivity : AppCompatActivity() {

    private lateinit var db: DBHelper
    private lateinit var rv: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        db = DBHelper(this)
        rv = findViewById(R.id.recyclerView)

        // 1. 그리드 레이아웃 설정 (3열)
        rv.layoutManager = GridLayoutManager(this, 3)

        // 2. 초기 데이터 로드 및 어댑터 연결
        setupAdapter()

        // 3. 카메라 버튼 클릭 시 촬영 선택 화면으로 이동
        findViewById<FloatingActionButton>(R.id.fab_camera).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    // 4. [핵심] 상세 화면에서 저장 후 돌아왔을 때 리스트를 새로고침함
    override fun onResume() {
        super.onResume()
        setupAdapter()
    }

    private fun setupAdapter() {
        // DB에서 최신 카테고리 목록을 가져와 어댑터에 전달
        rv.adapter = CategoryAdapter(db.getAllCategories()) { id, name ->
            // 특정 카테고리(예: 식비)를 클릭하면 해당 내역 리스트로 이동
            val intent = Intent(this, ListActivity::class.java)
            intent.putExtra("CATEGORY_ID", id)
            intent.putExtra("CATEGORY_NAME", name)
            startActivity(intent)
        }
    }
}