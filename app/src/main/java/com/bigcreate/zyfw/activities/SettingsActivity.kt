package com.bigcreate.zyfw.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bigcreate.library.transucentSystemUI
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.defaultSharedPreferences
import com.bigcreate.zyfw.base.myApplication
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar_settings)
        toolbar_settings.setNavigationOnClickListener {
            finish()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        window.transucentSystemUI(true)
        if (Attributes.loginUserInfo== null)
            account_exit_button.visibility = View.GONE
        else
            account_exit_button.visibility = View.VISIBLE
        account_exit_button.setOnClickListener {
            myApplication?.loginToken = null
            defaultSharedPreferences.edit()
                    .putString("username","")
                    .putString("password","")
                    .putBoolean("saved_account",false)
                    .apply()
            finish()
        }

    }
}
