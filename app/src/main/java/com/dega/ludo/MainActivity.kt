
package com.dega.ludo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

private val Navy = Color(0xFF10182B)
private val DeepNavy = Color(0xFF080F1D)
private val Gold = Color(0xFFFFC857)
private val RedPlayer = Color(0xFFE84A5F)
private val GreenPlayer = Color(0xFF25B879)
private val YellowPlayer = Color(0xFFFFC344)
private val BluePlayer = Color(0xFF4389ED)

private data class Player(
    val name: String,
    val color: Color
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DegaApp()
        }
    }
}

@Composable
fun DegaApp() {
    val players = listOf(
        Player("الأحمر", RedPlayer),
        Player("الأخضر", GreenPlayer),
        Player("الأصفر", YellowPlayer),
        Player("الأزرق", BluePlayer)
    )

    var currentPlayer by remember { mutableIntStateOf(0) }
    var diceNumber by remember { mutableIntStateOf(1) }
    var rotation by remember { mutableFloatStateOf(0f) }
    var rolling by remember { mutableStateOf(false) }
    var page by remember { mutableStateOf("home") }
    var coins by remember { mutableIntStateOf(2500) }
    var gems by remember { mutableIntStateOf(50) }

    val diceRotation by animateFloatAsState(
        targetValue = rotation,
        animationSpec = tween(durationMillis = 700),
        label = "diceRotation"
    )

    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Navy, DeepNavy)
                    )
                )
        ) {
            when (page) {
                "game" -> GameScreen(
                    players = players,
                    currentPlayer = currentPlayer,
                    diceNumber = diceNumber,
                    diceRotation = diceRotation,
                    rolling = rolling,
                    onRoll = {
                        if (!rolling) {
                            rolling = true
                            diceNumber = Random.nextInt(1, 7)
                            rotation += 720f
                            rolling = false
                            if (diceNumber != 6) {
                                currentPlayer =
                                    (currentPlayer + 1) % players.size
                            }
                        }
                    },
                    onBack = { page = "home" }
                )

                "store" -> SimplePage(
                    title = "متجر DEGA",
                    description = "العملات الذهبية والجواهر والخلفيات",
                    onBack = { page = "home" }
                )

                "tournaments" -> SimplePage(
                    title = "البطولات",
                    description = "استعد للمنافسة والفوز بالجوائز",
                    onBack = { page = "home" }
                )

                "profile" -> SimplePage(
                    title = "الملف الشخصي",
                    description = "ملف اللاعب والإحصائيات",
                    onBack = { page = "home" }
                )

                else -> HomeScreen(
                    coins = coins,
                    gems = gems,
                    onPlay = { page = "game" },
                    onStore = { page = "store" },
                    onTournaments = { page = "tournaments" },
                    onProfile = { page = "profile" }
                )
            }
        }
    }
}

@Composable
fun HomeScreen(
    coins: Int,
    gems: Int,
    onPlay: () -> Unit,
    onStore: () -> Unit,
    onTournaments: () -> Unit,
    onProfile: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "DEGA",
                    color = Gold,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    "LUDO",
                    color = Color.White,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text("🪙 $coins", color = Gold, fontWeight = FontWeight.Bold)
                Text("💎 $gems", color = Color.Cyan)
            }
        }

        Spacer(Modifier.height(35.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFF263B60),
                            Color(0xFF111C34),
                            Color(0xFF392B48)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🎲", fontSize = 65.sp)
                Text(
                    "DEGA LUDO",
                    color = Gold,
                    fontSize = 29.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    "استمتع بالتحدي",
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        Spacer(Modifier.height(28.dp))

        Button(
            onClick = onPlay,
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Gold,
                contentColor = Navy
            )
        ) {
            Text(
                "العب الآن  🎮",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black
            )
        }

        Spacer(Modifier.height(22.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            HomeTile("🛒", "المتجر", Modifier.weight(1f), onStore)
            HomeTile("🏆", "البطولات", Modifier.weight(1f), onTournaments)
        }

        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            HomeTile("👤", "حسابي", Modifier.weight(1f), onProfile)
            HomeTile("🎁", "المكافآت", Modifier.weight(1f)) {}
        }

        Spacer(Modifier.weight(1f))

        Text(
            "DEGA LUDO • PLAY TO WIN",
            color = Color.Gray,
            fontSize = 11.sp
        )
    }
}

