
package com.dega.ludo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                DegaLudoGame()
            }
        }
    }
}

private val playerColors = listOf(
    Color(0xFFE53935),
    Color(0xFF43A047),
    Color(0xFFFFC107),
    Color(0xFF1E88E5)
)

private val playerNames = listOf(
    "الأحمر", "الأخضر", "الأصفر", "الأزرق"
)

private val track = listOf(
    6 to 1, 6 to 2, 6 to 3, 6 to 4, 6 to 5,
    5 to 6, 4 to 6, 3 to 6, 2 to 6, 1 to 6, 0 to 6,
    0 to 7, 0 to 8,
    1 to 8, 2 to 8, 3 to 8, 4 to 8, 5 to 8,
    6 to 9, 6 to 10, 6 to 11, 6 to 12, 6 to 13, 6 to 14,
    7 to 14, 8 to 14,
    8 to 13, 8 to 12, 8 to 11, 8 to 10, 8 to 9,
    9 to 8, 10 to 8, 11 to 8, 12 to 8, 13 to 8, 14 to 8,
    14 to 7, 14 to 6,
    13 to 6, 12 to 6, 11 to 6, 10 to 6, 9 to 6,
    8 to 5, 8 to 4, 8 to 3, 8 to 2, 8 to 1, 8 to 0,
    7 to 0, 6 to 0
)

private val starts = listOf(0, 13, 26, 39)

private val homeLanes = listOf(
    (1..6).map { 7 to it },
    (1..6).map { it to 7 },
    (1..6).map { 7 to (14 - it) },
    (1..6).map { (14 - it) to 7 }
)

private val homeSlots = listOf(
    listOf(1 to 1, 1 to 4, 4 to 1, 4 to 4),
    listOf(1 to 10, 1 to 13, 4 to 10, 4 to 13),
    listOf(10 to 10, 10 to 13, 13 to 10, 13 to 13),
    listOf(10 to 1, 10 to 4, 13 to 1, 13 to 4)
)

private val safeSquares = setOf(0, 8, 13, 21, 26, 34, 39, 47)

private fun piecePosition(
    player: Int,
    piece: Int,
    progress: Int
): Pair<Int, Int> {
    return when {
        progress == -1 -> homeSlots[player][piece]
        progress <= 51 ->
            track[(starts[player] + progress) % 52]
        else ->
            homeLanes[player][progress - 52]
    }
}

