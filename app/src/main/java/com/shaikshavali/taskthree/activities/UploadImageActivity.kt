package com.shaikshavali.taskthree.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.shaikshavali.taskthree.R
import com.shaikshavali.taskthree.firestore.FirestoreClass
import com.shaikshavali.taskthree.models.User
import com.shaikshavali.taskthree.utils.Constants
import kotlinx.android.synthetic.main.activity_upload_image.*
import java.io.File
import java.io.FileOutputStream

class UploadImageActivity : BaseActivity() {

    private var selectedImgUri: Uri? = null

    //    a variable for storing the user selected image in a uri format....
    private var imageURL: String = ""

    //    after storing the image into the firebase it will generate the url
    var userData: User? = null

//    storing the user details using Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_image)
        setupActionBar()

        userData = intent.getParcelableExtra(Constants.USER)
//        getting the user details using Intent as User is a Model which extends Parcelable
        Log.e("User @UploadImg", userData!!.name)

        btn_upload_img_upload_act.setOnClickListener {

            chooseAction()
//            when we click on the upload image button it has to show us an alert dialog to select image from camera or from storage
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

//            Checking the result code as Ok or Not

            if (requestCode == Constants.IMAGE_REQUEST_CODE) {
                if (data != null) {
//      Checking the data has any information or not
                    selectedImgUri = data.data!!
//      we will get the data in the Uri format from the Gallery !!!

                    Log.e("Image_Gallery_URI: ", "$selectedImgUri")

                    img_upload_card.setImageURI(selectedImgUri)
//      set the selected image into the view

                    btn_upload_img_upload_act.text = resources.getString(R.string.add_image)
//      Changing the text of button to add image

                    waitToAddImage()
//      A method when user wait for 3 seconds then it will be uploaded into the cloud storage along with the users selected image
                }

            } else if (requestCode == Constants.CAMERA_IMAGE_REQ_CODE) {
//      checking the request code for camera
                val camBit: Bitmap = data!!.extras!!.get("data") as Bitmap
//      we will get the data as bitmap in camera so we have to convert it into uri !!!
                Log.e("Image_camera : ", "$camBit")

                img_upload_card.maxWidth = 385
                img_upload_card.maxHeight = 256

                img_upload_card.setImageBitmap(camBit)
//      set the selected image into view


                selectedImgUri = saveImgInLocStorageGetUri(camBit)

//      to get the uri of an image first we have to store it into the device and then get the Uri of it !!!

                Log.e("Image camera URI : ", "$selectedImgUri")

                btn_upload_img_upload_act.text = resources.getString(R.string.add_image)

                waitToAddImage()
//      A method when user wait for 3 seconds then it will be uploaded into the cloud storage along with the users selected image

            }


        }
    }

    private fun chooseAction() {

        val picdialog = AlertDialog.Builder(this)
        picdialog.setTitle("Select Action")             // an alert dialog for choosing the Action
        val items = arrayOf("Select photo from Gallery", "Capture photo from Camera")
        picdialog.setItems(items) { _, which ->     //setting the items into the dialog and waiting for the users action
            when (which) {
                0 -> chooseImageFromGallery()       //open gallery method
                1 -> chooseImageFromCamera()        //open camera method

            }

        }
        picdialog.show()                            //show the alert dialog
    }

    private fun saveImgInLocStorageGetUri(bitmap: Bitmap): Uri? {

        var uri: Uri? = null
//      First make Uri as Null
        try {
            val fileFol = File(this.cacheDir, "Selected_Images")
            fileFol.mkdirs()
//          Make a directory with name as "Selected_Images"

            val file = File(fileFol, "captured_img.jpg")

//      Make the file with name as a captured_img.jpg .... this image will not be seen in the local storage

            val stream = FileOutputStream(file)
//      making the stream from the file to read it and make it into the uri !!!
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

//      we use compress() for storing the image in the format and quality of the image from the stream

            stream.flush()
            stream.close()

            uri = FileProvider.getUriForFile(
                this.applicationContext,
                "com.shaikshavali.taskthree" + ".provider",
                file
            )
//      we will make the uri from the provider under manifest file and provider paths under provider_paths.xml file
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return uri

    }


    private fun waitToAddImage() {
        Handler().postDelayed({
            saveImageInFirestorage()        // if the user did not hit back button in 3 seconds then the image will be stored in the file storage
        }, 3000)
    }

    private fun saveImageInFirestorage() {
        showProgressDialog(resources.getString(R.string.uploading_image))
//  saving the images in the firestore
        val storageRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            "${userData!!.name}_Images__"
                    + System.currentTimeMillis() + "." + getFileExtension(selectedImgUri!!)
        )
//  creating the object of the storage reference with file name and extension of the image
        storageRef.putFile(selectedImgUri!!)
//  storing the images in the fire storage
            .addOnSuccessListener { task ->
                hideProgressDialog()
                task.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                    imageURL = uri.toString()
                    Log.e("URL : FireStorage ", uri.toString())
//  After successfully storing the images in the fire storage we get the image url
                    updateUserImages(imageURL)
//  we will store the image url into the selected images of the user.
                }
            }.addOnFailureListener {
                hideProgressDialog()
//  If it does not success then log the error message
                Log.e("Fire Storage : ", "${it.message}")
            }
    }

    private fun updateUserImages(imageURL: String) {
        userData!!.images.add(imageURL)
//  Add the url to the images of the user data and update it in the firebase
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().updateUser(this, userData!!)
    }

    private fun getFileExtension(selectedImgUri: Uri?): String? {
//  It is used to get the file extension of the Uri to save it in the FireStorage...
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(contentResolver.getType(selectedImgUri!!))
//  ex. .jpg, .png, .hief... etc.,
    }


    private fun chooseImageFromCamera() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {

                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(intent, Constants.CAMERA_IMAGE_REQ_CODE)
//  If all the permissions are granted then open the camera and wait for the result with image

                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    showRationalDialogForPermissions()
//  If any permission is denied then show the Alert dialog
                }

            }).onSameThread().check()

    }


    private fun chooseImageFromGallery() {

        Log.e("User @UploadImg ", "chooseImageFromGallery()")
        Dexter.withContext(this@UploadImageActivity)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {

                        val intent =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//                        If the user choosen the From Gallery then make an Intent which directs to the Gallery
                        startActivityForResult(intent, Constants.IMAGE_REQUEST_CODE)
//                        Start an activity with result as a Image from the Gallery

                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    showRationalDialogForPermissions()
//                    If the permission has denied then show dialog
                }

            }).onSameThread().check()
        //perform the permission action on the same thread.

    }


    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("You have Cancelled the permissions, You can Allow them in Application Settings")
            .setPositiveButton("GO TO SETTINGS") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
//     an alert dialog with positive button as settings which redirected to the app settings to enable the permissions

                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
//      Set negative button to cancel the dialog
            }
            .show()
//        show the dialog to the user
    }


    private fun setupActionBar() {
        setSupportActionBar(toolbar_upload_img)
        val action = supportActionBar
        if (action != null) {
            action.setDisplayHomeAsUpEnabled(true)                  //set up the action bar here
            action.setHomeAsUpIndicator(R.drawable.back_img_clr_black)
            action.title = ""
        }
        toolbar_upload_img.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun userUpdateSuccess() {

        hideProgressDialog()
        showStatusDialog(
            this,
            resources.getString(R.string.successfully_updated),
            userData!!.fcmToken
        )
//  After successfully updating the user in the firebase show the status dialog as successfully updated and
//  make the result of the task waiting for the image as OK it will be redirected to the Home Screen
        Activity.RESULT_OK

    }


}

