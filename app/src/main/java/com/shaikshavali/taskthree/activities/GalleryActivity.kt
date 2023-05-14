package com.shaikshavali.taskthree.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager

import com.shaikshavali.taskthree.R
import com.shaikshavali.taskthree.adapters.ImagesAdapter
import com.shaikshavali.taskthree.firestore.FirestoreClass
import com.shaikshavali.taskthree.models.User
import com.shaikshavali.taskthree.utils.Constants
import kotlinx.android.synthetic.main.activity_gallery.*
import kotlinx.android.synthetic.main.status_image.*

class GalleryActivity : BaseActivity() {

    private var userData: User? = null
    //for storing the user details for future purpose

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        setupActionBar()

        val userId = intent.getStringExtra(Constants.USER_ID_INTENT)

        //getting the userId from the intent i.e., from Home screen

        Log.e("User @Gall", userId!!)
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getUserDetails(this, userId)

//        loading the user details using user Id

        btn_upload_gallery.setOnClickListener {

            val intent = Intent(this, UploadImageActivity::class.java)
            intent.putExtra(Constants.USER, userData)
            startActivity(intent)
        }

    }

    private fun setUpRV() {

        if (userData!!.images.size > 0) {
//            checking whether the user has images or not

            rv_gallery.layoutManager = GridLayoutManager(this, 3)
//            setting he layout for images in recycler view to GridLayout for storing it in a grid manner in the span of 3
//            so that each line will have 3 images through out the recycler view
            rv_gallery.setHasFixedSize(true)

//            set the fixed size of the adapter as true

            val adapter = ImagesAdapter(this, userData!!.images)
//            initialise the adapter here
            rv_gallery.adapter = adapter

//            attaching the recycler view's adapter to the user adapter

            adapter.setOnClickListener(object : ImagesAdapter.OnItemListener {
//          adding the onclick functionality to the each item of the recycler view using the interface

                override fun onClick(position: Int, item: String) {
//          Inside the onclick method we get the position and image's url here from the adapter
                    Log.e("Inside OnClick() $position", "Success...")

                    Toast.makeText(this@GalleryActivity, "Onclick at $position", Toast.LENGTH_SHORT)
                        .show()

                    showImageDialog(item)

//  A method which is used to display the image in the dialog

                }

            })

        }

    }


    private fun setupActionBar() {
        setSupportActionBar(toolbar_gallery)
        val action = supportActionBar
        if (action != null) {
            action.setDisplayHomeAsUpEnabled(true)                      // setting the toolbar
            action.setHomeAsUpIndicator(R.drawable.back_img_clr_black)
            action.title = ""
        }
        toolbar_gallery.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun getUserDataSuccess(user: User) {
        hideProgressDialog()
        userData =
            user                            //on successfully loading the user details the recyclerview has to be load once for updated data
        setUpRV()
        Log.e("Get User Success  ", userData!!.name)
    }

}