package com.bigcreate.zyfw.activities

import android.app.NotificationChannelGroup
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bigcreate.library.isVisible
import com.bigcreate.library.startActivity
import com.bigcreate.library.toast
import com.bigcreate.library.translucentSystemUI
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.base.*
import com.bigcreate.zyfw.models.LoginModel
import com.bigcreate.zyfw.models.LoginRequest
import com.bigcreate.zyfw.mvp.user.LoginImpl
import com.bigcreate.zyfw.service.CheckUpdateService
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_launch.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URI

class LaunchActivity : AppCompatActivity(), LoginImpl.View {
    private val presenter = LoginImpl(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)
        window.translucentSystemUI(true)
        try {
            //Log.e("startService", "true")
            startService(Intent(this, CheckUpdateService::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        when {
            hasLoginInfo -> {
                GlobalScope.launch {
                    val request = LoginRequest(defaultSharedPreferences.getString("username", "")!!, defaultSharedPreferences.getString("password", "")!!)
                    presenter.doRequest(request)
                }
            }
            else -> {
                showProgress(false)
            }

        }
        buttonLoginLaunch.setOnClickListener {
            startActivityForResult(Intent(this@LaunchActivity, LoginActivity::class.java), RequestCode.LOGIN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RequestCode.LOGIN -> {
                if (resultCode == ResultCode.OK) {
                    startActivity<MainActivity>()
                    finish()
                }
            }
        }
    }

    override fun getViewContext(): Context {
        return this
    }


    override fun onLoginFailed(response: JsonObject) {
        showProgress(false)
        toast("登陆失败")
    }

    override fun onLoginSuccess(loginInfo: LoginModel) {
        Attributes.loginUserInfo = loginInfo
        startActivity<MainActivity>()
        finish()
    }

    override fun onNetworkFailed() {
        toast("网络连接失败")
    }

    override fun onRequesting() {
        showProgress(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    private fun showProgress(value: Boolean) {
        buttonLoginLaunch.isVisible = !value
        progressLaunch.isVisible = value
    }
}
