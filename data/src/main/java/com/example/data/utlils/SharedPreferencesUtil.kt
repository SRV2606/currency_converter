package com.example.data.utlils

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.WorkerThread
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferenceUtil @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {

        private val gson = Gson()
        const val EXTRA_USER_ID = "EXTRA_USER_ID"
        const val EXCHANGE_API_KEY = "EXCHANGE_API_KEY"

    }

    private val preferences =
        context.getSharedPreferences("CurrencyApp", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor by lazy { preferences.edit() }


    fun getBoolean(key: String, value: Boolean): Boolean {
        return preferences.getBoolean(key, value)
    }

    fun getBoolean(key: String): Boolean {
        return getBoolean(key, false)
    }

    fun getFloat(key: String, value: Float): Float {
        return preferences.getFloat(key, value)
    }

    fun getFloat(key: String): Float {
        return getFloat(key, 0f)
    }

    fun getInt(key: String, value: Int): Int {
        return preferences.getInt(key, value)
    }

    fun getInt(key: String): Int {
        return getInt(key, 0)
    }

    fun getLong(key: String, value: Long): Long {
        return preferences.getLong(key, value)
    }

    fun getLong(key: String): Long {
        return getLong(key, 0L)
    }

    fun getString(key: String, default: String): String {
        return preferences.getString(key, default) ?: default
    }

    fun getString(key: String): String {
        return getString(key, "")
    }

    fun putBoolean(key: String, value: Boolean) {
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun putFloat(key: String, value: Float) {
        editor.putFloat(key, value)
        editor.apply()
    }

    fun putInt(key: String, value: Int) {
        editor.putInt(key, value)
        editor.apply()
    }

    fun putLong(key: String, value: Long) {
        editor.putLong(key, value)
        editor.apply()
    }

    @WorkerThread
    suspend fun putString(key: String, value: String) {
        editor.putString(key, value)
        editor.commit()
    }

    fun putStringSync(key: String, value: String) {
        editor.putString(key, value)
        editor.commit()
    }

    fun toggle(key: String) {
        setEnabled(key, !isEnabled(key))
    }

    fun isEnabled(key: String): Boolean {
        return getBoolean(key)
    }

    fun setEnabled(preferenceKey: String, b: Boolean) {
        editor.putBoolean(preferenceKey, b).apply()
    }

    fun clearAll() {
        editor.clear().commit()
    }

    fun clear(key: String) {
        editor.remove(key).apply()
    }


    suspend fun putUserId(value: String) {
        putString(EXTRA_USER_ID, value)
    }


    fun getUserId(): String = getString(EXTRA_USER_ID, "")


}
