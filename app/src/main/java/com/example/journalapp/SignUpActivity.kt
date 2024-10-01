package com.example.journalapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.journalapp.databinding.ActivitySignUpBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class SignUpActivity : AppCompatActivity() {

    lateinit var binding : ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)

        auth = Firebase.auth

        binding.acctSignUpBtn.setOnClickListener {
            createUser()
        }
    }

    private fun createUser() {
        val email = binding.emailCreate.text.toString().trim()
        val password = binding.passwordCreate.text.toString().trim()
        val username = binding.usernameCreate.text.toString().trim()  // Assuming you added a username field

        // Validate username (no spaces)
        if (username.isEmpty()) {
            Toast.makeText(this, "Please enter a username.", Toast.LENGTH_LONG).show()
            return
        }

        if (username.contains(" ")) {
            Toast.makeText(this, "Username cannot contain spaces.", Toast.LENGTH_LONG).show()
            return
        }

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

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign in success
                Log.d("TAGY", "createUserWithEmail:success")
                redirectToLoginPageAndShowToast()
            } else {
                // Sign in failure
                Log.w("TAGY", "createUserWithEmail:failure", task.exception)
                handleSignUpFailure(task.exception)
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun handleSignUpFailure(exception: Exception?) {
        when (exception) {
            is FirebaseAuthWeakPasswordException -> {
                Toast.makeText(this, "Password is too weak.", Toast.LENGTH_LONG).show()
            }
            is FirebaseAuthInvalidCredentialsException -> {
                Toast.makeText(this, "Invalid email format.", Toast.LENGTH_LONG).show()
            }
            is FirebaseAuthUserCollisionException -> {
                Toast.makeText(this, "This email is already registered.", Toast.LENGTH_LONG).show()
            }
            else -> {
                Toast.makeText(this, "Authentication failed: ${exception?.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun redirectToLoginPageAndShowToast() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        Toast.makeText(this, "Account created successfully. Welcome to your journal app", Toast.LENGTH_LONG).show()

        startActivity(intent)
        finish()
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            reload()
        }
    }

    public fun reload() {}
}