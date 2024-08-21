package ai.rtvi.client.basicdemo

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf

object Preferences {

    private const val PREF_BACKEND_URL = "backend_url"
    private const val PREF_API_KEY = "api_key"

    private lateinit var prefs: SharedPreferences

    fun initAppStart(context: Context) {
        prefs = context.applicationContext.getSharedPreferences("prefs", Context.MODE_PRIVATE)

        listOf(backendUrl, apiKey).forEach { it.init() }
    }

    private fun getString(key: String): String? = prefs.getString(key, null)

    class StringPref(private val key: String) {
        private val cachedValue = mutableStateOf<String?>(null)

        fun init() {
            cachedValue.value = getString(key)
            prefs.registerOnSharedPreferenceChangeListener { _, changedKey ->
                if (key == changedKey) {
                    cachedValue.value = getString(key)
                }
            }
        }

        var value: String?
            get() = cachedValue.value
            set(newValue) {
                cachedValue.value = newValue
                prefs.edit().putString(key, newValue).apply()
            }
    }

    val backendUrl = StringPref(PREF_BACKEND_URL)
    val apiKey = StringPref(PREF_API_KEY)
}