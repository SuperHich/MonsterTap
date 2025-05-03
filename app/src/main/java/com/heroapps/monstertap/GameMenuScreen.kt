package com.heroapps.monstertap

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GameMenuScreen(onStartGame: (Difficulty) -> Unit) {
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
            Text(stringResource(R.string.start_game), style = MaterialTheme.typography.labelLarge)
        }
    }
}

enum class Difficulty {
    Easy,
    Medium,
    Hard
}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DifficultySlider(
    initialValue: Float = 1f,
    onValueChange: (Float) -> Unit = {}
) {
    var sliderPosition by remember { mutableFloatStateOf(initialValue) }

    // Calculate difficulty text based on slider position
    val difficultyTextId by derivedStateOf {
        when {
            sliderPosition < 0.75f -> R.string.difficulty_easy
            sliderPosition < 1.67f -> R.string.difficulty_medium
            else -> R.string.difficulty_hard
        }
    }

    // Calculate color based on slider position
    val gradientColor by animateColorAsState(
        targetValue = when {
            sliderPosition < 0.75f -> Color(0xFF4CAF50) // Green
            sliderPosition < 1.67f -> Color(0xFFFF9800) // Orange
            else -> Color(0xFFF44336) // Red
        },
        animationSpec = tween(300),
        label = "color"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Header with title and value
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.difficulty),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = stringResource(difficultyTextId),
                fontSize = 20.sp,
                color = gradientColor
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Custom slider track
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF4CAF50), // Green
                            Color(0xFFFF9800), // Orange
                            Color(0xFFF44336)  // Red
                        )
                    )
                )
        )

        // Slider
        Slider(
            value = sliderPosition,
            valueRange = 0f..2f,
            steps = 1,
            onValueChange = {
                sliderPosition = it
                onValueChange(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-36).dp),
            colors = SliderDefaults.colors(
                thumbColor = gradientColor,
                activeTrackColor = Color.Transparent,
                inactiveTrackColor = Color.Transparent
            ),
            thumb = {
                // Custom large thumb
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(gradientColor)
                        .padding(2.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                )
            }
        )
    }
}

// Usage example
@Preview(showBackground = true)
@Composable
fun DifficultySliderPreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            DifficultySlider { difficulty ->
                // Handle difficulty changes here
                println("Selected difficulty: $difficulty")
            }
        }
    }
}