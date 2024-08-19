package ai.rtvi.client.basicdemo.ui

import ai.rtvi.client.basicdemo.R
import ai.rtvi.client.basicdemo.ui.theme.Colors
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun Logo(modifier: Modifier) {

    val shape = RoundedCornerShape(12.dp)

    Box(
        modifier = modifier
            .size(64.dp)
            .shadow(5.dp, shape)
            .border(1.dp, Colors.logoBorder, shape)
            .clip(shape)
            .background(Color.White)
            .padding(top = 12.dp, bottom = 12.dp, start = 12.dp, end = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.size(40.dp, 44.dp),
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "RTVI"
        )
    }
}

@Composable
@Preview
fun PreviewLogo() {
    Logo(Modifier)
}