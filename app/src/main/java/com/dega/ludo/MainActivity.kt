
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

private val Red = Color(0xFFE94B55)
private val Green = Color(0xFF24B879)
private val Yellow = Color(0xFFFFC542)
private val Blue = Color(0xFF438BEE)
private val Gold = Color(0xFFFFD166)
private val Dark = Color(0xFF101827)
private val Panel = Color(0xFF1C293B)
private val SoftText = Color(0xFFB8C5D6)

private val playerPalette = listOf(Red, Green, Yellow, Blue)

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
    HOME, MODE, SETUP, GAME, SHOP, TOURNAMENTS,
    PROFILE, FRIENDS, MISSIONS, HISTORY, SETTINGS
}

@Composable
private fun DegaApp() {
    var page by remember { mutableStateOf(Page.HOME) }
    var playerCount by remember { mutableIntStateOf(2) }
    var selectedMode by remember { mutableStateOf("محلي") }
    var names by remember {
        mutableStateOf(listOf("اللاعب الأحمر", "اللاعب الأزرق", "اللاعب الأصفر", "اللاعب الأخضر"))
    }

    fun goBack() {
        page = when (page) {
            Page.HOME -> Page.HOME
            Page.MODE -> Page.HOME
            Page.SETUP -> Page.MODE
            Page.GAME -> Page.HOME
            else -> Page.HOME
        }
    }

    when (page) {
        Page.HOME -> HomeScreen(
            onPlay = { page = Page.MODE },
            onShop = { page = Page.SHOP },
            onTournaments = { page = Page.TOURNAMENTS },
            onProfile = { page = Page.PROFILE },
            onFriends = { page = Page.FRIENDS },
            onMissions = { page = Page.MISSIONS },
            onHistory = { page = Page.HISTORY },
            onSettings = { page = Page.SETTINGS }
        )

        Page.MODE -> ModeScreen(
            onBack = { goBack() },
            onSelect = {
                selectedMode = it
                if (it == "محلي") page = Page.SETUP
            }
        )

        Page.SETUP -> SetupScreen(
            playerCount = playerCount,
            names = names,
            onCount = { playerCount = it },
            onNames = { names = it },
            onBack = { goBack() },
            onStart = { page = Page.GAME }
        )

        Page.GAME -> GameScreen(
            playerCount = playerCount,
            names = names,
            onBack = { goBack() }
        )

        Page.SHOP -> SimpleScreen("متجر DEGA", "العملات الذهبية والجواهر والخلفيات", "🛍️", { goBack() })
        Page.TOURNAMENTS -> SimpleScreen("البطولات", "ستظهر البطولات والجوائز هنا عند تفعيل نظام المسابقات.", "🏆", { goBack() })
        Page.PROFILE -> SimpleScreen("الملف الشخصي", "ملف اللاعب والإحصائيات والمستوى.", "👤", { goBack() })
        Page.FRIENDS -> SimpleScreen("الأصدقاء", "قائمة الأصدقاء والدعوات ستتوفر عند تفعيل الاتصال.", "👥", { goBack() })
        Page.MISSIONS -> SimpleScreen("المهام اليومية", "أكمل المهام واجمع المكافآت.", "🎯", { goBack() })
        Page.HISTORY -> SimpleScreen("سجل المباريات", "سيظهر سجل مبارياتك هنا.", "📜", { goBack() })
        Page.SETTINGS -> SimpleScreen("الإعدادات", "إعدادات الصوت واللغة والحساب.", "⚙️", { goBack() })
    }
}

@Composable
private fun ScreenFrame(
    title: String,
    onBack: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Dark)
            .verticalScroll(rememberScrollState())
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null) {
                Text(
                    "‹",
                    color = Gold,
                    fontSize = 36.sp,
                    modifier = Modifier.clickable { onBack() }.padding(end = 12.dp)
                )
            }
            Text(
                title,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(18.dp))
        content()
    }
}

