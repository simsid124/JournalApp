package com.example.journalapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.journalapp.databinding.ActivityAddJournalBinding
import com.example.journalapp.databinding.ActivityJournalListBinding
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.Date

class AddJournalActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddJournalBinding

    //credentials
    var currentUserId: String = ""
    var currentUserName: String = ""

    //firebase
    lateinit var auth: FirebaseAuth
    lateinit var user: FirebaseUser

    //firebase firestore
    var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    lateinit var storageReference: StorageReference
    var collectionReference: CollectionReference = db.collection("Journal")
    lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_journal)

        storageReference = FirebaseStorage.getInstance().getReference()
        auth = Firebase.auth

        binding.apply {
            postProgressBar.visibility = View.INVISIBLE
            if (JournalUser.instance != null){
                currentUserId = auth.currentUser?.uid.toString()
                currentUserName = auth.currentUser?.displayName.toString()

                postUsernameTextView.text = currentUserName
            }

            //get image from gallery
            postCameraButton.setOnClickListener() {
                var i : Intent = Intent(Intent.ACTION_GET_CONTENT)
                i.setType("image/*")
                startActivityForResult(i, 1)
            }

            postSaveJournalBtn.setOnClickListener {
                SaveJournal()
            }
        }
    }

    private fun SaveJournal() {
        var title: String = binding.postTitleEt.text.toString().trim()
        var thoughts: String = binding.postDescriptionEt.text.toString().trim()

        binding.postProgressBar.visibility = View.VISIBLE

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(thoughts) && imageUri != null){
            //saving images into folder located at: ../journal_images/my_image.png
            var filePath: StorageReference = storageReference
                .child("journal_images")
                .child("my_image_"+Timestamp.now().seconds)

            //upload file to firebase storage
            filePath.putFile(imageUri).addOnSuccessListener() {
                filePath.getDownloadUrl().addOnSuccessListener {
                    var imageUri: String = it.toString()
                    var timestamp: Timestamp = Timestamp(Date())
                    //create object of journal
                    var journal: Journal = Journal(
                        title,
                        thoughts,
                        imageUri,
                        currentUserId,
                        timestamp,
                        currentUserName
                    )
                    //save new journal to firestore
                    collectionReference.add(journal).addOnSuccessListener {
                        binding.postProgressBar.visibility = View.INVISIBLE
                        var i = Intent(this, JournalList::class.java)
                        startActivity(i)
                        finish()
                    }
                }
            }.addOnFailureListener() {
                binding.postProgressBar.visibility = View.INVISIBLE
            }
        } else{
            binding.postProgressBar.visibility = View.INVISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK){
            if(data != null){
                imageUri = data.data!! //get image path
                binding.postImageView.setImageURI(imageUri) //show image
            }
        }
    }

    override fun onStart() {
        super.onStart()
        user = auth.currentUser!!
    }

    override fun onStop() {
        super.onStop()
        if(auth != null){

        }
    }
}