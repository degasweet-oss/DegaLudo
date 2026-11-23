package com.dega.ludo
import androidx.compose.ui.graphics.graphicsLayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

private val Gold = Color(0xFFFFD166)
private val Dark = Color(0xFF101827)
private val Panel = Color(0xFF1C293B)
private val Red = Color(0xFFE94B55)
private val Green = Color(0xFF24B879)
private val Yellow = Color(0xFFFFC542)
private val Blue = Color(0xFF438BEE)
private val WhiteBoard = Color(0xFFF7F3E9)

private val playerColors = listOf(Red, Green, Yellow, Blue)

private val track = listOf(
    6 to 1, 6 to 2, 6 to 3, 6 to 4, 6 to 5,
    5 to 6, 4 to 6, 3 to 6, 2 to 6, 1 to 6, 0 to 6,
    0 to 7, 0 to 8, 1 to 8, 2 to 8, 3 to 8, 4 to 8, 5 to 8,
    6 to 9, 6 to 10, 6 to 11, 6 to 12, 6 to 13, 6 to 14,
    7 to 14, 8 to 14, 8 to 13, 8 to 12, 8 to 11, 8 to 10, 8 to 9,
    9 to 8, 10 to 8, 11 to 8, 12 to 8, 13 to 8, 14 to 8,
    14 to 7, 14 to 6, 13 to 6, 12 to 6, 11 to 6, 10 to 6, 9 to 6,
    8 to 5, 8 to 4, 8 to 3, 8 to 2, 8 to 1, 8 to 0,
    7 to 0
)

private val starts = listOf(0, 13, 26, 39)

private val homeLanes = listOf(
    (1..5).map { 7 to it },
    (1..5).map { it to 7 },
    (13 downTo 9).map { 7 to it },
    (13 downTo 9).map { it to 7 }
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Gold,
                    background = Dark,
                    surface = Panel
                )
            ) {
                DegaApp()
            }
        }
    }
}

private enum class Page {
    HOME, SETUP, GAME, SHOP, OTHER
}

