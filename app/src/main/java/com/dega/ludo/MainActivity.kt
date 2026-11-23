
package com.dega.ludo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import kotlin.random.Random

// ==========================================
// إعدادات DEGA LUDO العامة
// ==========================================

private val Navy = Color(0xFF101B35)
private val Gold = Color(0xFFFFD36A)

private val palette = listOf(
    Color(0xFFE84152),
    Color(0xFF20B978),
    Color(0xFFFFC928),
    Color(0xFF398BFA),
    Color(0xFF9B59B6),
    Color(0xFFFF8A32)
)

var playerColors = listOf(
    palette[0], palette[1], palette[2], palette[3]
)

var playerNames = listOf(
    "الأحمر", "الأخضر", "الأصفر", "الأزرق"
)

object DegaGameConfig {
    var playerCount = 4
}

// ==========================================
// تشغيل التطبيق
// ==========================================

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                DegaApp()
            }
        }
    }
}

// ==========================================
// نظام الصفحات والتنقل
// ==========================================

private enum class DegaPage {
    HOME, MODE, SETUP, GAME, SHOP,
    TOURNAMENTS, PROFILE, SETTINGS
}

@Composable
fun DegaApp() {
    var page by remember { mutableStateOf(DegaPage.HOME) }
    var mode by remember { mutableStateOf("محلي على نفس الهاتف") }

    var count by remember { mutableIntStateOf(4) }

    var names by remember {
        mutableStateOf(listOf("الأحمر", "الأخضر", "الأصفر", "الأزرق"))
    }

    var colors by remember {
        mutableStateOf(palette.take(4))
    }

    var coins by remember { mutableIntStateOf(1500) }
    var gems by remember { mutableIntStateOf(25) }
    var notice by remember { mutableStateOf("") }

    BackHandler(enabled = page != DegaPage.HOME) {
        page = when (page) {
            DegaPage.GAME -> DegaPage.SETUP
            DegaPage.SETUP -> DegaPage.MODE
            DegaPage.MODE -> DegaPage.HOME
            else -> DegaPage.HOME
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Navy
    ) {
        when (page) {

            DegaPage.HOME -> HomePage(
                coins = coins,
                gems = gems,
                onPlay = { page = DegaPage.MODE },
                onShop = { page = DegaPage.SHOP },
                onTournaments = { page = DegaPage.TOURNAMENTS },
                onProfile = { page = DegaPage.PROFILE },
                onSettings = { page = DegaPage.SETTINGS }
            )

            DegaPage.MODE -> ModePage(
                onBack = { page = DegaPage.HOME },
                onSelect = {
                    mode = it
                    page = DegaPage.SETUP
                }
            )

            DegaPage.SETUP -> SetupPage(
                mode = mode,
                count = count,
                names = names,
                colors = colors,
                onBack = { page = DegaPage.MODE },
                onCount = { newCount ->
                    count = newCount
                    names = List(newCount) { index ->
                        names.getOrElse(index) { "اللاعب ${index + 1}" }
                    }
                    colors = List(newCount) { index ->
                        colors.getOrElse(index) { palette[index] }
                    }
                },
                onName = { index, value ->
                    names = names.toMutableList().also {
                        it[index] = value
                    }
                },
                onColor = { index, color ->
                    colors = colors.toMutableList().also {
                        it[index] = color
                    }
                },
                onStart = {
                    DegaGameConfig.playerCount = count
                    playerNames = names
                    playerColors = colors
                    page = DegaPage.GAME
                }
            )

            DegaPage.GAME -> Box(Modifier.fillMaxSize()) {
                DegaLudoGame()

                Button(
                    onClick = { page = DegaPage.HOME },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Gold,
                        contentColor = Navy
                    )
                ) {
                    Text("الرئيسية")
                }
            }

            DegaPage.SHOP -> ShopPage(
                coins = coins,
                gems = gems,
                notice = notice,
                onBack = { page = DegaPage.HOME },
                onBuyCoins = {
                    notice = "شراء العملات الحقيقي سيتوفر لاحقًا"
                },
                onBuyBackground = {
                    if (coins >= 500) {
                        coins -= 500
                        notice = "تم فتح الخلفية التجريبية!"
                    } else {
                        notice = "رصيد العملات غير كافٍ"
                    }
                }
            )

            DegaPage.TOURNAMENTS -> SimplePage(
                title = "المسابقات والبطولات",
                content = """
                    البطولات اليومية

                    بطولات الأصدقاء

                    لوحة المتصدرين

                    جوائز العملات والجواهر

                    هذه المميزات قيد التطوير.
                """.trimIndent(),
                onBack = { page = DegaPage.HOME }
            )

            DegaPage.PROFILE -> SimplePage(
                title = "الملف الشخصي",
                content = """
                    ملف لاعب DEGA LUDO

                    المستوى: 1
                    الانتصارات: 0
                    المباريات: 0

                    سنضيف الصورة والإحصائيات لاحقًا.
                """.trimIndent(),
                onBack = { page = DegaPage.HOME }
            )

            DegaPage.SETTINGS -> SimplePage(
                title = "الإعدادات",
                content = """
                    الصوت والموسيقى

                    اللغة

                    إشعارات اللعبة

                    المساعدة والدعم
                """.trimIndent(),
                onBack = { page = DegaPage.HOME }
            )
        }
    }
}

