package co.daily.bots.demo.ui

import co.daily.bots.demo.R
import co.daily.bots.demo.ui.theme.Colors
import co.daily.bots.demo.utils.Timestamp
import co.daily.bots.demo.utils.formatTimer
import co.daily.bots.demo.utils.rtcStateSecs
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.Duration

@Composable
fun Timer(
    expiryTime: Timestamp,
    modifier: Modifier,
) {
    val now by rtcStateSecs()

    val shape = RoundedCornerShape(
        topStart = 12.dp,
        bottomStart = 12.dp,
    )

    Row(
        modifier = modifier
            .widthIn(min = 100.dp)
            .clip(shape)
            .background(Colors.lightGrey)
            .padding(top = 12.dp, bottom = 12.dp, start = 12.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.timer_outline),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = Colors.expiryTimerForeground
        )

        Spacer(Modifier.width(8.dp))

        Text(
            text = formatTimer(duration = expiryTime - now),
            fontSize = 16.sp,
            fontWeight = FontWeight.W600,
            color = Colors.expiryTimerForeground
        )
    }
}

@Composable
@Preview
fun PreviewExpiryTimer() {
    Timer(Timestamp.now() + Duration.ofMinutes(5), Modifier)
}