@Composable
private fun DegaApp() {
    var page by remember { mutableStateOf(Page.HOME) }
    var count by remember { mutableIntStateOf(2) }
    var names by remember {
        mutableStateOf(listOf("الأحمر", "الأخضر", "الأصفر", "الأزرق"))
    }

    when (page) {
        Page.HOME -> HomeScreen(
            onPlay = { page = Page.SETUP },
            onShop = { page = Page.SHOP },
            onOther = { page = Page.OTHER }
        )
        Page.SETUP -> SetupScreen(
            count, names,
            onCount = { count = it },
            onNames = { names = it },
            onBack = { page = Page.HOME },
            onStart = { page = Page.GAME }
        )
        Page.GAME -> GameScreen(
            count, names,
            onBack = { page = Page.HOME }
        )
        Page.SHOP -> InfoScreen(
            "متجر DEGA",
            "العملات والجواهر والخلفيات",
            "🛍️"
        ) { page = Page.HOME }
        Page.OTHER -> InfoScreen(
            "قريبًا",
            "الأصدقاء والبطولات والمهام",
            "🏆"
        ) { page = Page.HOME }
    }
}
@Composable
private fun ScreenTitle(title: String, onBack: (() -> Unit)? = null) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (onBack != null) {
            Text("‹", color = Gold, fontSize = 34.sp,
                modifier = Modifier.clickable { onBack() }.padding(end = 12.dp))
        }
        Text(title, color = Gold, fontSize = 24.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun HomeScreen(
    onPlay: () -> Unit,
    onShop: () -> Unit,
    onOther: () -> Unit
) {
    Column(
        Modifier.fillMaxSize().background(Dark)
            .verticalScroll(rememberScrollState()).padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScreenTitle("DEGA LUDO")
        Spacer(Modifier.height(24.dp))
        Box(
            Modifier.fillMaxWidth().height(190.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(Brush.linearGradient(
                    listOf(Color(0xFF263D60), Color(0xFF151F32), Color(0xFF594323))
                )),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🎲", fontSize = 62.sp)
                Text("DEGA LUDO", color = Gold, fontSize = 32.sp,
                    fontWeight = FontWeight.Black)
                Text("PLAY • WIN • ENJOY", color = Color.White)
            }
        }
        Spacer(Modifier.height(18.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Balance("🪙", "1,250", Modifier.weight(1f))
            Balance("💎", "25", Modifier.weight(1f))
        }
        Spacer(Modifier.height(22.dp))
        Button(
            onClick = onPlay,
            modifier = Modifier.fillMaxWidth().height(60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Gold),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text("🎮  العب الآن", color = Dark, fontSize = 21.sp,
                fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(16.dp))
        MenuItem("🛍️", "متجر DEGA", onShop)
        MenuItem("🏆", "البطولات والجوائز", onOther)
        MenuItem("👤", "الملف الشخصي", onOther)
        MenuItem("👥", "الأصدقاء", onOther)
        MenuItem("🎯", "المهام اليومية", onOther)
        MenuItem("⚙️", "الإعدادات", onOther)
    }
}

@Composable
private fun Balance(icon: String, amount: String, modifier: Modifier = Modifier) {
    Column(modifier.clip(RoundedCornerShape(16.dp)).background(Panel).padding(16.dp)) {
        Text(icon, fontSize = 23.sp)
        Text(amount, color = Gold, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun MenuItem(icon: String, title: String, action: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 5.dp)
            .clip(RoundedCornerShape(15.dp)).background(Panel)
            .clickable { action() }.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(icon, fontSize = 25.sp)
        Spacer(Modifier.width(14.dp))
        Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.weight(1f))
        Text("›", color = Gold, fontSize = 26.sp)
    }
}

@Composable
private fun SetupScreen(
    count: Int,
    names: List<String>,
    onCount: (Int) -> Unit,
    onNames: (List<String>) -> Unit,
    onBack: () -> Unit,
    onStart: () -> Unit
) {
    Column(
        Modifier.fillMaxSize().background(Dark)
            .verticalScroll(rememberScrollState()).padding(18.dp)
    ) {
        ScreenTitle("إعداد المباراة", onBack)
        Spacer(Modifier.height(24.dp))
        Text("عدد اللاعبين", color = Color.White, fontSize = 17.sp)
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(2, 3, 4).forEach { n ->
                Button(
                    onClick = { onCount(n) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (count == n) Gold else Panel
                    )
                ) {
                    Text("$n", color = if (count == n) Dark else Color.White)
                }
            }
        }
        Spacer(Modifier.height(22.dp))
        Text("أسماء اللاعبين", color = Color.White, fontSize = 17.sp)
        repeat(count) { i ->
            OutlinedTextField(
                value = names[i],
                onValueChange = { v ->
                    onNames(names.toMutableList().also { it[i] = v })
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                label = { Text("اللاعب ${i + 1}") },
                singleLine = true
            )
        }
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = onStart,
            modifier = Modifier.fillMaxWidth().height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Gold)
        ) {
            Text("ابدأ المباراة", color = Dark, fontSize = 18.sp,
                fontWeight = FontWeight.Bold)
        }
    }
}

private fun legalPieces(player: Int, dice: Int, positions: List<Int>): List<Int> {
    return (0..3).filter { piece ->
        val p = positions[player * 4 + piece]
        (p == 0 && dice == 6) || (p in 1..56 && p + dice <= 57)
    }
}

private fun boardPoint(
    player: Int,
    progress: Int,
    piece: Int,
    cell: Float
): Offset {
    if (progress == 0) {
        val home = when (player) {
            0 -> 2.2f to 2.2f
            1 -> 2.2f to 10.2f
            2 -> 10.2f to 10.2f
            else -> 10.2f to 2.2f
        }
        return Offset(
            (home.first + piece % 2 * 1.6f) * cell,
            (home.second + piece / 2 * 1.6f) * cell
        )
    }

    if (progress == 57) {
        return Offset(
            7.5f * cell + ((piece % 2) - .5f) * cell * .35f,
            7.5f * cell + ((piece / 2) - .5f) * cell * .35f
        )
    }

    val (r, c) = if (progress <= 51) {
        track[(starts[player] + progress - 1) % 52]
    } else {
        homeLanes[player][progress - 52]
    }
    return Offset((c + .5f) * cell, (r + .5f) * cell)
}

private suspend fun movePiece(
    piece: Int,
    player: Int,
    dice: Int,
    playerCount: Int,
    positions: MutableList<Int>,
    onWin: (Boolean) -> Unit,
    onCapture: () -> Unit
) {
    val index = player * 4 + piece
    val old = positions[index]
    val target = if (old == 0) 1 else old + dice

    for (step in (old + 1)..target) {
        positions[index] = step
        delay(105)
    }

    if (target in 1..51) {
        val landing = (starts[player] + target - 1) % 52
        val safe = landing in listOf(0, 8, 13, 21, 26, 34, 39, 47)

        if (!safe) {
            for (other in 0 until playerCount) {
                if (other == player) continue
                for (otherPiece in 0..3) {
                    val oi = other * 4 + otherPiece
                    val op = positions[oi]
                    if (op in 1..51 &&
                        (starts[other] + op - 1) % 52 == landing
                    ) {
                        positions[oi] = 0
                        onCapture()
                    }
                }
            }
        }
    }

    onWin((0..3).all { positions[player * 4 + it] == 57 })
}
@Composable
private fun GameScreen(
    playerCount: Int,
    names: List<String>,
    onBack: () -> Unit
) {
    var turn by remember { mutableIntStateOf(0) }
    var dice by remember { mutableIntStateOf(1) }
    var rolled by remember { mutableStateOf(false) }
    var rolling by remember { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }
    var winner by remember { mutableIntStateOf(-1) }
    var message by remember { mutableStateOf("اضغط على النرد لبدء اللعب") }
    var spin by remember { mutableFloatStateOf(0f) }

    val scope = rememberCoroutineScope()
    val positions = remember {
        mutableStateListOf<Int>().apply { repeat(16) { add(0) } }
    }

    val legal = legalPieces(turn, dice, positions)

    suspend fun performMove(piece: Int) {
        if (busy || winner != -1) return

        busy = true
        rolled = false
        message = "تتحرك القطعة..."

        movePiece(
            piece, turn, dice, playerCount, positions,
            onWin = { won ->
                if (won) {
                    winner = turn
                    message = "🎉 ${names[turn]} فاز بالمباراة!"
                }
            },
            onCapture = {
                message = "رائع! أعدت قطعة المنافس إلى البداية"
            }
        )

        busy = false

        if (winner == -1 && dice != 6) {
            turn = (turn + 1) % playerCount
        } else if (winner == -1) {
            message = "رميت 6! العب مرة أخرى"
        } else {
            message = "انتهت المباراة"
        }
    }

    Column(
        Modifier.fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF17243A), Dark, Color(0xFF0B1220))
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "‹", color = Gold, fontSize = 34.sp,
                modifier = Modifier.clickable { onBack() }
            )
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text("DEGA LUDO", color = Gold, fontSize = 21.sp,
                    fontWeight = FontWeight.Black)
                Text("المباراة المحلية", color = Color.LightGray, fontSize = 12.sp)
            }
            Text("🪙 1,250", color = Gold, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            repeat(playerCount) { i ->
                Column(
                    Modifier.weight(1f)
                        .clip(RoundedCornerShape(13.dp))
                        .background(
                            if (turn == i) playerColors[i].copy(alpha = .22f)
                            else Panel
                        )
                        .border(
                            if (turn == i) 2.dp else 1.dp,
                            if (turn == i) playerColors[i]
                            else Color(0xFF34445A),
                            RoundedCornerShape(13.dp)
                        )
                        .padding(7.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        Modifier.size(14.dp).shadow(4.dp, CircleShape)
                            .clip(CircleShape).background(playerColors[i])
                    )
                    Text(names[i], color = Color.White, fontSize = 10.sp, maxLines = 1)
                    Text(
                        if (turn == i) "دوره الآن" else " ",
                        color = Gold, fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        LudoBoard(
            playerCount = playerCount,
            positions = positions,
            activePlayer = turn,
            selectablePieces = if (rolled) legal else emptyList(),
            onPieceTap = { piece ->
                if (rolled && !busy && piece in legal) {
                    scope.launch { performMove(piece) }
                }
            }
        )

        Spacer(Modifier.height(12.dp))

        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    if (winner >= 0) "الفائز: ${names[winner]}"
                    else "دور ${names[turn]}",
                    color = if (winner >= 0) Gold else playerColors[turn],
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
                Text(message, color = Color.LightGray, fontSize = 12.sp)
            }

            Dice3D(
                value = dice,
                rolling = rolling,
                enabled = !rolled && !rolling && !busy && winner == -1,
                onClick = {
                    rolling = true
                    spin += 1f

                    scope.launch {
                        repeat(16) {
                            dice = Random.nextInt(1, 7)
                            delay(65)
                        }

                        dice = Random.nextInt(1, 7)
                        rolling = false
                        rolled = true

                        val available = legalPieces(turn, dice, positions)

                        if (available.isEmpty()) {
                            message = "لا توجد حركة متاحة"
                            rolled = false

                            if (dice != 6) {
                                turn = (turn + 1) % playerCount
                            } else {
                                message = "رميت 6، حاول مرة أخرى"
                            }
                        } else if (available.size == 1) {
                            message = "حركة تلقائية"
                            performMove(available.first())
                        } else {
                            message = "اضغط على القطعة التي تريد تحريكها"
                        }
                    }
                }
            )
        }

        Spacer(Modifier.height(12.dp))

        Text(
            "اضغط على النرد للرمي، ثم اضغط على القطعة مباشرة لتحريكها",
            color = Color.LightGray,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun Dice3D(
    value: Int,
    rolling: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (rolling) 360f else 0f,
        animationSpec = tween(600),
        label = "diceRotation"
    )

    Box(
        Modifier.size(78.dp)
            .graphicsLayer {
                rotationZ = rotation
                rotationX = if (rolling) rotation else 0f
                rotationY = if (rolling) rotation * 1.5f else 0f
                cameraDistance = 12 * density
            }
            .shadow(14.dp, RoundedCornerShape(17.dp))
            .clip(RoundedCornerShape(17.dp))
            .background(Color(0xFFB7B7B7))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(4.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color.White, Color(0xFFE9E9E9), Color(0xFFBFC5D0))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            drawRoundRect(
                Color(0xFF808995).copy(alpha = .45f),
                Offset(w * .08f, h * .09f),
                Size(w * .84f, h * .84f),
                cornerRadius = CornerRadius(w * .17f)
            )

            val points = when (value) {
                1 -> listOf(.5f to .5f)
                2 -> listOf(.28f to .28f, .72f to .72f)
                3 -> listOf(.28f to .28f, .5f to .5f, .72f to .72f)
                4 -> listOf(
                    .28f to .28f, .72f to .28f,
                    .28f to .72f, .72f to .72f
                )
                5 -> listOf(
                    .28f to .28f, .72f to .28f,
                    .5f to .5f, .28f to .72f, .72f to .72f
                )
                else -> listOf(
                    .28f to .25f, .72f to .25f,
                    .28f to .5f, .72f to .5f,
                    .28f to .75f, .72f to .75f
                )
            }

            points.forEach { (x, y) ->
                drawCircle(
                    Color(0xFF1C2735),
                    w * .065f,
                    Offset(w * x, h * y)
                )
            }

            drawRoundRect(
                Color.White.copy(alpha = .55f),
                Offset(w * .12f, h * .10f),
                Size(w * .28f, h * .035f),
                cornerRadius = CornerRadius(w * .02f)
            )
        }
    }
}
@Composable
private fun LudoBoard(
    playerCount: Int,
    positions: List<Int>,
    activePlayer: Int,
    selectablePieces: List<Int>,
    onPieceTap: (Int) -> Unit
) {
    Canvas(
        Modifier.fillMaxWidth()
            .aspectRatio(1f)
            .shadow(16.dp, RoundedCornerShape(17.dp))
            .clip(RoundedCornerShape(17.dp))
            .background(WhiteBoard)
            .pointerInput(positions.toList(), activePlayer, selectablePieces) {
                detectTapGestures { tap ->
                    val cell = size.width / 15f
                    for (piece in 0..3) {
                        if (piece in selectablePieces) {
                            val point = boardPoint(
                                activePlayer,
                                positions[activePlayer * 4 + piece],
                                piece,
                                cell
                            )
                            if ((tap - point).getDistance() < cell * .62f) {
                                onPieceTap(piece)
                                return@detectTapGestures
                            }
                        }
                    }
                }
            }
    ) {
        val cell = size.width / 15f

        fun square(r: Int, c: Int, color: Color) {
            val top = Offset(c * cell, r * cell)
            drawRect(color, top, Size(cell, cell))
            drawRect(
                Color(0xFFB9B2A6).copy(alpha = .65f),
                top,
                Size(cell, cell),
                style = Stroke(1f)
            )
        }

        drawRect(WhiteBoard, size = size)

        val homes = listOf(
            Triple(0, 0, Red),
            Triple(0, 9, Green),
            Triple(9, 9, Yellow),
            Triple(9, 0, Blue)
        )

        homes.forEach { (r, c, color) ->
            drawRoundRect(
                Brush.linearGradient(listOf(color.copy(alpha = .9f), color)),
                Offset(c * cell, r * cell),
                Size(cell * 6, cell * 6),
                cornerRadius = CornerRadius(cell * .30f)
            )

            drawRoundRect(
                Color(0xFFFFFCF5),
                Offset((c + .72f) * cell, (r + .72f) * cell),
                Size(cell * 4.56f, cell * 4.56f),
                cornerRadius = CornerRadius(cell * .28f)
            )

            drawRoundRect(
                color.copy(alpha = .14f),
                Offset((c + 1.02f) * cell, (r + 1.02f) * cell),
                Size(cell * 3.96f, cell * 3.96f),
                cornerRadius = CornerRadius(cell * .25f)
            )

            for (a in 0..1) {
                for (b in 0..1) {
                    val center = Offset(
                        (c + 2.2f + b * 1.6f) * cell,
                        (r + 2.2f + a * 1.6f) * cell
                    )

                    drawCircle(
                        Color.Black.copy(alpha = .18f),
                        cell * .47f,
                        center + Offset(cell * .04f, cell * .07f)
                    )
                    drawCircle(color, cell * .39f, center)
                    drawCircle(
                        Color.White.copy(alpha = .85f),
                        cell * .39f,
                        center,
                        style = Stroke(cell * .065f)
                    )
                    drawCircle(
                        Color.White.copy(alpha = .65f),
                        cell * .11f,
                        center - Offset(cell * .10f, cell * .11f)
                    )
                }
            }
        }

        track.forEachIndexed { i, (r, c) ->
            val color = when (i) {
                0 -> Red
                13 -> Green
                26 -> Yellow
                39 -> Blue
                else -> Color.White
            }
            square(r, c, color)
        }

        for (i in 1..5) {
            square(7, i, Red)
            square(i, 7, Green)
            square(7, 14 - i, Yellow)
            square(14 - i, 7, Blue)
        }

        val center = Offset(7.5f * cell, 7.5f * cell)

        val triangles = listOf(
            listOf(
                Offset(6 * cell, 6 * cell),
                center,
                Offset(6 * cell, 9 * cell)
            ),
            listOf(
                Offset(6 * cell, 6 * cell),
                Offset(9 * cell, 6 * cell),
                center
            ),
            listOf(
                Offset(9 * cell, 6 * cell),
                Offset(9 * cell, 9 * cell),
                center
            ),
            listOf(
                Offset(6 * cell, 9 * cell),
                Offset(9 * cell, 9 * cell),
                center
            )
        )

        triangles.forEachIndexed { i, pts ->
            val path = Path().apply {
                moveTo(pts[0].x, pts[0].y)
                lineTo(pts[1].x, pts[1].y)
                lineTo(pts[2].x, pts[2].y)
                close()
            }
            drawPath(path, playerColors[i])
        }

        drawCircle(
            Gold,
            cell * .17f,
            center,
            style = Stroke(cell * .06f)
        )

        for (p in 0 until playerCount) {
            for (piece in 0..3) {
                val progress = positions[p * 4 + piece]
                val point = boardPoint(p, progress, piece, cell)
                val selected = p == activePlayer && piece in selectablePieces
                val radius = if (selected) cell * .42f else cell * .34f

                if (selected) {
                    drawCircle(
                        Gold.copy(alpha = .38f),
                        radius * 1.65f,
                        point
                    )
                    drawCircle(
                        Gold,
                        radius * 1.35f,
                        point,
                        style = Stroke(cell * .08f)
                    )
                }

                drawCircle(
                    Color.Black.copy(alpha = .28f),
                    radius,
                    point + Offset(cell * .06f, cell * .10f)
                )

                drawCircle(
                    playerColors[p].copy(alpha = .75f),
                    radius * 1.12f,
                    point + Offset(0f, cell * .035f)
                )

                drawCircle(
                    Brush.linearGradient(
                        listOf(
                            playerColors[p].copy(alpha = .98f),
                            playerColors[p],
                            playerColors[p].copy(alpha = .78f)
                        )
                    ),
                    radius,
                    point
                )

                drawCircle(
                    Color.White.copy(alpha = .85f),
                    radius,
                    point,
                    style = Stroke(cell * .045f)
                )

                drawCircle(
                    Color.White.copy(alpha = .65f),
                    radius * .25f,
                    point - Offset(radius * .28f, radius * .30f)
                )
            }
        }
    }
}

@Composable
private fun InfoScreen(
    title: String,
    description: String,
    emoji: String,
    onBack: () -> Unit
) {
    Column(
        Modifier.fillMaxSize().background(Dark).padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScreenTitle(title, onBack)
        Spacer(Modifier.height(45.dp))
        Text(emoji, fontSize = 60.sp)
        Spacer(Modifier.height(20.dp))
        Text(
            description,
            color = Color.White,
            textAlign = TextAlign.Center,
            fontSize = 16.sp
        )
        Spacer(Modifier.height(25.dp))
        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(containerColor = Gold)
        ) {
            Text("العودة للرئيسية", color = Dark)
        }
    }
}