// ==========================================
// الصفحة الرئيسية
// ==========================================

@Composable
private fun HomePage(
    coins: Int,
    gems: Int,
    onPlay: () -> Unit,
    onShop: () -> Unit,
    onTournaments: () -> Unit,
    onProfile: () -> Unit,
    onSettings: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(Modifier.height(24.dp))

            Text(
                "DEGA LUDO",
                color = Gold,
                fontSize = 34.sp,
                fontWeight = FontWeight.Black
            )

            Text(
                "ROYAL EDITION",
                color = Color.LightGray,
                letterSpacing = 5.sp,
                fontSize = 12.sp
            )

            Spacer(Modifier.height(22.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                BalanceCard(
                    value = "🪙 $coins",
                    label = "العملات",
                    modifier = Modifier.weight(1f)
                )
                BalanceCard(
                    value = "💎 $gems",
                    label = "الجواهر",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(28.dp))

            MenuButton("▶  العب الآن", onPlay, Gold)
            MenuButton("🏆  المسابقات والبطولات", onTournaments)
            MenuButton("🛒  متجر DEGA", onShop)
            MenuButton("👤  الملف الشخصي", onProfile)
            MenuButton("⚙  الإعدادات", onSettings)

            Spacer(Modifier.height(22.dp))

            Panel(Modifier.fillMaxWidth()) {
                Text(
                    "آخر الأخبار",
                    color = Gold,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "مرحبًا بك في DEGA LUDO!\n" +
                        "استعد للمنافسة والفوز بالمكافآت.\n" +
                        "ترقبوا التحديثات والبطولات القادمة.",
                    color = Color.White,
                    lineHeight = 24.sp
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "DEGA LUDO • LET'S PLAY",
                color = Color.Gray,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun BalanceCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Panel(modifier) {
        Text(
            value,
            color = Gold,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(label, color = Color.White, fontSize = 12.sp)
    }
}

// ==========================================
// اختيار طريقة اللعب
// ==========================================

@Composable
private fun ModePage(
    onBack: () -> Unit,
    onSelect: (String) -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PageHeader("اختر طريقة اللعب", onBack)

        Spacer(Modifier.height(25.dp))

        MenuButton("🤖  اللعب منفردًا ضد الكمبيوتر", {
            onSelect("ضد الكمبيوتر")
        }, Gold)

        MenuButton("🌐  اللعب أونلاين مع الأصدقاء", {
            onSelect("أونلاين مع الأصدقاء")
        })

        MenuButton("📱  اللعب على نفس الهاتف", {
            onSelect("محلي على نفس الهاتف")
        })

        Spacer(Modifier.height(18.dp))

        Panel(Modifier.fillMaxWidth()) {
            Text(
                "اللعب المحلي هو المتاح حاليًا. " +
                    "الكمبيوتر والأونلاين يحتاجان استكمال البرمجة.",
                color = Color.White,
                lineHeight = 23.sp
            )
        }
    }
}

// ==========================================
// إعداد اللاعبين والألوان
// ==========================================

@Composable
private fun SetupPage(
    mode: String,
    count: Int,
    names: List<String>,
    colors: List<Color>,
    onBack: () -> Unit,
    onCount: (Int) -> Unit,
    onName: (Int, String) -> Unit,
    onColor: (Int, Color) -> Unit,
    onStart: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PageHeader("إعداد اللاعبين", onBack)

        Text("طريقة اللعب: $mode", color = Gold)

        Spacer(Modifier.height(12.dp))

        Text("عدد اللاعبين", color = Color.White)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(2, 3, 4).forEach { number ->
                Button(
                    onClick = { onCount(number) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            if (count == number) Gold else Color(0xFF34435C),
                        contentColor =
                            if (count == number) Navy else Color.White
                    )
                ) {
                    Text("$number")
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        LazyColumn(Modifier.weight(1f)) {
            items(count) { index ->
                Panel(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                ) {
                    Text(
                        "اللاعب ${index + 1}",
                        color = colors[index],
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(6.dp))

                    OutlinedTextField(
                        value = names[index],
                        onValueChange = {
                            onName(index, it)
                        },
                        label = { Text("اسم اللاعب") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(8.dp))

                    Text("اختر اللون", color = Color.White)

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        palette.forEach { color ->
                            Box(
                                Modifier
                                    .size(32.dp)
                                    .background(
                                        color,
                                        RoundedCornerShape(50)
                                    )
                                    .then(
                                        if (colors[index] == color) {
                                            Modifier.border(
                                                3.dp,
                                                Color.White,
                                                RoundedCornerShape(50)
                                            )
                                        } else {
                                            Modifier
                                        }
                                    )
                                    .clickable {
                                        onColor(index, color)
                                    }
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = onStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Gold,
                contentColor = Navy
            )
        ) {
            Text(
                "ابدأ اللعب",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ==========================================
// المتجر
// ==========================================

@Composable
private fun ShopPage(
    coins: Int,
    gems: Int,
    notice: String,
    onBack: () -> Unit,
    onBuyCoins: () -> Unit,
    onBuyBackground: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(18.dp)
    ) {
        PageHeader("متجر DEGA", onBack)

        Text(
            "🪙 $coins       💎 $gems",
            color = Gold,
            fontSize = 20.sp
        )

        Spacer(Modifier.height(18.dp))

        Panel(Modifier.fillMaxWidth()) {
            Text("باقات العملات", color = Gold, fontSize = 19.sp)
            Text(
                "باقات عملات متنوعة ستتوفر عبر الشراء.",
                color = Color.White
            )
            Button(onClick = onBuyCoins) {
                Text("تفاصيل الباقات")
            }
        }

        Spacer(Modifier.height(12.dp))

        Panel(Modifier.fillMaxWidth()) {
            Text("خلفيات الطاولات", color = Gold, fontSize = 19.sp)
            Text("خلفية ملكية تجريبية • 500 عملة", color = Color.White)
            Button(onClick = onBuyBackground) {
                Text("فتح الخلفية")
            }
        }

        Spacer(Modifier.height(12.dp))

        Panel(Modifier.fillMaxWidth()) {
            Text("النرد والقطع", color = Gold, fontSize = 19.sp)
            Text("أشكال جديدة قيد التجهيز", color = Color.White)
        }

        if (notice.isNotBlank()) {
            Spacer(Modifier.height(12.dp))
            Text(notice, color = Gold)
        }
    }
}

// ==========================================
// صفحات إضافية
// ==========================================

@Composable
private fun SimplePage(
    title: String,
    content: String,
    onBack: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PageHeader(title, onBack)

        Spacer(Modifier.height(25.dp))

        Panel(Modifier.fillMaxWidth()) {
            Text(
                content,
                color = Color.White,
                lineHeight = 30.sp
            )
        }
    }
}

@Composable
private fun PageHeader(
    title: String,
    onBack: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onBack) {
            Text("رجوع", color = Gold)
        }

        Spacer(Modifier.weight(1f))

        Text(
            title,
            color = Gold,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun MenuButton(
    title: String,
    onClick: () -> Unit,
    color: Color = Color(0xFF263858)
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .height(58.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor =
                if (color == Gold) Navy else Color.White
        )
    ) {
        Text(
            title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun Panel(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier
            .background(
                Color(0xFF202F4D),
                RoundedCornerShape(16.dp)
            )
            .padding(15.dp),
        content = content
    )
}

// ==========================================
// مسار لعبة ليدو
// ==========================================

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

// ==========================================
// شاشة اللعب
// ==========================================

@Composable
fun DegaLudoGame() {
    var playerCount by remember {
        mutableIntStateOf(DegaGameConfig.playerCount)
    }

    var pieces by remember {
        mutableStateOf(List(4) { List(4) { -1 } })
    }

    var currentPlayer by remember { mutableIntStateOf(0) }
    var dice by remember { mutableIntStateOf(0) }

    var pendingMoves by remember {
        mutableStateOf<List<Int>>(emptyList())
    }

    var winner by remember { mutableIntStateOf(-1) }

    var message by remember {
        mutableStateOf("اضغط على النرد لبدء اللعب")
    }

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

        val updated = pieces.map {
            it.toMutableList()
        }.toMutableList()

        val oldStep = updated[currentPlayer][piece]

        val newStep = if (oldStep == -1) {
            0
        } else {
            oldStep + dice
        }

        updated[currentPlayer][piece] = newStep

        // أكل قطع الخصم
        if (newStep in 0..50) {
            val myIndex =
                (starts[currentPlayer] + newStep) % 52

            val safeSquares = listOf(0, 13, 26, 39)

            if (myIndex !in safeSquares) {
                for (enemy in 0 until playerCount) {
                    if (enemy == currentPlayer) continue

                    for (enemyPiece in 0..3) {
                        val enemyStep =
                            updated[enemy][enemyPiece]

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

        val finished =
            pieces[currentPlayer].all { it >= 56 }

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
            "DEGA LUDO",
            color = Gold,
            fontSize = 29.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            "ROYAL EDITION",
            color = Color.LightGray,
            fontSize = 11.sp,
            letterSpacing = 4.sp
        )

        Spacer(Modifier.height(12.dp))

        if (winner == -1) {
            Text(
                "عدد اللاعبين",
                color = Color.White,
                fontSize = 14.sp
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(2, 3, 4).forEach { number ->
                    Button(
                        onClick = {
                            playerCount = number
                            resetGame()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor =
                                if (playerCount == number) Gold
                                else Color.DarkGray,
                            contentColor =
                                if (playerCount == number) Navy
                                else Color.White
                        )
                    ) {
                        Text("$number لاعبين")
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))

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
                            if (active) {
                                playerColors[i].copy(alpha = 0.25f)
                            } else {
                                Color(0xFF202D49)
                            }
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
                        playerNames[i],
                        color = Color.White,
                        fontSize = 11.sp
                    )

                    Text(
                        "${pieces[i].count { it >= 56 }}/4",
                        color = Gold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        LudoBoard(
            pieces = pieces,
            playerCount = playerCount,
            currentPlayer = currentPlayer,
            legalPieces = pendingMoves,
            onPieceTap = { piece ->
                movePiece(piece)
            }
        )

        Spacer(Modifier.height(12.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "نرد ${playerNames[currentPlayer]}",
                color = playerColors[currentPlayer],
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(6.dp))

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
                    if (dice == 0) "🎲" else dice.toString(),
                    color = Navy,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Text(
                "اضغط لرمي النرد",
                color = Color.LightGray,
                fontSize = 11.sp
            )
        }

        Spacer(Modifier.height(10.dp))

        Text(
            message,
            color = Gold,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = { resetGame() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Gold,
                contentColor = Navy
            )
        ) {
            Text("لعبة جديدة", fontWeight = FontWeight.Bold)
        }
    }
}

// ==========================================
// رسم طاولة اللعبة والقطع
// ==========================================

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

        fun drawCell(x: Int, y: Int, color: Color) {
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

        drawRect(
            color = Color(0xFFF4EBDD),
            size = size
        )

        // بيوت اللاعبين
        drawRect(
            Red.copy(alpha = 0.85f),
            Offset(0f, 0f),
            Size(cell * 6, cell * 6)
        )

        drawRect(
            Color(0xFF20B978).copy(alpha = 0.85f),
            Offset(cell * 8, 0f),
            Size(cell * 6, cell * 6)
        )

        drawRect(
            Color(0xFF398BFA).copy(alpha = 0.85f),
            Offset(0f, cell * 8),
            Size(cell * 6, cell * 6)
        )

        drawRect(
            Color(0xFFFFC928).copy(alpha = 0.85f),
            Offset(cell * 8, cell * 8),
            Size(cell * 6, cell * 6)
        )

        // أماكن القطع في البيوت
        for (player in 0..3) {
            for (slot in homeSlots(player)) {
                val center = Offset(
                    (slot.first + 0.5f) * cell,
                    (slot.second + 0.5f) * cell
                )

                drawCircle(
                    color = Color.White,
                    radius = cell * 0.39f,
                    center = center
                )

                drawCircle(
                    color = playerColors[player],
                    radius = cell * 0.29f,
                    center = center,
                    style = Stroke(width = cell * 0.09f)
                )
            }
        }

        // المسار الخارجي
        track.forEachIndexed { index, point ->
            val color = when (index) {
                0 -> Red
                13 -> Color(0xFF20B978)
                26 -> Color(0xFFFFC928)
                39 -> Color(0xFF398BFA)
                else -> Color.White
            }

            drawCell(point.first, point.second, color)
        }

        // المسارات الداخلية
        for (player in 0..3) {
            homeLane(player).forEach { point ->
                drawCell(
                    point.first,
                    point.second,
                    playerColors[player]
                )
            }
        }

        // مركز اللوحة
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

                val selectable =
                    player == currentPlayer &&
                        piece in legalPieces

                val radius = cell * 0.31f

                if (selectable) {
                    drawCircle(
                        Gold,
                        radius * 1.45f,
                        center
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

                // جسم القطعة
                drawCircle(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.8f),
                            playerColors[player],
                            playerColors[player].copy(alpha = 0.7f)
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

                // لمعة
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

        // الإطار الذهبي
        drawRect(
            color = Gold,
            style = Stroke(width = 5f)
        )
    }
}
