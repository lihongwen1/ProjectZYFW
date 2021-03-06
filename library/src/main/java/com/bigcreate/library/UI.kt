package com.bigcreate.library

import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.ColorLong
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.lang.Exception

/**
 * Create by Sanlorng on 2018/4/9
 */
fun Window.statusBarTransucent(){
    this.statusBarColor = this.context.getColor(R.color.statusbarColor)
    this.fitSystemLayout()
}

fun Window.statusBarLight(light: Boolean){
    var ui = this.decorView.systemUiVisibility
    val nightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    ui = if (light && nightMode == Configuration.UI_MODE_NIGHT_NO)
        ui or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    else
        ui and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
    this.decorView.systemUiVisibility = ui
}

fun Window.navigationBarTransucent(){
    this.navigationBarColor = this.context.getColor(R.color.navigationColor)
    this.fitSystemLayout()
}
@TargetApi(26)
fun Window.navigationBarLight(light: Boolean){
    var ui = this.decorView.systemUiVisibility
    if (light)
        if (Build.VERSION.SDK_INT >= 26)
            ui = ui or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        else
            if (Build.VERSION.SDK_INT >= 26)
                ui = ui and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
    this.decorView.systemUiVisibility = ui
}

fun Window.systemLight(light: Boolean){
    statusBarLight(light)
    navigationBarLight(light)
}

fun Window.fitSystemLayout(){
    var ui = this.decorView.systemUiVisibility
    ui = ui or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    this.decorView.systemUiVisibility = ui
    this.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

}
fun Window.translucentSystemUI(){
    this.translucentSystemUI(false)
}
fun Window.translucentSystemUI(light: Boolean){
    val enable = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("status_bar_mask",false)
    this.statusBarColor = if (enable)
        context.getColor(R.color.statusbarColor)
    else
        context.getColor(R.color.zeroColor)
    this.navigationBarColor = this.context.getColor(R.color.navigationColor)
    this.systemLight(light)
    this.fitSystemLayout()
}
fun Window.systemLowProfile(hide: Boolean){
    var ui = this.decorView.systemUiVisibility
    if (hide)
        ui = ui or View.SYSTEM_UI_FLAG_LOW_PROFILE
    else
        ui = ui and View.SYSTEM_UI_FLAG_LOW_PROFILE.inv()
    this.decorView.systemUiVisibility = ui
}

fun Context.exceptionDialog(exception: Exception) {
    val dialog = AlertDialog.Builder(this)
    dialog.setTitle(exception::class.toString())
    dialog.setMessage(exception.message)
    dialog.setCancelable(true)
    dialog.setPositiveButton("OK") {
        _, _ ->
    }
    dialog.show()
}
fun Window.setFullTruncentStatusBar(){
    this.statusBarColor = this.context.getColor(R.color.navigationColor)
}



fun Intent.startBy(context:Context?){
    context?.startActivity(this)
}
inline fun <reified T:Activity> Context.startActivity(bundle: Bundle? = null,block:(Intent.() -> Unit)){
    val intent = Intent(this,T::class.java)
    block(intent)
    startActivity(intent,bundle)
}

fun Window.openStatusBarMask(enable:Boolean){
    val isOpen = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("status_bar_mask",false)
    Log.d("window pacakge",context.packageName)
    if (isOpen != enable) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putBoolean("status_bar_mask", enable)
                .apply()
        statusBarColor = if (enable)
            context.getColor(R.color.statusbarColor)
        else
            context.getColor(R.color.zeroColor)
    }
}

fun Window.defaultStatusBarMask(){
    PreferenceManager.getDefaultSharedPreferences(context).getBoolean("status_bar_mask",true)
}
fun Window.setStatusBarMask(value: Boolean){
    statusBarColor = if (value)
        context.getColor(R.color.statusbarColor)
    else
        context.getColor(R.color.zeroColor)
}
fun Context.dialog(title:String, content:String,posButton:String,posListener:DialogInterface.OnClickListener){
    androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(content)
            .setCancelable(true)
            .setPositiveButton(posButton,posListener)
            .create().apply {
                setDefaultStyle(TypedValue().run {
                    theme.resolveAttribute(R.attr.colorAccent,this,true)
                    data
                })
            }.show()
}

fun Context.dialog(title:String, content:String,posButton:String,posListener:DialogInterface.OnClickListener,negButton:String, negListener:DialogInterface.OnClickListener){

    MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setMessage(content)
            .setCancelable(true)
            .setPositiveButton(posButton,posListener)
            .setNegativeButton(negButton,negListener)
            .create()
            .show()
//            .create().apply {
//                setDefaultStyle(TypedValue().run {
//                    theme.resolveAttribute(R.attr.colorAccent,this,true)
//                    data
//                })
//            }.show()
}

fun String.isEmpty():Boolean{
    return TextUtils.isEmpty(this)
}

fun EditText.string():String{
    return this.text.toString()
}

fun EditText.isEmpty():Boolean{
    return text.isEmpty()
}
fun Context.toast(string: String){
    toast(string,Toast.LENGTH_SHORT)
}
fun Context.toast(string: String,length:Int){
    Toast.makeText(this,string,length).show()
}
fun Context.longToast(string: String){
    toast(string,Toast.LENGTH_LONG)
}
var View.isVisible:Boolean
set(value) {
    visibility = if (value)
        View.VISIBLE
    else
        View.GONE
}
get() {
    return visibility == View.VISIBLE
}


fun MenuItem.setIconTint(@ColorLong id: Int){
    icon = icon.apply {
        DrawableCompat.setTint(this, id)
    }
}

var MenuItem.iconTintListCompact: ColorStateList?
set(value) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        iconTintList = value
    }else {
        icon?.apply {
            DrawableCompat.setTintList(this,value)
        }
    }
}
get() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        return iconTintList
    }else {
        return null
    }
}

fun androidx.appcompat.app.AlertDialog.setDefaultStyle(color:Int){
    val array = arrayOf(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE,
            androidx.appcompat.app.AlertDialog.BUTTON_NEUTRAL,
            androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
    array.forEach {
        getButton(it)?.apply {
            background = ColorDrawable(Color.parseColor("#00000000"))
            setTextColor(color)
        }
    }
}

fun Context.startInstallApp(file: File) {
    Intent(Intent.ACTION_VIEW).apply {
        if (BuildConfig.DEBUG)
            Log.e("startInstallApp","true")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val apkUri = FileProvider.getUriForFile(this@startInstallApp, packageName+ ".file.provider",file)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setDataAndType(apkUri,"application/vnd.android.package-archive")
            Log.e("uri",apkUri.path)
        } else {
            setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive")
        }
        if (BuildConfig.DEBUG)
            Log.e("startActivity","true")
        startActivity(this)
    }
}
val Context.statusBarHeight
get() = resources.getDimensionPixelOffset(resources.getIdentifier("status_bar_height", "dimen", "android"))
fun Fragment.toast(str: String) {
    context?.toast(str)
}

inline fun <reified T:Activity> Fragment.startActivity(bundle: Bundle? = null, block: (Intent.() -> Unit)) {
    context?.startActivity<T>(bundle, block)
}
inline fun <reified T:Activity> Fragment.startActivity(bundle: Bundle? = null) {
    context?.startActivity<T>(bundle)
}
inline fun <reified T: Activity> Context.startActivity(bundle: Bundle? = null) {
    val intent = Intent(this,T::class.java)
    startActivity(intent)
}


