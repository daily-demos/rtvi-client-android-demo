package ai.rtvi.client.basicdemo

import ai.rtvi.client.basicdemo.ui.InCallLayout
import ai.rtvi.client.basicdemo.ui.Logo
import ai.rtvi.client.basicdemo.ui.PermissionScreen
import ai.rtvi.client.basicdemo.ui.theme.Colors
import ai.rtvi.client.basicdemo.ui.theme.RTVIClientTheme
import ai.rtvi.client.basicdemo.ui.theme.TextStyles
import ai.rtvi.client.basicdemo.ui.theme.textFieldColors
import ai.rtvi.client.types.ActionDescription
import ai.rtvi.client.types.Type
import ai.rtvi.client.types.Value
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val voiceClientManager = VoiceClientManager(this)

        val baseUrl = mutableStateOf("")

        setContent {
            RTVIClientTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        PermissionScreen()

                        val vcState = voiceClientManager.state.value

                        if (vcState != null) {
                            InCallLayout(voiceClientManager)

                        } else {
                            ConnectSettings(voiceClientManager, baseUrl)
                        }

                        voiceClientManager.errors.firstOrNull()?.let { errorText ->

                            val dismiss: () -> Unit = { voiceClientManager.errors.removeFirst() }

                            AlertDialog(
                                onDismissRequest = dismiss,
                                confirmButton = {
                                    Button(onClick = dismiss) {
                                        Text(
                                            text = "OK",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.W700,
                                            color = Color.White,
                                            style = TextStyles.base
                                        )
                                    }
                                },
                                containerColor = Color.White,
                                title = {
                                    Text(
                                        text = "Error",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.W600,
                                        color = Color.Black,
                                        style = TextStyles.base
                                    )
                                },
                                text = {
                                    Text(
                                        text = errorText.message,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.W400,
                                        color = Color.Black,
                                        style = TextStyles.base
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConnectSettings(
    voiceClientManager: VoiceClientManager,
    baseUrl: MutableState<String>
) {
    val scrollState = rememberScrollState()

    val start = { voiceClientManager.start(baseUrl.value) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .imePadding()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(Colors.mainSurfaceBackground)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = 24.dp,
                        horizontal = 28.dp
                    )
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Logo(Modifier)
                }

                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = "Connect to an RTVI server",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.W700,
                    style = TextStyles.base
                )

                Spacer(modifier = Modifier.height(36.dp))

                Text(
                    text = "Backend URL",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W700,
                    style = TextStyles.base
                )

                Spacer(modifier = Modifier.height(12.dp))

                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Colors.textFieldBorder, RoundedCornerShape(12.dp)),
                    value = baseUrl.value,
                    onValueChange = { baseUrl.value = it },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Uri,
                        imeAction = ImeAction.Go
                    ),
                    colors = textFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardActions = KeyboardActions(
                        onDone = { start() }
                    )
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    modifier = Modifier.align(Alignment.End),
                    onClick = start,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Connect",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W700,
                        style = TextStyles.base
                    )
                }
            }
        }
    }
}

@Composable
fun ColumnScope.ActionList(
    voiceClientManager: VoiceClientManager,
    actions: List<ActionDescription>
) {
    var resultDialogText by remember { mutableStateOf<String?>(null) }

    actions.forEach { action ->

        Spacer(Modifier.height(12.dp))

        Box(
            modifier = Modifier.border(
                width = 2.dp,
                color = Colors.buttonSection,
                shape = RoundedCornerShape(12.dp)
            ),
        ) {
            val arguments: MutableMap<String, Value> = remember { mutableStateMapOf() }

            Column(
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "${action.service} | ${action.action}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W500
                )

                action.arguments.forEach { arg ->

                    val argValue = arguments[arg.name]

                    Spacer(Modifier.height(8.dp))

                    when (arg.type) {
                        Type.Str -> {
                            TextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = (argValue as? Value.Str)?.value ?: "", onValueChange = {
                                    arguments[arg.name] = Value.Str(it)
                                },
                                label = {
                                    Text(
                                        text = arg.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.W500
                                    )
                                },
                                colors = textFieldColors()
                            )
                        }

                        Type.Bool -> {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = (argValue as? Value.Bool)?.value ?: false,
                                    onCheckedChange = { arguments[arg.name] = Value.Bool(it) }
                                )
                                Text(
                                    text = arg.name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.W500
                                )
                            }
                        }

                        Type.Number -> {}
                        Type.Array -> {}
                        Type.Object -> {}
                    }
                }

                Button(
                    modifier = Modifier.align(Alignment.End),
                    onClick = {
                        voiceClientManager.action(
                            service = action.service,
                            action = action.action,
                            args = arguments
                        )?.withCallback {
                            resultDialogText = it.toString()
                        }
                    }
                ) {
                    Text(
                        text = "Send",
                        fontSize = 16.sp
                    )
                }
            }
        }
    }

    resultDialogText?.let {
        AlertDialog(
            onDismissRequest = { resultDialogText = null },
            confirmButton = {
                Button(
                    onClick = { resultDialogText = null }
                ) {
                    Text(text = "Close", fontSize = 16.sp)
                }
            },
            title = {
                Text(text = "Action result", fontSize = 20.sp, fontWeight = FontWeight.W700)
            },
            text = {
                Text(text = it, fontSize = 16.sp)
            }
        )
    }
}

@Immutable
enum class ButtonType {
    Normal,
    Warning
}

@Composable
fun TextButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int? = null,
    type: ButtonType = ButtonType.Normal
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        colors = ButtonColors(
            containerColor = when (type) {
                ButtonType.Normal -> Colors.buttonNormal
                ButtonType.Warning -> Colors.buttonWarning
            },
            contentColor = Color.White,
            disabledContentColor = Color.White,
            disabledContainerColor = Color.Transparent
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    modifier = Modifier.size(22.dp),
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(Modifier.width(8.dp))
            }

            Text(
                text = text,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun SectionButton(
    expanded: MutableState<Boolean>,
    textExpanded: String,
    textCollapsed: String,
    modifier: Modifier = Modifier,
) {
    Button(
        modifier = modifier,
        onClick = { expanded.value = !expanded.value },
        colors = ButtonColors(
            containerColor = Colors.buttonSection,
            contentColor = Color.Black,
            disabledContentColor = Color.Black,
            disabledContainerColor = Color.Transparent
        ),
        contentPadding = PaddingValues(top = 10.dp, bottom = 10.dp, start = 12.dp, end = 20.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(26.dp),
                painter = painterResource(
                    id = if (expanded.value) {
                        R.drawable.chevron_down
                    } else {
                        R.drawable.chevron_right
                    }
                ),
                contentDescription = null,
                tint = Color.Black
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = if (expanded.value) textExpanded else textCollapsed,
                fontSize = 16.sp
            )
        }
    }
}