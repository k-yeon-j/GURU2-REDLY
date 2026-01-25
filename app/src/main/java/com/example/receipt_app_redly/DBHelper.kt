package com.example.receipt_app_redly

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// DB 버전을 5로 올렸습니다.
class DBHelper(context: Context) : SQLiteOpenHelper(context, "guru2DB", null, 5) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE user (ID TEXT PRIMARY KEY, PW TEXT);")
        db.execSQL("CREATE TABLE categories (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, emoji TEXT)")

        // [수정] user_id 컬럼을 추가하여 어떤 사용자의 영수증인지 구분합니다.
        db.execSQL("CREATE TABLE receipts (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id TEXT, " +
                "image TEXT, " +
                "memo TEXT, " +
                "category_id INTEGER, " +
                "date LONG, " +
                "FOREIGN KEY(user_id) REFERENCES user(ID))")

        insertDefaultCategories(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS user")
        db.execSQL("DROP TABLE IF EXISTS categories")
        db.execSQL("DROP TABLE IF EXISTS receipts")
        onCreate(db)
    }

    private fun insertDefaultCategories(db: SQLiteDatabase) {
        val categories = arrayOf(
            arrayOf("식비", "🍚"), arrayOf("카페", "☕"), arrayOf("마트", "🛒"), arrayOf("술", "🍺"),
            arrayOf("쇼핑", "🛍️"), arrayOf("취미", "🎮"), arrayOf("의료", "🏥"), arrayOf("주거", "🏠"),
            arrayOf("금융", "📑"), arrayOf("미용", "💄"), arrayOf("교통", "🚗"), arrayOf("여행", "✈️"),
            arrayOf("교육", "🎓"), arrayOf("생활", "🧺"), arrayOf("기부", "💖"), arrayOf("기타", "💬")
        )
        for (cat in categories) {
            val values = ContentValues().apply {
                put("name", cat[0]); put("emoji", cat[1])
            }
            db.insert("categories", null, values)
        }
    }

    fun signup(id: String, pw: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply { put("ID", id); put("PW", pw) }
        return db.insert("user", null, values) != -1L
    }

    fun login(id: String, pw: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM user WHERE ID=? AND PW=?", arrayOf(id, pw))
        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

    fun getAllCategories(): List<Map<String, Any>> {
        val list = mutableListOf<Map<String, Any>>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM categories", null)
        if (cursor.moveToFirst()) {
            do {
                val map = mutableMapOf<String, Any>()
                map["id"] = cursor.getInt(0)
                map["name"] = cursor.getString(1)
                map["emoji"] = cursor.getString(2)
                list.add(map)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    // [수정] userId 매개변수를 추가하여 저장 시 유저 식별자를 함께 기록합니다.
    fun insertReceipt(userId: String, categoryId: Int, date: Long, memo: String, imagePath: String?): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("user_id", userId) // 추가된 부분
            put("category_id", categoryId)
            put("date", date)
            put("memo", memo)
            put("image", imagePath)
        }
        return db.insert("receipts", null, values)
    }

    // [수정] userId 매개변수를 추가하여 본인의 영수증만 불러오도록 쿼리를 변경했습니다.
    fun getReceiptsByCategory(userId: String, categoryId: Int): List<Map<String, Any?>> {
        val list = mutableListOf<Map<String, Any?>>()
        val db = this.readableDatabase

        // WHERE 절에 user_id 조건을 추가했습니다.
        val query = "SELECT * FROM receipts WHERE category_id = ? AND user_id = ? ORDER BY date DESC"
        val cursor = db.rawQuery(query, arrayOf(categoryId.toString(), userId))

        if (cursor.moveToFirst()) {
            do {
                val map = mutableMapOf<String, Any?>()
                map["id"] = cursor.getInt(0)
                map["userId"] = cursor.getString(1)
                map["imagePath"] = cursor.getString(2)
                map["memo"] = cursor.getString(3)
                map["date"] = cursor.getLong(5)
                list.add(map)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }
}