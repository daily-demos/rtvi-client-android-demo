package ai.rtvi.client.basicdemo

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

        override val models = listOf(Llama8B, Llama70B, Llama405B)
    }

    object Anthropic : LLMProvider {

        override val name = "Anthropic"
        override val id = "anthropic"

        override val models = listOf(
            "Claude Sonnet 3.5" isModel "claude-3-5-sonnet-20240620"
        )
    }

    object Cartesia : TTSProvider {

        override val name = "Cartesia"
        override val id = "cartesia"

        override val voices = listOf(
            "British lady" isVoice "79a125e8-cd45-4c13-8a67-188112f4dd22",
            "California girl" isVoice "b7d50908-b17c-442d-ad8d-810c63997ed9",
            "Doctor mischief" isVoice "fb26447f-308b-471e-8b00-8e9f04284eb5",
            "Child" isVoice "2ee87190-8f84-4925-97da-e52547f9462c",
            "Merchant" isVoice "50d6beb4-80ea-4802-8387-6c948fe84208",
            "Kentucky man" isVoice "726d5ae5-055f-4c3d-8355-d9677de68937"
        )
    }

    val llmProviders = listOf(Anthropic, Together)

    val ttsProviders = listOf(Cartesia)
}

interface NamedOption {
    val name: String
}

interface TTSProvider: NamedOption {
    override val name: String
    val id: String
    val voices: List<TTSOptionVoice>
}

interface LLMProvider: NamedOption {
    override val name: String
    val id: String
    val models: List<LLMOptionModel>
}

data class LLMOptionModel(
    override val name: String,
    val id: String,
): NamedOption

data class TTSOptionVoice(
    override val name: String,
    val id: String,
): NamedOption

private infix fun String.isModel(id: String) = LLMOptionModel(name = this, id = id)
private infix fun String.isVoice(id: String) = TTSOptionVoice(name = this, id = id)