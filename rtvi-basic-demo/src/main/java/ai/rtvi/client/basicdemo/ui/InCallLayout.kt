package ai.rtvi.client.basicdemo.ui

import ai.rtvi.client.basicdemo.VoiceClientManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

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

            }
        }
    }
}