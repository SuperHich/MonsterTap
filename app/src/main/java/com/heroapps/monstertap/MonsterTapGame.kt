package com.heroapps.monstertap

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heroapps.library.admob.AdMobBanner
import com.heroapps.library.admob.LaunchInterstitialAds
import com.heroapps.library.compose.AudioFeedback
import com.heroapps.library.compose.Difficulty
import com.heroapps.monstertap.AudioFeedbackExtension.loadSounds
import com.heroapps.monstertap.AudioFeedbackExtension.playCatch
import com.heroapps.monstertap.AudioFeedbackExtension.playClick
import com.heroapps.monstertap.AudioFeedbackExtension.playFailed
import com.heroapps.monstertap.AudioFeedbackExtension.playSuccess
import kotlin.random.Random

@Composable
fun MonsterTapGame(difficulty: Difficulty, onExit: () -> Unit) {
    val context = LocalContext.current
    LaunchInterstitialAds(context, Constants.FULL_PAGE_UNIT_ID)

    val defaultGameState = when (difficulty) {
        Difficulty.Easy -> GameState(gridSize = 3, maxAttempts = 6, monsterCount = 4)
        Difficulty.Medium -> GameState(gridSize = 4, maxAttempts = 10, monsterCount = 6)
        Difficulty.Hard -> GameState(gridSize = 5, maxAttempts = 15, monsterCount = 8)
    }

    var gameState by remember { mutableStateOf(defaultGameState) }
    var showGameOverDialog by remember { mutableStateOf(false) }
    val audioFeedback = remember { AudioFeedback(context) }

    // Load sounds when the composable enters composition
    LaunchedEffect(Unit) {
        audioFeedback.loadSounds()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 8.dp, end = 8.dp, top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GameHeader(
            score = gameState.score,
            monstersRemaining = gameState.monstersRemaining,
            attempts = gameState.attempts,
            maxAttempts = gameState.maxAttempts
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Grille adaptative prenant en compte la taille de l'écran
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            GameGrid(
                cells = gameState.cells,
                onCellClick = { row, col ->
                    if (!gameState.isGameOver && !gameState.cells[row][col].isRevealed) {
                        gameState = gameState.revealCell(row, col)

                        if (gameState.cells[row][col].hasMonster) {
                            audioFeedback.playCatch()
                        } else {
                            audioFeedback.playClick()
                        }

                        if (gameState.gameResult != GameResult.IN_PROGRESS) {
                            showGameOverDialog = true

                            if (gameState.gameResult == GameResult.WIN) {
                                audioFeedback.playSuccess()
                            } else {
                                audioFeedback.playFailed()
                            }
                        }
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { gameState = defaultGameState },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(stringResource(R.string.new_game))
            }

            Button(
                onClick = onExit,
                modifier = Modifier.padding(8.dp)
            ) {
                Text(stringResource(R.string.exit))
            }
        }

        AdMobBanner(
            adUnitId = Constants.BANNER_UNIT_ID
        )
    }

    if (showGameOverDialog) {
        AlertDialog(
            onDismissRequest = { showGameOverDialog = false },
            title = {
                Text(
                    stringResource(
                        if (gameState.gameResult == GameResult.WIN) R.string.congrats else R.string.failed
                    )
                )
            },
            text = {
                if (gameState.gameResult == GameResult.WIN) {
                    Text(
                        stringResource(
                            R.string.win_message,
                            gameState.attempts,
                            gameState.maxAttempts,
                            gameState.score
                        )
                    )
                } else {
                    Text(
                        stringResource(
                            R.string.loose_message,
                            gameState.maxAttempts,
                            gameState.monsterCount - gameState.monstersRemaining,
                            gameState.monsterCount,
                            gameState.score
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        gameState = defaultGameState
                        showGameOverDialog = false
                    }
                ) {
                    Text(stringResource(R.string.new_game))
                }
            }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            audioFeedback.release()
        }
    }
}

@Composable
fun GameHeader(score: Int, monstersRemaining: Int, attempts: Int, maxAttempts: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            stringResource(R.string.app_name),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ScoreItem(title = stringResource(R.string.score), value = score.toString())
            ScoreItem(title = stringResource(R.string.monsters), value = "$monstersRemaining")
            ScoreItem(title = stringResource(R.string.attempts), value = "$attempts/$maxAttempts")
        }
    }
}

@Composable
fun ScoreItem(title: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        Text(
            title,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun GameGrid(cells: Array<Array<Cell>>, onCellClick: (Int, Int) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
    ) {
        for (row in cells.indices) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                for (col in cells[row].indices) {
                    GameCell(
                        modifier = Modifier.weight(1f), // Distribution égale de l'espace disponible,
                        cell = cells[row][col],
                        onClick = { onCellClick(row, col) }
                    )
                }
            }
        }
    }
}

