package com.example.journalapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil
import com.example.journalapp.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.createAcctBtn.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.emailSignInBtn.setOnClickListener(){
            LoginWithEmailPassword(
                binding.email.text.toString().trim(),
                binding.password.text.toString().trim()
            )
        }

        //auth ref
        auth =Firebase.auth
    }

    private fun LoginWithEmailPassword(email: String, password: String) {

        // Validate email
        if (!isValidEmail(email)) {
            Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_LONG).show()
            return
        }

        // Validate password
        if (password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long.", Toast.LENGTH_LONG).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this){ task ->
            //sign in success
            if (task.isSuccessful) {

                var journal : JournalUser = JournalUser.instance!!
                journal.userId = auth.currentUser!!.uid
                journal.username = auth.currentUser!!.displayName

                goToJournalList()
            } else {
                // Handle Firebase sign-in failure
                handleSignInFailure(task.exception)
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun handleSignInFailure(exception: Exception?) {
        when (exception) {
            is FirebaseAuthInvalidCredentialsException -> {
                Toast.makeText(this, "Invalid email or password.", Toast.LENGTH_LONG).show()
            }
            is FirebaseAuthInvalidUserException -> {
                Toast.makeText(this, "No account found with this email.", Toast.LENGTH_LONG).show()
            }
            else -> {
                Toast.makeText(this, "Authentication failed: ${exception?.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null) {
            goToJournalList()
        }
    }

    private fun goToJournalList() {
        startActivity(Intent(this, JournalList::class.java))
        finish()
    }
}