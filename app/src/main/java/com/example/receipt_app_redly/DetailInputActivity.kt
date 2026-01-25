package com.example.receipt_app_redly

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class DetailInputActivity : AppCompatActivity() {
    private var selectedCategoryId: Int? = null
    private var selectedDateMillis: Long? = null
    private lateinit var textRecognizer: com.google.mlkit.vision.text.TextRecognizer
    private lateinit var db: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // [수정] R 에러 방지를 위해 전체 패키지 경로를 사용하여 레이아웃 설정
        setContentView(com.example.receipt_app_redly.R.layout.activity_detail_input)

        db = DBHelper(this)
        textRecognizer = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())

        // [수정] 모든 뷰 연결 시 전체 패키지 경로(com.example.receipt_app_redly.R)를 명시
        val imageView = findViewById<ImageView>(com.example.receipt_app_redly.R.id.imagePreview)
        val editDate = findViewById<TextInputEditText>(com.example.receipt_app_redly.R.id.editDate)
        val editMemo = findViewById<TextInputEditText>(com.example.receipt_app_redly.R.id.editMemo)
        val categoryGroup = findViewById<MaterialButtonToggleGroup>(com.example.receipt_app_redly.R.id.groupCategory)
        val btnOcr = findViewById<Button>(com.example.receipt_app_redly.R.id.btnOcr)
        val btnSave = findViewById<Button>(com.example.receipt_app_redly.R.id.btnSave)

        setupMemoInnerScroll(editMemo)

        DataBridge.tempBitmap?.let {
            imageView.setImageBitmap(it)
        }

        // 카테고리 버튼 생성
        setupCategoryButtons(categoryGroup, db.getAllCategories())

        // 날짜 선택 설정
        val datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("날짜 선택").build()
        editDate.setOnClickListener {
            if (!datePicker.isAdded) {
                datePicker.show(supportFragmentManager, "MATERIAL_DATE_PICKER")
            }
        }
        datePicker.addOnPositiveButtonClickListener {
            selectedDateMillis = it
            editDate.setText(SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(Date(it)))
        }

        btnOcr.setOnClickListener {
            DataBridge.tempBitmap?.let { bitmap ->
                btnOcr.isEnabled = false
                btnOcr.text = "인식 중…"
                val input = InputImage.fromBitmap(bitmap, 0)
                textRecognizer.process(input)
                    .addOnSuccessListener { result ->
                        val recognized = result.text.trim()
                        if (recognized.isNotBlank()) {
                            val currentText = editMemo.text.toString()
                            val newText = if (currentText.isEmpty()) recognized else "$currentText\n$recognized"
                            editMemo.setText(newText)
                        }
                    }
                    .addOnCompleteListener {
                        btnOcr.isEnabled = true
                        btnOcr.text = "OCR로 텍스트 추출"
                    }
            }
        }

        // [최종 수정] 저장 버튼 로직
        btnSave.setOnClickListener {
            // [추가] 저장 버튼을 누르는 시점에 체크된 카테고리를 다시 한 번 확인 (가장 확실한 방법)
            val checkedId = categoryGroup.checkedButtonId
            if (checkedId != View.NO_ID) {
                val checkedButton = findViewById<MaterialButton>(checkedId)
                selectedCategoryId = checkedButton.tag as? Int
            }

            val memoText = editMemo.text.toString()

            // 디버깅을 위해 어느 값이 누락되었는지 구체적으로 체크
            if (selectedCategoryId != null && selectedDateMillis != null) {
                val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                val currentUserId = sharedPref.getString("current_user_id", "") ?: ""

                var imagePath: String? = null
                try {
                    DataBridge.tempBitmap?.let { bitmap ->
                        val fileName = "receipt_${System.currentTimeMillis()}.jpg"
                        val file = File(filesDir, fileName)
                        val out = FileOutputStream(file)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                        out.close()
                        imagePath = file.absolutePath
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                val result = db.insertReceipt(
                    currentUserId,
                    selectedCategoryId!!,
                    selectedDateMillis!!,
                    memoText,
                    imagePath
                )

                if (result != -1L) {
                    Toast.makeText(this, "저장되었습니다!", Toast.LENGTH_SHORT).show()
                    DataBridge.tempBitmap = null
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                }
            } else {
                // 어떤 정보가 누락되었는지 토스트로 알림
                val errorMsg = when {
                    selectedCategoryId == null && selectedDateMillis == null -> "날짜와 카테고리를 모두 선택해주세요."
                    selectedCategoryId == null -> "카테고리를 선택해주세요."
                    selectedDateMillis == null -> "날짜를 선택해주세요."
                    else -> "필수 정보를 입력해주세요."
                }
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupMemoInnerScroll(edit: TextInputEditText) {
        edit.setOnTouchListener { v, event ->
            if (v.hasFocus()) {
                v.parent.requestDisallowInterceptTouchEvent(true)
                if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                    v.parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            false
        }
    }

    private fun setupCategoryButtons(group: MaterialButtonToggleGroup, categories: List<Map<String, Any>>) {
        group.removeAllViews()
        categories.forEach { cat ->
            // 여기서도 R 에러 방지를 위해 전체 경로 사용
            val btn = MaterialButton(this, null, com.google.android.material.R.attr.materialButtonOutlinedStyle).apply {
                text = "${cat["emoji"]} ${cat["name"]}"
                isCheckable = true
                this.id = View.generateViewId()
                tag = cat["id"] as Int

                setOnClickListener {
                    selectedCategoryId = tag as Int
                }
            }
            group.addView(btn)
        }
    }
}