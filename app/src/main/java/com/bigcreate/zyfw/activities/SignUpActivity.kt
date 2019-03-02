package com.bigcreate.zyfw.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.bigcreate.library.transucentSystemUI
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.fragments.SetupInfoFragment
import com.bigcreate.zyfw.fragments.SignUpFragment

class SignUpActivity : AppCompatActivity() {
    private var fragmentTransaction: FragmentTransaction? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        window.transucentSystemUI(true)
        val type = intent.type
        fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction?.run {
            if (type != null && type == "setupInfo")
                replace(R.id.container_sign_up, SetupInfoFragment())
            else
                replace(R.id.container_sign_up, SignUpFragment())

            commit()
        }

    }

    override fun onBackPressed() {
        val type = intent.getStringExtra("type")
        if (type != null && type == "setupInfo")
            Toast.makeText(this, "请设置完你的个人信息，再进行其他操作", Toast.LENGTH_SHORT).show()
        else
            super.onBackPressed()
    }
}
