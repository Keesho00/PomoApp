package com.example.pomo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val Cream = Color(0xFFFFFCF8)
private val Card = Color(0xFFFFFEFC)
private val Pink = Color(0xFFFF6380)
private val LightPink = Color(0xFFFFE7EC)
private val SoftPinkBorder = Color(0xFFFFDCE3)
private val Brown = Color(0xFF431C12)
private val Muted = Color(0xFF665B57)
private val Green = Color(0xFF8D9C55)

private data class Task(
    val title: String,
    val tomatoes: Int,
    val completed: Boolean = false
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PomoTheme {
                PomoApp()
            }
        }
    }
}

@Composable
private fun PomoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = androidx.compose.material3.lightColorScheme(
            primary = Pink,
            background = Cream,
            surface = Card,
            onSurface = Brown
        ),
        typography = androidx.compose.material3.Typography(),
        content = content
    )
}

@Composable
private fun PomoApp() {
    var selectedTab by remember { mutableIntStateOf(0) }
    var secondsLeft by remember { mutableIntStateOf(25 * 60) }
    var running by remember { mutableStateOf(false) }
    var showAddTask by remember { mutableStateOf(false) }

    val tasks = remember {
        mutableStateListOf(
            Task("Math homework", 2),
            Task("Read chapter 5", 2),
            Task("Practice guitar", 2)
        )
    }

    LaunchedEffect(running) {
        while (running && secondsLeft > 0) {
            delay(1000)
            secondsLeft--
        }
        if (secondsLeft == 0) running = false
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF202020))
            .padding(8.dp)
            .clip(RoundedCornerShape(30.dp)),
        color = Cream
    ) {
        Column(Modifier.fillMaxSize()) {
            if (selectedTab == 0) {
                HomeScreen(
                    secondsLeft = secondsLeft,
                    running = running,
                    onToggleTimer = {
                        if (secondsLeft > 0) running = !running
                    },
                    tasks = tasks,
                    onToggleTask = { index ->
                        tasks[index] = tasks[index].copy(completed = !tasks[index].completed)
                    },
                    onAddTask = { showAddTask = true }
                )
            } else {
                PlaceholderTab(
                    title = listOf("Home", "Stats", "Shop", "Profile")[selectedTab],
                    icon = when (selectedTab) {
                        1 -> Icons.Outlined.BarChart
                        2 -> Icons.Outlined.ShoppingBag
                        else -> Icons.Outlined.PersonOutline
                    }
                )
            }

            BottomNavigation(
                selectedTab = selectedTab,
                onSelect = { selectedTab = it }
            )
        }
    }

    if (showAddTask) {
        AddTaskDialog(
            onDismiss = { showAddTask = false },
            onAdd = { title ->
                tasks.add(Task(title, 0))
                showAddTask = false
            }
        )
    }
}

@Composable
private fun HomeScreen(
    secondsLeft: Int,
    running: Boolean,
    onToggleTimer: () -> Unit,
    tasks: List<Task>,
    onToggleTask: (Int) -> Unit,
    onAddTask: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = 22.dp,
            end = 22.dp,
            top = 18.dp,
            bottom = 12.dp
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            TopBar()
        }

        item {
            MascotArea()
        }

        item {
            FocusCard(
                secondsLeft = secondsLeft,
                running = running,
                onToggleTimer = onToggleTimer
            )
        }

        item {
            TodayCard(
                tasks = tasks,
                onToggleTask = onToggleTask,
                onAddTask = onAddTask
            )
        }
    }
}

@Composable
private fun TopBar() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {}) {
            Icon(Icons.Outlined.Menu, contentDescription = "Menu", tint = Brown)
        }

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("◈", color = Green, fontSize = 12.sp)
            Spacer(Modifier.width(6.dp))
            Text(
                "Pomo",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Brown
            )
            Text(":", fontSize = 28.sp, color = Pink, fontWeight = FontWeight.Bold)
        }

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .border(1.dp, SoftPinkBorder, RoundedCornerShape(50))
                .background(Color.White)
                .padding(horizontal = 11.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFA72E))
                    .border(1.dp, Color(0xFFE9791C), CircleShape)
            )
            Spacer(Modifier.width(7.dp))
            Text("2450", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Brown)
        }

        Box {
            IconButton(onClick = {}) {
                Icon(
                    Icons.Outlined.NotificationsNone,
                    contentDescription = "Notifications",
                    tint = Brown
                )
            }
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(Pink)
                    .align(Alignment.TopEnd)
                    .padding(top = 8.dp, end = 8.dp)
            )
        }
    }
}

@Composable
private fun MascotArea() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(122.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Box {
                Surface(
                    color = Pink,
                    shape = RoundedCornerShape(50),
                    shadowElevation = 1.dp
                ) {
                    Text(
                        "Aya",
                        modifier = Modifier.padding(horizontal = 11.dp, vertical = 3.dp),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
            Spacer(Modifier.height(5.dp))
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(
                    topStart = 10.dp,
                    topEnd = 10.dp,
                    bottomStart = 10.dp,
                    bottomEnd = 0.dp
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, SoftPinkBorder)
            ) {
                Text(
                    "Ready to focus?\nLet's get it! ✨",
                    modifier = Modifier.padding(13.dp),
                    color = Brown,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            }
        }

        Mascot(
            modifier = Modifier
                .width(170.dp)
                .height(120.dp)
        )
    }
}


@Composable
private fun Mascot(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.mascot),
        contentDescription = "Aya",
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun FocusCard(
    secondsLeft: Int,
    running: Boolean,
    onToggleTimer: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(22.dp)),
        color = Card,
        shape = RoundedCornerShape(22.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftPinkBorder)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, SoftPinkBorder, CircleShape)
                ) {
                    Icon(Icons.Outlined.Tune, contentDescription = "Settings", tint = Brown)
                }
            }

            Text(
                "Focus Time  ◈",
                color = Brown,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )

            Spacer(Modifier.height(7.dp))

            Text(
                formatTime(secondsLeft),
                fontSize = 58.sp,
                fontWeight = FontWeight.Medium,
                color = Brown,
                letterSpacing = (-2).sp
            )

            Surface(
                color = LightPink,
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    "Session 1/4",
                    modifier = Modifier.padding(horizontal = 17.dp, vertical = 6.dp),
                    color = Brown,
                    fontSize = 14.sp
                )
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onToggleTimer,
                modifier = Modifier
                    .fillMaxWidth(0.62f)
                    .height(48.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Pink,
                    contentColor = Color.White
                )
            ) {
                Text(
                    if (running) "Pause" else "Start",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(5.dp))

            Tomato()
        }
    }
}

