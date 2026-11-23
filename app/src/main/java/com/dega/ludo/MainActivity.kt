
package com.dega.ludo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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
    Color(0xFF20A65A),
    Color(0xFFFFC928),
    Color(0xFF2688E8)
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

private val safeSquares =
    setOf(0, 8, 13, 21, 26, 34, 39, 47)

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
    var message by remember {
        mutableStateOf("اضغط على النرد لبدء اللعب")
    }
    var winner by remember { mutableIntStateOf(-1) }

    val activePlayers = when (playerCount) {
        2 -> listOf(0, 2)
        3 -> listOf(0, 1, 2)
        else -> listOf(0, 1, 2, 3)
    }

    fun nextTurn() {
        val position = activePlayers.indexOf(currentPlayer)
        currentPlayer =
            activePlayers[(position + 1) % activePlayers.size]
        pendingDice = 0
        legalPieces = emptyList()
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
            message = "ممتاز! لديك رمية إضافية"
        } else {
            message = if (captured) {
                "رائع! أكلت قطعة الخصم"
            } else {
                "تم تحريك القطعة"
            }
            nextTurn()
        }
    }

    fun rollDice() {
        if (pendingDice != 0 || winner != -1) return

        dice = Random.nextInt(1, 7)
        pendingDice = dice

        val available = (0..3)
            .map { currentPlayer * 4 + it }
            .filter { index ->
                val progress = pieces[index]
                if (progress == -1) {
                    dice == 6
                } else {
                    progress + dice <= 57
                }
            }

        legalPieces = available

        when {
            available.isEmpty() -> {
                message = if (dice == 6) {
                    "لا توجد حركة، ارْمِ مرة أخرى"
                } else {
                    "لا توجد حركة متاحة"
                }
                pendingDice = 0
                legalPieces = emptyList()

                if (dice != 6) {
                    nextTurn()
                }
            }

            available.size == 1 -> {
                message = "حركة تلقائية!"
                movePiece(available.first())
            }

            else -> {
                message = "اختر القطعة بالضغط عليها"
            }
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

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF101A2D)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "DEGA LUDO",
                color = Color(0xFFFFD45C),
                fontSize = 27.sp,
                fontWeight = FontWeight.Black
            )

            Text(
                "ROYAL GOLD EDITION",
                color = Color.LightGray,
                fontSize = 10.sp,
                letterSpacing = 2.sp
            )

            Spacer(Modifier.height(5.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    "عدد اللاعبين",
                    color = Color.White,
                    fontSize = 12.sp
                )

                Spacer(Modifier.width(7.dp))

                listOf(2, 3, 4).forEach { count ->
                    Button(
                        onClick = {
                            playerCount = count
                            restartGame()
                        },
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .height(35.dp),
                        contentPadding = PaddingValues(
                            horizontal = 14.dp,
                            vertical = 0.dp
                        ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor =
                                if (playerCount == count)
                                    Color(0xFFD4A83D)
                                else
                                    Color(0xFF34435C)
                        )
                    ) {
                        Text("$count")
                    }
                }
            }

            Spacer(Modifier.height(5.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                activePlayers.forEach { player ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            Modifier
                                .size(13.dp)
                                .clip(RoundedCornerShape(50))
                                .background(playerColors[player])
                        )

                        Text(
                            playerNames[player],
                            color = if (currentPlayer == player)
                                Color(0xFFFFD45C)
                            else Color.White,
                            fontSize = 11.sp,
                            fontWeight = if (currentPlayer == player)
                                FontWeight.Bold
                            else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(Modifier.height(5.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                LudoBoard(
                    pieces = pieces,
                    legalPieces = legalPieces,
                    activePlayers = activePlayers,
                    onPieceTap = { index -> movePiece(index) },
                    onRollDice = { rollDice() },
                    dice = dice,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )
            }

            Spacer(Modifier.height(5.dp))

            Text(
                message,
                color = Color(0xFFFFD45C),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                "الدور الحالي: ${playerNames[currentPlayer]}",
                color = Color.White,
                fontSize = 12.sp
            )

            if (winner != -1) {
                Spacer(Modifier.height(5.dp))
                Button(
                    onClick = { restartGame() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD4A83D)
                    )
                ) {
                    Text("ابدأ مباراة جديدة")
                }
            }

            Spacer(Modifier.height(5.dp))
            Text(
                "DEGA LUDO • LET'S PLAY",
                color = Color.Gray,
                fontSize = 9.sp
            )
        }
    }
}