@Composable
fun DegaLudoGame() {
    var playerCount by remember { mutableIntStateOf(4) }
    var pieces by remember { mutableStateOf(List(16) { -1 }) }
    var currentPlayer by remember { mutableIntStateOf(0) }
    var dice by remember { mutableIntStateOf(1) }
    var pendingDice by remember { mutableIntStateOf(0) }
    var legalPieces by remember { mutableStateOf(emptyList<Int>()) }
    var message by remember { mutableStateOf("اضغط على النرد لبدء اللعب") }
    var winner by remember { mutableIntStateOf(-1) }

    val activePlayers = when (playerCount) {
        2 -> listOf(0, 2)
        3 -> listOf(0, 1, 2)
        else -> listOf(0, 1, 2, 3)
    }

    fun nextTurn() {
        val position = activePlayers.indexOf(currentPlayer)
        currentPlayer = activePlayers[
            (position + 1) % activePlayers.size
        ]
        pendingDice = 0
        legalPieces = emptyList()
    }

    fun rollDice() {
        if (pendingDice != 0 || winner != -1) return

        dice = Random.nextInt(1, 7)
        pendingDice = dice

        legalPieces = (0..3)
            .map { currentPlayer * 4 + it }
            .filter { index ->
                val progress = pieces[index]
                if (progress == -1) {
                    dice == 6
                } else {
                    progress + dice <= 57
                }
            }

        if (legalPieces.isEmpty()) {
            message = "لا توجد حركة متاحة"
            nextTurn()
        } else {
            message = "ظهر الرقم $dice - اختر القطعة"
        }
    }

    fun movePiece(index: Int) {
        if (index !in legalPieces || pendingDice == 0) return

        val newPieces = pieces.toMutableList()
        val oldProgress = newPieces[index]

        val newProgress =
            if (oldProgress == -1) 0
            else oldProgress + pendingDice

        newPieces[index] = newProgress

        val player = index / 4
        var captured = false

        if (newProgress in 0..51) {
            val globalPosition =
                (starts[player] + newProgress) % 52

            if (globalPosition !in safeSquares) {
                for (enemy in 0 until 16) {
                    if (
                        enemy / 4 != player &&
                        enemy / 4 in activePlayers &&
                        newPieces[enemy] in 0..51
                    ) {
                        val enemyPosition =
                            (starts[enemy / 4] + newPieces[enemy]) % 52

                        if (enemyPosition == globalPosition) {
                            newPieces[enemy] = -1
                            captured = true
                        }
                    }
                }
            }
        }

        pieces = newPieces

        val finished = (0..3).all { piece ->
            newPieces[player * 4 + piece] == 57
        }

        pendingDice = 0
        legalPieces = emptyList()

        if (finished) {
            winner = player
            message = "الفائز هو ${playerNames[player]}!"
            return
        }

        if (dice == 6) {
            message = "ممتاز! لديك دور إضافي"
        } else {
            if (captured) {
                message = "رائع! أكلت قطعة الخصم"
            } else {
                message = "تم تحريك القطعة"
            }
            nextTurn()
        }
    }

    fun restartGame() {
        pieces = List(16) { -1 }
        currentPlayer = 0
        dice = 1
        pendingDice = 0
        legalPieces = emptyList()
        winner = -1
        message = "لعبة جديدة! اضغط على النرد"
    }

    val animatedDice by animateFloatAsState(
        targetValue = if (pendingDice == 0) 1f else 1.12f,
        animationSpec = tween(180),
        label = "diceAnimation"
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF101A2D)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "DEGA LUDO",
                color = Color(0xFFFFD45C),
                fontSize = 30.sp,
                fontWeight = FontWeight.Black
            )

            Text(
                text = "ROYAL GOLD EDITION",
                color = Color.LightGray,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "اللاعبون:",
                    color = Color.White,
                    fontSize = 13.sp
                )

                listOf(2, 3, 4).forEach { count ->
                    Button(
                        onClick = {
                            playerCount = count
                            restartGame()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor =
                                if (playerCount == count)
                                    Color(0xFFD4A83D)
                                else
                                    Color(0xFF34435C)
                        ),
                        contentPadding = PaddingValues(
                            horizontal = 13.dp,
                            vertical = 4.dp
                        )
                    ) {
                        Text("$count")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                activePlayers.forEach { player ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .background(
                                    playerColors[player],
                                    CircleShape
                                )
                        )
                        Text(
                            playerNames[player],
                            color = if (currentPlayer == player)
                                Color(0xFFFFD45C)
                            else
                                Color.White,
                            fontSize = 11.sp,
                            fontWeight = if (currentPlayer == player)
                                FontWeight.Bold
                            else
                                FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LudoBoard(
                pieces = pieces,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message,
                color = Color(0xFFFFD45C),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { rollDice() },
                enabled = pendingDice == 0 && winner == -1,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD4A83D),
                    contentColor = Color(0xFF17243D)
                )
            ) {
                Text(
                    text = "🎲   رمي النرد: $dice",
                    fontSize = (22 * animatedDice).sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (legalPieces.isNotEmpty()) {
                Text(
                    "اختر القطعة التي تريد تحريكها",
                    color = Color.White,
                    fontSize = 13.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    legalPieces.forEach { index ->
                        val pieceNumber = index % 4 + 1

                        Button(
                            onClick = { movePiece(index) },
                            modifier = Modifier.padding(3.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor =
                                    playerColors[currentPlayer]
                            )
                        ) {
                            Text(
                                "قطعة $pieceNumber",
                                color = Color.White
                            )
                        }
                    }
                }
            }

            if (winner != -1) {
                Button(
                    onClick = { restartGame() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD4A83D)
                    )
                ) {
                    Text("ابدأ مباراة جديدة")
                }
            }

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                "DEGA LUDO • LET'S PLAY",
                color = Color.Gray,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun LudoBoard(
    pieces: List<Int>,
    modifier: Modifier = Modifier
) {
    val animatedProgress = remember { mutableStateListOf<Float>() }

    for (index in 0 until 16) {
        val animated by animateFloatAsState(
            targetValue = pieces[index].toFloat(),
            animationSpec = tween(280),
            label = "piece$index"
        )

        if (animatedProgress.size <= index) {
            animatedProgress.add(animated)
        } else {
            animatedProgress[index] = animated
        }
    }

    Canvas(
        modifier = modifier
            .aspectRatio(1f)
            .background(Color(0xFF18243A))
    ) {
        val cell = size.width / 15f

        fun cellRect(
            x: Int,
            y: Int,
            width: Int = 1,
            height: Int = 1
        ) {
            drawRoundRect(
                color = Color(0xFFF3EBD8),
                topLeft = Offset(x * cell, y * cell),
                size = Size(width * cell, height * cell),
                cornerRadius = CornerRadius(cell * 0.12f)
            )
        }

        fun homeArea(
            x: Int,
            y: Int,
            color: Color
        ) {
            drawRoundRect(
                color = color,
                topLeft = Offset(x * cell, y * cell),
                size = Size(6 * cell, 6 * cell),
                cornerRadius = CornerRadius(cell * 0.5f)
            )

            drawRoundRect(
                color = Color.White,
                topLeft = Offset(
                    (x + 1) * cell,
                    (y + 1) * cell
                ),
                size = Size(4 * cell, 4 * cell),
                cornerRadius = CornerRadius(cell * 0.4f)
            )
        }

        homeArea(0, 0, playerColors[0])
        homeArea(9, 0, playerColors[1])
        homeArea(9, 9, playerColors[2])
        homeArea(0, 9, playerColors[3])

        track.forEachIndexed { index, point ->
            val color = when (index) {
                0 -> playerColors[0]
                13 -> playerColors[1]
                26 -> playerColors[2]
                39 -> playerColors[3]
                else -> Color(0xFFF3EBD8)
            }

            cellRect(point.second, point.first)

            if (index in safeSquares) {
                drawCircle(
                    color = Color(0xFFD4A83D),
                    radius = cell * 0.13f,
                    center = Offset(
                        (point.second + 0.5f) * cell,
                        (point.first + 0.5f) * cell
                    )
                )
            }

            drawCircle(
                color = color,
                radius = cell * 0.12f,
                center = Offset(
                    (point.second + 0.5f) * cell,
                    (point.first + 0.5f) * cell
                )
            )
        }

        homeLanes.forEachIndexed { player, lane ->
            lane.forEach { point ->
                drawRoundRect(
                    color = playerColors[player],
                    topLeft = Offset(
                        point.second * cell,
                        point.first * cell
                    ),
                    size = Size(cell, cell),
                    cornerRadius = CornerRadius(cell * 0.1f)
                )
            }
        }

        drawRoundRect(
            color = Color(0xFFD4A83D),
            topLeft = Offset(6 * cell, 6 * cell),
            size = Size(3 * cell, 3 * cell),
            cornerRadius = CornerRadius(cell * 0.3f)
        )

        drawCircle(
            color = Color(0xFF17243D),
            radius = cell * 0.8f,
            center = Offset(7.5f * cell, 7.5f * cell)
        )

        drawCircle(
            color = Color(0xFFFFD45C),
            radius = cell * 0.45f,
            center = Offset(7.5f * cell, 7.5f * cell)
        )

        for (player in 0 until 4) {
            for (piece in 0 until 4) {
                val index = player * 4 + piece
                val progress = animatedProgress[index]
                    .toInt()
                    .coerceIn(-1, 57)

                val point = piecePosition(
                    player,
                    piece,
                    progress
                )

                val center = Offset(
                    (point.second + 0.5f) * cell,
                    (point.first + 0.5f) * cell
                )

                drawCircle(
                    color = Color.Black.copy(alpha = 0.25f),
                    radius = cell * 0.34f,
                    center = Offset(
                        center.x + cell * 0.04f,
                        center.y + cell * 0.05f
                    )
                )

                drawCircle(
                    color = playerColors[player],
                    radius = cell * 0.31f,
                    center = center
                )

                drawCircle(
                    color = Color.White,
                    radius = cell * 0.12f,
                    center = center
                )
            }
        }
    }
}

