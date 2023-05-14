package com.shaikshavali.taskthree.activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.shaikshavali.taskthree.R
import com.shaikshavali.taskthree.databinding.StatusImageBinding
import com.shaikshavali.taskthree.utils.Constants
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dialog_progress.*
import kotlinx.android.synthetic.main.status.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

open class BaseActivity : AppCompatActivity() {

    private lateinit var progressDialog: Dialog

    private lateinit var statusDialog: Dialog


    fun showProgressDialog(text: String) {
        progressDialog = Dialog(this)
        progressDialog.setContentView(R.layout.dialog_progress)
        progressDialog.tv_progress.text = text

        progressDialog.show()
    }

    fun hideProgressDialog() {
        progressDialog.dismiss()
    }


    fun showStatusDialog(activity: Activity, status: String, token: String) {
        statusDialog = Dialog(this)
        statusDialog.setContentView(R.layout.status)
        statusDialog.tv_status.text = status
        Log.e("Status @Base", status)
        statusDialog.show()

        Log.e("FCM token @Base", token)

        BaseActivity().SendNotificationAsyncTask(status, token).execute()

        Handler().postDelayed({
            goToHomeScreen(activity)
        }, 2000)
    }

    private fun goToHomeScreen(activity: Activity) {

        statusDialog.dismiss()
        val intent = Intent(activity, HomeScreen::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()

    }

    fun showImageDialog(imagePath: String) {
        val dialog = Dialog(this)
        val dialogBinding = StatusImageBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        Picasso.get()
            .load(imagePath)
            .resize(380, 256)
            .into(dialogBinding.imgStatusLayout)

        // Close the dialog when clicked
        dialogBinding.imgStatusLayout.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


    fun getCurrentUserID(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }


    inner class SendNotificationAsyncTask(
        private val msgStatus: String,
        private val token: String
    ) :
        AsyncTask<Void, Void, String>() {


        override fun doInBackground(vararg params: Void): String {
            var result: String
            var connection: HttpURLConnection? = null
            try {
                val url = URL(Constants.FCM_BASE_URL) // Base Url
                connection = url.openConnection() as HttpURLConnection

                connection.doOutput = true
                connection.doInput = true

                connection.instanceFollowRedirects = false

                connection.requestMethod = "POST"

                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("charset", "utf-8")
                connection.setRequestProperty("Accept", "application/json")

                connection.setRequestProperty(
                    Constants.FCM_AUTHORIZATION, "${Constants.FCM_KEY}=${Constants.FCM_SERVER_KEY}"
                )

                connection.useCaches = false

                val wr = DataOutputStream(connection.outputStream)

                val jsonRequest =
                    JSONObject()                          //contains data to whom we have to send it
                val dataObject = JSONObject()

                dataObject.put(Constants.FCM_KEY_TITLE, "Status")

                dataObject.put(
                    Constants.FCM_KEY_MESSAGE,
                    msgStatus                //contains all the data of the notification
                )

                jsonRequest.put(Constants.FCM_KEY_DATA, dataObject)     //
                jsonRequest.put(Constants.FCM_KEY_TO, token)

                wr.writeBytes(jsonRequest.toString())
                wr.flush()
                wr.close()

                val httpResult: Int = connection.responseCode

                if (httpResult == HttpURLConnection.HTTP_OK) {

                    val inputStream = connection.inputStream

                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val sb = StringBuilder()
                    var line: String?
                    try {

                        while (reader.readLine().also { line = it } != null) {
                            sb.append(line + "\n")
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } finally {
                        try {

                            inputStream.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    result = sb.toString()
                } else {
                    result = connection.responseMessage
                }

            } catch (e: Exception) {
                result = "Error : " + e.message
            } finally {
                connection?.disconnect()
            }
            return result
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)

            Log.e("JSON Response Result", result)

        }
    }

}