@Composable
private fun LudoBoard(
    pieces: List<Int>,
    legalPieces: List<Int>,
    activePlayers: List<Int>,
    onPieceTap: (Int) -> Unit,
    onRollDice: () -> Unit,
    dice: Int,
    modifier: Modifier = Modifier
) {
    val animatedProgress = (0 until 16).map { index ->
        animateFloatAsState(
            targetValue = pieces[index].toFloat(),
            animationSpec = tween(350),
            label = "piece$index"
        ).value
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFD4A83D))
            .padding(4.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFEEE8D8))
                .pointerInput(pieces, legalPieces, activePlayers) {
                    detectTapGestures { tap ->
                        if (legalPieces.isEmpty()) return@detectTapGestures

                        val cell = size.width / 15f

                        val tappedPiece = legalPieces
                            .map { index ->
                                val player = index / 4
                                val piece = index % 4
                                val progress = pieces[index]

                                val point = piecePosition(
                                    player,
                                    piece,
                                    progress
                                )

                                val center = tokenCenter(
                                    point,
                                    piece,
                                    cell
                                )

                                val dx = tap.x - center.x
                                val dy = tap.y - center.y

                                Triple(
                                    index,
                                    dx * dx + dy * dy,
                                    cell * 0.52f
                                )
                            }
                            .filter {
                                it.second <= it.third * it.third
                            }
                            .minByOrNull { it.second }

                        tappedPiece?.let {
                            onPieceTap(it.first)
                        }
                    }
                }
        ) {
            val cell = size.width / 15f

            fun cellRect(
                x: Int,
                y: Int,
                color: Color,
                width: Int = 1,
                height: Int = 1
            ) {
                drawRoundRect(
                    color = color,
                    topLeft = Offset(x * cell, y * cell),
                    size = Size(width * cell, height * cell),
                    cornerRadius = CornerRadius(cell * 0.09f)
                )

                drawRoundRect(
                    color = Color(0xFF26344A).copy(alpha = 0.15f),
                    topLeft = Offset(x * cell, y * cell),
                    size = Size(width * cell, height * cell),
                    cornerRadius = CornerRadius(cell * 0.09f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = cell * 0.035f
                    )
                )
            }

            fun homeArea(x: Int, y: Int, color: Color) {
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        listOf(color, color.copy(alpha = 0.78f)),
                        startY = y * cell,
                        endY = (y + 6) * cell
                    ),
                    topLeft = Offset(x * cell, y * cell),
                    size = Size(6 * cell, 6 * cell),
                    cornerRadius = CornerRadius(cell * 0.45f)
                )

                drawRoundRect(
                    color = Color(0xFFFFFDF4),
                    topLeft = Offset(
                        (x + 0.65f) * cell,
                        (y + 0.65f) * cell
                    ),
                    size = Size(4.7f * cell, 4.7f * cell),
                    cornerRadius = CornerRadius(cell * 0.3f)
                )

                drawRoundRect(
                    color = color.copy(alpha = 0.16f),
                    topLeft = Offset(
                        (x + 0.9f) * cell,
                        (y + 0.9f) * cell
                    ),
                    size = Size(4.2f * cell, 4.2f * cell),
                    cornerRadius = CornerRadius(cell * 0.25f)
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
                    else -> Color(0xFFFFFDF4)
                }

                cellRect(point.second, point.first, color)

                if (index in safeSquares) {
                    drawCircle(
                        color = Color(0xFFD4A83D),
                        radius = cell * 0.20f,
                        center = Offset(
                            (point.second + 0.5f) * cell,
                            (point.first + 0.5f) * cell
                        )
                    )

                    drawCircle(
                        color = Color(0xFF72551C),
                        radius = cell * 0.08f,
                        center = Offset(
                            (point.second + 0.5f) * cell,
                            (point.first + 0.5f) * cell
                        )
                    )
                }
            }

            homeLanes.forEachIndexed { player, lane ->
                lane.forEach { point ->
                    cellRect(
                        point.second,
                        point.first,
                        playerColors[player]
                    )
                }
            }

            // Center of the board
            drawRoundRect(
                color = Color(0xFF17243D),
                topLeft = Offset(6 * cell, 6 * cell),
                size = Size(3 * cell, 3 * cell),
                cornerRadius = CornerRadius(cell * 0.25f)
            )

            drawCircle(
                brush = Brush.radialGradient(
                    listOf(
                        Color(0xFFFFE89A),
                        Color(0xFFD4A83D),
                        Color(0xFF8A6422)
                    ),
                    center = Offset(7.5f * cell, 7.5f * cell),
                    radius = cell * 1.25f
                ),
                radius = cell * 1.05f,
                center = Offset(7.5f * cell, 7.5f * cell)
            )

            // Home token slots
            for (player in activePlayers) {
                for (piece in 0 until 4) {
                    val point = homeSlots[player][piece]
                    val center = Offset(
                        (point.second + 0.5f) * cell,
                        (point.first + 0.5f) * cell
                    )

                    drawCircle(
                        color = playerColors[player].copy(alpha = 0.18f),
                        radius = cell * 0.37f,
                        center = center
                    )
                }
            }

            // Physical-looking player pawns
            for (player in activePlayers) {
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

                    val center = tokenCenter(point, piece, cell)
                    val isSelectable = index in legalPieces
                    val radius = cell * if (isSelectable) 0.38f else 0.32f

                    // Shadow
                    drawOval(
                        color = Color.Black.copy(alpha = 0.28f),
                        topLeft = Offset(
                            center.x - radius * 0.82f + cell * 0.07f,
                            center.y + radius * 0.35f
                        ),
                        size = Size(
                            radius * 1.65f,
                            radius * 0.55f
                        )
                    )

                    // Pawn base
                    drawCircle(
                        color = playerColors[player].copy(alpha = 0.65f),
                        radius = radius,
                        center = Offset(
                            center.x,
                            center.y + cell * 0.07f
                        )
                    )

                    // Pawn body
                    drawOval(
                        brush = Brush.verticalGradient(
                            listOf(
                                playerColors[player].copy(alpha = 0.75f),
                                playerColors[player],
                                playerColors[player].copy(alpha = 0.65f)
                            ),
                            startY = center.y - radius,
                            endY = center.y + radius
                        ),
                        topLeft = Offset(
                            center.x - radius * 0.70f,
                            center.y - radius * 0.15f
                        ),
                        size = Size(
                            radius * 1.4f,
                            radius * 1.35f
                        )
                    )

                    // Pawn head
                    drawCircle(
                        brush = Brush.linearGradient(
                            listOf(
                                Color.White.copy(alpha = 0.85f),
                                playerColors[player],
                                playerColors[player].copy(alpha = 0.65f)
                            ),
                            start = Offset(
                                center.x - radius,
                                center.y - radius
                            ),
                            end = Offset(
                                center.x + radius,
                                center.y + radius
                            )
                        ),
                        radius = radius * 0.68f,
                        center = Offset(
                            center.x,
                            center.y - radius * 0.48f
                        )
                    )

                    // Highlight
                    drawCircle(
                        color = Color.White.copy(alpha = 0.65f),
                        radius = radius * 0.16f,
                        center = Offset(
                            center.x - radius * 0.28f,
                            center.y - radius * 0.70f
                        )
                    )

                    if (isSelectable) {
                        drawCircle(
                            color = Color(0xFFFFE36B),
                            radius = radius * 1.25f,
                            center = center,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(
                                width = cell * 0.07f
                            )
                        )
                    }
                }
            }
        }

        // Dice sitting on the board
        DiceOnTable(
            value = dice,
            onClick = onRollDice,
            modifier = Modifier
                .align(Alignment.Center)
                .size(62.dp)
        )
    }
}

