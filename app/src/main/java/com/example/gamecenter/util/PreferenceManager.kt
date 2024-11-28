package com.example.gamecenter.util

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "gamecenterPrefs"
        private const val KEY_USER_ID = "USER_ID"
        private const val KEY_USER_EMAIL = "USER_EMAIL"
        private const val KEY_USER_TOKEN = "USER_TOKEN"
    }

    fun saveUserToken(token: String) {
        prefs.edit().putString(KEY_USER_TOKEN, token).apply()
    }

    fun getUserToken(): String {
        return prefs.getString(KEY_USER_TOKEN, "") ?: ""
    }

    fun saveUserId(id: Int) {
        prefs.edit().putInt(KEY_USER_ID, id).apply()
    }

    fun getUserId(): Int {
        return prefs.getInt(KEY_USER_ID, -1)
    }

    fun saveUserEmail(email: String) {
        prefs.edit().putString(KEY_USER_EMAIL, email).apply()
    }

    fun getUserEmail(): String? {
        return prefs.getString(KEY_USER_EMAIL, null)
    }

    fun clearPreferences() {
        prefs.edit().clear().apply()
    }
}
