package co.daily.bots.demo

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private val JSON_INSTANCE = Json { ignoreUnknownKeys = true }

object Preferences {

    private const val PREF_BACKEND_URL = "backend_url"
    private const val PREF_API_KEY = "api_key"
    private const val PREF_INIT_OPTIONS = "init_options"

    private lateinit var prefs: SharedPreferences

    fun initAppStart(context: Context) {
        prefs = context.applicationContext.getSharedPreferences("prefs", Context.MODE_PRIVATE)

        listOf(backendUrl, apiKey, lastInitOptions).forEach { it.init() }
    }

    private fun getString(key: String): String? = prefs.getString(key, null)

    interface BasePref {
        fun init()
    }

    class StringPref(private val key: String): BasePref {
        private val cachedValue = mutableStateOf<String?>(null)

        override fun init() {
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

    class JsonPref<E>(private val key: String, private var serializer: KSerializer<E>): BasePref {
        private val cachedValue = mutableStateOf<E?>(null)

        private fun lookupValue(): E? =
            getString(key)?.let { JSON_INSTANCE.decodeFromString(serializer, it) }

        override fun init() {
            cachedValue.value = lookupValue()
            prefs.registerOnSharedPreferenceChangeListener { _, changedKey ->
                if (key == changedKey) {
                    cachedValue.value = lookupValue()
                }
            }
        }

        var value: E?
            get() = cachedValue.value
            set(newValue) {
                cachedValue.value = newValue
                prefs.edit()
                    .putString(key, newValue?.let { JSON_INSTANCE.encodeToString(serializer, it) })
                    .apply()
            }
    }

    val backendUrl = StringPref(PREF_BACKEND_URL)
    val apiKey = StringPref(PREF_API_KEY)
    val lastInitOptions = JsonPref(PREF_INIT_OPTIONS, LastInitOptions.serializer())
}

@Serializable
data class LastInitOptions(
    val botProfile: String? = null,
    val ttsProvider: String? = null,
    val ttsVoice: String? = null,
    val llmProvider: String? = null,
    val llmModel: String? = null,
    val sttProvider: String? = null,
    val sttModel: String? = null,
    val sttLanguage: String? = null,
) {
    companion object {
        fun from(initOptions: VoiceClientManager.InitOptions, runtimeOptions: VoiceClientManager.RuntimeOptions) = LastInitOptions(
            botProfile = initOptions.botProfile.id,
            ttsProvider = initOptions.ttsProvider.id,
            ttsVoice = runtimeOptions.ttsVoice.id,
            llmProvider = initOptions.llmProvider.id,
            llmModel = runtimeOptions.llmModel.id
        )
    }

    fun inflateInit(): VoiceClientManager.InitOptions {

        val botProfile = ConfigConstants.botProfiles.byIdOrDefault(botProfile)

        return VoiceClientManager.InitOptions(
            botProfile = botProfile,
            ttsProvider = botProfile.ttsProviders.byIdOrDefault(ttsProvider),
            llmProvider = botProfile.llmProviders.byIdOrDefault(llmProvider),
            sttProvider = botProfile.sttProviders.byIdOrDefault(sttProvider)
        )
    }

    fun inflateRuntime(initOptions: VoiceClientManager.InitOptions): VoiceClientManager.RuntimeOptions {

        val sttModelInstance = initOptions.sttProvider.models.byIdOrDefault(sttModel)

        return VoiceClientManager.RuntimeOptions(
            ttsVoice = initOptions.ttsProvider.voices.byIdOrDefault(ttsVoice),
            llmModel = initOptions.llmProvider.models.byIdOrDefault(llmModel),
            sttModel = sttModelInstance,
            sttLanguage = sttModelInstance.languages.byIdOrDefault(sttLanguage)
        )
    }
}