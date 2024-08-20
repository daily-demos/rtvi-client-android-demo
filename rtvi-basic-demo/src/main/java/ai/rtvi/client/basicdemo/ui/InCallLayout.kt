package ai.rtvi.client.basicdemo.ui

import ai.rtvi.client.basicdemo.VoiceClientManager
import ai.rtvi.client.basicdemo.ui.theme.Colors
import ai.rtvi.client.basicdemo.ui.theme.TextStyles
import ai.rtvi.client.basicdemo.ui.theme.textFieldColors
import ai.rtvi.client.result.Result
import ai.rtvi.client.result.VoiceError
import ai.rtvi.client.types.Type
import ai.rtvi.client.types.Value
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
private val JSON_PRETTY = Json {
    prettyPrint = true
    prettyPrintIndent = " "
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InCallLayout(voiceClientManager: VoiceClientManager) {

    var commandsExpanded by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {

        InCallHeader(startTime = voiceClientManager.startTime)

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
            ) {
                BotIndicator(
                    modifier = Modifier,
                    isReady = voiceClientManager.botReady.value,
                    isTalking = voiceClientManager.botIsTalking,
                    audioLevel = voiceClientManager.botAudioLevel
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UserMicButton(
                        onClick = voiceClientManager::toggleMic,
                        micEnabled = voiceClientManager.mic.value,
                        modifier = Modifier,
                        isTalking = voiceClientManager.userIsTalking,
                        audioLevel = voiceClientManager.userAudioLevel
                    )

                    UserCamButton(
                        onClick = voiceClientManager::toggleCamera,
                        camEnabled = voiceClientManager.camera.value,
                        modifier = Modifier
                    )
                }
            }
        }

        InCallFooter(
            onClickCommands = { commandsExpanded = true },
            onClickEnd = voiceClientManager::stop
        )

        if (commandsExpanded) {
            ModalBottomSheet(
                onDismissRequest = { commandsExpanded = false },
                containerColor = Color.White,
            ) {
                ActionList(voiceClientManager)
            }
        }
    }
}

@Composable
fun ActionList(voiceClientManager: VoiceClientManager) {

    val resultDialogText: SnapshotStateList<Result<Value, VoiceError>> =
        remember { mutableStateListOf() }

    val actions = voiceClientManager.actionDescriptions.value?.valueOrNull ?: emptyList()

    val scrollState = rememberScrollState()

    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(24.dp)
    ) {
        actions.forEach { action ->

            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = Colors.unmutedMicBackground,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clip(RoundedCornerShape(12.dp)),
            ) {
                val arguments: MutableMap<String, Value> = remember { mutableStateMapOf() }

                Column(
                    Modifier
                        .fillMaxWidth()
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(Colors.lightGrey)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = "${action.service} : ${action.action}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W700,
                            style = TextStyles.base
                        )

                        Box(
                            Modifier
                                .border(1.dp, Colors.logoBorder, RoundedCornerShape(6.dp))
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.White)
                                .clickable {
                                    voiceClientManager
                                        .action(
                                            service = action.service,
                                            action = action.action,
                                            args = arguments
                                        )
                                        ?.withCallback {
                                            resultDialogText.add(it)
                                        }
                                }
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "Send",
                                fontSize = 14.sp,
                                style = TextStyles.base,
                                fontWeight = FontWeight.W700,
                            )
                        }
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        action.arguments.forEach { arg ->

                            val argValue = arguments[arg.name]

                            @Composable
                            fun Textbox(
                                toValue: (String) -> Value?,
                            ) {
                                var textValue by remember { mutableStateOf("") }

                                var isValid by remember { mutableStateOf(true) }

                                val borderColor by animateColorAsState(
                                    if (isValid) {
                                        Colors.logoBorder
                                    } else {
                                        Colors.mutedMicBackground
                                    }
                                )

                                val type = when (arg.type) {
                                    Type.Str -> "string"
                                    Type.Bool -> "bool"
                                    Type.Number -> "number"
                                    Type.Array -> "JSON array"
                                    Type.Object -> "JSON object"
                                }

                                val shape = RoundedCornerShape(12.dp)

                                Box(
                                    Modifier.padding(12.dp)
                                ) {
                                    TextField(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .border(1.dp, borderColor, shape),
                                        value = textValue,
                                        onValueChange = {
                                            textValue = it
                                            val newValue = toValue(it)
                                            isValid = (newValue != null)

                                            if (newValue != null) {
                                                arguments[arg.name] = newValue
                                            } else {
                                                arguments[arg.name] = Value.Null
                                            }
                                        },
                                        label = {
                                            Text(
                                                text = "${arg.name} ($type)",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.W500,
                                                style = TextStyles.base
                                            )
                                        },
                                        colors = textFieldColors(),
                                        textStyle = TextStyles.base,
                                        shape = shape
                                    )
                                }
                            }

                            when (arg.type) {
                                Type.Str -> {
                                    Textbox { Value.Str(it) }
                                }

                                Type.Bool -> {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Checkbox(
                                            checked = (argValue as? Value.Bool)?.value ?: false,
                                            onCheckedChange = {
                                                arguments[arg.name] = Value.Bool(it)
                                            }
                                        )
                                        Text(
                                            text = "${arg.name} (bool)",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.W500,
                                            style = TextStyles.base
                                        )
                                    }
                                }

                                Type.Number -> {
                                    Textbox {
                                        Value.Number(
                                            it.toDoubleOrNull() ?: return@Textbox null
                                        )
                                    }
                                }

                                Type.Array, Type.Object -> {
                                    Textbox {
                                        try {
                                            Json.decodeFromString<Value>(it)
                                        } catch (e: Exception) {
                                            null
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    resultDialogText.firstOrNull()?.let {
        AlertDialog(
            onDismissRequest = { resultDialogText.removeFirst() },
            confirmButton = {
                Button(
                    onClick = { resultDialogText.removeFirst() }
                ) {
                    Text(text = "Close", fontSize = 16.sp)
                }
            },
            title = {
                Text(text = "Action result", fontSize = 20.sp, fontWeight = FontWeight.W700)
            },
            text = {
                Text(text = when (it) {
                    is Result.Err -> "Error: ${it.error.description}"
                    is Result.Ok -> {
                        JSON_PRETTY.encodeToString(Value.serializer(), it.value)
                    }
                }, fontSize = 16.sp)
            }
        )
    }

}