private fun tokenCenter(
    point: Pair<Int, Int>,
    piece: Int,
    cell: Float
): Offset {
    val offsets = listOf(
        -0.13f to -0.13f,
        0.13f to -0.13f,
        -0.13f to 0.13f,
        0.13f to 0.13f
    )

    val offset = offsets[piece % 4]

    return Offset(
        (point.second + 0.5f + offset.first) * cell,
        (point.first + 0.5f + offset.second) * cell
    )
}

@Composable
private fun DiceOnTable(
    value: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var rolling by remember { mutableIntStateOf(0) }

    val animatedScale by animateFloatAsState(
        targetValue = if (rolling % 2 == 0) 1f else 1.13f,
        animationSpec = tween(130),
        label = "diceScale"
    )

    Box(
        modifier = modifier
            .offset(y = (-animatedScale + 1f).dp)
            .clip(RoundedCornerShape(13.dp))
            .background(Color(0xFF17243D))
            .clickable {
                rolling++
                onClick()
            }
            .padding(5.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val side = size.minDimension * 0.88f
            val left = (size.width - side) / 2f
            val top = (size.height - side) / 2f

            drawRoundRect(
                color = Color.Black.copy(alpha = 0.4f),
                topLeft = Offset(left + 3.dp.toPx(), top + 4.dp.toPx()),
                size = Size(side, side),
                cornerRadius = CornerRadius(side * 0.2f)
            )

            drawRoundRect(
                brush = Brush.linearGradient(
                    listOf(
                        Color.White,
                        Color(0xFFE9E9E9),
                        Color(0xFFC7CBD2)
                    ),
                    start = Offset(left, top),
                    end = Offset(left + side, top + side)
                ),
                topLeft = Offset(left, top),
                size = Size(side, side),
                cornerRadius = CornerRadius(side * 0.2f)
            )

            val pip = side * 0.085f
            val positions = listOf(
                0.25f to 0.25f,
                0.75f to 0.25f,
                0.25f to 0.75f,
                0.75f to 0.75f,
                0.5f to 0.5f,
                0.25f to 0.5f,
                0.75f to 0.5f
            )

            val visible = when (value) {
                1 -> listOf(4)
                2 -> listOf(0, 3)
                3 -> listOf(0, 4, 3)
                4 -> listOf(0, 1, 2, 3)
                5 -> listOf(0, 1, 2, 3, 4)
                else -> listOf(0, 1, 2, 3, 5, 6)
            }

            visible.forEach { index ->
                val position = positions[index]
                drawCircle(
                    color = Color(0xFF17243D),
                    radius = pip,
                    center = Offset(
                        left + side * position.first,
                        top + side * position.second
                    )
                )
            }

            drawRoundRect(
                color = Color.White.copy(alpha = 0.55f),
                topLeft = Offset(
                    left + side * 0.12f,
                    top + side * 0.10f
                ),
                size = Size(side * 0.32f, side * 0.045f),
                cornerRadius = CornerRadius(side * 0.04f)
            )
        }
    }
}
