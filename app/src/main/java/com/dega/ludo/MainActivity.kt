
package com.dega.ludo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { DegaLudoGame() }
    }
}

private val playerColors = listOf(
    Color(0xFFE53935),
    Color(0xFF43A047),
    Color(0xFFFFC107),
    Color(0xFF1E88E5)
)

private val playerNames = listOf("الأحمر", "الأخضر", "الأصفر", "الأزرق")

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

private fun piecePosition(player: Int, piece: Int, progress: Int): Pair<Int, Int> {
    return when {
        progress == -1 -> homeSlots[player][piece]
        progress <= 51 -> track[(starts[player] + progress) % 52]
        else -> homeLanes[player][progress - 52]
    }
}

@Composable
fun DegaLudoGame() {
    var players by remember { mutableIntStateOf(4) }
    var pieces by remember { mutableStateOf(List(16) { -1 }) }
    var currentPlayer by remember { mutableIntStateOf(0) }
    var dice by remember { mutableIntStateOf(1) }
    var pendingDice by remember { mutableIntStateOf(0) }
    var legalPieces by remember { mutableStateOf(emptyList<Int>()) }
    var message by remember { mutableStateOf("اضغط على النرد لبدء اللعب") }
    var winner by remember { mutableIntStateOf(-1) }

    val activePlayers = when (players) {
        2 -> listOf(0, 2)
        3 -> listOf(0, 1, 2)
        else -> listOf(0, 1, 2, 3)
    }

    fun nextTurn() {
        val position = activePlayers.indexOf(currentPlayer)
        currentPlayer = activePlayers[(position + 1) % activePlayers.size]
        pendingDice = 0
        legalPieces = emptyList()
    }

    fun rollDice() {
        if (pendingDice != 0 || winner != -1) return

        dice = Random.nextInt(1, 7)
        pendingDice = dice

        legalPieces = (0..3).map { currentPlayer * 4 + it }
            .filter { index ->
                val progress = pieces[index]
                if (progress == -1) {
                    dice == 6
                } else {
                    progress + dice <= 57
                }
            }

        if (legalPieces.isEmpty()) {
            message = "لا توجد حركة متاحة، دور اللاعب التالي"
            nextTurn()
        } else {
            message = "ظهر $dice - اختر الحجر الذي تريد تحريكه"
        }
    }

    fun movePiece(index: Int) {
        if (index !in legalPieces || pendingDice == 0) return

        val newPieces = pieces.toMutableList()
        val oldProgress = newPieces[index]
        val newProgress = if (oldProgress == -1) 0 else oldProgress + pendingDice
        newPieces[index] = newProgress

        val player = index / 4
        var captured = false

        if (newProgress in 0..51) {
            val globalPosition = (starts[player] + newProgress) % 52

            if (globalPosition !in safeSquares) {
                for (enemy in 0 until 16) {
                    if (enemy / 4 != player &&
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
            newPieces[player