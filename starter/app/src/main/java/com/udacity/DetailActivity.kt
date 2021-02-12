package com.udacity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
    }

    companion object{
        const val downloadTitle = "ARG_NAME"
        const val downloadStatus = "ARG_STATUS"

        fun getIntent(context : Context, title : String, status : Boolean) : Intent{
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(downloadTitle, title)
            intent.putExtra(downloadStatus, status)
            return intent
        }
    }
}
