package ai.rtvi.client.basicdemo.ui

import ai.rtvi.client.basicdemo.ui.theme.Colors
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.FloatState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun BotIndicator(
    modifier: Modifier,
    isTalking: State<Boolean>,
    audioLevel: FloatState,
) {
    Box(
        modifier = modifier.padding(15.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .aspectRatio(1f)
                .fillMaxSize()
                .shadow(20.dp, CircleShape)
                .border(12.dp, Color.White, CircleShape)
                .border(1.dp, Colors.lightGrey, CircleShape)
                .clip(CircleShape)
                .background(Colors.botIndicatorBackground)
                .padding(50.dp),
            contentAlignment = Alignment.Center,
        ) {
            ListeningAnimation(
                modifier = Modifier.fillMaxSize(),
                active = isTalking.value,
                level = audioLevel.floatValue,
                color = Color.White
            )
        }
    }
}

@Composable
@Preview
fun PreviewBotIndicator() {
    BotIndicator(
        modifier = Modifier,
        isTalking = remember { mutableStateOf(true) },
        audioLevel = remember { mutableFloatStateOf(1.0f) }
    )
}