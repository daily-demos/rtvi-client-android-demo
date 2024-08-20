package ai.rtvi.client.basicdemo

import ai.rtvi.client.basicdemo.ui.InCallLayout
import ai.rtvi.client.basicdemo.ui.theme.Colors
import ai.rtvi.client.basicdemo.ui.theme.RTVIClientTheme
import ai.rtvi.client.basicdemo.ui.theme.textFieldColors
import ai.rtvi.client.result.Future
import ai.rtvi.client.result.Result
import ai.rtvi.client.types.ActionDescription
import ai.rtvi.client.types.Type
import ai.rtvi.client.types.Value
import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.animation.animateContentSize
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
import androidx.compose.runtime.State
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val voiceClientManager = VoiceClientManager(this)

        setContent {
            val scrollState = rememberScrollState()

            RTVIClientTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    val vcState = voiceClientManager.state.value

                    if (vcState != null) {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .padding(innerPadding)) {
                            InCallLayout(
                                onClickEnd = voiceClientManager::stop,
                                onClickMic = voiceClientManager::toggleMic,
                                onClickCam = voiceClientManager::toggleCamera,
                                startTime = voiceClientManager.startTime,
                                botIsReady = voiceClientManager.botReady,
                                botIsTalking = voiceClientManager.botIsTalking,
                                botAudioLevel = voiceClientManager.botAudioLevel,
                                userIsTalking = voiceClientManager.userIsTalking,
                                userAudioLevel = voiceClientManager.userAudioLevel,
                                userMicEnabled = voiceClientManager.mic.value,
                                userCamEnabled = voiceClientManager.camera.value
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                                .imePadding()
                                .padding(innerPadding)
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
                                        .animateContentSize()
                                ) {
                                    MainContent(voiceClientManager)
                                }
                            }
                        }
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
                                        fontWeight = FontWeight.W400,
                                        color = Color.White
                                    )
                                }
                            },
                            containerColor = Color.White,
                            title = {
                                Text(
                                    text = "Error",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.W600,
                                    color = Color.Black
                                )
                            },
                            text = {
                                Text(
                                    text = errorText.message,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.W400,
                                    color = Color.Black
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ColumnScope.MainContent(voiceClientManager: VoiceClientManager) {

    val baseUrl = remember {
        mutableStateOf("")
    }

    val state = voiceClientManager.state.value

    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)
    val micPermission = rememberPermissionState(Manifest.permission.RECORD_AUDIO)

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { isGranted ->
        Log.i("MainActivity", "Permissions granted: $isGranted")
    }

    if (!cameraPermission.status.isGranted || !micPermission.status.isGranted) {
        PermissionScreen(
            grantPermissions = {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO
                    )
                )
            }
        )
    } else if (state == null) {
        ConnectSettings(voiceClientManager, baseUrl)
    }
}

@Composable
fun ColumnScope.ConnectSettings(
    voiceClientManager: VoiceClientManager,
    baseUrl: MutableState<String>
) {
    Text(
        text = "Connect to an RTVI server",
        fontSize = 22.sp,
        fontWeight = FontWeight.W700
    )

    Spacer(modifier = Modifier.height(18.dp))

    Text(
        text = "Backend URL",
        fontSize = 18.sp
    )

    Spacer(modifier = Modifier.height(8.dp))

    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = baseUrl.value,
        onValueChange = { baseUrl.value = it },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Uri
        ),
        colors = textFieldColors()
    )

    Spacer(modifier = Modifier.height(18.dp))

    Button(
        modifier = Modifier.align(Alignment.End),
        onClick = { voiceClientManager.start(baseUrl.value) }
    ) {
        Text(
            text = "Connect",
            fontSize = 16.sp
        )
    }
}

@Composable
fun ColumnScope.PermissionScreen(
    grantPermissions: () -> Unit
) {
    Text(
        text = "Permissions",
        fontSize = 24.sp,
        fontWeight = FontWeight.W700
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "Please grant camera and mic permissions to continue",
        fontSize = 18.sp
    )

    Spacer(modifier = Modifier.height(18.dp))

    Button(
        modifier = Modifier.align(Alignment.End),
        onClick = grantPermissions
    ) {
        Text(
            text = "Grant permissions",
            fontSize = 16.sp
        )
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

@Composable
fun <V, E> Future<V, E>.observeAsState(): State<Result<V, E>?> {
    return remember(this) {
        mutableStateOf<Result<V, E>?>(null).apply {
            withCallback {
                value = it
            }
        }
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