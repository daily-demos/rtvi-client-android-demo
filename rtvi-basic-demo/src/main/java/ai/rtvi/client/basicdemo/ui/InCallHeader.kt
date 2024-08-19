package ai.rtvi.client.basicdemo.ui

import ai.rtvi.client.basicdemo.utils.Timestamp
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout

@Composable
fun InCallHeader(
    startTime: State<Timestamp?>
) {

    ConstraintLayout(
        Modifier.fillMaxWidth().padding(vertical = 15.dp)
    ) {
        val (refLogo, refTimer) = createRefs()

        Logo(Modifier.constrainAs(refLogo) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            start.linkTo(parent.start, 15.dp)
        })

        AnimatedContent(
            modifier = Modifier.constrainAs(refTimer) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                end.linkTo(parent.end)
            },
            targetState = startTime.value,
            transitionSpec = { fadeIn() togetherWith fadeOut() }
        ) { startTimeVal ->
            if (startTimeVal != null) {
                Timer(startTimeVal, Modifier)
            }
        }
    }
}

@Composable
@Preview
fun PreviewInCallHeader() {
    InCallHeader(
        remember { mutableStateOf(Timestamp.now() + java.time.Duration.ofMinutes(3)) }
    )
}