package com.avaya.axp.client.sample.ui.call

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.avaya.android.calling.webrtc.DtmfTone
import com.avaya.android.calling.webrtc.asDtmfTone


@Composable
fun KeypadDialog(onDismissRequest: () -> Unit, onKeyPressed: (Key) -> Unit) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Surface {
            KeypadGrid(onKeyPressed = onKeyPressed)
        }
    }
}

@Composable
private fun KeypadGrid(modifier: Modifier = Modifier, onKeyPressed: (Key) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        modifier = modifier,
    ) {
        items(Keys) {
            KeypadButton(
                primaryLabel = it.primaryLabel,
                secondaryLabel = it.secondaryLabel,
                onClick = { onKeyPressed(it) },
            )
        }
    }
}

@Composable
private fun KeypadButton(primaryLabel: String, secondaryLabel: String = "", onClick: () -> Unit) {
    Button(onClick = { onClick() }) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = primaryLabel, style = MaterialTheme.typography.labelLarge)
            Text(text = secondaryLabel, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Preview
@Composable
fun KeypadPreview() {
    MaterialTheme(colorScheme = darkColorScheme()) {
        KeypadGrid(onKeyPressed = {})
    }
}

private val Keys = listOf(
    Key("1", ""),
    Key("2", "ABC"),
    Key("3", "DEF"),
    Key("4", "GHI"),
    Key("5", "JKL"),
    Key("6", "MNO"),
    Key("7", "PQRS"),
    Key("8", "TUV"),
    Key("9", "WXY"),
    Key("*", ""),
    Key("0", "+"),
    Key("#", "")
)

data class Key(
    val primaryLabel: String,
    val secondaryLabel: String = "",

    ) {
    val dtmfTone: DtmfTone = primaryLabel[0].asDtmfTone!!
}