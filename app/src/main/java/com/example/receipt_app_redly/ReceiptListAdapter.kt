package com.example.receipt_app_redly

import android.app.Dialog
import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class ReceiptListAdapter(private val receiptList: List<Map<String, Any?>>) :
    RecyclerView.Adapter<ReceiptListAdapter.ReceiptViewHolder>() {

    class ReceiptViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgReceipt: ImageView = view.findViewById(R.id.imgReceipt)
        val tvMemo: TextView = view.findViewById(R.id.tvMemo)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_receipt, parent, false)
        return ReceiptViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReceiptViewHolder, position: Int) {
        val item = receiptList[position]
        val imagePath = item["imagePath"] as? String

        // 경로에 있는 파일을 이미지뷰에 표시
        if (!imagePath.isNullOrEmpty()) {
            holder.imgReceipt.setImageURI(android.net.Uri.fromFile(java.io.File(imagePath)))
        }

        holder.tvMemo.text = item["memo"] as? String
        val dateMillis = item["date"] as? Long ?: 0L
        holder.tvDate.text = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(Date(dateMillis))

        // [추가] 사진 클릭 시 크게 보기 기능
        holder.imgReceipt.setOnClickListener {
            showFullImageDialog(holder.itemView.context, imagePath)
        }
    }

    private fun showFullImageDialog(context: Context, path: String?) {
        if (path.isNullOrEmpty()) return
        val dialog = Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.setContentView(R.layout.dialog_full_image)
        val fullImageView = dialog.findViewById<ImageView>(R.id.fullImageView)
        fullImageView.setImageURI(android.net.Uri.fromFile(java.io.File(path)))

        // 닫기 기능: 이미지 클릭 시 다이얼로그 종료
        fullImageView.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    override fun getItemCount(): Int = receiptList.size
}