@Composable
fun GameCell(modifier: Modifier = Modifier, cell: Cell, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(4.dp)
            .aspectRatio(1f) // Maintient un ratio 1:1 (carré)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (cell.isRevealed) {
                    if (cell.hasMonster) Color(0xFFFFD7D7) else Color(0xFFE0E0E0)
                } else {
                    Color(0xFF9FC9F3)
                }
            )
            .border(
                width = 2.dp,
                color = Color(0xFF5C6BC0),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(enabled = !cell.isRevealed, onClick = onClick)
    ) {
        if (cell.isRevealed) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(300)) +
                        scaleIn(animationSpec = tween(300))
            ) {
                if (cell.hasMonster) {
                    Image(
                        modifier = Modifier.wrapContentSize(),
                        painter = painterResource(R.drawable.img_monster),
                        contentDescription = null
                    )
                } else {
                    Image(
                        modifier = Modifier.wrapContentSize(),
                        painter = painterResource(R.drawable.img_empty_wall),
                        contentDescription = null
                    )
                }
            }
        }
    }
}

enum class GameResult {
    IN_PROGRESS,
    WIN,
    LOSE
}

data class Cell(
    val hasMonster: Boolean = false,
    val isRevealed: Boolean = false
)

class GameState(
    val gridSize: Int = 5,
    val monsterCount: Int = 8,
    val maxAttempts: Int = 15
) {
    var cells: Array<Array<Cell>> = Array(gridSize) { Array(gridSize) { Cell() } }
    var score: Int = 0
    var attempts: Int = 0
    var monstersRemaining: Int = monsterCount
    var isGameOver: Boolean = false
    var gameResult: GameResult = GameResult.IN_PROGRESS

    init {
        // Placement aléatoire des monstres
        var monstersPlaced = 0
        while (monstersPlaced < monsterCount) {
            val row = Random.nextInt(0, gridSize)
            val col = Random.nextInt(0, gridSize)

            if (!cells[row][col].hasMonster) {
                cells[row][col] = Cell(hasMonster = true)
                monstersPlaced++
            }
        }
    }

    fun revealCell(row: Int, col: Int): GameState {
        val newCells = Array(gridSize) { r ->
            Array(gridSize) { c ->
                if (r == row && c == col) {
                    Cell(
                        hasMonster = cells[r][c].hasMonster,
                        isRevealed = true
                    )
                } else {
                    cells[r][c]
                }
            }
        }

        val foundMonster = cells[row][col].hasMonster
        val newScore = score + if (foundMonster) 100 else -10
        val newAttempts = attempts + 1
        val newMonstersRemaining = monstersRemaining - if (foundMonster) 1 else 0

        // Determine game result
        val newGameResult = when {
            newMonstersRemaining == 0 -> GameResult.WIN
            newAttempts >= maxAttempts -> GameResult.LOSE
            else -> GameResult.IN_PROGRESS
        }

        val newIsGameOver = newGameResult != GameResult.IN_PROGRESS

        return GameState(gridSize, monsterCount, maxAttempts).apply {
            cells = newCells
            score = newScore
            attempts = newAttempts
            monstersRemaining = newMonstersRemaining
            isGameOver = newIsGameOver
            gameResult = newGameResult
        }
    }
}
