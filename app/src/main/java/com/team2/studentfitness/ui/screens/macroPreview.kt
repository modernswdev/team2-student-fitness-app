package com.team2.studentfitness.ui.macro

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team2.studentfitness.ui.theme.CardBg
import com.team2.studentfitness.ui.theme.NeonOrange

@Composable
fun MacroPreviewCard(
    uiState: MacroCardUiState,
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit = {},
    onViewDetailsClick: () -> Unit = {},
    onChangeDisplayClick: () -> Unit = {},
    onChooseVisibleMacrosClick: () -> Unit = {},
    onChangeTargetsClick: () -> Unit = {},
    onRefreshClick: () -> Unit = {}
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Macro Preview",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "A quick look at your daily nutrition targets.",
                        fontSize = 13.sp,
                        color = Color.Black.copy(alpha = 0.68f)
                    )
                }

                Surface(
                    color = NeonOrange,
                    shape = RoundedCornerShape(999.dp)
                ) {
                    Text(
                        text = "Today",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Daily Calories",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black.copy(alpha = 0.62f)
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = uiState.calorieGoal?.let { "$it kcal" } ?: "--",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Today's Intake",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )

                            Spacer(Modifier.width(10.dp))

                            IconButton(
                                onClick = onAddClick,
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(Color(0xFFF6F8FA), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add intake",
                                    tint = Color(0xFF7D8B97),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Box {
                            IconButton(onClick = { menuExpanded = true }) {
                                Icon(
                                    imageVector = Icons.Default.MoreHoriz,
                                    contentDescription = "Macro options",
                                    tint = Color(0xFF8EA0AD)
                                )
                            }

                            DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Change Display") },
                                    onClick = {
                                        menuExpanded = false
                                        onChangeDisplayClick()
                                    }
                                )

                                DropdownMenuItem(
                                    text = { Text("Choose Visible Macros") },
                                    onClick = {
                                        menuExpanded = false
                                        onChooseVisibleMacrosClick()
                                    }
                                )

                                DropdownMenuItem(
                                    text = { Text("Change Targets") },
                                    onClick = {
                                        menuExpanded = false
                                        onChangeTargetsClick()
                                    }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    val visibleItems = uiState.trackedMacros.filter {
                        uiState.settings.visibleMacros.contains(it.type)
                    }

                    if (visibleItems.isEmpty()) {
                        Text(
                            text = "No macro data yet",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            visibleItems.forEach { macro ->
                                MacroProgressItem(
                                    macro = macro,
                                    style = uiState.settings.displayStyle
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            Text(
                text = "Based on your profile, activity level, and workout goal. These values may update when your settings change.",
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = Color.Black.copy(alpha = 0.72f)
            )

            Spacer(Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onViewDetailsClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonOrange)
                ) {
                    Text("View Details", color = Color.Black, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onRefreshClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Refresh")
                }
            }
        }
    }
}

@Composable
private fun MacroProgressItem(
    macro: MacroProgress,
    style: MacroDisplayStyle
) {
    val current = macro.current
    val target = macro.target

    if (current == null || target == null || target <= 0) {
        MacroEmptyItem(type = macro.type)
        return
    }

    val progress = (current.toFloat() / target.toFloat()).coerceIn(0f, 1f)

    when (style) {
        MacroDisplayStyle.BAR -> MacroBarItem(macro, progress)
        MacroDisplayStyle.CIRCLE -> MacroCircleItem(macro, progress)
        MacroDisplayStyle.VALUE_ONLY -> MacroValueOnlyItem(macro)
    }
}

@Composable
private fun MacroEmptyItem(
    type: MacroType
) {
    Column {
        Text(
            text = type.label.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF7A8791)
        )

        Spacer(Modifier.height(6.dp))

        HorizontalDivider(
            thickness = 8.dp,
            color = Color(0xFFD6D6D6)
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "-- / --",
            fontSize = 12.sp,
            color = Color(0xFF5C646B),
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun MacroBarItem(
    macro: MacroProgress,
    progress: Float
) {
    val current = macro.current ?: return
    val target = macro.target ?: return

    Column {
        Text(
            text = macro.type.label.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF7A8791)
        )

        Spacer(Modifier.height(6.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = macroColor(macro.type),
            trackColor = Color(0xFFD6D6D6)
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "${formatMacroValue(current, macro.type)} / ${formatMacroValue(target, macro.type)}",
            fontSize = 12.sp,
            color = Color(0xFF5C646B),
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun MacroCircleItem(
    macro: MacroProgress,
    progress: Float
) {
    val current = macro.current ?: return
    val target = macro.target ?: return

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = macro.type.label.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF7A8791)
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "${formatMacroValue(current, macro.type)} / ${formatMacroValue(target, macro.type)}",
                fontSize = 12.sp,
                color = Color(0xFF5C646B),
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(Modifier.width(12.dp))

        CircleProgress(
            progress = progress,
            color = macroColor(macro.type)
        )
    }
}

@Composable
private fun MacroValueOnlyItem(
    macro: MacroProgress
) {
    val current = macro.current
    val target = macro.target

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFF7F7F7)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = macro.type.label.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF7A8791)
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = if (current != null && target != null) {
                    "${formatMacroValue(current, macro.type)} / ${formatMacroValue(target, macro.type)}"
                } else {
                    "-- / --"
                },
                fontSize = 12.sp,
                color = Color(0xFF5C646B),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun CircleProgress(
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(54.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = 7.dp.toPx()

            drawArc(
                color = Color(0xFFD6D6D6),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
                size = Size(size.width, size.height),
                topLeft = Offset.Zero
            )

            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
                size = Size(size.width, size.height),
                topLeft = Offset.Zero
            )
        }

        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = Color.White
        ) {}

        Text(
            text = "${(progress * 100).toInt()}%",
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black
        )
    }
}

private fun macroColor(type: MacroType): Color {
    return when (type) {
        MacroType.PROTEIN -> Color(0xFFFF6B6B)
        MacroType.CARBS -> Color(0xFF42C9CF)
        MacroType.FAT -> Color(0xFFB3C833)
        MacroType.CALORIES -> Color(0xFF8F8F8F)
        MacroType.FIBER -> Color(0xFF8C6DFD)
        MacroType.SUGAR -> Color(0xFFFF8FB1)
        MacroType.SODIUM -> Color(0xFF7E8790)
    }
}

private fun formatMacroValue(value: Int, type: MacroType): String {
    return when (type) {
        MacroType.CALORIES -> "$value kcal"
        MacroType.SODIUM -> "$value mg"
        else -> "$value g"
    }
}
