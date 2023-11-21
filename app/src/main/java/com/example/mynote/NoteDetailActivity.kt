package com.example.mynote

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.mynote.database.Note
import com.example.mynote.database.NoteDao
import com.example.mynote.database.NoteRoomDatabase
import com.example.mynote.databinding.ActivityNoteDetailBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class NoteDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoteDetailBinding
    private lateinit var mNotesDao: NoteDao
    private var updateId: Int = 0
    private lateinit var executorService: ExecutorService
    private var noteToDelete: Note? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        executorService = Executors.newSingleThreadExecutor()
        val db = NoteRoomDatabase.getDatabase(this)
        mNotesDao = db!!.noteDao()!!
        updateId = intent.getIntExtra("EXTRA_NOTE_ID", 0)

        with(binding) {
            if (updateId != 0) {
                // Jika updateId tidak 0, berarti sedang mengedit catatan yang sudah ada
                // Ambil data catatan dari database dan tampilkan di EditText
                executorService.execute {
                    val existingNote = mNotesDao.getNoteById(updateId)
                    noteToDelete = existingNote
                    existingNote?.let {
                        runOnUiThread {
                            editTextTitle.setText(it.title ?: "")
                            editTextContent.setText(it.description ?: "")
                        }
                    }
                }
            }

            btnBack.setOnClickListener {
                finish()
            }
            btnSave.setOnClickListener {
                if(updateId==0) {
                    insert(
                        Note(
                            title = binding.editTextTitle.text.toString(),
                            description = binding.editTextContent.text.toString()
                        )
                    )
                }
                else{
                    update(
                        Note(
                            id = updateId,
                            title = editTextTitle.getText().toString(),
                            description = editTextContent.getText().toString(),
                        )
                    )
                    updateId = 0
                }
                finish()
            }
            btnDelete.setOnClickListener{
                noteToDelete?.let {
                    delete(it)
                    noteToDelete = null // Reset the noteToDelete after deletion
                }
                finish()
            }

        }
    }



    companion object {
        const val EXTRA_NOTE_ID = 0
    }

    private fun insert(note: Note) {
        executorService.execute { mNotesDao.insert(note) }
    }
    private fun delete(note: Note) {
        executorService.execute { mNotesDao.delete(note) }
    }
    private fun update(note: Note) {
        executorService.execute { mNotesDao.update(note) }
    }

}