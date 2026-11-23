package com.dega.ludo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.random.Random

private val Navy = Color(0xFF101B35)
private val Gold = Color(0xFFFFD36A)
private val Red = Color(0xFFE84152)
private val Green = Color(0xFF20B978)
private val Yellow = Color(0xFFFFC928)
private val Blue = Color(0xFF398BFA)

private val playerColors = listOf(Red, Green, Yellow, Blue)
private val playerNames = listOf("الأحمر", "الأخضر", "الأصفر", "الأزرق")

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

private fun makeTrack(): List<Pair<Int, Int>> {
    val result = mutableListOf<Pair<Int, Int>>()

    for (x in 6..13) result.add(x to 0)
    for (y in 1..13) result.add(13 to y)
    for (x in 12 downTo 0) result.add(x to 13)
    for (y in 12 downTo 0) result.add(0 to y)
    for (x in 1..5) result.add(x to 0)

    return result
}

private val track = makeTrack()

private val starts = listOf(0, 13, 26, 39)

private fun homeLane(player: Int): List<Pair<Int, Int>> {
    return when (player) {
        0 -> (1..6).map { 7 to it }
        1 -> (12 downTo 7).map { it to 7 }
        2 -> (12 downTo 7).map { 7 to it }
        else -> (1..6).map { it to 7 }
    }
}

private fun homeSlots(player: Int): List<Pair<Int, Int>> {
    return when (player) {
        0 -> listOf(2 to 2, 4 to 2, 2 to 4, 4 to 4)
        1 -> listOf(9 to 2, 11 to 2, 9 to 4, 11 to 4)
        2 -> listOf(9 to 9, 11 to 9, 9 to 11, 11 to 11)
        else -> listOf(2 to 9, 4 to 9, 2 to 11, 4 to 11)
    }
}

private fun piecePosition(
    player: Int,
    step: Int,
    piece: Int
): Pair<Int, Int> {
    if (step < 0) {
        return homeSlots(player)[piece]
    }

    if (step >= 56) {
        return 7 to 7
    }

    if (step >= 51) {
        return homeLane(player)[step - 51]
    }

    val index = (starts[player] + step) % 52
    return track[index]
}

private fun legalMoves(
    pieces: List<List<Int>>,
    player: Int,
    dice: Int
): List<Int> {
    if (dice == 0) return emptyList()

    return (0..3).filter { piece ->
        val step = pieces[player][piece]

        when {
            step == -1 -> dice == 6
            step >= 56 -> false
            step + dice <= 56 -> true
            else -> false
        }
    }
}

