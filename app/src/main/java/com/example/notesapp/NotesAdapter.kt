package com.example.notesapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotesAdapter(
    private var notes: List<Note>,
    private val onDeleteClick: (Note) -> Unit,
    private val onEditClick: (Note) -> Unit
) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.noteTitle)
        val contentText: TextView = itemView.findViewById(R.id.noteContent)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteBtn)
        val editButton: ImageButton = itemView.findViewById(R.id.editBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun getItemCount(): Int = notes.size

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.titleText.text = note.title
        holder.contentText.text = note.content

        holder.deleteButton.setOnClickListener {
            onDeleteClick(note)
        }

        holder.editButton.setOnClickListener {
            onEditClick(note)
        }
    }

    fun updateList(newNotes: List<Note>) {
        notes = newNotes
        notifyDataSetChanged()
    }
}
