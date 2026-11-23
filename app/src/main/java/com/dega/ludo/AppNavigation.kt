
package com.dega.ludo

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object DegaGameConfig {
    var playerCount = 4
}

private val pageNavy = Color(0xFF101B35)
private val pageGold = Color(0xFFFFD36A)
private val palette = listOf(
    Color(0xFFE84152),
    Color(0xFF20B978),
    Color(0xFFFFC928),
    Color(0xFF398BFA),
    Color(0xFF9B59B6),
    Color(0xFFFF8A32)
)

private enum class DegaPage {
    HOME, MODE, SETUP, GAME, SHOP, TOURNAMENTS, PROFILE, SETTINGS
}

@Composable
fun DegaApp() {
    var page by remember { mutableStateOf(DegaPage.HOME) }
    var mode by remember { mutableStateOf("محلي على نفس الهاتف") }
    var count by remember { mutableIntStateOf(4) }
    var names by remember {
        mutableStateOf(listOf("الأحمر", "الأخضر", "الأصفر", "الأزرق"))
    }
    var selectedColors by remember { mutableStateOf(palette.take(4)) }
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

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = pageNavy
        ) {
            when (page) {
                DegaPage.HOME -> HomePage(
                    coins, gems,
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
                    colors = selectedColors,
                    onBack = { page = DegaPage.MODE },
                    onCount = {
                        count = it
                        names = List(it) { index ->
                            names.getOrElse(index) { "اللاعب ${index + 1}" }
                        }
                        selectedColors = List(it) { index ->
                            selectedColors.getOrElse(index) { palette[index] }
                        }
                    },
                    onName = { index, value ->
                        names = names.toMutableList().also {
                            it[index] = value
                        }
                    },
                    onColor = { index, color ->
                        selectedColors = selectedColors.toMutableList().also {
                            it[index] = color
                        }
                    },
                    onStart = {
                        DegaGameConfig.playerCount = count
                        playerNames = names
                        playerColors = selectedColors
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
                            containerColor = pageGold,
                            contentColor = pageNavy
                        )
                    ) {
                        Text("الرئيسية")
                    }
                }

                DegaPage.SHOP -> ShopPage(
                    coins, gems,
                    onBack = { page = DegaPage.HOME },
                    onBuyCoins = {
                        notice = "شراء العملات الحقيقي سيُفعّل لاحقًا"
                    },
                    onBuyBackground = {
                        if (coins >= 500) {
                            coins -= 500
                            notice = "تم فتح الخلفية التجريبية!"
                        } else notice = "رصيد العملات غير كافٍ"
                    },
                    notice = notice
                )

                DegaPage.TOURNAMENTS -> SimplePage(
                    "المسابقات والبطولات",
                    "بطولة يومية • قريبًا\n\n"
                            + "بطولة الأصدقاء • قريبًا\n\n"
                            + "لوحة المتصدرين • قريبًا\n\n"
                            + "جوائز العملات والجواهر • قريبًا",
                    { page = DegaPage.HOME }
                )

                DegaPage.PROFILE -> SimplePage(
                    "الملف الشخصي",
                    "ملف لاعب DEGA LUDO\n\n"
                            + "المستوى: 1\n"
                            + "الانتصارات: 0\n"
                            + "المباريات: 0\n\n"
                            + "سنضيف الصورة والإحصائيات لاحقًا.",
                    { page = DegaPage.HOME }
                )

                DegaPage.SETTINGS -> SimplePage(
                    "الإعدادات",
                    "الصوت والموسيقى\n\n"
                            + "اللغة\n\n"
                            + "إشعارات اللعبة\n\n"
                            + "المساعدة والدعم",
                    { page = DegaPage.HOME }
                )
            }
        }
    }
}

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
                color = pageGold,
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
                BalanceCard("🪙 $coins", "العملات")
                BalanceCard("💎 $gems", "الجواهر")
            }
            Spacer(Modifier.height(28.dp))
            MenuButton("▶  العب الآن", onPlay, pageGold)
            MenuButton("🏆  المسابقات والبطولات", onTournaments)
            MenuButton("🛒  متجر DEGA", onShop)
            MenuButton("👤  الملف الشخصي", onProfile)
            MenuButton("⚙  الإعدادات", onSettings)
            Spacer(Modifier.height(22.dp))
            Panel {
                Text("آخر الأخبار", color = pageGold, fontSize = 19.sp,
                    fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(
                    "مرحبًا بك في DEGA LUDO!\n"
                            + "استعد للمنافسة والفوز بالمكافآت.\n"
                            + "ترقبوا التحديثات والبطولات القادمة.",
                    color = Color.White,
                    lineHeight = 24.sp
                )
            }
            Spacer(Modifier.height(20.dp))
            Text("DEGA LUDO • LET'S PLAY",
                color = Color.Gray, fontSize = 11.sp)
        }
    }
}

