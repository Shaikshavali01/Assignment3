package com.shaikshavali.taskthree.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.shaikshavali.taskthree.R
import com.shaikshavali.taskthree.firestore.FirestoreClass
import com.shaikshavali.taskthree.models.User
import com.shaikshavali.taskthree.utils.Constants
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setupActionBar()

        btn_login2.setOnClickListener {
            signInRegisteredUser()
        }

    }

    private fun signInRegisteredUser() {
        // Here we get the text from editText and trim the space
        val email: String = et_email.text.toString().trim { it <= ' ' }
        val password: String = et_password.text.toString().trim { it <= ' ' }

        if (checkValues(email, password)) {
            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))

            // Sign-In using FirebaseAuth
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Calling the FirestoreClass signInUser function to get the data of user from database.
                        FirestoreClass().loadUser(this@LoginActivity)
                    } else {
                        hideProgressDialog()
                        Toast.makeText(
                            this@LoginActivity,
                            task.exception!!.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }

    private fun checkValues(email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(email) -> {
                Toast.makeText(this, "Please Enter Name", Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(password) -> {
                Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show()
                false
            }
            else -> {
                true
            }
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_login_screen)
        val action = supportActionBar
        if (action != null) {
            action.setDisplayHomeAsUpEnabled(true)
            action.setHomeAsUpIndicator(R.drawable.img_back_arrow)
            action.title = ""
        }
        toolbar_login_screen.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun signInSuccess(user: User) {

        hideProgressDialog()
        val intent = Intent(this@LoginActivity, HomeScreen::class.java)
        intent.putExtra(Constants.USER_ID_INTENT, user.id)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()

    }

}