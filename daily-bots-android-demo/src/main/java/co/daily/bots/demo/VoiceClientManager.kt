package co.daily.bots.demo

import ai.rtvi.client.VoiceClient
import ai.rtvi.client.VoiceClientOptions
import ai.rtvi.client.VoiceEventCallbacks
import co.daily.bots.demo.utils.Timestamp
import ai.rtvi.client.daily.DailyVoiceClient
import ai.rtvi.client.result.Future
import ai.rtvi.client.result.Result
import ai.rtvi.client.result.VoiceError
import ai.rtvi.client.types.ActionDescription
import ai.rtvi.client.types.Option
import ai.rtvi.client.types.Participant
import ai.rtvi.client.types.PipecatMetrics
import ai.rtvi.client.types.ServiceConfig
import ai.rtvi.client.types.ServiceRegistration
import ai.rtvi.client.types.Tracks
import ai.rtvi.client.types.Transcript
import ai.rtvi.client.types.TransportState
import ai.rtvi.client.types.Value
import android.content.Context
import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf

@Immutable
data class Error(val message: String)

@Stable
class VoiceClientManager(private val context: Context) {

    companion object {
        private const val TAG = "VoiceClientManager"
    }

    @Immutable
    data class InitOptions(
        val botProfile: BotProfile,
        val ttsProvider: TTSProvider,
        val llmProvider: LLMProvider,
    ) {
        companion object {
            fun default() = ConfigConstants.botProfiles.default.let { botProfile ->
                InitOptions(
                    botProfile = botProfile,
                    ttsProvider = botProfile.ttsProviders.default,
                    llmProvider = botProfile.llmProviders.default,
                )
            }
        }
    }

    @Immutable
    data class RuntimeOptions(
        val ttsVoice: TTSOptionVoice,
        val llmModel: LLMOptionModel,
    ) {
        companion object {
            fun default() = ConfigConstants.botProfiles.default.let { botProfile ->
                RuntimeOptions(
                    ttsVoice = botProfile.ttsProviders.default.voices.default,
                    llmModel = botProfile.llmProviders.default.models.default,
                )
            }
        }
    }

    private val client = mutableStateOf<VoiceClient?>(null)

    val state = mutableStateOf<TransportState?>(null)

    val errors = mutableStateListOf<Error>()

    val actionDescriptions =
        mutableStateOf<Result<List<ActionDescription>, VoiceError>?>(null)

    val expiryTime = mutableStateOf<Timestamp?>(null)

    val botReady = mutableStateOf(false)
    val botIsTalking = mutableStateOf(false)
    val userIsTalking = mutableStateOf(false)
    val botAudioLevel = mutableFloatStateOf(0f)
    val userAudioLevel = mutableFloatStateOf(0f)

    val mic = mutableStateOf(false)
    val camera = mutableStateOf(false)
    val tracks = mutableStateOf<Tracks?>(null)

    private fun <E> Future<E, VoiceError>.displayErrors() = withErrorCallback {
        Log.e(TAG, "Future resolved with error: ${it.description}", it.exception)
        errors.add(Error(it.description))
    }

