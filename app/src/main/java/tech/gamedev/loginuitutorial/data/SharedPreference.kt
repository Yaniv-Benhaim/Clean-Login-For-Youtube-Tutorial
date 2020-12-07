package tech.gamedev.loginuitutorial.data

import android.content.Context
import android.content.SharedPreferences

class SharedPreference(context:Context) {

    private val PREFS_NAME = "saved_login"
    val sharedPref: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveUsername(KEY_NAME: String, value: String){
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(KEY_NAME, value)
        editor.apply()
    }

    fun savePassword(KEY_NAME: String, value: String){
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(KEY_NAME, value)
        editor.apply()
    }

    fun saveRememberMe(KEY_NAME: String ,isChecked: Boolean){
        val editor = sharedPref.edit()
        editor.putBoolean(KEY_NAME,isChecked)
        editor.apply()
    }

    fun getValueString(KEY_NAME: String): String? {

        return sharedPref.getString(KEY_NAME, null)
    }

    fun deleteUserNameAndPassWord(){
        val editor = sharedPref.edit()
        editor.remove("USER_NAME")
        editor.remove("PASS_WORD")
        editor.apply()
    }



}