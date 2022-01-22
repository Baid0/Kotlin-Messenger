package com.example.messengerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var refUsers: DatabaseReference
    private var firebaseUserID: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val toolbar: Toolbar = findViewById(R.id.toolbar_register)


        supportActionBar!!.title = "რეგისტრაცია"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this@RegisterActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }


        mAuth = FirebaseAuth.getInstance()


        register_btn.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser()
    {
        val username: String = username_register.text.toString()
        val email: String = email_register.text.toString()
        val password: String = password_register.text.toString()

        if (username == "")
        {
            Toast.makeText(this@RegisterActivity, "გთხოვთ დაწერეთ ნიკი", Toast.LENGTH_LONG).show()
        }
        else if (email == "")
        {
            Toast.makeText(this@RegisterActivity, "გთხოვთ დაწერეთ მეილი", Toast.LENGTH_LONG).show()
        }
        else if (password == "")
        {
            Toast.makeText(this@RegisterActivity, "გთხოვთ დაწერეთ პაროლი", Toast.LENGTH_LONG).show()
        }
        else
        {
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener{ task ->
                    if (task.isSuccessful)
                    {
                        firebaseUserID = mAuth.currentUser!!.uid
                        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUserID)

                        val userHashMap = HashMap<String, Any>()
                        userHashMap["uid"] = firebaseUserID
                        userHashMap["username"] = username
                        userHashMap["profile"] = "https://i.ibb.co/bNfBmDN/btu-logo-202103171612-1.png"
                        userHashMap["cover"] = "https://i.ibb.co/NTv3WGG/8fd58d7058687d6e02994a12ab8ed459-1-2.png"
                        userHashMap["status"] = "offline"
                        userHashMap["search"] = username.toLowerCase()
                        userHashMap["facebook"] = "https://m.facebook.com"
                        userHashMap["instagram"] = "https://m.instagram.com"
                        userHashMap["website"] = "https://www.google.com"

                        refUsers.updateChildren(userHashMap)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful)
                                {
                                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                    return@addOnCompleteListener
                                    finish()
                                }
                            }
                    }
                    else
                    {
                        Toast.makeText(this@RegisterActivity, "ერორი " + task.exception!!.message.toString(), Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}