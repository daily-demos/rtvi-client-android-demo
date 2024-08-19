package ai.rtvi.client.basicdemo

import ai.rtvi.client.VoiceClient
import ai.rtvi.client.VoiceClientOptions
import ai.rtvi.client.VoiceEventCallbacks
import ai.rtvi.client.basicdemo.utils.Timestamp
import ai.rtvi.client.daily.DailyVoiceClient
import ai.rtvi.client.result.Result
import ai.rtvi.client.result.VoiceError
import ai.rtvi.client.types.ActionDescription
import ai.rtvi.client.types.Option
import ai.rtvi.client.types.Participant
import ai.rtvi.client.types.PipecatMetrics
import ai.rtvi.client.types.ServiceConfig
import ai.rtvi.client.types.ServiceRegistration
import ai.rtvi.client.types.Transcript
import ai.rtvi.client.types.TransportState
import ai.rtvi.client.types.Value
import android.content.Context
import android.util.Log
import androidx.compose.runtime.FloatState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf

@Stable
class VoiceClientManager(private val context: Context) {

    companion object {
        private const val TAG = "VoiceClientManager"

        private val options = VoiceClientOptions(
            services = listOf(
                ServiceRegistration("tts", "cartesia"),
                ServiceRegistration("llm", "together"),
            ),
            config = listOf(
                ServiceConfig(
                    "tts", listOf(
                        Option("voice", "79a125e8-cd45-4c13-8a67-188112f4dd22")
                    )
                ),
                ServiceConfig(
                    "llm", listOf(
                        Option("model", "meta-llama/Meta-Llama-3.1-8B-Instruct-Turbo"),
                        Option(
                            "initial_messages", Value.Array(
                                Value.Object(
                                    "role" to Value.Str("system"),
                                    "content" to Value.Str("You are a helpful voice assistant.")
                                )
                            )
                        )
                    )
                )
            )
        )
    }

    private val client = mutableStateOf<VoiceClient?>(null)

    private val clientState = mutableStateOf<TransportState?>(null)
    private val clientError = mutableStateOf<VoiceError?>(null)
    private val clientActionDescriptions =
        mutableStateOf<Result<List<ActionDescription>, VoiceError>?>(null)
    private val clientStartTime = mutableStateOf<Timestamp?>(null)

    private val clientBotIsTalking = mutableStateOf(false)
    private val clientUserIsTalking = mutableStateOf(false)
    private val clientBotAudioLevel = mutableFloatStateOf(0f)
    private val clientUserAudioLevel = mutableFloatStateOf(0f)

    private val clientMic = mutableStateOf(false)
    private val clientCamera = mutableStateOf(false)

    val state: State<TransportState?> = clientState
    val error: State<VoiceError?> = clientError
    val actionDescriptions: State<Result<List<ActionDescription>, VoiceError>?> =
        clientActionDescriptions
    val startTime: State<Timestamp?> = clientStartTime

    val botIsTalking: State<Boolean> = clientBotIsTalking
    val userIsTalking: State<Boolean> = clientUserIsTalking
    val botAudioLevel: FloatState = clientBotAudioLevel
    val userAudioLevel: FloatState = clientUserAudioLevel

    val mic: State<Boolean> = clientMic
    val camera: State<Boolean> = clientCamera

    var connectionIndex = 0

    fun start(baseUrl: String) {

        if (client.value != null) {
            return
        }

        clientState.value = TransportState.Idle

        val currentConnectionIndex = connectionIndex

        val callbacks = object : VoiceEventCallbacks() {
            override fun onTransportStateChanged(state: TransportState) {
                if (currentConnectionIndex == connectionIndex) {
                    clientState.value = state
                }
            }

            override fun onBackendError(message: String) {
                Log.e(TAG, "Error from backend: $message")
            }

            override fun onBotReady(version: String, config: List<ServiceConfig>) {

                Log.i(TAG, "Bot ready. Version $version, config: $config")

                client.value?.describeActions()?.withCallback {
                    if (currentConnectionIndex == connectionIndex) {
                        clientActionDescriptions.value = it
                    }
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

            override fun onBotStartedSpeaking(participant: Participant) {
                Log.i(TAG, "Bot started speaking: ${participant.name}")
                clientBotIsTalking.value = true
            }

            override fun onBotStoppedSpeaking(participant: Participant) {
                Log.i(TAG, "Bot stopped speaking")
                clientBotIsTalking.value = false
            }

            override fun onUserStartedSpeaking() {
                Log.i(TAG, "User started speaking")
                clientUserIsTalking.value = true
            }

            override fun onUserStoppedSpeaking() {
                Log.i(TAG, "User stopped speaking")
                clientUserIsTalking.value = false
            }

            override fun onInputsUpdated(camera: Boolean, mic: Boolean) {
                clientCamera.value = camera
                clientMic.value = mic
            }

            override fun onConnected() {
                clientStartTime.value = Timestamp.now()
            }

            override fun onDisconnected() {
                clientStartTime.value = null
                clientActionDescriptions.value = null
                clientBotIsTalking.value = false
                clientUserIsTalking.value = false
                clientError.value = null
                clientState.value = null
                clientActionDescriptions.value = null

                client.value?.release()
                client.value = null
            }
        }

        val client = DailyVoiceClient(context, baseUrl, callbacks, options)

        client.start().withErrorCallback {
            if (currentConnectionIndex == connectionIndex) {
                clientError.value = it
            }
        }

        this.client.value = client
    }

    fun enableCamera(enabled: Boolean) {
        client.value?.enableCam(enabled)
    }

    fun enableMic(enabled: Boolean) {
        client.value?.enableMic(enabled)
    }

    fun toggleCamera() = enableCamera(!camera.value)
    fun toggleMic() = enableMic(!mic.value)

    fun stop() {
        connectionIndex++
        client.value?.disconnect()
    }

    fun action(service: String, action: String, args: Map<String, Value>) =
        client.value?.action(
            service = service,
            action = action,
            arguments = args.map { Option(it.key, it.value) })
}