@Composable
fun HomeTile(
    emoji: String,
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .height(105.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF1A2942))
            .clickable { onClick() }
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(emoji, fontSize = 29.sp)
        Spacer(Modifier.height(5.dp))
        Text(title, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun GameScreen(
    players: List<Player>,
    currentPlayer: Int,
    diceNumber: Int,
    diceRotation: Float,
    rolling: Boolean,
    onRoll: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "‹",
                color = Color.White,
                fontSize = 36.sp,
                modifier = Modifier.clickable { onBack() }
            )
            Spacer(Modifier.width(12.dp))
            Text(
                "DEGA LUDO",
                color = Gold,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(Modifier.weight(1f))
            Text("⚙", color = Color.White, fontSize = 25.sp)
        }

        Spacer(Modifier.height(12.dp))

        PlayerBar(
            player = players[currentPlayer],
            diceNumber = diceNumber,
            diceRotation = diceRotation,
            rolling = rolling,
            onRoll = onRoll
        )

        Spacer(Modifier.height(12.dp))

        LudoBoard(
            players = players,
            currentPlayer = currentPlayer,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Text(
            "دور اللاعب ${players[currentPlayer].name}",
            color = players[currentPlayer].color,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            players.forEachIndexed { index, player ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(35.dp)
                            .clip(CircleShape)
                            .background(player.color),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "${index + 1}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        player.name,
                        color = if (index == currentPlayer) Gold else Color.LightGray,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(Modifier.weight(1f))

        Text(
            "اضغط على النرد لرميه",
            color = Color.LightGray,
            fontSize = 13.sp
        )
    }
}

@Composable
fun PlayerBar(
    player: Player,
    diceNumber: Int,
    diceRotation: Float,
    rolling: Boolean,
    onRoll: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF1A2942))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(player.color),
            contentAlignment = Alignment.Center
        ) {
            Text("♟", color = Color.White, fontSize = 23.sp)
        }

        Spacer(Modifier.width(10.dp))

        Column {
            Text(
                player.name,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                "دورك الآن",
                color = player.color,
                fontSize = 12.sp
            )
        }

        Spacer(Modifier.weight(1f))

        Box(
            modifier = Modifier
                .size(58.dp)
                .graphicsLayer {
                    rotationX = diceRotation
                    rotationY = diceRotation * 0.7f
                    rotationZ = diceRotation * 0.2f
                    cameraDistance = 12f * density
                }
                .clip(RoundedCornerShape(13.dp))
                .background(
                    Brush.linearGradient(
                        listOf(Color.White, Color(0xFFD7DFEA))
                    )
                )
                .clickable(enabled = !rolling) { onRoll() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                diceFace(diceNumber),
                fontSize = 35.sp,
                color = Color(0xFF17243A)
            )
        }
    }
}

fun diceFace(number: Int): String {
    return when (number) {
        1 -> "⚀"
        2 -> "⚁"
        3 -> "⚂"
        4 -> "⚃"
        5 -> "⚄"
        else -> "⚅"
    }
}