@Composable
private fun DegaLudoGame() {
    var playerCount by remember { mutableIntStateOf(4) }

    var pieces by remember {
        mutableStateOf(
            List(4) { List(4) { -1 } }
        )
    }

    var currentPlayer by remember { mutableIntStateOf(0) }
    var dice by remember { mutableIntStateOf(0) }
    var pendingMoves by remember { mutableStateOf<List<Int>>(emptyList()) }
    var winner by remember { mutableIntStateOf(-1) }
    var message by remember { mutableStateOf("اضغط على النرد لبدء اللعب") }
    var rolling by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (rolling) 360f else 0f,
        label = "diceRotation"
    )

    fun nextTurn() {
        currentPlayer = (currentPlayer + 1) % playerCount
        dice = 0
        pendingMoves = emptyList()
        message = "دور اللاعب ${playerNames[currentPlayer]}"
    }

    fun movePiece(piece: Int) {
        if (winner != -1 || piece !in pendingMoves) return

        val updated = pieces.map { it.toMutableList() }.toMutableList()
        val oldStep = updated[currentPlayer][piece]

        val newStep = if (oldStep == -1) 0 else oldStep + dice

        updated[currentPlayer][piece] = newStep

        // أكل قطع الخصم عند الوقوف على نفس المربع
        if (newStep in 0..50) {
            val myIndex = (starts[currentPlayer] + newStep) % 52

            val safeSquares = listOf(0, 13, 26, 39)

            if (myIndex !in safeSquares) {
                for (enemy in 0 until playerCount) {
                    if (enemy == currentPlayer) continue

                    for (enemyPiece in 0..3) {
                        val enemyStep = updated[enemy][enemyPiece]

                        if (enemyStep in 0..50) {
                            val enemyIndex =
                                (starts[enemy] + enemyStep) % 52

                            if (enemyIndex == myIndex) {
                                updated[enemy][enemyPiece] = -1
                            }
                        }
                    }
                }
            }
        }

        pieces = updated.map { it.toList() }

        val finished = pieces[currentPlayer].all { it >= 56 }

        if (finished) {
            winner = currentPlayer
            message = "فاز اللاعب ${playerNames[currentPlayer]}!"
            pendingMoves = emptyList()
            return
        }

        val rolledSix = dice == 6

        pendingMoves = emptyList()
        dice = 0

        if (rolledSix) {
            message = "رائع! حصلت على 6، ارْمِ مرة أخرى"
        } else {
            nextTurn()
        }
    }

    fun rollDice() {
        if (winner != -1 || pendingMoves.isNotEmpty()) return

        rolling = true

        val result = Random.nextInt(1, 7)
        dice = result

        rolling = false

        val available = legalMoves(
            pieces,
            currentPlayer,
            result
        )

        pendingMoves = available

        when {
            available.isEmpty() -> {
                message = if (result == 6) {
                    "لا توجد حركة متاحة، ارْمِ مرة أخرى"
                } else {
                    "لا توجد حركة متاحة"
                }

                if (result != 6) {
                    nextTurn()
                }
            }

            available.size == 1 -> {
                message = "تحريك القطعة المتاحة تلقائيًا"
                movePiece(available.first())
            }

            else -> {
                message = "اختر القطعة التي تريد تحريكها"
            }
        }
    }

    fun resetGame() {
        pieces = List(4) { List(4) { -1 } }
        currentPlayer = 0
        dice = 0
        pendingMoves = emptyList()
        winner = -1
        message = "اضغط على النرد لبدء اللعب"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Navy)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "DEGA LUDO",
            color = Gold,
            fontSize = 29.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            text = "ROYAL EDITION",
            color = Color.LightGray,
            fontSize = 11.sp,
            letterSpacing = 4.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (winner == -1) {
            Text(
                text = "عدد اللاعبين",
                color = Color.White,
                fontSize = 14.sp
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(2, 3, 4).forEach { count ->
                    Button(
                        onClick = {
                            playerCount = count
                            resetGame()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor =
                                if (playerCount == count) Gold else Color.DarkGray,
                            contentColor =
                                if (playerCount == count) Navy else Color.White
                        )
                    ) {
                        Text("$count لاعبين")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until playerCount) {
                val active = currentPlayer == i && winner == -1

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(3.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (active) playerColors[i].copy(alpha = 0.25f)
                            else Color(0xFF202D49)
                        )
                        .padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(15.dp)
                            .clip(RoundedCornerShape(50))
                            .background(playerColors[i])
                    )

                    Text(
                        text = playerNames[i],
                        color = Color.White,
                        fontSize = 11.sp
                    )

                    Text(
                        text = "${pieces[i].count { it >= 56 }}/4",
                        color = Gold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LudoBoard(
            pieces = pieces,
            playerCount = playerCount,
            currentPlayer = currentPlayer,
            legalPieces = pendingMoves,
            onPieceTap = { piece ->
                movePiece(piece)
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "نرد ${playerNames[currentPlayer]}",
                    color = playerColors[currentPlayer],
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color.White, Color(0xFFE2E2E2))
                            )
                        )
                        .clickable(
                            enabled = pendingMoves.isEmpty() && winner == -1
                        ) {
                            rollDice()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (dice == 0) "🎲" else dice.toString(),
                        color = Navy,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier
                            .offset(y = 0.dp)
                    )
                }

                Text(
                    text = "اضغط لرمي النرد",
                    color = Color.LightGray,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = message,
            color = Gold,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { resetGame() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Gold,
                contentColor = Navy
            )
        ) {
            Text(
                text = "لعبة جديدة",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun LudoBoard(
    pieces: List<List<Int>>,
    playerCount: Int,
    currentPlayer: Int,
    legalPieces: List<Int>,
    onPieceTap: (Int) -> Unit
) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF182541))
            .pointerInput(
                pieces,
                currentPlayer,
                legalPieces
            ) {
                detectTapGestures { tap ->
                    val cell = size.width / 14f

                    val tappedPiece = legalPieces.map { piece ->
                        val position = piecePosition(
                            currentPlayer,
                            pieces[currentPlayer][piece],
                            piece
                        )

                        val center = Offset(
                            (position.first + 0.5f) * cell,
                            (position.second + 0.5f) * cell
                        )

                        val dx = tap.x - center.x
                        val dy = tap.y - center.y

                        Triple(
                            piece,
                            dx * dx + dy * dy,
                            cell * 0.48f
                        )
                    }.filter {
                        it.second <= it.third * it.third
                    }.minByOrNull {
                        it.second
                    }

                    if (tappedPiece != null) {
                        onPieceTap(tappedPiece.first)
                    }
                }
            }
    ) {
        val cell = size.width / 14f

        fun cellRect(x: Int, y: Int): Offset {
            return Offset(x * cell, y * cell)
        }

        fun drawCell(
            x: Int,
            y: Int,
            color: Color
        ) {
            drawRect(
                color = color,
                topLeft = cellRect(x, y),
                size = Size(cell, cell)
            )

            drawRect(
                color = Color.White.copy(alpha = 0.25f),
                topLeft = cellRect(x, y),
                size = Size(cell, cell),
                style = Stroke(width = 1f)
            )
        }

        // خلفية اللوحة
        drawRect(
            color = Color(0xFFF4EBDD),
            size = size
        )

        // بيوت اللاعبين
        val baseColors = listOf(
            Red,
            Green,
            Yellow,
            Blue
        )

        drawRect(
            color = Red.copy(alpha = 0.85f),
            topLeft = Offset(0f, 0f),
            size = Size(cell * 6, cell * 6)
        )

        drawRect(
            color = Green.copy(alpha = 0.85f),
            topLeft = Offset(cell * 8, 0f),
            size = Size(cell * 6, cell * 6)
        )

        drawRect(
            color = Blue.copy(alpha = 0.85f),
            topLeft = Offset(0f, cell * 8),
            size = Size(cell * 6, cell * 6)
        )

        drawRect(
            color = Yellow.copy(alpha = 0.85f),
            topLeft = Offset(cell * 8, cell * 8),
            size = Size(cell * 6, cell * 6)
        )

        // مساحات القطع داخل البيوت
        for (player in 0..3) {
            for (slot in homeSlots(player)) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.9f),
                    radius = cell * 0.39f,
                    center = Offset(
                        (slot.first + 0.5f) * cell,
                        (slot.second + 0.5f) * cell
                    )
                )

                drawCircle(
                    color = baseColors[player],
                    radius = cell * 0.29f,
                    center = Offset(
                        (slot.first + 0.5f) * cell,
                        (slot.second + 0.5f) * cell
                    ),
                    style = Stroke(width = cell * 0.09f)
                )
            }
        }

        // مسار اللعب الخارجي
        track.forEachIndexed { index, point ->
            val color = when (index) {
                0 -> Red
                13 -> Green
                26 -> Yellow
                39 -> Blue
                else -> Color.White
            }

            drawCell(
                point.first,
                point.second,
                color
            )
        }

        // المسارات الداخلية
        for (player in 0..3) {
            val lane = homeLane(player)

            lane.forEach { point ->
                drawCell(
                    point.first,
                    point.second,
                    baseColors[player]
                )
            }
        }

        // مركز الفوز
        drawCircle(
            color = Gold,
            radius = cell * 1.05f,
            center = Offset(cell * 7.5f, cell * 7.5f)
        )

        drawCircle(
            color = Navy,
            radius = cell * 0.65f,
            center = Offset(cell * 7.5f, cell * 7.5f)
        )

        // القطع
        for (player in 0 until playerCount) {
            for (piece in 0..3) {
                val step = pieces[player][piece]

                if (step >= 56) continue

                val position = piecePosition(
                    player,
                    step,
                    piece
                )

                val center = Offset(
                    (position.first + 0.5f) * cell,
                    (position.second + 0.5f) * cell
                )

                val isSelectable =
                    player == currentPlayer &&
                    piece in legalPieces

                val radius = cell * 0.31f

                if (isSelectable) {
                    drawCircle(
                        color = Gold,
                        radius = radius * 1.45f,
                        center = center
                    )
                }

                // ظل القطعة
                drawCircle(
                    color = Color.Black.copy(alpha = 0.35f),
                    radius = radius,
                    center = Offset(
                        center.x + cell * 0.06f,
                        center.y + cell * 0.08f
                    )
                )

                // جسم القطعة بتدرج لامع
                drawCircle(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.8f),
                            baseColors[player],
                            baseColors[player].copy(alpha = 0.7f)
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
                    radius = radius,
                    center = center
                )

                drawCircle(
                    color = Color.White.copy(alpha = 0.7f),
                    radius = radius * 0.22f,
                    center = Offset(
                        center.x - radius * 0.3f,
                        center.y - radius * 0.35f
                    )
                )

                drawCircle(
                    color = Color.Black.copy(alpha = 0.2f),
                    radius = radius,
                    center = center,
                    style = Stroke(width = 1.5f)
                )
            }
        }

        // إطار ذهبي
        drawRect(
            color = Gold,
            style = Stroke(width = 5f)
        )
    }
}