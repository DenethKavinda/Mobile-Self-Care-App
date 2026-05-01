package com.example.dailyselfcare

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class JournalActivity : AppCompatActivity() {

    private lateinit var entryEditText: EditText
    private lateinit var addButton: Button
    private lateinit var entriesListView: ListView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: ArrayAdapter<String>

    private var entries = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journal)

        dbHelper = DatabaseHelper(this)
        entryEditText = findViewById(R.id.entryEditText)
        addButton = findViewById(R.id.addButton)
        entriesListView = findViewById(R.id.entriesListView)

        // Load saved journals
        entries = dbHelper.getAllJournals()
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, entries)
        entriesListView.adapter = adapter

        // Add new journal
        addButton.setOnClickListener {
            val text = entryEditText.text.toString().trim()
            if (text.isNotEmpty()) {
                val result = dbHelper.insertJournal(text)
                if (result != -1L) {
                    entries.add(0, text)
                    adapter.notifyDataSetChanged()
                    entryEditText.text.clear()
                    Toast.makeText(this, "Journal saved!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to save journal", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please write something", Toast.LENGTH_SHORT).show()
            }
        }

        // Long click → Edit/Delete
        entriesListView.setOnItemLongClickListener { _, _, position, _ ->
            val selected = entries[position]
            val options = arrayOf("Edit", "Delete", "Cancel")

            AlertDialog.Builder(this)
                .setTitle("Manage Journal Entry")
                .setItems(options) { dialog, which ->
                    when (which) {
                        0 -> editEntry(position, selected)
                        1 -> deleteEntry(position, selected)
                        else -> dialog.dismiss()
                    }
                }
                .show()
            true
        }
        val backHomeButton: Button = findViewById(R.id.backHomeButton)
        backHomeButton.setOnClickListener {
            finish() // simply closes JournalActivity and returns to HomeActivity
        }

    }

    private fun editEntry(position: Int, oldText: String) {
        val input = EditText(this)
        input.setText(oldText)

        AlertDialog.Builder(this)
            .setTitle("Edit Journal")
            .setView(input)
            .setPositiveButton("Save") { dialog, _ ->
                val newText = input.text.toString().trim()
                if (newText.isNotEmpty()) {
                    val updated = dbHelper.updateJournal(oldText, newText)
                    if (updated) {
                        entries[position] = newText
                        adapter.notifyDataSetChanged()
                        Toast.makeText(this, "Updated!", Toast.LENGTH_SHORT).show()
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteEntry(position: Int, text: String) {
        AlertDialog.Builder(this)
            .setTitle("Delete Entry")
            .setMessage("Are you sure you want to delete this journal entry?")
            .setPositiveButton("Yes") { dialog, _ ->
                val deleted = dbHelper.deleteJournal(text)
                if (deleted) {
                    entries.removeAt(position)
                    adapter.notifyDataSetChanged()
                    Toast.makeText(this, "Deleted!", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("No", null)
            .show()
    }


}
