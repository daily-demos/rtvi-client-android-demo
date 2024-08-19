package ai.rtvi.client.basicdemo.ui

import ai.rtvi.client.basicdemo.ui.theme.Colors
import ai.rtvi.client.basicdemo.utils.Timestamp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout

@Composable
fun InCallLayout(
    expiryTime: Timestamp
) {

    ConstraintLayout(
        Modifier
            .fillMaxSize()
            .background(Colors.activityBackground)
    ) {

        val (refLogo, refTimer) = createRefs()

        Logo(Modifier.constrainAs(refLogo) {
            top.linkTo(parent.top, 15.dp)
            start.linkTo(parent.start, 15.dp)
        })

        ExpiryTimer(expiryTime, Modifier.constrainAs(refTimer) {
            top.linkTo(parent.top, 29.dp)
            end.linkTo(parent.end)
        })
    }

}

@Composable
@Preview
fun PreviewInCallLayout() {
    InCallLayout(Timestamp.now() + java.time.Duration.ofMinutes(3))
}