@Composable
private fun BalanceCard(value: String, label: String) {
    Panel(Modifier.weight(1f)) {
        Text(value, color = pageGold, fontSize = 20.sp,
            fontWeight = FontWeight.Bold)
        Text(label, color = Color.White, fontSize = 12.sp)
    }
}

@Composable
private fun ModePage(
    onBack: () -> Unit,
    onSelect: (String) -> Unit
) {
    Column(
        Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PageHeader("اختر طريقة اللعب", onBack)
        Spacer(Modifier.height(25.dp))
        MenuButton("🤖  اللعب منفردًا ضد الكمبيوتر", {
            onSelect("ضد الكمبيوتر")
        }, pageGold)
        MenuButton("🌐  اللعب أونلاين مع الأصدقاء", {
            onSelect("أونلاين مع الأصدقاء")
        })
        MenuButton("📱  اللعب على نفس الهاتف", {
            onSelect("محلي على نفس الهاتف")
        })
        Spacer(Modifier.height(18.dp))
        Panel {
            Text(
                "ملاحظة: اللعب المحلي هو المتاح حاليًا. "
                        + "الكمبيوتر والأونلاين يحتاجان استكمال برمجتهما.",
                color = Color.White,
                lineHeight = 23.sp
            )
        }
    }
}

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
        Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PageHeader("إعداد اللاعبين", onBack)
        Text("طريقة اللعب: $mode", color = pageGold)
        Spacer(Modifier.height(12.dp))
        Text("عدد اللاعبين", color = Color.White)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(2, 3, 4).forEach {
                Button(
                    onClick = { onCount(it) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (count == it) pageGold else Color(0xFF34435C),
                        contentColor = if (count == it) pageNavy else Color.White
                    )
                ) { Text("$it") }
            }
        }
        Spacer(Modifier.height(10.dp))
        LazyColumn(Modifier.weight(1f)) {
            items(count) { index ->
                Panel(Modifier.fillMaxWidth().padding(vertical = 5.dp)) {
                    Text(
                        "اللاعب ${index + 1}",
                        color = colors[index],
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = names[index],
                        onValueChange = { onName(index, it) },
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
                                    .background(color, RoundedCornerShape(50))
                                    .then(
                                        if (colors[index] == color)
                                            Modifier.border(3.dp, Color.White, RoundedCornerShape(50))
                                        else Modifier
                                    )
                                    .clickable { onColor(index, color) }
                            )
                        }
                    }
                }
            }
        }
        Button(
            onClick = onStart,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = pageGold,
                contentColor = pageNavy
            )
        ) {
            Text("ابدأ اللعب", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ShopPage(
    coins: Int,
    gems: Int,
    onBack: () -> Unit,
    onBuyCoins: () -> Unit,
    onBuyBackground: () -> Unit,
    notice: String
) {
    Column(Modifier.fillMaxSize().padding(18.dp)) {
        PageHeader("متجر DEGA", onBack)
        Text("🪙 $coins     💎 $gems", color = pageGold, fontSize = 20.sp)
        Spacer(Modifier.height(18.dp))
        Panel(Modifier.fillMaxWidth()) {
            Text("باقات العملات", color = pageGold, fontSize = 19.sp)
            Text("باقات عملات متنوعة ستتوفر عبر الشراء.", color = Color.White)
            Button(onClick = onBuyCoins) { Text("تفاصيل الباقات") }
        }
        Spacer(Modifier.height(12.dp))
        Panel(Modifier.fillMaxWidth()) {
            Text("خلفيات الطاولات", color = pageGold, fontSize = 19.sp)
            Text("خلفية ملكية تجريبية • 500 عملة", color = Color.White)
            Button(onClick = onBuyBackground) { Text("فتح الخلفية") }
        }
        Spacer(Modifier.height(12.dp))
        Panel(Modifier.fillMaxWidth()) {
            Text("النرد والقطع", color = pageGold, fontSize = 19.sp)
            Text("أشكال جديدة قيد التجهيز", color = Color.White)
        }
        if (notice.isNotBlank()) {
            Spacer(Modifier.height(12.dp))
            Text(notice, color = pageGold)
        }
    }
}

@Composable
private fun SimplePage(
    title: String,
    content: String,
    onBack: () -> Unit
) {
    Column(
        Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PageHeader(title, onBack)
        Spacer(Modifier.height(25.dp))
        Panel(Modifier.fillMaxWidth()) {
            Text(content, color = Color.White, lineHeight = 30.sp)
        }
    }
}

@Composable
private fun PageHeader(title: String, onBack: () -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onBack) {
            Text("رجوع", color = pageGold)
        }
        Spacer(Modifier.weight(1f))
        Text(title, color = pageGold, fontSize = 20.sp,
            fontWeight = FontWeight.Bold)
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
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 6.dp)
            .height(58.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = if (color == pageGold) pageNavy else Color.White
        )
    ) {
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun Panel(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier
            .background(Color(0xFF202F4D), RoundedCornerShape(16.dp))
            .padding(15.dp),
        content = content
    )
}
