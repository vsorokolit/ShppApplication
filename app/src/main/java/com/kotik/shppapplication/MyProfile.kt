package com.kotik.shppapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kotik.shppapplication.databinding.ActivityMyProfileBinding

class MyProfile : AppCompatActivity() {

    lateinit var binding: ActivityMyProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


        procAutoLogin()

        binding.logOutButton.setOnClickListener { procClickLofOutButton() }
    }

    private fun procClickLofOutButton() {
        val intent = Intent(this, SignUp::class.java)

        val sharedPreferences = getSharedPreferences("AutoLogin", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("Email")
        editor.remove("Password")
        editor.apply()

        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    private fun procAutoLogin() {
        val sharedPreferences = getSharedPreferences("AutoLogin", MODE_PRIVATE)
        val email = sharedPreferences.getString("Email", null)
        val password = sharedPreferences.getString("Password", null)

        if (email != null && password != null) {
            binding.name.text = email
            binding.career.text = password
        } else {
            binding.name.text = intent.getStringExtra("email")
            binding.career.text = intent.getStringExtra("password")
        }
    }
}