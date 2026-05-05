package su.makskok.magisksu.data.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*

object AppSettings {
    // Приватные состояния (Compose-observable)
    var _autoCheckRoot by mutableStateOf(true)
    val autoCheckRoot: Boolean get() = _autoCheckRoot

    var _showNotifications by mutableStateOf(false)
    val showNotifications: Boolean get() = _showNotifications

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        if (prefs != null) return // уже инициализированы
        prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        _autoCheckRoot = prefs!!.getBoolean("auto_check_root", true)
        _showNotifications = prefs!!.getBoolean("show_notifications", false)
    }

    fun setAutoCheckRoot(value: Boolean) {
        _autoCheckRoot = value
        prefs?.edit()?.putBoolean("auto_check_root", value)?.apply()
    }

    fun setShowNotifications(value: Boolean) {
        _showNotifications = value
        prefs?.edit()?.putBoolean("show_notifications", value)?.apply()
    }

    fun resetAll() {
        prefs?.edit()?.clear()?.apply()
        _autoCheckRoot = true
        _showNotifications = false
    }
}