@Composable
private fun HomeScreen(
    onPlay: () -> Unit,
    onShop: () -> Unit,
    onTournaments: () -> Unit,
    onProfile: () -> Unit,
    onFriends: () -> Unit,
    onMissions: () -> Unit,
    onHistory: () -> Unit,
    onSettings: () -> Unit
) {
    ScreenFrame("DEGA LUDO") {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    androidx.compose.ui.graphics.Brush.linearGradient(
                        listOf(Color(0xFF263D60), Color(0xFF151F32), Color(0xFF513B20))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🎲", fontSize = 48.sp)
                Text("DEGA LUDO", color = Gold, fontSize = 30.sp, fontWeight = FontWeight.Black)
                Text("PLAY • WIN • ENJOY", color = Color.White, fontSize = 12.sp)
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            BalanceCard("🪙", "العملات", "1,250", Modifier.weight(1f))
            BalanceCard("💎", "الجواهر", "25", Modifier.weight(1f))
        }

        Spacer(Modifier.height(18.dp))

        Button(
            onClick = onPlay,
            modifier = Modifier.fillMaxWidth().height(62.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Gold)
        ) {
            Text("🎮  العب الآن", color = Dark, fontSize = 21.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(18.dp))
        Text("اكتشف DEGA LUDO", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))

        FeatureRow("🛍️", "المتجر", "الخلفيات والعملات", onShop)
        FeatureRow("🏆", "البطولات", "تحديات وجوائز", onTournaments)
        FeatureRow("👤", "الملف الشخصي", "بياناتك وإحصائياتك", onProfile)
        FeatureRow("👥", "الأصدقاء", "الأصدقاء والدعوات", onFriends)
        FeatureRow("🎯", "المهام اليومية", "اجمع المكافآت", onMissions)
        FeatureRow("📜", "سجل المباريات", "مبارياتك السابقة", onHistory)
        FeatureRow("⚙️", "الإعدادات", "تخصيص اللعبة", onSettings)

        Spacer(Modifier.height(20.dp))
        Text(
            "DEGA LUDO  •  إصدار تجريبي",
            color = SoftText,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun BalanceCard(
    emoji: String,
    label: String,
    amount: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Panel)
            .padding(14.dp)
    ) {
        Text("$emoji  $label", color = SoftText, fontSize = 13.sp)
        Text(amount, color = Gold, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun FeatureRow(
    emoji: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(Panel)
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(emoji, fontSize = 28.sp)
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(subtitle, color = SoftText, fontSize = 12.sp)
        }
        Text("›", color = Gold, fontSize = 28.sp)
    }
}

@Composable
private fun ModeScreen(onBack: () -> Unit, onSelect: (String) -> Unit) {
    ScreenFrame("اختر طريقة اللعب", onBack) {
        ModeCard("🎲", "لعب محلي", "العب مع أصدقائك على نفس الهاتف", true) {
            onSelect("محلي")
        }
        ModeCard("🤖", "ضد الكمبيوتر", "سيتم تفعيل الذكاء الاصطناعي في تحديث لاحق", false) {}
        ModeCard("🌐", "أونلاين مع الأصدقاء", "اللعب عبر الإنترنت قيد التطوير", false) {}
    }
}

@Composable
private fun ModeCard(
    emoji: String,
    title: String,
    subtitle: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Panel)
            .clickable(enabled = enabled) { onClick() }
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(emoji, fontSize = 34.sp)
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = if (enabled) Color.White else SoftText, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Text(subtitle, color = SoftText, fontSize = 12.sp)
        }
        Text(if (enabled) "ابدأ ›" else "قريبًا", color = if (enabled) Gold else SoftText, fontSize = 13.sp)
    }
}