private fun formatTime(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

@Composable
private fun Tomato() {
    Box(
        modifier = Modifier.size(48.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.size(43.dp)) {
            drawCircle(Color(0xFFFF5D63), 18f, Offset(size.width / 2, size.height / 2 + 3))
            drawCircle(Color(0xFFFF6C70), 13f, Offset(size.width / 2 - 4, size.height / 2 - 2))
            drawOval(
                color = Color(0xFF5E9A48),
                topLeft = Offset(size.width / 2 - 5, 2f),
                size = Size(12f, 7f)
            )
            drawOval(
                color = Color(0xFF5E9A48),
                topLeft = Offset(size.width / 2 + 2, 4f),
                size = Size(9f, 6f)
            )
            drawCircle(Brown, 2f, Offset(size.width / 2 - 6, size.height / 2 + 3))
            drawCircle(Brown, 2f, Offset(size.width / 2 + 6, size.height / 2 + 3))
        }
    }
}

@Composable
private fun TodayCard(
    tasks: List<Task>,
    onToggleTask: (Int) -> Unit,
    onAddTask: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Card,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftPinkBorder)
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Today",
                    modifier = Modifier.weight(1f),
                    color = Brown,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable(onClick = onAddTask)
                        .padding(horizontal = 9.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = "Add task", tint = Pink, modifier = Modifier.size(16.dp))
                    Text("Add Task", color = Pink, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Text("Delete Task", color = Pink, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(4.dp))

            tasks.forEachIndexed { index, task ->
                TaskRow(
                    task = task,
                    onClick = { onToggleTask(index) }
                )
            }
        }
    }
}

@Composable
private fun TaskRow(task: Task, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .border(1.5.dp, if (task.completed) Pink else Color(0xFFFFB8C4), CircleShape)
                .background(if (task.completed) Pink else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            if (task.completed) {
                Text("✓", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.width(10.dp))

        Text(
            task.title,
            modifier = Modifier.weight(1f),
            color = if (task.completed) Muted else Brown,
            fontSize = 13.sp
        )

        repeat(2) {
            TomatoMini(filled = it < task.tomatoes)
            Spacer(Modifier.width(4.dp))
        }

        repeat(2) {
            Box(
                Modifier
                    .size(17.dp)
                    .clip(CircleShape)
                    .border(1.3.dp, Color(0xFFFFB8C4), CircleShape)
            )
            Spacer(Modifier.width(4.dp))
        }
    }
}

@Composable
private fun TomatoMini(filled: Boolean) {
    Canvas(Modifier.size(18.dp)) {
        drawCircle(
            color = if (filled) Color(0xFFFF5D63) else Color.Transparent,
            radius = 6.5f,
            center = Offset(size.width / 2, size.height / 2 + 1)
        )
        if (filled) {
            drawOval(
                color = Color(0xFF5E9A48),
                topLeft = Offset(size.width / 2 - 3, 2f),
                size = Size(7f, 4f)
            )
        }
    }
}

@Composable
private fun BottomNavigation(
    selectedTab: Int,
    onSelect: (Int) -> Unit
) {
    val labels = listOf("Home", "Stats", "Shop", "Profile")
    val icons = listOf(
        Icons.Rounded.Home,
        Icons.Outlined.BarChart,
        Icons.Outlined.ShoppingBag,
        Icons.Outlined.PersonOutline
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .background(Color.White.copy(alpha = 0.97f))
            .padding(top = 8.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        labels.forEachIndexed { index, label ->
            val active = selectedTab == index
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSelect(index) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    icons[index],
                    contentDescription = label,
                    modifier = Modifier.size(25.dp),
                    tint = if (active) Pink else Brown
                )
                Text(
                    label,
                    color = if (active) Pink else Brown,
                    fontSize = 11.sp,
                    fontWeight = if (active) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun PlaceholderTab(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(
        modifier = Modifier

            .fillMaxWidth()
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = title, modifier = Modifier.size(58.dp), tint = Pink)
        Spacer(Modifier.height(14.dp))
        Text(title, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Brown)
        Spacer(Modifier.height(7.dp))
        Text(
            "This tab is ready for your next screen.",
            textAlign = TextAlign.Center,
            color = Muted
        )
    }
}

@Composable
private fun AddTaskDialog(
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit
) {
    var title by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Task", color = Brown, fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                singleLine = true,
                label = { Text("Task name") }
            )
        },
        confirmButton = {
            TextButton(
                onClick = { if (title.isNotBlank()) onAdd(title.trim()) }
            ) {
                Text("Add", color = Pink, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Muted)
            }
        },
        containerColor = Cream
    )
}
