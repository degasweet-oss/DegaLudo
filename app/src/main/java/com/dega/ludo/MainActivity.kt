
package com.dega.ludo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

private val Gold = Color(0xFFFFD166)
private val Dark = Color(0xFF101827)
private val Panel = Color(0xFF1C293B)
private val Red = Color(0xFFE94B55)
private val Green = Color(0xFF24B879)
private val Yellow = Color(0xFFFFC542)
private val Blue = Color(0xFF438BEE)
private val colors = listOf(Red, Green, Yellow, Blue)

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

private enum class Page { HOME, SETUP, GAME, SHOP, OTHER }

@Composable
private fun DegaApp() {
    var page by remember { mutableStateOf(Page.HOME) }
    var count by remember { mutableIntStateOf(2) }
    var names by remember {
        mutableStateOf(listOf("الأحمر", "الأزرق", "الأصفر", "الأخضر"))
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
        Page.SHOP -> InfoScreen("متجر DEGA", "العملات والجواهر والخلفيات ستضاف في مرحلة المتجر.", "🛍️") { page = Page.HOME }
        Page.OTHER -> InfoScreen("قريبًا", "الأصدقاء والبطولات والمهام والملف الشخصي.", "🏆") { page = Page.HOME }
    }
}

@Composable
private fun ScreenTitle(title: String, onBack: (() -> Unit)? = null) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (onBack != null) {
            Text(
                "‹",
                color = Gold,
                fontSize = 34.sp,
                modifier = Modifier.clickable { onBack() }.padding(end = 12.dp)
            )
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
                .background(
                    androidx.compose.ui.graphics.Brush.linearGradient(
                        listOf(Color(0xFF263D60), Color(0xFF151F32), Color(0xFF594323))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🎲", fontSize = 62.sp)
                Text("DEGA LUDO", color = Gold, fontSize = 32.sp, fontWeight = FontWeight.Black)
                Text("PLAY • WIN • ENJOY", color = Color.White, fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(18.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Balance("🪙", "1,250")
            Balance("💎", "25")
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
private fun Balance(icon: String, amount: String) {
    Column(
        Modifier.weight(1f).clip(RoundedCornerShape(16.dp))
            .background(Panel).padding(16.dp)
    ) {
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
                onValueChange = { value ->
                    onNames(names.toMutableList().also { it[i] = value })
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
            Text("ابدأ المباراة", color = Dark, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
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
    var message by remember { mutableStateOf("اضغط على النرد لبدء اللعب") }
    var winner by remember { mutableIntStateOf(-1) }
    val positions = remember { mutableStateListOf<Int>().apply { repeat(16) { add(0) } } }
    val rotation by animateFloatAsState(
        targetValue = if (rolling) 720f else 0f,
        label = "diceRoll"
    )
    val legal = (0..3).filter { piece ->
        val p = positions[turn * 4 + piece]
        (p == 0 && dice == 6) || (p in 1..56 && p + dice <= 57)
    }

    LaunchedEffect(rolling) {
        if (rolling) {
            repeat(9) {
                dice = Random.nextInt(1, 7)
                delay(75)
            }
            dice = Random.nextInt(1, 7)
            rolling = false
            rolled = true
            message = "ظهر الرقم $dice"
            val available = (0..3).filter { piece ->
                val p = positions[turn * 4 + piece]
                (p == 0 && dice == 6) || (p in 1..56 && p + dice <= 57)
            }
            if (available.isEmpty()) {
                message = "لا توجد حركة متاحة"
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
                message = "اختر القطعة التي تريد تحريكها"
            }
        }
    }

    Column(
        Modifier.fillMaxSize().background(Dark)
            .verticalScroll(rememberScrollState()).padding(10.dp)
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
                Column(
                    Modifier.weight(1f).clip(RoundedCornerShape(12.dp))
                        .background(if (turn == i) colors[i].copy(alpha = .25f) else Panel)
                        .border(if (turn == i) 2.dp else 1.dp,
                            if (turn == i) colors[i] else Panel, RoundedCornerShape(12.dp))
                        .padding(7.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(Modifier.size(13.dp).clip(CircleShape).background(colors[i]))
                    Text(names[i], color = Color.White, fontSize = 10.sp, maxLines = 1)
                    Text(if (turn == i) "دوره الآن" else "", color = Gold, fontSize = 10.sp)
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        LudoBoard(playerCount, positions)
        Spacer(Modifier.height(12.dp))
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Column(Modifier.weight(1f)) {
                Text(if (winner >= 0) "الفائز: ${names[winner]}" else "دور ${names[turn]}",
                    color = colors[turn], fontWeight = FontWeight.Bold)
                Text(message, color = Color.LightGray, fontSize = 12.sp)
            }
            Box(
                Modifier.size(82.dp).shadow(12.dp, RoundedCornerShape(18.dp))
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White)
                    .clickable(enabled = !rolled && !rolling && winner == -1) {
                        rolling = true
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    diceFace(dice),
                    color = Dark,
                    fontSize = 46.sp,
                    modifier = Modifier.offset(x = (rotation % 3).dp)
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        if (rolled && winner == -1) {
            Text("اختر القطعة:", color = Gold, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                legal.forEach { piece ->
                    Button(
                        onClick = {
                            movePiece(piece, turn, dice, positions) { won ->
                                if (won) winner = turn
                            }
                            rolled = false
                            if (dice != 6 && winner == -1) turn = (turn + 1) % playerCount
                            message = "تم تحريك القطعة"
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = colors[turn])
                    ) {
                        Text("قطعة ${piece + 1}", color = Color.White, fontSize = 11.sp)
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            "اضغط على النرد لرميه. عند وجود أكثر من حركة اختر القطعة المناسبة.",
            color = Color.LightGray, fontSize = 11.sp, textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
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

private fun diceFace(value: Int): String = when (value) {
    1 -> "⚀"
    2 -> "⚁"
    3 -> "⚂"
    4 -> "⚃"
    5 -> "⚄"
    else -> "⚅"
}

@Composable
private fun LudoBoard(playerCount: Int, positions: List<Int>) {
    Canvas(
        Modifier.fillMaxWidth().aspectRatio(1f)
            .shadow(12.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp)).background(Color(0xFFF4F1E8))
    ) {
        val cell = size.width / 15f
        fun square(r: Int, c: Int, color: Color) {
            drawRect(color, Offset(c * cell, r * cell), Size(cell, cell))
            drawRect(Color(0xFFB8B8B8), Offset(c * cell, r * cell),
                Size(cell, cell), style = Stroke(1f))
        }

        drawRect(Color(0xFFF4F1E8), size = size)
        val homes = listOf(
            Triple(0, 0, Red), Triple(0, 9, Green),
            Triple(9, 9, Yellow), Triple(9, 0, Blue)
        )
        homes.forEach { (r, c, color) ->
            drawRect(color, Offset(c * cell, r * cell), Size(cell * 6, cell * 6))
            drawRoundRect(
                Color.White,
                Offset((c + 1) * cell, (r + 1) * cell),
                Size(cell * 4, cell * 4),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(cell * .25f)
            )
            for (a in 0..1) for (b in 0..1) {
                drawCircle(
                    color.copy(alpha = .35f), cell * .36f,
                    Offset((c + 2 + b * 2) * cell, (r + 2 + a * 2) * cell)
                )
            }
        }

        track.forEachIndexed { i, (r, c) ->
            val special = when (i) {
                0 -> Red
                13 -> Green
                26 -> Yellow
                39 -> Blue
                else -> Color.White
            }
            square(r, c, special)
        }
        for (i in 1..5) {
            square(7, i, Red)
            square(i, 7, Green)
            square(7, 14 - i, Yellow)
            square(14 - i, 7, Blue)
        }

        val center = Offset(size.width / 2, size.height / 2)
        drawCircle(Color(0xFF26364B), cell * 1.45f, center)
        drawCircle(Gold, cell * 1.05f, center, style = Stroke(cell * .16f))
        drawCircle(Color.White, cell * .35f, center)

        val starts = listOf(0, 13, 26, 39)
        for (p in 0 until playerCount) {
            for (piece in 0..3) {
                val progress = positions[p * 4 + piece]
                val home = when (p) {
                    0 -> Offset((2.2f + piece % 2 * 1.6f) * cell,
                        (2.2f + piece / 2 * 1.6f) * cell)
                    1 -> Offset((10.2f + piece % 2 * 1.6f) * cell,
                        (2.2f + piece / 2 * 1.6f) * cell)
                    2 -> Offset((10.2f + piece % 2 * 1.6f) * cell,
                        (10.2f + piece / 2 * 1.6f) * cell)
                    else -> Offset((2.2f + piece % 2 * 1.6f) * cell,
                        (10.2f + piece / 2 * 1.6f) * cell)
                }
                val point = if (progress == 0) home else {
                    val (r, c) = track[(starts[p] + progress - 1) % track.size]
                    Offset((c + .5f) * cell, (r + .5f) * cell)
                }
                drawCircle(Color.Black.copy(alpha = .3f), cell * .39f,
                    point + Offset(2f, 3f))
                drawCircle(colors[p], cell * .34f, point)
                drawCircle(Color.White, cell * .34f, point, style = Stroke(2f))
                drawCircle(Color.White.copy(alpha = .7f), cell * .1f,
                    point + Offset(-cell * .1f, -cell * .1f))
            }
        }
    }
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
