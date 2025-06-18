package com.example.notesapp

import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notesapp.databinding.ActivityNotesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.Locale

class NotesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotesBinding
    private lateinit var database: DatabaseReference
    private lateinit var notesAdapter: NotesAdapter
    private val notesList = mutableListOf<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().getReference("notes")
        notesAdapter = NotesAdapter(notesList,
            onDeleteClick = { note -> deleteNote(note) },
            onEditClick = { note -> editNote(note) }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = notesAdapter


        fetchNotes()

        binding.fab.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            startActivity(intent)
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filterNotes(newText.orEmpty())
                return true
            }
        })
    }

    private fun fetchNotes() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        database.child(uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                notesList.clear()
                for (noteSnap in snapshot.children) {
                    val note = noteSnap.getValue(Note::class.java)
                    note?.let { notesList.add(it) }
                }
                notesAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }

    private fun deleteNote(note: Note) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        database.child(uid).child(note.id).removeValue()
    }

    private fun editNote(note: Note) {
        val intent = Intent(this, AddNoteActivity::class.java)
        intent.putExtra("note_id", note.id)
        intent.putExtra("note_title", note.title)
        intent.putExtra("note_content", note.content)
        startActivity(intent)
    }

    private fun filterNotes(query: String) {
        val filtered = notesList.filter {
            it.title.lowercase(Locale.getDefault()).contains(query.lowercase(Locale.getDefault()))
        }
        notesAdapter.updateList(filtered)
    }
}
