package com.example.dailyselfcare

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "selfcare.db"
        private const val DATABASE_VERSION = 3  // Updated for Habits table

        // Users Table
        private const val TABLE_USERS = "users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_CONTACT = "contact"

        // Journal Table
        private const val TABLE_JOURNAL = "journal"
        private const val COLUMN_JOURNAL_ID = "journal_id"
        private const val COLUMN_JOURNAL_TEXT = "text"

        // Habits Table
        private const val TABLE_HABITS = "habits"
        private const val COLUMN_HABIT_ID = "habit_id"
        private const val COLUMN_HABIT_NAME = "habit_name"
        private const val COLUMN_HABIT_DONE = "done"
        private const val COLUMN_HABIT_DATE = "date"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT,
                $COLUMN_EMAIL TEXT UNIQUE,
                $COLUMN_PASSWORD TEXT,
                $COLUMN_CONTACT TEXT
            )
        """.trimIndent()

        val createJournalTable = """
            CREATE TABLE $TABLE_JOURNAL (
                $COLUMN_JOURNAL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_JOURNAL_TEXT TEXT
            )
        """.trimIndent()

        val createHabitsTable = """
            CREATE TABLE $TABLE_HABITS (
                $COLUMN_HABIT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_HABIT_NAME TEXT,
                $COLUMN_HABIT_DONE INTEGER DEFAULT 0,
                $COLUMN_HABIT_DATE TEXT
            )
        """.trimIndent()

        db?.execSQL(createUsersTable)
        db?.execSQL(createJournalTable)
        db?.execSQL(createHabitsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db?.execSQL("""CREATE TABLE IF NOT EXISTS $TABLE_JOURNAL (
                $COLUMN_JOURNAL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_JOURNAL_TEXT TEXT
            )""".trimIndent())
        }
        if (oldVersion < 3) {
            db?.execSQL("""CREATE TABLE IF NOT EXISTS $TABLE_HABITS (
                $COLUMN_HABIT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_HABIT_NAME TEXT,
                $COLUMN_HABIT_DONE INTEGER DEFAULT 0,
                $COLUMN_HABIT_DATE TEXT
            )""".trimIndent())
        }
    }

    // --------------------------
    // User Operations
    // --------------------------
    fun addUser(username: String, email: String, password: String, contact: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, password)
            put(COLUMN_CONTACT, contact)
        }
        val result = db.insert(TABLE_USERS, null, values)
        db.close()
        return result != -1L
    }

    fun checkUser(email: String, password: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_USERS WHERE $COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?",
            arrayOf(email, password)
        )
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun updateUser(currentEmail: String, username: String, email: String, contact: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_EMAIL, email)
            put(COLUMN_CONTACT, contact)
        }
        val rows = db.update(TABLE_USERS, values, "$COLUMN_EMAIL=?", arrayOf(currentEmail))
        db.close()
        return rows > 0
    }

    fun deleteUser(email: String): Boolean {
        val db = writableDatabase
        val rows = db.delete(TABLE_USERS, "$COLUMN_EMAIL=?", arrayOf(email))
        db.close()
        return rows > 0
    }

    // --------------------------
    // Journal Operations
    // --------------------------
    fun insertJournal(text: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply { put(COLUMN_JOURNAL_TEXT, text) }
        val result = db.insert(TABLE_JOURNAL, null, values)
        db.close()
        return result
    }

    fun getAllJournals(): MutableList<String> {
        val entries = mutableListOf<String>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_JOURNAL ORDER BY $COLUMN_JOURNAL_ID DESC", null
        )
        if (cursor.moveToFirst()) {
            do {
                val text = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JOURNAL_TEXT))
                entries.add(text)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return entries
    }

    fun updateJournal(oldText: String, newText: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply { put(COLUMN_JOURNAL_TEXT, newText) }
        val rows = db.update(TABLE_JOURNAL, values, "$COLUMN_JOURNAL_TEXT=?", arrayOf(oldText))
        db.close()
        return rows > 0
    }

    fun deleteJournal(text: String): Boolean {
        val db = writableDatabase
        val rows = db.delete(TABLE_JOURNAL, "$COLUMN_JOURNAL_TEXT=?", arrayOf(text))
        db.close()
        return rows > 0
    }

    // --------------------------
    // Habits Operations
    // --------------------------
    data class Habit(val id: Int, val name: String, var done: Boolean)

    fun addHabit(name: String, date: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_HABIT_NAME, name)
            put(COLUMN_HABIT_DONE, 0)
            put(COLUMN_HABIT_DATE, date)
        }
        val result = db.insert(TABLE_HABITS, null, values)
        db.close()
        return result != -1L
    }

    fun getHabits(date: String): MutableList<Habit> {
        val list = mutableListOf<Habit>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_HABITS WHERE $COLUMN_HABIT_DATE=?",
            arrayOf(date)
        )
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HABIT_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HABIT_NAME))
                val done = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HABIT_DONE)) == 1
                list.add(Habit(id, name, done))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }

    fun updateHabitDone(id: Int, done: Boolean) {
        val db = writableDatabase
        val values = ContentValues().apply { put(COLUMN_HABIT_DONE, if (done) 1 else 0) }
        db.update(TABLE_HABITS, values, "$COLUMN_HABIT_ID=?", arrayOf(id.toString()))
        db.close()
    }

    fun deleteHabit(id: Int) {
        val db = writableDatabase
        db.delete(TABLE_HABITS, "$COLUMN_HABIT_ID=?", arrayOf(id.toString()))
        db.close()
    }
}
