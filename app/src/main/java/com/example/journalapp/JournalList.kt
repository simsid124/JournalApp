package com.example.journalapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.journalapp.databinding.ActivityJournalListBinding
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference

class JournalList : AppCompatActivity() {

    lateinit var binding: ActivityJournalListBinding

    //firebase references
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var user: FirebaseUser
    var db = FirebaseFirestore.getInstance()
    lateinit var storageReference: StorageReference
    var collectionReference: CollectionReference = db.collection("Journal")

    lateinit var journalList: MutableList<Journal>

    lateinit var adapter: JournalRecyclerAdapter

    lateinit var noPostsTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_journal_list)

        //firebase authentication
        firebaseAuth = Firebase.auth
        user = firebaseAuth.currentUser!!

        //recycler view
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        journalList = arrayListOf<Journal>()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //menu items
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add -> if (user != null && firebaseAuth != null) {
                val intent = Intent(this, AddJournalActivity::class.java)
                startActivity(intent)
            }
            R.id.action_sign_out -> if (user != null && firebaseAuth != null) {
                firebaseAuth.signOut()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // getting all posts
    override fun onStart() {
        super.onStart()
        collectionReference.whereEqualTo("userId", user.uid).get().addOnSuccessListener {
            Log.i("TAGY", "size: ${it.size()}")
            if (!it.isEmpty) {
                Log.i("TAGY", "Elements: ${it}")

                for (document in it) {
                    val title = document.getString("title") ?: ""
                    val thoughts = document.getString("thoughts") ?: ""
                    val imageUrl = document.getString("imageUrl") ?: ""
                    val userId = document.getString("userId") ?: ""
                    val timeAdded = document.getTimestamp("timeAdded") ?: Timestamp.now()
                    val username =document.getString("username") ?: ""

                    val journal = Journal(title, thoughts, imageUrl, userId, timeAdded, username)
                    journalList.add(journal)
                }

                //recycler view
                adapter = JournalRecyclerAdapter(this, journalList)
                binding.recyclerView.setAdapter(adapter)
                adapter.notifyDataSetChanged()
            }
            else{
                binding.listNoPosts.visibility = View.VISIBLE
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Oops! Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }


}