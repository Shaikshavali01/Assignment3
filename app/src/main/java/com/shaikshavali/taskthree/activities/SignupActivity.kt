package com.shaikshavali.taskthree.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.shaikshavali.taskthree.R
import com.shaikshavali.taskthree.firestore.FirestoreClass
import com.shaikshavali.taskthree.models.User
import kotlinx.android.synthetic.main.activity_signup.*


class SignupActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        setupActionBar()

        auth = FirebaseAuth.getInstance()

        btn_login_signup.setOnClickListener {

            registerUser()

        }

    }

    private fun registerUser() {

        val name = et_uname_signup.text.toString().trim { it <= ' ' }
        val email = et_email_signup.text.toString().trim { it <= ' ' }               //loading all the user entered details into variables
        val password = et_password_signup.text.toString().trim { it <= ' ' }
        val rePassword = et_re_password_signup.text.toString()
        val cBox = cb_signin.isChecked

//        get user entered Details and check whether they are null or not

        if (checkValues(name, email, password, rePassword, cBox)) {

            showProgressDialog(resources.getString(R.string.please_wait))
            auth.createUserWithEmailAndPassword(email, password)        //creating the user with email and password
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val fbUser = task.result!!.user                 //getting the registered user here using result
                        Toast.makeText(this, "$name Registered successfully", Toast.LENGTH_SHORT).show()

                        val user = User(fbUser!!.uid, name, email)
                        FirestoreClass().registerUser(this@SignupActivity, user)

                        //registering the user into the firebase

//                        FirebaseAuth.getInstance().signOut()
//                        finish()
                    } else {
                        Toast.makeText(this, task.exception!!.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                .addOnFailureListener {
                    Log.e("Auth : ", it.printStackTrace().toString())
                    Toast.makeText(this, "Not Registered", Toast.LENGTH_SHORT).show()
                }
        }


    }

    fun registerSuccess() {
        hideProgressDialog()

        //after successful registering the user he will be redirected to the login activity using Intent

        val intent = Intent(this@SignupActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
        //finish the activity here because the user will not come to this activity again
    }

    private fun checkValues(
        name: String,
        email: String,
        password: String,                           //Checking whether the user has entered the details or not
        rePassword: String,
        cBox: Boolean
    ): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                Toast.makeText(this, "Enter name", Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(email) -> {
                Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(password) -> {
                Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(rePassword) -> {
                Toast.makeText(this, "Re Enter password", Toast.LENGTH_SHORT).show()
                false
            }
            password != rePassword -> {
                Toast.makeText(this, "Password does not matches", Toast.LENGTH_SHORT).show()
                false
            }
            !cBox -> {
                Toast.makeText(this, "Agree Terms and Conditions ", Toast.LENGTH_SHORT).show()
                false
            }

            else -> {
                true
            }
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_signup_screen)
        val action = supportActionBar
        if (action != null) {                                               //setting the tool bar here
            action.setDisplayHomeAsUpEnabled(true)
            action.setHomeAsUpIndicator(R.drawable.img_back_arrow)          //setting the vector asset here
            action.title = ""
        }
        toolbar_signup_screen.setNavigationOnClickListener {
            onBackPressed()                                                 //on clicking the vector asset it should be
                                                                            // redirected to previous activity
        }
    }


}


