package co.daily.bots.demo.ui

import co.daily.bots.demo.utils.Timestamp
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout

@Composable
fun InCallHeader(
    expiryTime: Timestamp?
) {
    ConstraintLayout(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 15.dp)
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
            targetState = expiryTime,
            transitionSpec = { fadeIn() togetherWith fadeOut() }
        ) { expiryTimeVal ->
            if (expiryTimeVal != null) {
                Timer(expiryTime = expiryTimeVal, modifier = Modifier)
            }
        }
    }
}

@Composable
@Preview
fun PreviewInCallHeader() {
    InCallHeader(
        Timestamp.now() + java.time.Duration.ofMinutes(3)
    )
}