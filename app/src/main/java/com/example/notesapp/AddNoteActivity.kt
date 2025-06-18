package com.example.notesapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AddNoteActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText
    private lateinit var saveButton: Button

    private var noteId: String? = null
    private var isEditing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        titleEditText = findViewById(R.id.titleEditText)
        contentEditText = findViewById(R.id.contentEditText)
        saveButton = findViewById(R.id.saveNoteButton)

        // If we are editing a note, get data from intent
        val intent = intent
        if (intent != null && intent.hasExtra("note_id")) {
            isEditing = true
            noteId = intent.getStringExtra("note_id")
            titleEditText.setText(intent.getStringExtra("note_title"))
            contentEditText.setText(intent.getStringExtra("note_content"))
            saveButton.text = "Update Note"
        }


        saveButton.setOnClickListener {
            val title = titleEditText.text.toString().trim()
            val content = contentEditText.text.toString().trim()

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener
            val dbRef = FirebaseDatabase.getInstance().getReference("notes").child(userId)

            if (isEditing) {
                val updatedNote = Note(noteId!!, title, content)
                dbRef.child(noteId!!).setValue(updatedNote).addOnCompleteListener {
                    Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } else {
                val newId = dbRef.push().key!!
                val newNote = Note(newId, title, content)
                dbRef.child(newId).setValue(newNote).addOnCompleteListener {
                    Toast.makeText(this, "Note added", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}