@Composable
fun LudoBoard(
    players: List<Player>,
    currentPlayer: Int,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF26354B))
    ) {
        val cell = size.width / 15f
        val boardSize = size.width

        fun cellRect(
            col: Int,
            row: Int,
            color: Color
        ) {
            drawRoundRect(
                color = color,
                topLeft = Offset(col * cell, row * cell),
                size = androidx.compose.ui.geometry.Size(cell, cell),
                cornerRadius = CornerRadius(cell * 0.08f)
            )
        }

        // القاعدة الخارجية
        drawRoundRect(
            color = Color(0xFF17243A),
            size = size,
            cornerRadius = CornerRadius(24f)
        )

        // بيوت اللاعبين الأربعة
        val homes = listOf(
            Triple(0, 0, RedPlayer),
            Triple(9, 0, GreenPlayer),
            Triple(0, 9, BluePlayer),
            Triple(9, 9, YellowPlayer)
        )

        homes.forEach { (x, y, color) ->
            drawRoundRect(
                color = color,
                topLeft = Offset(x * cell, y * cell),
                size = androidx.compose.ui.geometry.Size(
                    6 * cell,
                    6 * cell
                ),
                cornerRadius = CornerRadius(12f)
            )

            drawRoundRect(
                color = Color.White.copy(alpha = 0.92f),
                topLeft = Offset(
                    (x + 1) * cell,
                    (y + 1) * cell
                ),
                size = androidx.compose.ui.geometry.Size(
                    4 * cell,
                    4 * cell
                ),
                cornerRadius = CornerRadius(12f)
            )

            val tokenPositions = listOf(
                Offset((x + 2) * cell, (y + 2) * cell),
                Offset((x + 4) * cell, (y + 2) * cell),
                Offset((x + 2) * cell, (y + 4) * cell),
                Offset((x + 4) * cell, (y + 4) * cell)
            )

            tokenPositions.forEach { pos ->
                drawCircle(
                    color = color.copy(alpha = 0.25f),
                    radius = cell * 0.43f,
                    center = Offset(
                        pos.x + cell / 2,
                        pos.y + cell / 2 + 3f
                    )
                )
                drawCircle(
                    color = color,
                    radius = cell * 0.34f,
                    center = Offset(
                        pos.x + cell / 2,
                        pos.y + cell / 2
                    )
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.55f),
                    radius = cell * 0.11f,
                    center = Offset(
                        pos.x + cell * 0.39f,
                        pos.y + cell * 0.38f
                    )
                )
            }
        }

        // مسارات اللعب
        for (row in 0 until 15) {
            for (col in 0 until 15) {
                val onTrack =
                    (row in 6..8 || col in 6..8) &&
                    !(row in 0..5 && col in 0..5) &&
                    !(row in 0..5 && col in 9..14) &&
                    !(row in 9..14 && col in 0..5) &&
                    !(row in 9..14 && col in 9..14)

                if (onTrack) {
                    cellRect(
                        col,
                        row,
                        Color(0xFFF5F2EA)
                    )
                    drawRoundRect(
                        color = Color(0xFFD2D6DF),
                        topLeft = Offset(col * cell, row * cell),
                        size = androidx.compose.ui.geometry.Size(
                            cell,
                            cell
                        ),
                        cornerRadius = CornerRadius(cell * 0.08f),
                        style = Stroke(width = 1.2f)
                    )
                }
            }
        }

        // المسارات النهائية الملونة
        for (i in 0..5) {
            cellRect(7, i + 1, RedPlayer)
            cellRect(7, 13 - i, YellowPlayer)
            cellRect(i + 1, 7, BluePlayer)
            cellRect(13 - i, 7, GreenPlayer)
        }

        // مركز الطاولة
        val center = boardSize / 2f
        val path = Path().apply {
            moveTo(center, center)
            lineTo(6 * cell, 6 * cell)
            lineTo(9 * cell, 6 * cell)
            close()
        }
        drawPath(path, GreenPlayer)

        val path2 = Path().apply {
            moveTo(center, center)
            lineTo(9 * cell, 6 * cell)
            lineTo(9 * cell, 9 * cell)
            close()
        }
        drawPath(path2, YellowPlayer)

        val path3 = Path().apply {
            moveTo(center, center)
            lineTo(9 * cell, 9 * cell)
            lineTo(6 * cell, 9 * cell)
            close()
        }
        drawPath(path3, BluePlayer)

        val path4 = Path().apply {
            moveTo(center, center)
            lineTo(6 * cell, 9 * cell)
            lineTo(6 * cell, 6 * cell)
            close()
        }
        drawPath(path4, RedPlayer)

        drawCircle(
            color = Gold,
            radius = cell * 0.38f,
            center = Offset(center, center)
        )
        drawCircle(
            color = Color.White,
            radius = cell * 0.16f,
            center = Offset(center, center)
        )

        // حدود خارجية
        drawRoundRect(
            color = Gold.copy(alpha = 0.8f),
            size = size,
            cornerRadius = CornerRadius(24f),
            style = Stroke(width = 4f)
        )
    }
}

@Composable
fun SimplePage(
    title: String,
    description: String,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "‹",
                color = Color.White,
                fontSize = 35.sp,
                modifier = Modifier.clickable { onBack() }
            )
            Spacer(Modifier.width(12.dp))
            Text(
                title,
                color = Gold,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(80.dp))

        Text("🎮", fontSize = 65.sp)

        Spacer(Modifier.height(20.dp))

        Text(
            title,
            color = Color.White,
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        Text(
            description,
            color = Color.LightGray,
            fontSize = 15.sp
        )

        Spacer(Modifier.height(30.dp))

        Text(
            "سيتم تطوير هذه الصفحة في المرحلة القادمة",
            color = Gold,
            fontSize = 13.sp
        )
    }
}
