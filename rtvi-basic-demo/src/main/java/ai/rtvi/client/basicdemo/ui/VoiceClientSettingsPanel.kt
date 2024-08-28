package ai.rtvi.client.basicdemo.ui

import ai.rtvi.client.basicdemo.ConfigConstants
import ai.rtvi.client.basicdemo.NamedOption
import ai.rtvi.client.basicdemo.NamedOptionList
import ai.rtvi.client.basicdemo.VoiceClientManager
import ai.rtvi.client.basicdemo.ui.theme.Colors
import ai.rtvi.client.basicdemo.ui.theme.RTVIClientTheme
import ai.rtvi.client.basicdemo.ui.theme.TextStyles
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
    initOptions: MutableState<VoiceClientManager.InitOptions>,
    runtimeOptions: MutableState<VoiceClientManager.RuntimeOptions>,
) {
    val scrollState = rememberScrollState()

    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)) {

        Header("Text to Speech")

        RadioGroup(
            label = "Service",
            onSelect = { initOptions.update { copy(ttsProvider = it) } },
            selected = initOptions.value.ttsProvider,
            options = ConfigConstants.ttsProviders,
        )

        RadioGroup(
            label = "Voice",
            onSelect = { runtimeOptions.update { copy(ttsVoice = it) } },
            selected = runtimeOptions.value.ttsVoice,
            options = initOptions.value.ttsProvider.voices
        )

        Header("Language Model")

        RadioGroup(
            label = "Service",
            onSelect = { initOptions.update { copy(llmProvider = it) } },
            selected = initOptions.value.llmProvider,
            options = ConfigConstants.llmProviders
        )

        RadioGroup(
            label = "Model",
            onSelect = { runtimeOptions.update { copy(llmModel = it) } },
            selected = runtimeOptions.value.llmModel,
            options = initOptions.value.llmProvider.models
        )

        Spacer(Modifier.height(36.dp))
    }
}

@Composable
private fun ColumnScope.Header(text: String) {

    Spacer(Modifier.height(30.dp))

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
    Spacer(Modifier.height(20.dp))

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

    LaunchedEffect(onSelect, selected, options) {
        if (!options.options.contains(selected)) {
            onSelect(options.default)
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
            initOptions = remember { mutableStateOf(VoiceClientManager.InitOptions.default()) },
            runtimeOptions = remember { mutableStateOf(VoiceClientManager.RuntimeOptions.default()) }
        )
    }
}