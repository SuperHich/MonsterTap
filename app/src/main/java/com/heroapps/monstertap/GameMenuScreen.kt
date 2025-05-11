package com.heroapps.monstertap

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.heroapps.library.admob.AdMobBanner
import com.heroapps.library.compose.Difficulty
import com.heroapps.library.compose.DifficultySlider

@Composable
fun GameMenuScreen(onStartGame: (Difficulty) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .size(128.dp)
                    .clip(RoundedCornerShape(32.dp)),
                painter = painterResource(R.drawable.img_monster_big),
                contentDescription = null
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Difficulty Slider
            var difficulty by rememberSaveable { mutableStateOf(Difficulty.Medium) }
            var selectedIndex by rememberSaveable { mutableIntStateOf(1) } // Default to Medium

            DifficultySlider(
                initialValue = selectedIndex.toFloat(),
                onValueChange = { selectedIndex = it.toInt() }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Start Button
            Button(
                onClick = {
                    difficulty = Difficulty.entries[selectedIndex]
                    onStartGame(difficulty)
                },
                modifier = Modifier
                    .width(200.dp)
                    .height(50.dp)
            ) {
                Text(
                    stringResource(R.string.start_game),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        AdMobBanner(
            adUnitId = Constants.BANNER_UNIT_ID,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}