package com.dega.ludo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.graphicsLayer
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

private val Gold = Color(0xFFFFD166)
private val Dark = Color(0xFF0B1220)
private val Panel = Color(0xFF18263A)
private val BoardIvory = Color(0xFFF4EEDC)
private val Red = Color(0xFFE64B55)
private val Green = Color(0xFF20A96B)
private val Yellow = Color(0xFFF4BE36)
private val Blue = Color(0xFF4389E8)
private val playerColors = listOf(Red, Blue, Yellow, Green)

private val ludoTrack = listOf(
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

private enum class Screen { HOME, SETUP, GAME, SHOP, OTHER }

@Composable
private fun DegaApp() {
    var screen by remember { mutableStateOf(Screen.HOME) }
    var playerCount by remember { mutableIntStateOf(2) }
    var names by remember {
        mutableStateOf(listOf("الأحمر", "الأزرق", "الأصفر", "الأخضر"))
    }

    when (screen) {
        Screen.HOME -> HomeScreen(
            onPlay = { screen = Screen.SETUP },
            onShop = { screen = Screen.SHOP },
            onOther = { screen = Screen.OTHER }
        )
        Screen.SETUP -> SetupScreen(
            count = playerCount,
            names = names,
            onCount = { playerCount = it },
            onNames = { names = it },
            onBack = { screen = Screen.HOME },
            onStart = { screen = Screen.GAME }
        )
        Screen.GAME -> GameScreen(
            playerCount = playerCount,
            names = names,
            onBack = { screen = Screen.HOME }
        )
        Screen.SHOP -> InfoScreen(
            "متجر DEGA",
            "العملات الذهبية والجواهر والخلفيات ستضاف في مرحلة المتجر.",
            "🛍️"
        ) { screen = Screen.HOME }
        Screen.OTHER -> InfoScreen(
            "قريبًا",
            "الأصدقاء والبطولات والمهام والملف الشخصي.",
            "🏆"
        ) { screen = Screen.HOME }
    }
}

@Composable
private fun ScreenTitle(title: String, onBack: (() -> Unit)? = null) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (onBack != null) {
            Text(
                "‹",
                color = Gold,
                fontSize = 36.sp,
                modifier = Modifier.clickable { onBack() }.padding(end = 12.dp)
            )
        }
        Text(title, color = Gold, fontSize = 24.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun HomeScreen(onPlay: () -> Unit, onShop: () -> Unit, onOther: () -> Unit) {
    Column(
        Modifier.fillMaxSize().background(Dark).verticalScroll(rememberScrollState()).padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScreenTitle("DEGA LUDO")
        Spacer(Modifier.height(22.dp))
        Box(
            Modifier.fillMaxWidth().height(185.dp).clip(RoundedCornerShape(26.dp))
                .background(Brush.linearGradient(listOf(Color(0xFF263D60), Color(0xFF111B2D), Color(0xFF604522)))),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🎲", fontSize = 58.sp)
                Text("DEGA LUDO", color = Gold, fontSize = 32.sp, fontWeight = FontWeight.Black)
                Text("PLAY • WIN • ENJOY", color = Color.White, fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(18.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            BalanceCard("🪙", "1,250", Modifier.weight(1f))
            BalanceCard("💎", "25", Modifier.weight(1f))
        }
        Spacer(Modifier.height(22.dp))
        Button(
            onClick = onPlay,
            modifier = Modifier.fillMaxWidth().height(60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Gold),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text("🎮  العب الآن", color = Dark, fontSize = 21.sp, fontWeight = FontWeight.Bold)
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
private fun BalanceCard(icon: String, amount: String, modifier: Modifier = Modifier) {
    Column(modifier.clip(RoundedCornerShape(16.dp)).background(Panel).padding(16.dp)) {
        Text(icon, fontSize = 23.sp)
        Text(amount, color = Gold, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun MenuItem(icon: String, title: String, action: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 5.dp).clip(RoundedCornerShape(15.dp))
            .background(Panel).clickable { action() }.padding(16.dp),
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
        Modifier.fillMaxSize().background(Dark).verticalScroll(rememberScrollState()).padding(18.dp)
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
                    colors = ButtonDefaults.buttonColors(containerColor = if (count == n) Gold else Panel)
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
                onValueChange = { value -> onNames(names.toMutableList().also { it[i] = value }) },
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
            Text("ابدأ المباراة", color = Dark, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun GameScreen(playerCount: Int, names: List<String>, onBack: () -> Unit) {
    var turn by remember { mutableIntStateOf(0) }
    var dice by remember { mutableIntStateOf(1) }
    var rolled by remember { mutableStateOf(false) }
    var rolling by remember { mutableStateOf(false) }
    var rollCycle by remember { mutableIntStateOf(0) }
    var message by remember { mutableStateOf("اضغط على النرد الموجود في منتصف الطاولة") }
    var winner by remember { mutableIntStateOf(-1) }
    val positions = remember { mutableStateListOf<Int>().apply { repeat(16) { add(0) } } }

    val legalPieces = remember(turn, dice, positions.toList(), rolled) {
        if (!rolled) emptyList()
        else (0..3).filter { piece ->
            val p = positions[turn * 4 + piece]
            (p == 0 && dice == 6) || (p in 1..56 && p + dice <= 57)
        }
    }

    LaunchedEffect(rolling, rollCycle) {
        if (rolling) {
            repeat(14) {
                dice = Random.nextInt(1, 7)
                delay(65)
            }
            dice = Random.nextInt(1, 7)
            rolling = false
            rolled = true
            message = "ظهر الرقم $dice — اختر قطعتك"
            val available = (0..3).filter { piece ->
                val p = positions[turn * 4 + piece]
                (p == 0 && dice == 6) || (p in 1..56 && p + dice <= 57)
            }
            if (available.isEmpty()) {
                message = "لا توجد حركة متاحة لهذا اللاعب"
                rolled = false
                if (dice != 6) turn = (turn + 1) % playerCount
            } else if (available.size == 1) {
                movePiece(available[0], turn, dice, positions) { won ->
                    if (won) winner = turn
                }
                message = "تحركت القطعة تلقائيًا"
                rolled = false
                if (dice != 6 && winner == -1) turn = (turn + 1) % playerCount
            } else {
                message = "لديك ${available.size} قطع متاحة — اختر واحدة"
            }
        }
    }

    Column(
        Modifier.fillMaxSize().background(Dark).verticalScroll(rememberScrollState()).padding(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("‹", color = Gold, fontSize = 34.sp, modifier = Modifier.clickable { onBack() })
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text("DEGA LUDO", color = Gold, fontSize = 21.sp, fontWeight = FontWeight.Black)
                Text("مباراة محلية", color = Color.LightGray, fontSize = 12.sp)
            }
            Text("🪙 1,250", color = Gold, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            repeat(playerCount) { i ->
                val active = turn == i
                Column(
                    Modifier.weight(1f).clip(RoundedCornerShape(12.dp))
                        .background(if (active) playerColors[i].copy(alpha = .24f) else Panel)
                        .border(if (active) 2.dp else 1.dp, if (active) playerColors[i] else Panel, RoundedCornerShape(12.dp))
                        .padding(7.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        Box(Modifier.size(11.dp).clip(CircleShape).background(playerColors[i]))
                        if (active) {
                            Spacer(Modifier.width(4.dp))
                            Text("🎲", fontSize = 13.sp)
                        }
                    }
                    Text(names[i], color = Color.White, fontSize = 10.sp, maxLines = 1)
                    Text(if (active) "دوره الآن" else " ", color = Gold, fontSize = 10.sp)
                }
            }
        }
        Spacer(Modifier.height(10.dp))

        Box(Modifier.fillMaxWidth().aspectRatio(1f)) {
            LudoBoard(
                playerCount = playerCount,
                positions = positions,
                activePlayer = turn,
                selectablePieces = if (rolled) legalPieces else emptyList(),
                onPieceTap = { piece ->
                    if (rolled && piece in legalPieces && winner == -1) {
                        movePiece(piece, turn, dice, positions) { won -> if (won) winner = turn }
                        rolled = false
                        message = "تم تحريك القطعة"
                        if (dice != 6 && winner == -1) turn = (turn + 1) % playerCount
                    }
                }
            )
            Dice3D(
                value = dice,
                rolling = rolling,
                rollCycle = rollCycle,
                enabled = !rolled && !rolling && winner == -1,
                onClick = {
                    if (!rolling && !rolled && winner == -1) {
                        rollCycle++
                        rolling = true
                        message = "النرد بيتدحرج..."
                    }
                },
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(Modifier.height(10.dp))
        Row(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                .background(Panel).padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.size(15.dp).clip(CircleShape).background(playerColors[turn]))
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Text(if (winner >= 0) "الفائز: ${names[winner]}" else "دور ${names[turn]}", color = playerColors[turn], fontWeight = FontWeight.Bold)
                Text(message, color = Color.LightGray, fontSize = 12.sp)
            }
            if (!rolled && winner == -1) {
                Text("اضغط النرد", color = Gold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (rolled && legalPieces.size > 1 && winner == -1) {
            Spacer(Modifier.height(10.dp))
            Text("اختر القطعة التي تريد تحريكها", color = Gold, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                legalPieces.forEach { piece ->
                    Button(
                        onClick = {
                            movePiece(piece, turn, dice, positions) { won -> if (won) winner = turn }
                            rolled = false
                            message = "تم تحريك القطعة"
                            if (dice != 6 && winner == -1) turn = (turn + 1) % playerCount
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = playerColors[turn])
                    ) {
                        Text("قطعة ${piece + 1}", color = Color.White, fontSize = 11.sp)
                    }
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(
            "اضغط على النرد ثلاثي الأبعاد في الطاولة لرميه. القطعة الوحيدة المتاحة تتحرك تلقائيًا.",
            color = Color.LightGray, fontSize = 11.sp, textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun Dice3D(
    value: Int,
    rolling: Boolean,
    rollCycle: Int,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (rolling) 720f + rollCycle * 720f else 0f,
        animationSpec = tween(durationMillis = if (rolling) 950 else 260),
        label = "diceRotation"
    )
    val tilt by animateFloatAsState(
        targetValue = if (rolling) 28f else 0f,
        animationSpec = tween(durationMillis = 950),
        label = "diceTilt"
    )
    Box(
        modifier.size(76.dp)
            .shadow(18.dp, RoundedCornerShape(17.dp))
            .graphicsLayer {
                rotationZ = rotation
                rotationX = tilt
                rotationY = tilt * .72f
                cameraDistance = 12 * density
            }
            .clip(RoundedCornerShape(17.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color.White, Color(0xFFE7EAF0), Color(0xFFB9C1CF))
                )
            )
            .border(2.dp, Color(0xFFFFE7A1), RoundedCornerShape(17.dp))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.fillMaxSize().padding(10.dp)) {
            val dot = size.minDimension * .105f
            val positions = when (value.coerceIn(1, 6)) {
                1 -> listOf(.5f to .5f)
                2 -> listOf(.27f to .27f, .73f to .73f)
                3 -> listOf(.27f to .27f, .5f to .5f, .73f to .73f)
                4 -> listOf(.27f to .27f, .73f to .27f, .27f to .73f, .73f to .73f)
                5 -> listOf(.27f to .27f, .73f to .27f, .5f to .5f, .27f to .73f, .73f to .73f)
                else -> listOf(.27f to .24f, .73f to .24f, .27f to .5f, .73f to .5f, .27f to .76f, .73f to .76f)
            }
            positions.forEach { (x, y) ->
                drawCircle(Color(0xFF182235), dot, Offset(size.width * x, size.height * y))
                drawCircle(Color(0xFF56647A), dot * .32f, Offset(size.width * x - dot * .22f, size.height * y - dot * .22f))
            }
        }
    }
}

private fun movePiece(
    piece: Int,
    player: Int,
    dice: Int,
    positions: MutableList<Int>,
    onWin: (Boolean) -> Unit
) {
    val index = player * 4 + piece
    val old = positions[index]
    if (old == 0 && dice == 6) positions[index] = 1
    else if (old in 1..56 && old + dice <= 57) positions[index] = old + dice
    onWin((0..3).all { positions[player * 4 + it] == 57 })
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
        Modifier.fillMaxSize()
            .shadow(16.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(BoardIvory)
            .pointerInput(playerCount, positions.toList(), activePlayer, selectablePieces) {
                detectTapGestures { tap ->
                    val cell = size.width / 15f
                    var bestPiece = -1
                    var bestDistance = Float.MAX_VALUE
                    for (piece in selectablePieces) {
                        val globalIndex = activePlayer * 4 + piece
                        val progress = positions[globalIndex]
                        val point = piecePoint(activePlayer, piece, progress, cell)
                        val distance = (point - tap).let { it.x * it.x + it.y * it.y }
                        if (distance < bestDistance) {
                            bestDistance = distance
                            bestPiece = piece
                        }
                    }
                    if (bestPiece >= 0 && bestDistance < cell * cell * 1.25f) onPieceTap(bestPiece)
                }
            }
    ) {
        val cell = size.width / 15f
        drawRoundRect(
            Brush.linearGradient(listOf(Color(0xFFFFFDF5), Color(0xFFE7DFC9))),
            size = size,
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(cell * .28f)
        )

        fun tile(row: Int, col: Int, color: Color) {
            val top = Offset(col * cell, row * cell)
            drawRect(color, top, Size(cell, cell))
            drawRect(Color(0xFFB7B1A3), top, Size(cell, cell), style = Stroke(cell * .025f))
            drawRect(Color.White.copy(alpha = .28f), top + Offset(1f, 1f), Size(cell - 2f, cell * .08f))
        }

        val homes = listOf(
            Triple(0, 0, Red), Triple(0, 9, Green),
            Triple(9, 9, Yellow), Triple(9, 0, Blue)
        )
        homes.forEach { (r, c, color) ->
            drawRoundRect(
                Brush.linearGradient(listOf(color.copy(alpha = .96f), color.copy(alpha = .72f))),
                Offset(c * cell, r * cell), Size(cell * 6, cell * 6),
                androidx.compose.ui.geometry.CornerRadius(cell * .22f)
            )
            drawRoundRect(
                Color(0xFFFFFEF8), Offset((c + 1) * cell, (r + 1) * cell),
                Size(cell * 4, cell * 4), androidx.compose.ui.geometry.CornerRadius(cell * .22f)
            )
            drawRoundRect(
                color.copy(alpha = .20f), Offset((c + 1.18f) * cell, (r + 1.18f) * cell),
                Size(cell * 3.64f, cell * 3.64f), androidx.compose.ui.geometry.CornerRadius(cell * .18f),
                style = Stroke(cell * .07f)
            )
            for (a in 0..1) for (b in 0..1) {
                val center = Offset((c + 2.05f + b * 1.9f) * cell, (r + 2.05f + a * 1.9f) * cell)
                drawCircle(Color(0xFFB8B8B8).copy(alpha = .28f), cell * .49f, center + Offset(0f, cell * .08f))
                drawCircle(Brush.radialGradient(listOf(Color.White, color.copy(alpha = .22f))), cell * .43f, center)
                drawCircle(color, cell * .32f, center, style = Stroke(cell * .045f))
            }
        }

        ludoTrack.forEachIndexed { index, (r, c) ->
            val special = when (index) {
                0 -> Red
                13 -> Green
                26 -> Yellow
                39 -> Blue
                else -> Color(0xFFFFFEFA)
            }
            tile(r, c, special)
            if (index in listOf(8, 21, 34, 47)) {
                val center = Offset((c + .5f) * cell, (r + .5f) * cell)
                drawCircle(Gold, cell * .18f, center)
            }
        }

        for (i in 1..5) {
            tile(7, i, Red.copy(alpha = .84f))
            tile(i, 7, Green.copy(alpha = .84f))
            tile(7, 14 - i, Yellow.copy(alpha = .88f))
            tile(14 - i, 7, Blue.copy(alpha = .84f))
        }

        val center = Offset(size.width / 2f, size.height / 2f)
        val half = cell * 1.5f
        val triangles = listOf(
            Triple(Offset(center.x - half, center.y - half), Offset(center.x + half, center.y - half), Red),
            Triple(Offset(center.x + half, center.y - half), Offset(center.x + half, center.y + half), Green),
            Triple(Offset(center.x + half, center.y + half), Offset(center.x - half, center.y + half), Yellow),
            Triple(Offset(center.x - half, center.y + half), Offset(center.x - half, center.y - half), Blue)
        )
        triangles.forEach { (a, b, color) ->
            val path = Path().apply {
                moveTo(a.x, a.y)
                lineTo(b.x, b.y)
                lineTo(center.x, center.y)
                close()
            }
            drawPath(path, color)
        }
        drawCircle(Color(0xFF17263A), cell * .42f, center)
        drawCircle(Gold, cell * .32f, center, style = Stroke(cell * .055f))
        drawCircle(Color.White, cell * .12f, center)

        for (p in 0 until playerCount) {
            for (piece in 0..3) {
                val progress = positions[p * 4 + piece]
                val point = piecePoint(p, piece, progress, cell)
                val color = playerColors[p]
                val isSelectable = p == activePlayer && piece in selectablePieces
                val radius = cell * if (isSelectable) .37f else .32f
                if (isSelectable) drawCircle(Gold, radius * 1.48f, point)
                drawCircle(Color.Black.copy(alpha = .28f), radius, point + Offset(cell * .05f, cell * .09f))
                drawCircle(
                    Brush.linearGradient(listOf(color.copy(red = (color.red + .22f).coerceAtMost(1f)), color, color.copy(red = color.red * .72f))),
                    radius, point
                )
                drawCircle(Color.White.copy(alpha = .92f), radius, point, style = Stroke(cell * .055f))
                drawCircle(Color.White.copy(alpha = .72f), radius * .22f, point + Offset(-radius * .28f, -radius * .30f))
            }
        }
    }
}

private fun piecePoint(player: Int, piece: Int, progress: Int, cell: Float): Offset {
    if (progress == 0) {
        return when (player) {
            0 -> Offset((2.15f + piece % 2 * 1.7f) * cell, (2.15f + piece / 2 * 1.7f) * cell)
            1 -> Offset((2.15f + piece % 2 * 1.7f) * cell, (11.15f + piece / 2 * 1.7f) * cell)
            2 -> Offset((11.15f + piece % 2 * 1.7f) * cell, (11.15f + piece / 2 * 1.7f) * cell)
            else -> Offset((11.15f + piece % 2 * 1.7f) * cell, (2.15f + piece / 2 * 1.7f) * cell)
        }
    }
    val starts = listOf(0, 13, 26, 39)
    val (r, c) = ludoTrack[(starts[player] + progress - 1) % ludoTrack.size]
    return Offset((c + .5f) * cell, (r + .5f) * cell)
}

@Composable
private fun InfoScreen(title: String, description: String, emoji: String, onBack: () -> Unit) {
    Column(
        Modifier.fillMaxSize().background(Dark).padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScreenTitle(title, onBack)
        Spacer(Modifier.height(45.dp))
        Text(emoji, fontSize = 60.sp)
        Spacer(Modifier.height(20.dp))
        Text(description, color = Color.White, textAlign = TextAlign.Center, fontSize = 16.sp)
        Spacer(Modifier.height(25.dp))
        Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Gold)) {
            Text("العودة للرئيسية", color = Dark)
        }
    }
}