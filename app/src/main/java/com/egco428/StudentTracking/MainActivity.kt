package com.egco428.StudentTracking

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.login.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        //first commit
        login_btn.setOnClickListener {
            val intent = Intent(this@MainActivity, HomeTeacher::class.java)
            startActivity(intent)
        }

        creatnewacc_btn.setOnClickListener {
            val intent = Intent(this@MainActivity, Register::class.java)
            startActivity(intent)
       }
    }

}