@Composable
private fun SetupScreen(
    playerCount: Int,
    names: List<String>,
    onCount: (Int) -> Unit,
    onNames: (List<String>) -> Unit,
    onBack: () -> Unit,
    onStart: () -> Unit
) {
    ScreenFrame("إعداد اللاعبين", onBack) {
        Text("عدد اللاعبين", color = Color.White, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(2, 3, 4).forEach { count ->
                Button(
                    onClick = { onCount(count) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (playerCount == count) Gold else Panel
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("$count", color = if (playerCount == count) Dark else Color.White)
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        Text("أسماء اللاعبين", color = Color.White, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

        repeat(playerCount) { index ->
            OutlinedTextField(
                value = names[index],
                onValueChange = { value ->
                    onNames(names.toMutableList().also { it[index] = value })
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                label = { Text("اللاعب ${index + 1}") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Gold,
                    unfocusedBorderColor = SoftText,
                    focusedLabelColor = Gold,
                    unfocusedLabelColor = SoftText
                )
            )
        }

        Spacer(Modifier.height(20.dp))
        Button(
            onClick = onStart,
            modifier = Modifier.fillMaxWidth().height(56.dp),
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
    var currentPlayer by remember { mutableIntStateOf(0) }
    var dice by remember { mutableIntStateOf(1) }
    var hasRolled by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("اضغط على النرد لبدء اللعب") }
    val positions = remember {
        mutableStateListOf<Int>().apply { repeat(16) { add(0) } }
    }
    val diceRotation by animateFloatAsState(
        targetValue = if (hasRolled) 360f else 0f,
        label = "diceRotation"
    )

    val colors = playerPalette.take(playerCount)
    val currentColor = colors[currentPlayer]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Dark)
            .verticalScroll(rememberScrollState())
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "‹",
                color = Gold,
                fontSize = 34.sp,
                modifier = Modifier.clickable { onBack() }.padding(end = 12.dp)
            )
            Column(Modifier.weight(1f)) {
                Text("DEGA LUDO", color = Gold, fontSize = 21.sp, fontWeight = FontWeight.Black)
                Text("مباراة محلية", color = SoftText, fontSize = 12.sp)
            }
            Text("🪙 1,250", color = Gold, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(playerCount) { index ->
                val color = colors[index]
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (currentPlayer == index) color.copy(alpha = 0.24f) else Panel)
                        .border(
                            width = if (currentPlayer == index) 2.dp else 1.dp,
                            color = if (currentPlayer == index) color else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(Modifier.size(12.dp).clip(CircleShape).background(color))
                    Text(
                        names[index].ifBlank { "لاعب ${index + 1}" },
                        color = Color.White,
                        fontSize = 10.sp,
                        maxLines = 1
                    )
                    Text(
                        if (currentPlayer == index) "دورك" else " ",
                        color = Gold,
                        fontSize = 10.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        LudoBoard(
            playerCount = playerCount,
            positions = positions,
            onPieceTap = { pieceIndex ->
                val start = currentPlayer * 4
                val end = start + 4
                if (pieceIndex in start until end && hasRolled) {
                    val old = positions[pieceIndex]
                    if (old > 0 || dice == 6) {
                        positions[pieceIndex] = if (old == 0) 1 else (old + dice).coerceAtMost(57)
                        hasRolled = false
                        message = if (positions[pieceIndex] >= 57) {
                            "وصلت قطعة إلى النهاية!"
                        } else {
                            "تم تحريك القطعة"
                        }
                        currentPlayer = (currentPlayer + 1) % playerCount
                    } else {
                        message = "تحتاج إلى رقم 6 لإخراج القطعة"
                    }
                }
            }
        )

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("دور ${names[currentPlayer]}", color = currentColor, fontWeight = FontWeight.Bold)
                Text(message, color = SoftText, fontSize = 12.sp)
            }
            Spacer(Modifier.width(18.dp))
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .shadow(10.dp, RoundedCornerShape(18.dp))
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White)
                    .clickable {
                        if (!hasRolled) {
                            dice = Random.nextInt(1, 7)
                            hasRolled = true
                            message = "ظهر الرقم $dice — اختر قطعة"
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    diceFace(dice),
                    color = Dark,
                    fontSize = 42.sp,
                    modifier = Modifier.offset(x = (diceRotation % 2).dp)
                )
            }
        }

        Spacer(Modifier.height(12.dp))
        Text(
            "بعد رمي النرد، اضغط على إحدى قطع لون اللاعب الحالي لتحريكها.",
            color = SoftText,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private fun diceFace(value: Int): String {
    return when (value) {
        1 -> "⚀"
        2 -> "⚁"
        3 -> "⚂"
        4 -> "⚃"
        5 -> "⚄"
        else -> "⚅"
    }
}

private val trackCells = listOf(
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

@Composable
private fun LudoBoard(
    playerCount: Int,
    positions: List<Int>,
    onPieceTap: (Int) -> Unit
) {
    val boardColors = listOf(Red, Green, Yellow, Blue)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .shadow(12.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF2F0E9))
            .clickable {
                // القطع تُختار من أزرارها أسفل اللوحة
            }
    ) {
        val cell = size.width / 15f

        drawRoundRect(
            color = Color(0xFFF4F1E8),
            size = size,
            cornerRadius = CornerRadius(18f, 18f)
        )

        fun cellRect(row: Int, col: Int, color: Color) {
            drawRoundRect(
                color = color,
                topLeft = Offset(col * cell + 1f, row * cell + 1f),
                size = Size(cell - 2f, cell - 2f),
                cornerRadius = CornerRadius(3f, 3f)
            )
        }

        // قواعد اللاعبين الأربع
        cellRect(0, 0, Red)
        cellRect(0, 9, Green)
        cellRect(9, 0, Blue)
        cellRect(9, 9, Yellow)

        // مساحات القواعد الداخلية
        val bases = listOf(
            Triple(1, 1, Color.White),
            Triple(1, 10, Color.White),
            Triple(10, 1, Color.White),
            Triple(10, 10, Color.White)
        )
        bases.forEach { (r, c, color) ->
            drawRoundRect(
                color = color,
                topLeft = Offset(c * cell, r * cell),
                size = Size(cell * 4, cell * 4),
                cornerRadius = CornerRadius(10f, 10f)
            )
            for (i in 0..1) {
                for (j in 0..1) {
                    drawCircle(
                        color = Color(0xFFE6E8EB),
                        radius = cell * 0.34f,
                        center = Offset(
                            (c + 1.25f + j * 1.5f) * cell,
                            (r + 1.25f + i * 1.5f) * cell
                        )
                    )
                }
            }
        }

        // مسار اللعب
        trackCells.forEachIndexed { index, pair ->
            val (r, c) = pair
            val special = when (index) {
                0 -> Red
                13 -> Green
                26 -> Yellow
                39 -> Blue
                else -> Color.White
            }
            cellRect(r, c, special)
            drawRect(
                color = Color(0xFFB8B8B8),
                topLeft = Offset(c * cell, r * cell),
                size = Size(cell, cell),
                style = Stroke(width = 1f)
            )
        }

        // الممرات الملونة باتجاه المركز
        for (i in 1..5) {
            cellRect(7, i, Red)
            cellRect(i, 7, Green)
            cellRect(7, 14 - i, Yellow)
            cellRect(14 - i, 7, Blue)
        }

        // مركز اللوحة
        val center = Offset(size.width / 2f, size.height / 2f)
        drawCircle(Color(0xFF26364B), cell * 1.45f, center)
        drawCircle(Gold, cell * 0.95f, center, style = Stroke(width = cell * 0.12f))
        drawCircle(Color.White, cell * 0.35f, center)

        // قطع اللاعبين
        val startOffsets = listOf(0, 13, 26, 39)
        for (player in 0 until playerCount) {
            for (piece in 0 until 4) {
                val index = player * 4 + piece
                val progress = positions.getOrElse(index) { 0 }
                val homePositions = listOf(
                    Offset(2.2f, 2.2f),
                    Offset(3.8f, 2.2f),
                    Offset(2.2f, 3.8f),
                    Offset(3.8f, 3.8f)
                )
                val baseOffset = when (player) {
                    0 -> homePositions[piece]
                    1 -> Offset(10f + (piece % 2) * 1.6f, 2.2f + (piece / 2) * 1.6f)
                    2 -> Offset(10f + (piece % 2) * 1.6f, 10f + (piece / 2) * 1.6f)
                    else -> Offset(2.2f + (piece % 2) * 1.6f, 10f + (piece / 2) * 1.6f)
                }

                val tokenCenter = if (progress <= 0) {
                    Offset(baseOffset.x * cell, baseOffset.y * cell)
                } else {
                    val trackIndex = (startOffsets[player] + progress - 1) % trackCells.size
                    val (r, c) = trackCells[trackIndex]
                    Offset((c + 0.5f) * cell, (r + 0.5f) * cell)
                }

                drawCircle(
                    color = Color.Black.copy(alpha = 0.25f),
                    radius = cell * 0.38f,
                    center = tokenCenter + Offset(1.5f, 2.5f)
                )
                drawCircle(boardColors[player], cell * 0.34f, tokenCenter)
                drawCircle(Color.White, cell * 0.34f, tokenCenter, style = Stroke(width = 2f))
                drawCircle(Color.White.copy(alpha = 0.65f), cell * 0.10f, tokenCenter + Offset(-cell * 0.1f, -cell * 0.1f))
            }
        }
    }

    Spacer(Modifier.height(8.dp))

    // اختيار القطع من أزرار واضحة أسفل اللوحة
    Column {
        repeat(playerCount) { player ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.size(12.dp).clip(CircleShape).background(boardColors[player]))
                Spacer(Modifier.width(6.dp))
                Text("اللاعب ${player + 1}", color = Color.White, fontSize = 11.sp)
                Spacer(Modifier.weight(1f))
                repeat(4) { piece ->
                    val pieceIndex = player * 4 + piece
                    Text(
                        "● ${piece + 1}",
                        color = boardColors[player],
                        fontSize = 12.sp,
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(Panel)
                            .clickable { onPieceTap(pieceIndex) }
                            .padding(horizontal = 7.dp, vertical = 5.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SimpleScreen(
    title: String,
    description: String,
    emoji: String,
    onBack: () -> Unit
) {
    ScreenFrame(title, onBack) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(Panel),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(emoji, fontSize = 54.sp)
                Spacer(Modifier.height(10.dp))
                Text(title, color = Gold, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(18.dp))
        Text(description, color = SoftText, fontSize = 15.sp)
        Spacer(Modifier.height(22.dp))
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Gold)
        ) {
            Text("العودة للرئيسية", color = Dark, fontWeight = FontWeight.Bold)
        }
    }
}
