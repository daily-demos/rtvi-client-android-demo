package ai.rtvi.client.basicdemo.ui

import ai.rtvi.client.basicdemo.utils.Timestamp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.FloatState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun InCallLayout(
    onClickEnd: () -> Unit,
    onClickMic: () -> Unit,
    onClickCam: () -> Unit,
    startTime: State<Timestamp?>,
    botIsTalking: State<Boolean>,
    botAudioLevel: FloatState,
    userIsTalking: State<Boolean>,
    userAudioLevel: FloatState,
    userMicEnabled: Boolean,
    userCamEnabled: Boolean,
) {
    var commandsExpanded by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {

        InCallHeader(startTime = startTime)

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
                    isTalking = botIsTalking,
                    audioLevel = botAudioLevel
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UserMicButton(
                        onClick = onClickMic,
                        micEnabled = userMicEnabled,
                        modifier = Modifier,
                        isTalking = userIsTalking,
                        audioLevel = userAudioLevel
                    )

                    UserCamButton(
                        onClick = onClickCam,
                        camEnabled = userCamEnabled,
                        modifier = Modifier
                    )
                }
            }
        }

        InCallFooter(
            onClickCommands = { commandsExpanded = true },
            onClickEnd = onClickEnd
        )
    }
}