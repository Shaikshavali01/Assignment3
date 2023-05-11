package com.shaikshavali.taskthree.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import com.shaikshavali.taskthree.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupActionBar()
        btn_login.setOnClickListener {
            val intent =Intent(this,LoginActivity::class.java)
            startActivity(intent)

        }

        btn_signup.setOnClickListener {
            val intent =Intent(this,SignupActivity::class.java)
            startActivity(intent)

        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.settings_img,menu)

    }
    private fun setupActionBar(){
        setSupportActionBar(toolbar_welcome_screen)
        val action = supportActionBar
        if(action!= null){
            action.title=""
//            action.setDisplayHomeAsUpEnabled(true)
           // action.setHomeAsUpIndicator(R.drawable.img_back_arrow)

        }


    }

}