    fun start(
        baseUrl: String,
        apiKey: String?,
        initOptions: InitOptions,
        runtimeOptions: RuntimeOptions,
    ) {

        if (client.value != null) {
            return
        }

        val options = VoiceClientOptions(
            services = listOf(
                ServiceRegistration("tts", initOptions.ttsProvider.id),
                ServiceRegistration("llm", initOptions.llmProvider.id),
            ),
            config = listOf(
                ServiceConfig(
                    "tts", listOf(
                        Option("voice", runtimeOptions.ttsVoice.id)
                    )
                ),
                ServiceConfig(
                    "llm", listOf(
                        Option("model", runtimeOptions.llmModel.id),
                        Option(
                            "initial_messages", Value.Array(
                                Value.Object(
                                    "role" to Value.Str("system"),
                                    "content" to Value.Str("You are a helpful voice assistant. Keep answers brief, and do not include markdown or other formatting in your responses, as they will be read out using TTS. Please greet the user and offer to assist them.")
                                )
                            )
                        ),
                        Option("run_on_config", true),
                    )
                )
            ),
            // Note: For security reasons, don't include your API key in a production
            // client app. See: https://docs.dailybots.ai/architecture
            customHeaders = apiKey
                ?.takeUnless { it.isEmpty() }
                ?.let { listOf("Authorization" to "Bearer $it") }
                ?: emptyList(),
            customBodyParams = listOf(
                "bot_profile" to Value.Str(initOptions.botProfile.id),
                "max_duration" to Value.Number(600.0)
            )
        )

        state.value = TransportState.Idle

        val callbacks = object : VoiceEventCallbacks() {
            override fun onTransportStateChanged(state: TransportState) {
                this@VoiceClientManager.state.value = state
            }

            override fun onBackendError(message: String) {
                "Error from backend: $message".let {
                    Log.e(TAG, it)
                    errors.add(Error(it))
                }
            }

            override fun onBotReady(version: String, config: List<ServiceConfig>) {

                Log.i(TAG, "Bot ready. Version $version, config: $config")

                botReady.value = true

                client.value?.describeActions()?.withCallback {
                    actionDescriptions.value = it
                }
            }

            override fun onPipecatMetrics(data: PipecatMetrics) {
                Log.i(TAG, "Pipecat metrics: $data")
            }

            override fun onUserTranscript(data: Transcript) {
                Log.i(TAG, "User transcript: $data")
            }

            override fun onBotTranscript(text: String) {
                Log.i(TAG, "Bot transcript: $text")
            }

            override fun onBotStartedSpeaking() {
                Log.i(TAG, "Bot started speaking")
                botIsTalking.value = true
            }

            override fun onBotStoppedSpeaking() {
                Log.i(TAG, "Bot stopped speaking")
                botIsTalking.value = false
            }

            override fun onUserStartedSpeaking() {
                Log.i(TAG, "User started speaking")
                userIsTalking.value = true
            }

            override fun onUserStoppedSpeaking() {
                Log.i(TAG, "User stopped speaking")
                userIsTalking.value = false
            }

            override fun onTracksUpdated(tracks: Tracks) {
                this@VoiceClientManager.tracks.value = tracks
            }

            override fun onInputsUpdated(camera: Boolean, mic: Boolean) {
                this@VoiceClientManager.camera.value = camera
                this@VoiceClientManager.mic.value = mic
            }

            override fun onConnected() {
                expiryTime.value = client.value?.expiry?.let(Timestamp::ofEpochSecs)
            }

            override fun onDisconnected() {
                expiryTime.value = null
                actionDescriptions.value = null
                botIsTalking.value = false
                userIsTalking.value = false
                state.value = null
                actionDescriptions.value = null
                botReady.value = false
                tracks.value = null

                client.value?.release()
                client.value = null
            }

            override fun onUserAudioLevel(level: Float) {
                userAudioLevel.floatValue = level
            }

            override fun onRemoteAudioLevel(level: Float, participant: Participant) {
                botAudioLevel.floatValue = level
            }
        }

        val client = DailyVoiceClient(context, baseUrl, callbacks, options)

        client.start().displayErrors().withErrorCallback {
            callbacks.onDisconnected()
        }

        this.client.value = client
    }

    fun enableCamera(enabled: Boolean) {
        client.value?.enableCam(enabled)?.displayErrors()
    }

    fun enableMic(enabled: Boolean) {
        client.value?.enableMic(enabled)?.displayErrors()
    }

    fun toggleCamera() = enableCamera(!camera.value)
    fun toggleMic() = enableMic(!mic.value)

    fun stop() {
        client.value?.disconnect()?.displayErrors()
    }

    fun action(service: String, action: String, args: Map<String, Value>) =
        client.value?.action(
            service = service,
            action = action,
            arguments = args.map { Option(it.key, it.value) })?.displayErrors()
}