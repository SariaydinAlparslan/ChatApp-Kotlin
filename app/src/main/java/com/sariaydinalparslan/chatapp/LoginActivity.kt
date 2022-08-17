package com.sariaydinalparslan.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()


        buttonlogin.setOnClickListener {
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()

                if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
                    Toast.makeText(
                        applicationContext,
                        "email and password are required",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    auth!!.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) {
                            if (it.isSuccessful) {
                                etEmail.setText("")
                                etPassword.setText("")
                                val intent = Intent(
                                    this@LoginActivity,
                                    UsersActivity::class.java
                                )
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "email or password invalid",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }
            buttonsignup.setOnClickListener {
                val intent = Intent(
                    this@LoginActivity,
                    MainActivity::class.java
                )
                startActivity(intent)
                finish()
            }
        }
    }
