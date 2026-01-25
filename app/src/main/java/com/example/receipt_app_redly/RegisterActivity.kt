package com.example.receipt_app_redly

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class RegisterActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var photoHint: TextView
    private var selectedBitmap: Bitmap? = null

    // 카메라 및 갤러리 실행 결과 처리는 기존과 동일합니다.
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val bitmap = result.data?.extras?.get("data") as? Bitmap
            bitmap?.let { updatePreview(it) }
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            uri?.let {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(contentResolver, it)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(contentResolver, it)
                }
                updatePreview(bitmap)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        imageView = findViewById(R.id.imageView)
        photoHint = findViewById(R.id.photoHint)
        val btnCamera = findViewById<Button>(R.id.btnCamera)
        val btnGallery = findViewById<Button>(R.id.btnGallery)
        val fabSave = findViewById<FloatingActionButton>(R.id.fab)

        // [로그아웃 설정] XML의 @+id/profile 아이디를 그대로 사용합니다.
        val btnProfile = findViewById<ImageView>(R.id.profile)

        // 프로필 아이콘 클릭 시 로그아웃 팝업 띄우기
        btnProfile.setOnClickListener { view ->
            val popup = PopupMenu(this, view)
            popup.menu.add("로그아웃") // 메뉴 아이템 생성

            popup.setOnMenuItemClickListener { item ->
                if (item.title == "로그아웃") {
                    performLogout() // 로그아웃 로직 실행
                    true
                } else {
                    false
                }
            }
            popup.show()
        }

        // 기존 카메라/갤러리/저장 버튼 로직
        btnCamera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraLauncher.launch(intent)
        }

        btnGallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            galleryLauncher.launch(intent)
        }

        fabSave.setOnClickListener {
            if (selectedBitmap != null) {
                DataBridge.tempBitmap = selectedBitmap
                val intent = Intent(this, DetailInputActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "먼저 사진을 등록해주세요!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 로그아웃 수행 함수
    private fun performLogout() {
        // 1. SharedPreferences에서 유저 데이터 삭제
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            remove("current_user_id") // 로그인 정보를 지웁니다.
            apply()
        }

        // 2. 로그인 화면(MainActivity)으로 이동하며 스택 정리
        val intent = Intent(this, MainActivity::class.java)
        // 뒤로가기를 눌러도 다시 이 화면으로 오지 못하게 설정합니다.
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun updatePreview(bitmap: Bitmap) {
        selectedBitmap = bitmap
        imageView.setImageBitmap(bitmap)
        photoHint.visibility = View.GONE
    }
}