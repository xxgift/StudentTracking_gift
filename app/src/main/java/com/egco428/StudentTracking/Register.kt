package com.egco428.StudentTracking

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.bestregister.*
import kotlinx.android.synthetic.main.register.*

class Register:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bestregister)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        register_btn.setOnClickListener {
            val intent = Intent(this@Register, HomeTeacher::class.java)
            startActivity(intent)
        }
    }

}