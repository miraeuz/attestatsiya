package com.mirae.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mirae.app.AppViewModel
import com.mirae.app.data.*
import com.mirae.app.ui.MiraeCyan
import com.mirae.app.ui.MiraeInk
import com.mirae.app.ui.MiraeLavender
import com.mirae.app.ui.MiraePink
import com.mirae.app.ui.MiraePurple
import com.mirae.app.ui.MiraeViolet

private object Routes {
    const val HOME = "home"
    const val PRACTICE = "practice"
    const val STATS = "stats"
    const val SETTINGS = "settings"
    const val SUBJECT = "subject/{id}"
    const val QUIZ = "quiz"
    const val RESULT = "result"
}

@Composable
fun MiraeApp(vm: AppViewModel, darkTheme: Boolean, onDarkThemeChange: (Boolean) -> Unit) {
    val state by vm.state.collectAsState()
    val nav = rememberNavController()
    val backStackEntry by nav.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    if (state.loading) {
        SplashScreen()
    } else if (state.error != null) {
        ErrorScreen(state.error!!)
    } else {
        Scaffold(
                containerColor = MaterialTheme.colorScheme.background,
                bottomBar = {
                    if (currentRoute in setOf(
                            Routes.HOME, Routes.PRACTICE, Routes.STATS, Routes.SETTINGS
                        )
                    ) {
                        BottomBar(nav)
                    }
                }
            ) { padding ->
                NavHost(
                    navController = nav,
                    startDestination = Routes.HOME,
                    modifier = Modifier.padding(padding)
                ) {
                    composable(Routes.HOME) {
                        HomeScreen(state, onPractice = { nav.navigate(Routes.PRACTICE) },
                            onSubject = { nav.navigate("subject/$it") },
                            onStats = { nav.navigate(Routes.STATS) })
                    }
                    composable(Routes.PRACTICE) {
                        PracticeScreen(
                            state = state,
                            onBack = { nav.popBackStack() },
                            onStart = { subject, topic, difficulty, count ->
                                vm.startQuiz(subject, topic, difficulty, count)
                                nav.navigate(Routes.QUIZ)
                            }
                        )
                    }
                    composable(Routes.STATS) {
                        StatsScreen(state.stats)
                    }
                    composable(Routes.SETTINGS) {
                        SettingsScreen(darkTheme, onDarkThemeChange = onDarkThemeChange)
                    }
                    composable(Routes.SUBJECT) { backStack ->
                        val id = backStack.arguments?.getString("id").orEmpty()
                        SubjectScreen(
                            state = state,
                            subjectId = id,
                            onBack = { nav.popBackStack() },
                            onStart = { topicId ->
                                vm.startQuiz(subjectId = id, topicId = topicId, count = 10)
                                nav.navigate(Routes.QUIZ)
                            }
                        )
                    }
                    composable(Routes.QUIZ) {
                        QuizScreen(
                            state = state,
                            onAnswer = vm::answer,
                            onNext = vm::nextQuestion,
                            onBack = { nav.popBackStack() }
                        )
                        LaunchedEffect(state.quizResult) {
                            if (state.quizResult != null) {
                                nav.navigate(Routes.RESULT) {
                                    popUpTo(Routes.QUIZ) { inclusive = true }
                                }
                            }
                        }
                    }
                    composable(Routes.RESULT) {
                        state.quizResult?.let { result ->
                            ResultScreen(
                                result = result,
                                onAgain = {
                                    vm.startQuiz(count = result.total)
                                    nav.navigate(Routes.QUIZ)
                                },
                                onHome = {
                                    vm.clearResult()
                                    nav.navigate(Routes.HOME) {
                                        popUpTo(Routes.HOME) { inclusive = false }
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SplashScreen() {
    Box(
        modifier = Modifier.fillMaxSize().background(
            Brush.linearGradient(listOf(Color(0xFF150B3D), MiraePurple, Color(0xFF29135D)))
        ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            LogoMark(92.dp)
            Spacer(Modifier.height(18.dp))
            Text("MIRAE", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Black, letterSpacing = 6.sp)
            Text("Attestatsiya platformasi", color = Color.White.copy(alpha = .7f))
        }
    }
}

@Composable
private fun ErrorScreen(message: String) {
    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Outlined.CloudOff, null, modifier = Modifier.size(56.dp), tint = MiraePurple)
            Spacer(Modifier.height(16.dp))
            Text("Mirae ishga tushmadi", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(message, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun BottomBar(nav: NavHostController) {
    val backStackEntry by nav.currentBackStackEntryAsState()
    val current = backStackEntry?.destination?.route
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = .96f)
    ) {
        NavItem(nav, Routes.HOME, "Bosh sahifa", Icons.Outlined.Home, Icons.Filled.Home, current)
        NavItem(nav, Routes.PRACTICE, "Mashq", Icons.Outlined.School, Icons.Filled.School, current)
        NavItem(nav, Routes.STATS, "Natijalar", Icons.Outlined.BarChart, Icons.Filled.BarChart, current)
        NavItem(nav, Routes.SETTINGS, "Sozlamalar", Icons.Outlined.Settings, Icons.Filled.Settings, current)
    }
}

@Composable
private fun NavItem(
    nav: NavHostController,
    route: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    current: String?
) {
    val selected = current == route
    NavigationBarItem(
        selected = selected,
        onClick = {
            nav.navigate(route) {
                launchSingleTop = true
                restoreState = true
                popUpTo(Routes.HOME) { saveState = true }
            }
        },
        icon = { Icon(if (selected) selectedIcon else icon, contentDescription = label) },
        label = { Text(label) }
    )
}

@Composable
private fun HomeScreen(
    state: com.mirae.app.AppState,
    onPractice: () -> Unit,
    onSubject: (String) -> Unit,
    onStats: () -> Unit
) {
    val catalog = state.catalog ?: return
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                LogoMark(46.dp)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("MIRAE", fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                    Text("Attestatsiyaga tayyorlaning", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = onStats) {
                    Icon(Icons.Outlined.NotificationsNone, contentDescription = "Bildirishnomalar")
                }
            }
        }
        item {
            HeroCard(
                title = "Bilimingizni sinab ko‘ring",
                subtitle = "${catalog.questions.size} ta savol · ${catalog.topics.size} ta mavzu",
                button = "Testni boshlash",
                onClick = onPractice
            )
        }
        item {
            Row(verticalAlignment = Alignment.Bottom) {
                Text("Yo‘nalishlar", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text("${catalog.subjects.size} ta fan", color = MiraePurple)
            }
        }
        items(catalog.subjects) { subject ->
            SubjectCard(
                subject = subject,
                questionCount = catalog.questions.count { it.subjectId == subject.id },
                onClick = { onSubject(subject.id) }
            )
        }
        item {
            StatsMiniCard(state.stats, onClick = onStats)
        }
    }
}

@Composable
private fun HeroCard(title: String, subtitle: String, button: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(30.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF4C1EDB), MiraePurple, Color(0xFF9A48FF))))
            .padding(22.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(title, color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(8.dp))
                    Text(subtitle, color = Color.White.copy(alpha = .78f))
                }
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = .12f),
                    modifier = Modifier.size(62.dp)
                ) {
                    Icon(Icons.Outlined.AutoAwesome, null, tint = Color.White, modifier = Modifier.padding(17.dp))
                }
            }
            Spacer(Modifier.height(22.dp))
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = MiraeCyan, contentColor = MiraeInk),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth().height(54.dp)
            ) {
                Icon(Icons.Filled.PlayArrow, null)
                Spacer(Modifier.width(8.dp))
                Text(button, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SubjectCard(subject: Subject, questionCount: Int, onClick: () -> Unit) {
    val icon = when (subject.id) {
        "informatika" -> Icons.Outlined.Memory
        "pedagogika" -> Icons.Outlined.MenuBook
        else -> Icons.Outlined.Work
    }
    val accent = when (subject.id) {
        "informatika" -> MiraePurple
        "pedagogika" -> MiraeCyan
        else -> MiraePink
    }
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(18.dp), color = accent.copy(alpha = .13f), modifier = Modifier.size(56.dp)) {
                Icon(icon, null, tint = accent, modifier = Modifier.padding(15.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(subject.name, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text("$questionCount ta savol", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
            }
            Icon(Icons.Outlined.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun StatsMiniCard(stats: UserStats, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MiraeLavender.copy(alpha = .72f))
    ) {
        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = MiraePurple, modifier = Modifier.size(52.dp)) {
                Icon(Icons.Filled.EmojiEvents, null, tint = Color.White, modifier = Modifier.padding(14.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text("Sizning natijalaringiz", fontWeight = FontWeight.Bold)
                Text("${stats.xp} XP · ${stats.accuracy}% aniqlik", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Outlined.ChevronRight, null)
        }
    }
}

@Composable
private fun SubjectScreen(
    state: com.mirae.app.AppState,
    subjectId: String,
    onBack: () -> Unit,
    onStart: (String?) -> Unit
) {
    val catalog = state.catalog ?: return
    val subject = catalog.subjects.firstOrNull { it.id == subjectId } ?: return
    val topics = catalog.topics.filter { it.subjectId == subjectId }

    Column(Modifier.fillMaxSize()) {
        TopBar(subject.name, onBack)
        LazyColumn(
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                HeroCard(
                    title = "Tezkor test",
                    subtitle = "${catalog.questions.count { it.subjectId == subjectId }} ta savoldan tanlang",
                    button = "Aralash test",
                    onClick = { onStart(null) }
                )
            }
            item {
                Text("Mavzular", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            items(topics) { topic ->
                Card(
                    onClick = { onStart(topic.id) },
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(Modifier.padding(17.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = CircleShape, color = MiraePurple.copy(alpha = .1f), modifier = Modifier.size(44.dp)) {
                            Text(
                                topic.officialSlots?.replace("–", "–") ?: "•",
                                fontSize = 12.sp,
                                color = MiraePurple,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 14.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(topic.name, fontWeight = FontWeight.SemiBold)
                            Text("${topic.questionCount} ta savol", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                        }
                        Icon(Icons.Outlined.PlayCircle, null, tint = MiraePurple)
                    }
                }
            }
        }
    }
}

@Composable
private fun PracticeScreen(
    state: com.mirae.app.AppState,
    onBack: () -> Unit,
    onStart: (String?, String?, String?, Int) -> Unit
) {
    val catalog = state.catalog ?: return
    var subjectId by rememberSaveable { mutableStateOf<String?>(null) }
    var topicId by rememberSaveable { mutableStateOf<String?>(null) }
    var difficulty by rememberSaveable { mutableStateOf<String?>(null) }
    var count by rememberSaveable { mutableIntStateOf(10) }

    val topics = catalog.topics.filter { subjectId == null || it.subjectId == subjectId }

    Column(Modifier.fillMaxSize()) {
        TopBar("Mashqni sozlash", onBack)
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { SectionTitle("Fan") }
            item {
                ChoiceRow(
                    values = listOf("Barchasi" to null) + catalog.subjects.map { it.name to it.id },
                    selected = subjectId,
                    onSelect = { subjectId = it; topicId = null }
                )
            }
            item { SectionTitle("Mavzu") }
            item {
                ChoiceRow(
                    values = listOf("Barchasi" to null) + topics.map { it.name to it.id },
                    selected = topicId,
                    onSelect = { topicId = it }
                )
            }
            item { SectionTitle("Daraja") }
            item {
                ChoiceRow(
                    values = listOf("Barchasi" to null, "Oson" to "easy", "O‘rta" to "medium", "Qiyin" to "hard"),
                    selected = difficulty,
                    onSelect = { difficulty = it }
                )
            }
            item { SectionTitle("Savollar soni") }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf(10, 20, 40).forEach {
                        FilterChip(
                            selected = count == it,
                            onClick = { count = it },
                            label = { Text("$it") }
                        )
                    }
                }
            }
            item {
                Button(
                    onClick = { onStart(subjectId, topicId, difficulty, count) },
                    enabled = catalog.questions.any {
                        (subjectId == null || it.subjectId == subjectId) &&
                        (topicId == null || it.topicId == topicId) &&
                        (difficulty == null || it.difficulty.equals(difficulty, true))
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Icon(Icons.Filled.PlayArrow, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Testni boshlash", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ChoiceRow(
    values: List<Pair<String, String?>>,
    selected: String?,
    onSelect: (String?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        values.forEach { (label, value) ->
            FilterChip(
                selected = selected == value,
                onClick = { onSelect(value) },
                label = { Text(label, maxLines = 2) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun QuizScreen(
    state: com.mirae.app.AppState,
    onAnswer: (Int) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val item = state.currentQuizQuestion
    if (item == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Test topilmadi") }
        return
    }
    val q = item.question
    val progress = (state.quizIndex + 1f) / state.quizQuestions.size
    val selected = item.selectedPosition

    Column(Modifier.fillMaxSize().padding(horizontal = 18.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.Close, "Chiqish") }
            Column(Modifier.weight(1f)) {
                Text("MIRAE TEST", fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                Text("${state.quizIndex + 1} / ${state.quizQuestions.size}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            }
            Surface(shape = RoundedCornerShape(14.dp), color = MiraeLavender) {
                Row(Modifier.padding(horizontal = 11.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Bolt, null, tint = MiraePurple, modifier = Modifier.size(18.dp))
                    Text("${q.xp} XP", color = MiraePurple, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(7.dp).clip(CircleShape)
        )
        Spacer(Modifier.height(20.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Text(
                    q.text,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 31.sp
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    when (q.difficulty) {
                        "easy" -> "Oson"
                        "hard" -> "Qiyin"
                        else -> "O‘rta"
                    },
                    color = MiraePurple,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
            items(q.options) { option ->
                val isSelected = selected == option.position
                val isCorrect = option.isCorrect
                val container = when {
                    !item.answered -> MaterialTheme.colorScheme.surface
                    isCorrect -> MiraeCyan.copy(alpha = .16f)
                    isSelected -> Color(0xFFFFE4EE)
                    else -> MaterialTheme.colorScheme.surface
                }
                Card(
                    onClick = { if (!item.answered) onAnswer(option.position) },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = container),
                    border = if (isSelected || (item.answered && isCorrect)) {
                        androidx.compose.foundation.BorderStroke(1.5.dp, if (isCorrect) MiraeCyan else MiraePink)
                    } else null
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = if (isSelected) MiraePurple else MiraeLavender,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Text(
                                ('A'.code + option.position).toChar().toString(),
                                color = if (isSelected) Color.White else MiraePurple,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 9.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(option.text, modifier = Modifier.weight(1f), fontSize = 15.sp)
                        if (item.answered && isCorrect) {
                            Icon(Icons.Filled.CheckCircle, null, tint = Color(0xFF0AAE98))
                        } else if (item.answered && isSelected) {
                            Icon(Icons.Filled.Cancel, null, tint = MiraePink)
                        }
                    }
                }
            }
            if (item.answered) {
                item {
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = MiraeLavender.copy(alpha = .85f))
                    ) {
                        Column(Modifier.padding(18.dp)) {
                            Text(
                                if (q.options.firstOrNull { it.position == selected }?.isCorrect == true) "To‘g‘ri javob 🎉" else "Javobni tekshiring",
                                fontWeight = FontWeight.ExtraBold,
                                color = MiraePurple
                            )
                            q.solution?.takeIf { it.isNotBlank() }?.let {
                                Spacer(Modifier.height(8.dp))
                                Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 21.sp)
                            }
                        }
                    }
                }
                item {
                    Button(
                        onClick = onNext,
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text(if (state.quizIndex == state.quizQuestions.lastIndex) "Natijani ko‘rish" else "Keyingi savol")
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Filled.ArrowForward, null)
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultScreen(result: QuizResult, onAgain: () -> Unit, onHome: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        Surface(shape = CircleShape, color = MiraePurple, modifier = Modifier.size(86.dp)) {
            Icon(
                if (result.percentage >= 70) Icons.Filled.EmojiEvents else Icons.Filled.Refresh,
                null,
                tint = Color.White,
                modifier = Modifier.padding(22.dp)
            )
        }
        Spacer(Modifier.height(18.dp))
        Text("Test yakunlandi", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
        Text("${result.correct} / ${result.total} to‘g‘ri", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(22.dp))
        Text("${result.percentage}%", fontSize = 58.sp, fontWeight = FontWeight.Black, color = MiraePurple)
        Text("+${result.xp} XP", color = MiraeCyan, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(24.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ResultMetric("Savollar", "${result.total}", Modifier.weight(1f))
            ResultMetric("To‘g‘ri", "${result.correct}", Modifier.weight(1f))
            ResultMetric("Xato", "${result.total - result.correct}", Modifier.weight(1f))
        }
        Spacer(Modifier.weight(1f))
        Button(
            onClick = onAgain,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(18.dp)
        ) {
            Icon(Icons.Filled.Refresh, null)
            Spacer(Modifier.width(8.dp))
            Text("Yana bir test")
        }
        Spacer(Modifier.height(10.dp))
        OutlinedButton(
            onClick = onHome,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text("Bosh sahifaga")
        }
    }
}

@Composable
private fun ResultMetric(label: String, value: String, modifier: Modifier) {
    Surface(modifier = modifier, shape = RoundedCornerShape(18.dp), color = MiraeLavender) {
        Column(Modifier.padding(vertical = 15.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontWeight = FontWeight.ExtraBold, color = MiraePurple)
            Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun StatsScreen(stats: UserStats) {
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Natijalar", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
            Text("Mirae bilan o‘qish progressi", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { stats.accuracy / 100f },
                    strokeWidth = 12.dp,
                    modifier = Modifier.size(160.dp),
                    color = MiraePurple,
                    trackColor = MiraeLavender
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${stats.accuracy}%", fontSize = 34.sp, fontWeight = FontWeight.Black)
                    Text("aniqlik", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatTile("XP", "${stats.xp}", Icons.Outlined.Bolt, Modifier.weight(1f))
                StatTile("Testlar", "${stats.quizzes}", Icons.Outlined.EmojiEvents, Modifier.weight(1f))
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatTile("Javoblar", "${stats.answered}", Icons.Outlined.CheckCircle, Modifier.weight(1f))
                StatTile("Rekord", "${stats.bestScore}%", Icons.Outlined.TrendingUp, Modifier.weight(1f))
            }
        }
        item {
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF21164C))
            ) {
                Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.AutoAwesome, null, tint = MiraeCyan, modifier = Modifier.size(34.dp))
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("Maqsad", color = Color.White, fontWeight = FontWeight.Bold)
                        Text("Har kuni kamida 10 ta savol yeching.", color = Color.White.copy(alpha = .72f))
                    }
                }
            }
        }
    }
}

@Composable
private fun StatTile(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier) {
    Card(modifier, shape = RoundedCornerShape(22.dp)) {
        Column(Modifier.padding(16.dp)) {
            Icon(icon, null, tint = MiraePurple)
            Spacer(Modifier.height(10.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Black)
            Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        }
    }
}

@Composable
private fun SettingsScreen(darkTheme: Boolean, onDarkThemeChange: (Boolean) -> Unit) {
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("Sozlamalar", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(16.dp))
        Card(shape = RoundedCornerShape(24.dp)) {
            Column {
                ListItem(
                    headlineContent = { Text("Tungi rejim", fontWeight = FontWeight.SemiBold) },
                    supportingContent = { Text("Mirae interfeysini qorong‘i ko‘rinishga o‘tkazish") },
                    leadingContent = { Icon(Icons.Outlined.DarkMode, null, tint = MiraePurple) },
                    trailingContent = {
                        Switch(checked = darkTheme, onCheckedChange = onDarkThemeChange)
                    }
                )
                HorizontalDivider()
                ListItem(
                    headlineContent = { Text("Til", fontWeight = FontWeight.SemiBold) },
                    supportingContent = { Text("O‘zbek tili") },
                    leadingContent = { Icon(Icons.Outlined.Language, null, tint = MiraePurple) }
                )
                HorizontalDivider()
                ListItem(
                    headlineContent = { Text("Savollar bazasi", fontWeight = FontWeight.SemiBold) },
                    supportingContent = { Text("Mirae Informatika Attestatsiya") },
                    leadingContent = { Icon(Icons.Outlined.Storage, null, tint = MiraePurple) }
                )
            }
        }
        Spacer(Modifier.height(18.dp))
        Text("Dizayn", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(
            "Mirae birinchi yuborgan UI namunadagi yumaloq kartalar, binafsha gradientlar, neon-cyan CTA va yumshoq soyalarni zamonaviy Material 3 arxitekturasi bilan birlashtiradi.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 21.sp
        )
        Spacer(Modifier.weight(1f))
        Text("MIRAE 1.0.0", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun TopBar(title: String, onBack: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Orqaga") }
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
}

@Composable
private fun LogoMark(size: androidx.compose.ui.unit.Dp) {
    androidx.compose.foundation.Image(
        painter = androidx.compose.ui.res.painterResource(com.mirae.app.R.drawable.mirae_logo),
        contentDescription = "Mirae",
        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(size * .28f))
    )
}
