
package com.dega.ludo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
        setContent {
            DegaLudoGame()
        }
    }
}

@Composable
fun DegaLudoGame() {
    var dice by remember { mutableIntStateOf(1) }
    var players by remember { mutableIntStateOf(4) }
    var message by remember { mutableStateOf("اضغط على النرد لبدء اللعب") }

    val colors = listOf(
        Color(0xFFE53935),
        Color(0xFF43A047),
        Color(0xFFFFC107),
        Color(0xFF1E88E5)
    )

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF101827))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "DEGA LUDO",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFFFD54F)
            )

            Text(
                text = "لعبة اللودو الممتعة",
                fontSize = 18.sp,
                color = Color.White
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1E293B)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "اختر عدد اللاعبين",
                        color = Color.White,
                        fontSize = 20.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        (2..4).forEach { count ->
                            Button(
                                onClick = { players = count },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor =
                                        if (players == count)
                                            Color(0xFFFFC107)
                                        else Color(0xFF475569)
                                )
                            ) {
                                Text(
                                    "$count",
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            Text(
                "لوحة اللعب",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    repeat(2) { index ->
                        PlayerBox(
                            color = colors[index],
                            name = if (index == 0) "الأحمر" else "الأخضر"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.size(130.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "$dice",
                            fontSize = 64.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF101827)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    repeat(2) { index ->
                        PlayerBox(
                            color = colors[index + 2],
                            name = if (index == 0) "الأصفر" else "الأزرق"
                        )
                    }
                }
            }

            Button(
                onClick = {
                    dice = Random.nextInt(1, 7)
                    message = "ظهر الرقم $dice"
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFC107)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    "🎲 ارمي النرد",
                    color = Color.Black,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                message,
                color = Color.White,
                fontSize = 16.sp
            )

            Text(
                "عدد اللاعبين: $players",
                color = Color.LightGray
            )
        }
    }
}

@Composable
fun PlayerBox(color: Color, name: String) {
    Card(
        modifier = Modifier.size(125.dp, 85.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}
