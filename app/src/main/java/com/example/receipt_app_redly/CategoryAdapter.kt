package com.example.receipt_app_redly

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.receipt_app_redly.R

class CategoryAdapter(
    private val categories: List<Map<String, Any>>,
    private val onClick: (Int, String) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvEmoji: TextView = view.findViewById(R.id.tvEmoji)
        val tvCategoryName: TextView = view.findViewById(R.id.tvCategoryName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = categories[position]
        holder.tvEmoji.text = item["emoji"]?.toString() ?: ""
        holder.tvCategoryName.text = item["name"]?.toString() ?: ""

        holder.itemView.setOnClickListener {
            val id = item["id"] as? Int ?: 0
            val name = item["name"]?.toString() ?: ""
            onClick(id, name)
        }
    }

    override fun getItemCount() = categories.size
}