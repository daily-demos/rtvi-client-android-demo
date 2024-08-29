package ai.rtvi.client.basicdemo

import androidx.compose.runtime.Immutable

object ConfigConstants {

    object Together : LLMProvider {

        val Llama8B =
            "Llama 3.1 8B Instruct Turbo" isModel "meta-llama/Meta-Llama-3.1-8B-Instruct-Turbo"
        val Llama70B =
            "Llama 3.1 70B Instruct Turbo" isModel "meta-llama/Meta-Llama-3.1-70B-Instruct-Turbo"
        val Llama405B =
            "Llama 3.1 405B Instruct Turbo" isModel "meta-llama/Meta-Llama-3.1-405B-Instruct-Turbo"

        override val name = "Together AI"
        override val id = "together"

        override val models =
            NamedOptionList(listOf(Llama8B, Llama70B, Llama405B), default = Llama70B)
    }

    object Anthropic : LLMProvider {

        override val name = "Anthropic"
        override val id = "anthropic"

        override val models =
            NamedOptionList(listOf("Claude Sonnet 3.5" isModel "claude-3-5-sonnet-20240620"))
    }

    object Cartesia : TTSProvider {

        override val name = "Cartesia"
        override val id = "cartesia"

        override val voices = NamedOptionList(
            listOf(
                "British lady" isVoice "79a125e8-cd45-4c13-8a67-188112f4dd22",
                "California girl" isVoice "b7d50908-b17c-442d-ad8d-810c63997ed9",
                "Doctor mischief" isVoice "fb26447f-308b-471e-8b00-8e9f04284eb5",
                "Child" isVoice "2ee87190-8f84-4925-97da-e52547f9462c",
                "Merchant" isVoice "50d6beb4-80ea-4802-8387-6c948fe84208",
                "Kentucky man" isVoice "726d5ae5-055f-4c3d-8355-d9677de68937"
            )
        )
    }

    val llmProviders = NamedOptionList<LLMProvider>(listOf(Anthropic, Together), default = Together)

    val ttsProviders = NamedOptionList<TTSProvider>(listOf(Cartesia))

    val botProfiles = NamedOptionList(
        listOf(
            "Voice only" isProfile "voice_2024_08",
            "Voice and vision" isProfile "vision_2024_08"
        )
    )
}

@Immutable
data class NamedOptionList<E : NamedOption>(
    val options: List<E>,
    val default: E = options.first()
) {
    fun byIdOrDefault(id: String?) =
        id?.let { idNonNull -> options.firstOrNull { it.id == idNonNull } } ?: default
}

interface NamedOption {
    val name: String
    val id: String
}

interface TTSProvider : NamedOption {
    override val name: String
    override val id: String
    val voices: NamedOptionList<TTSOptionVoice>
}

interface LLMProvider : NamedOption {
    override val name: String
    override val id: String
    val models: NamedOptionList<LLMOptionModel>
}

data class BotProfile(
    override val name: String,
    override val id: String,
) : NamedOption

data class LLMOptionModel(
    override val name: String,
    override val id: String,
) : NamedOption

data class TTSOptionVoice(
    override val name: String,
    override val id: String,
) : NamedOption

private infix fun String.isModel(id: String) = LLMOptionModel(name = this, id = id)
private infix fun String.isVoice(id: String) = TTSOptionVoice(name = this, id = id)
private infix fun String.isProfile(id: String) = BotProfile(name = this, id = id)