package co.daily.bots.demo

import androidx.compose.runtime.Immutable

object ConfigConstants {

    object Together : LLMProvider {

        val Llama8B =
            "Llama 3.1 8B Instruct Turbo" isLLMModel "meta-llama/Meta-Llama-3.1-8B-Instruct-Turbo"
        val Llama70B =
            "Llama 3.1 70B Instruct Turbo" isLLMModel "meta-llama/Meta-Llama-3.1-70B-Instruct-Turbo"
        val Llama405B =
            "Llama 3.1 405B Instruct Turbo" isLLMModel "meta-llama/Meta-Llama-3.1-405B-Instruct-Turbo"

        override val name = "Together AI"
        override val id = "together"

        override val models =
            NamedOptionList(listOf(Llama8B, Llama70B, Llama405B), default = Llama70B)
    }

    object Anthropic : LLMProvider {

        override val name = "Anthropic"
        override val id = "anthropic"

        override val models =
            NamedOptionList(listOf("Claude Sonnet 3.5" isLLMModel "claude-3-5-sonnet-20240620"))
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

    object Deepgram : STTProvider {
        override val name = "Deepgram"
        override val id = "deepgram"

        val English = "English" isSTTLang "en"

        override val models = NamedOptionList(
            listOf(
                STTOptionModel(
                    name = "Nova 2 Conversational AI (English)",
                    id = "nova-2-conversationalai",
                    languages = NamedOptionList(
                        listOf("English" isSTTLang "en")
                    )
                ),
                STTOptionModel(
                    name = "Nova 2 General (Multilingual)",
                    id = "nova-2-general",
                    languages = NamedOptionList(
                        listOf(
                            "Bulgarian" isSTTLang "bg",
                            "Catalan" isSTTLang "ca",
                            "Chinese (Mandarin, Simplified)" isSTTLang "zh",
                            "Chinese (Mandarin, Traditional)" isSTTLang "zh-TW",
                            "Czech" isSTTLang "cs",
                            "Danish" isSTTLang "da",
                            "Danish" isSTTLang "da-DK",
                            "Dutch" isSTTLang "nl",
                            English,
                            "English (US)" isSTTLang "en-US",
                            "English (AU)" isSTTLang "en-AU",
                            "English (GB)" isSTTLang "en-GB",
                            "English (NZ)" isSTTLang "en-NZ",
                            "English (IN)" isSTTLang "en-IN",
                            "Estonian" isSTTLang "et",
                            "Finnish" isSTTLang "fi",
                            "Flemish" isSTTLang "nl-BE",
                            "French" isSTTLang "fr",
                            "French (CA)" isSTTLang "fr-CA",
                            "German" isSTTLang "de",
                            "German (Switzerland)" isSTTLang "de-CH",
                            "Greek" isSTTLang "el",
                            "Hindi" isSTTLang "hi",
                            "Hungarian" isSTTLang "hu",
                            "Indonesian" isSTTLang "id",
                            "Italian" isSTTLang "it",
                            "Japanese" isSTTLang "ja",
                            "Korean" isSTTLang "ko",
                            "Korean" isSTTLang "ko-KR",
                            "Latvian" isSTTLang "lv",
                            "Lithuanian" isSTTLang "lt",
                            "Malay" isSTTLang "ms",
                            "Multilingual (Spanish + English)" isSTTLang "multi",
                            "Norwegian" isSTTLang "no",
                            "Polish" isSTTLang "pl",
                            "Portuguese" isSTTLang "pt",
                            "Portuguese (BR)" isSTTLang "pt-BR",
                            "Romanian" isSTTLang "ro",
                            "Russian" isSTTLang "ru",
                            "Slovak" isSTTLang "sk",
                            "Spanish" isSTTLang "es",
                            "Spanish (Latin America)" isSTTLang "es-419",
                            "Swedish" isSTTLang "sv",
                            "Swedish" isSTTLang "sv-SE",
                            "Thai" isSTTLang "th",
                            "Thai" isSTTLang "th-TH",
                            "Turkish" isSTTLang "tr",
                            "Ukrainian" isSTTLang "uk",
                            "Vietnamese" isSTTLang "vi",
                        ),
                        default = English
                    )
                ),
            )
        )
    }

    val botProfiles = NamedOptionList(
        listOf(
            BotProfile(
                name = "Voice only",
                id = "voice_2024_08",
                llmProviders = NamedOptionList(listOf(Anthropic, Together), default = Together),
                ttsProviders = NamedOptionList(listOf(Cartesia)),
                sttProviders = NamedOptionList(listOf(Deepgram))
            ),
            BotProfile(
                name = "Voice and vision",
                id = "vision_2024_08",
                llmProviders = NamedOptionList(listOf(Anthropic)),
                ttsProviders = NamedOptionList(listOf(Cartesia)),
                sttProviders = NamedOptionList(listOf(Deepgram))
            ),
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

interface STTProvider : NamedOption {
    override val name: String
    override val id: String
    val models: NamedOptionList<STTOptionModel>
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
    val llmProviders: NamedOptionList<LLMProvider>,
    val ttsProviders: NamedOptionList<TTSProvider>,
    val sttProviders: NamedOptionList<STTProvider>,
) : NamedOption

data class STTOptionModel(
    override val name: String,
    override val id: String,
    val languages: NamedOptionList<STTOptionLanguage>
) : NamedOption

data class STTOptionLanguage(
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

private infix fun String.isLLMModel(id: String) = LLMOptionModel(name = this, id = id)
private infix fun String.isSTTLang(id: String) = STTOptionLanguage(name = this, id = id)
private infix fun String.isVoice(id: String) = TTSOptionVoice(name = this, id = id)