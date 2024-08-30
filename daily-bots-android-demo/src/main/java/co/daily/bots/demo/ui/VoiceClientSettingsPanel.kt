package co.daily.bots.demo.ui

import co.daily.bots.demo.ConfigConstants
import co.daily.bots.demo.LastInitOptions
import co.daily.bots.demo.NamedOption
import co.daily.bots.demo.NamedOptionList
import co.daily.bots.demo.Preferences
import co.daily.bots.demo.VoiceClientManager
import co.daily.bots.demo.ui.theme.Colors
import co.daily.bots.demo.ui.theme.RTVIClientTheme
import co.daily.bots.demo.ui.theme.TextStyles
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun VoiceClientSettingsPanel(
    initOptions: VoiceClientManager.InitOptions,
    runtimeOptions: VoiceClientManager.RuntimeOptions,
) {
    val scrollState = rememberScrollState()

    val pref = Preferences.lastInitOptions

    fun updatePref(action: LastInitOptions.() -> LastInitOptions) {
        pref.value = action(pref.value ?: LastInitOptions.from(initOptions, runtimeOptions))
    }

    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
    ) {
        Header("Bot Configuration")

        RadioGroup(
            label = "Bot Profile",
            onSelect = { updatePref { copy(botProfile = it.id) } },
            selected = initOptions.botProfile,
            options = ConfigConstants.botProfiles,
        )

        Header("Text to Speech")

        RadioGroup(
            label = "Service",
            onSelect = { updatePref { copy(ttsProvider = it.id) } },
            selected = initOptions.ttsProvider,
            options = initOptions.botProfile.ttsProviders,
        )

        RadioGroup(
            label = "Voice",
            onSelect = { updatePref { copy(ttsVoice = it.id) } },
            selected = runtimeOptions.ttsVoice,
            options = initOptions.ttsProvider.voices
        )

        Header("Language Model")

        RadioGroup(
            label = "Service",
            onSelect = { updatePref { copy(llmProvider = it.id) } },
            selected = initOptions.llmProvider,
            options = initOptions.botProfile.llmProviders
        )

        RadioGroup(
            label = "Model",
            onSelect = { updatePref { copy(llmModel = it.id) } },
            selected = runtimeOptions.llmModel,
            options = initOptions.llmProvider.models
        )

        Spacer(Modifier.height(36.dp))
    }
}

@Composable
private fun ColumnScope.Header(text: String) {

    Spacer(Modifier.height(42.dp))

    Text(
        text = text,
        fontSize = 22.sp,
        fontWeight = FontWeight.W700,
        style = TextStyles.base
    )
}

@Composable
private fun <E : NamedOption> ColumnScope.RadioGroup(
    label: String,
    onSelect: (E) -> Unit,
    selected: E,
    options: NamedOptionList<E>,
) {
    Spacer(Modifier.height(26.dp))

    Text(
        text = label,
        fontSize = 18.sp,
        fontWeight = FontWeight.W700,
        style = TextStyles.base
    )

    Spacer(Modifier.height(12.dp))

    val shape = RoundedCornerShape(12.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Colors.textFieldBorder, shape)
            .clip(shape)
            .background(Color.White)
    ) {
        var first = true

        for (option in options.options) {

            if (first) {
                first = false
            } else {
                HorizontalDivider(color = Colors.textFieldBorder, thickness = 1.dp)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(option) }
                    .padding(vertical = 16.dp, horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = option.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W500,
                    style = TextStyles.base
                )

                Spacer(Modifier.width(8.dp))

                RadioButton(selected = selected == option, onClick = null)
            }
        }
    }
}

private fun <E> MutableState<E>.update(action: E.() -> E) {
    value = action(value)
}

@Composable
@Preview
private fun PreviewVoiceClientSettingsPanel() {
    RTVIClientTheme {
        VoiceClientSettingsPanel(
            initOptions = VoiceClientManager.InitOptions.default(),
            runtimeOptions = VoiceClientManager.RuntimeOptions.default()
        )
    }
}