package com.example.mynote

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.mynote.NoteDetailActivity
import com.example.mynote.NoteDetailActivity.Companion.EXTRA_NOTE_ID
import com.example.mynote.database.Note
import com.example.mynote.database.NoteDao
import com.example.mynote.database.NoteRoomDatabase
import com.example.mynote.databinding.ActivityMainBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mNotesDao: NoteDao
    private var updateId: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = NoteRoomDatabase.getDatabase(this)
        mNotesDao = db!!.noteDao()!!

        with(binding) {

            val intent = Intent(this@MainActivity, NoteDetailActivity::class.java)

            // Ketika tombol tambah diklik
            btnAddNote.setOnClickListener(View.OnClickListener {
                intent.putExtra("EXTRA_NOTE_ID", 0)
                startActivity(intent)
            })

            // Ketika item list diklik
            listView.setOnItemClickListener { adapterView, _, i, _ ->
                val item = adapterView.adapter.getItem(i) as Note
                if (item.id != 0) {
                    updateId = item.id
                    intent.putExtra("EXTRA_NOTE_ID", updateId)
                    startActivity(intent)
                }
            }
        }
    }

    private fun getAllNotes() {
        mNotesDao.allNotes.observe(this) { notes ->
            val adapter: ArrayAdapter<Note> = ArrayAdapter<Note>(
                this,
                android.R.layout.simple_list_item_1, notes
            )
            binding.listView.adapter = adapter
        }
    }

    override fun onResume() {
        super.onResume()
        getAllNotes()
    }

}
