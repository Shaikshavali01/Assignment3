package com.shaikshavali.taskthree.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.messaging.FirebaseMessaging
import com.shaikshavali.taskthree.R
import com.shaikshavali.taskthree.firestore.FirestoreClass
import com.shaikshavali.taskthree.models.User
import com.shaikshavali.taskthree.utils.Constants
import kotlinx.android.synthetic.main.activity_home_screen.*

class HomeScreen : BaseActivity() {
    private var userId: String? = null                      //to get the user id from login activity
    private var userData: User? = null                      //to store the logged in user details
    private lateinit var mSharedPreferences: SharedPreferences      //for storing whether the user has token or not for notifications

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)

        mSharedPreferences = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE)
        //getting the shared preferences in private mode so it can be used in this project only

        userId = getCurrentUserID()
        //getting the current userId from the authentication and from base class so that it can be helpful in many situations

        val checkTokenUpdated = mSharedPreferences.getBoolean(Constants.FCM_TOKEN_UPDATED, false)
        //checking whether the user has the updated token or not


        if (checkTokenUpdated) {
            Log.e("Token @home ", "checkToken is already updated ....")

            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getUserDetails(this, userId!!)
            //get the user details using the userId from firebase

        } else {
            FirebaseMessaging.getInstance().token.addOnSuccessListener { result ->
//          If the user does not have the updated token then get it from the  FirebaseMessaging service
                if (result != null) {
                    updateFCMTokenToFb(result)
//                    updating the token into user in firebase
                }
            }
        }

        Log.e("User @HomeScreen: ", userId!!)


        btn_gallery_home.setOnClickListener {

            val intent = Intent(this, GalleryActivity::class.java)
            intent.putExtra(Constants.USER_ID_INTENT, userId)
            startActivity(intent)

            //transferring the user from home screen to Gallery where all the images will be stored when clicked

        }

        btn_location_home.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra(Constants.USER_ID_INTENT, userId)
            startActivity(intent)

//            transferring to map activity
        }


    }

    fun getUserdataSuccess(user: User) {
        hideProgressDialog()
//        after successfully loading the user details from the database it will be stored in the global variable for future purpose
        userData = user
        Log.e("GetUser @Home", "getting user data success....${userData!!.name}")

    }

    fun tokenUpdateSuccess() {
        hideProgressDialog()
//        it will be invoked when the token has stored in the firebase successfully

        Log.e("Update success @Home", "token update success")

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getUserDetails(this, userId!!)

//        after storing into firebase again load the usr for updated contents

        val editor: SharedPreferences.Editor =
            mSharedPreferences.edit()        //going to edit the shared preferences
        editor.putBoolean(Constants.FCM_TOKEN_UPDATED, true)    //make changes here
        editor.apply()  //apply the changes made

//        here updating the shared preferences such that the user has the updated token

    }


    private fun updateFCMTokenToFb(token: String) {

        val userHashMap = HashMap<String, Any>()
        userHashMap[Constants.FCM_TOKEN] =
            token      //  if the user does not have the updated token then update it in firebase

        Log.e("update fcm ", "Going to update fcm token in fb $token")

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().updateUserWithHashmap(this@HomeScreen, userHashMap)

//        update the user with token in firebase
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        Log.e("Onbackpressed @Home ","Hii....")
        mSharedPreferences.edit().clear().apply()
    }

}