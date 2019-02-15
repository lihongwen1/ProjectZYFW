package com.bigcreate.zyfw.activities

import android.Manifest.permission.READ_CONTACTS
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bigcreate.library.*
import com.bigcreate.zyfw.BuildConfig
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.base.*
import com.bigcreate.zyfw.models.LoginRequest
import com.bigcreate.zyfw.models.LoginByPassResponse
import com.bigcreate.zyfw.mvp.base.LoginModel
import com.bigcreate.zyfw.mvp.user.LoginContract
import com.bigcreate.zyfw.mvp.user.LoginImpl
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.MediaType
import kotlinx.coroutines.GlobalScope

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity(),LoginContract.View {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private var mAuthTask: UserLoginTask? = null
    private val mGson = Gson()
    private var mResponseString: String? = null
    private var mSinOrSup = true
    private var userId:String? = null
    private var registUrl = "ProjectForDaChuang/register"
    private var loginUrl = "ProjectForDaChuang/login"
    val JSON = MediaType.parse("application/json; charset=utf-8")
    val loginPresenter = LoginImpl(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(toolbar_login)
        window.transucentSystemUI(true)
        toolbar_login.setNavigationOnClickListener {
            finish()
        }
        val application = application as MyApplication
        application.loginUser
        supportActionBar?.title = getString(R.string.action_sign_in)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Set up the login form.
        populateAutoComplete()
        password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })

        email_sign_in_button.setOnClickListener {
            mSinOrSup = true
            attemptLogin()
        }
        sign_up.setOnClickListener {
            startActivity(SignUpActivity::class.java)
        }
        /*button_sign_up.setOnClickListener{
            mSinOrSup = false
            attemptLogin()
        }*/
    }

    override fun onResume() {
        if (Attributes.loginUserInfo != null)
            finish()
        super.onResume()
    }

    private fun populateAutoComplete() {
        if (!mayRequestContacts()) {
            return
        }
    }

    private fun mayRequestContacts(): Boolean {
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(email, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok
                    ) { requestPermissions(arrayOf(READ_CONTACTS), REQUEST_READ_CONTACTS) }
        } else {
            requestPermissions(arrayOf(READ_CONTACTS), REQUEST_READ_CONTACTS)
        }
        return false
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete()
            }
        }
    }

    override fun onLoginFailed(response: JsonObject) {
        toast("登录失败")
        toast("s")
        showProgress(false)
    }

    override fun onLoginSuccess(loginInfo: LoginModel) {
        Attributes.loginUserInfo = loginInfo
        setResult(ResultCode.OK)
        finish()
    }

    override fun getViewContext(): Context {
        return this
    }

    override fun onNetworkFailed() {
        toast("网络连接失败")
        showProgress(false)
    }



    override fun onRequesting() {
        showProgress(true)
    }
    override fun onDestroy() {
        super.onDestroy()
        loginPresenter.detachView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            RequestCode.REGISTER-> if (resultCode == ResultCode.OK){setResult(ResultCode.OK);finish()}
        }
    }
    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun attemptLogin() {
        if (mAuthTask != null) {
            return
        }
        email_sign_in_button.isEnabled = false
        // Reset errors.
        email.error = null
        password.error = null

        // Store values at the time of the login attempt.
        val emailStr = email.text.toString()
        val passwordStr = password.text.toString()

        var cancel = false
        var focusView: View? = null

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(passwordStr) && !isPasswordValid(passwordStr)) {
            password.error = getString(R.string.error_invalid_password)
            focusView = password
            cancel = true
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(emailStr)) {
            email.error = getString(R.string.error_field_required)
            focusView = email
            cancel = true
        } else if (!isPhoneValid(emailStr)) {
            email.error = getString(R.string.error_invalid_email)
            focusView = email
            cancel = true
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView?.requestFocus()
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true)
//            mAuthTask = UserLoginTask(emailStr, passwordStr)
//            mAuthTask!!.execute(null as Void?)
            loginPresenter.doLoginByPass(LoginRequest(emailStr,passwordStr))
        }
        email_sign_in_button.isEnabled = true
    }

    private fun isPhoneValid(email: String): Boolean {
        return email.length == 11
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 4
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

        login_form.visibility = if (show) View.GONE else View.VISIBLE
        login_form.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 0 else 1).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        login_form.visibility = if (show) View.GONE else View.VISIBLE
                    }
                })

        login_progress.visibility = if (show) View.VISIBLE else View.GONE
        login_progress.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 1 else 0).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        login_progress.visibility = if (show) View.VISIBLE else View.GONE
                    }
                })
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    @SuppressLint("StaticFieldLeak")
    inner class UserLoginTask internal constructor(private val mEmail: String, private val mPassword: String) : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void): Boolean? {
            // TODO: attempt authentication against a network service.

            return try {
                tryLoginTask()
                val response = mGson.fromJson(mResponseString, LoginByPassResponse::class.java)
                response?.run {
                    userId = content
                }
                response?.token != null
            } catch (e: Exception) {
                false
            }

        }

        override fun onPostExecute(success: Boolean?) {
            mAuthTask = null
            showProgress(false)
            if (success!!) {
                //myApplication?.loginUser = User(mEmail,mPassword,myApplication?.loginToken!!,userId!!)
                defaultSharedPreferences.edit()
                        .putString("user_name",mEmail)
                        .putString("user_pass",mPassword)
                        .putString("user_id",userId)
                        .putString("user_token",myApplication?.loginToken)
                        .apply()
                myApplication?.loginUser = null
                finish()
            } else {
                password.error = getString(R.string.error_incorrect_password)
                password.requestFocus()
            }
        }

        override fun onCancelled() {
            mAuthTask = null
            showProgress(false)
        }

        private fun tryLoginTask() {
            var temp: String? = null
            temp = if (mSinOrSup)
                loginUrl
            else
                registUrl
            //TODO
//            val loginRequest = LoginRequest(ipAddress!!, mEmail, mPassword, null)
            val loginRequest = ""
            Log.d("jsonToServer", Gson().toJson(loginRequest))
            myApplication?.run {
                val response = WebKit.okClient.postRequest(WebInterface.LOGIN_URL,WebInterface.TYPE_JSON,WebKit.gson.toJson(loginRequest))
                mResponseString = response?.string()
                Log.d("response", mResponseString)
                loginToken = try {
                    WebKit.gson.fromJson<LoginByPassResponse>(mResponseString,LoginByPassResponse::class.java::class.java).token
                }catch (e:Exception){
                    Log.d("loginToken ", "null")
                    null
                }
            }
            Log.d("response", mResponseString)

        }
    }
    companion object {

        /**
         * Id to identity READ_CONTACTS permission request.
         */
        private const val REQUEST_READ_CONTACTS = 0

        /**
         * A dummy authentication store containing known user names and passwords.
         * TODO: remove after connecting to a real authentication system.
         */
    }

}
