package su.makskok.magisksu.kernel.ui.menu

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import su.makskok.magisksu.data.AppLogger
import su.makskok.magisksu.kernel.data.MagiskBoot
import su.makskok.magisksu.ui.theme.Green
import su.makskok.magisksu.ui.theme.Surface
import su.makskok.magisksu.ui.theme.Divider
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootSetupScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Состояния
    var selectedFilePath by remember { mutableStateOf("") }
    var statusText by remember { mutableStateOf("Выберите boot.img для патчинга") }
    var isPatching by remember { mutableStateOf(false) }
    var patchDone by remember { mutableStateOf(false) }
    var patchedFilePath by remember { mutableStateOf("") }

    // Выбор файла
    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val cacheDir = context.cacheDir
            val destFile = File(cacheDir, "boot.img")
            try {
                context.contentResolver.openInputStream(it)?.use { input ->
                    FileOutputStream(destFile).use { output ->
                        input.copyTo(output)
                    }
                }
                selectedFilePath = destFile.absolutePath
                statusText = "Выбран: ${destFile.name}"
                patchDone = false
                patchedFilePath = ""
                AppLogger.info("u", "I_3u6", "Выбран boot.img", selectedFilePath)
            } catch (e: Exception) {
                statusText = "Ошибка копирования файла"
                AppLogger.error("r", "E_2r-p1", "Ошибка копирования boot.img", e.message ?: "")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Верхняя панель
        TopAppBar(
            title = {
                Text(
                    "Установка Root",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Назад", tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
        )

        HorizontalDivider(color = Divider)

        // Контент с прокруткой
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Иконка-подсказка
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = null,
                tint = Green.copy(alpha = 0.6f),
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Статус / инструкция
            Text(
                text = statusText,
                color = if (patchDone) Green else Color.White,
                fontSize = 16.sp,
                fontWeight = if (patchDone) FontWeight.Bold else FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Карточка выбора файла
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Divider),
                colors = CardDefaults.cardColors(containerColor = Surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "1. Подготовка boot.img",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Для патчинга нужен оригинальный boot.img вашей прошивки.",
                        color = Color(0xFF888888),
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = { filePicker.launch("application/*") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Green.copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.Default.FileOpen, null, tint = Green)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Выбрать файл", color = Color.White)
                    }

                    if (selectedFilePath.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = selectedFilePath,
                            color = Color(0xFFAAAAAA),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Карточка патчинга
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Divider),
                colors = CardDefaults.cardColors(containerColor = Surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "2. Патчинг",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Magiskboot изменит boot.img для поддержки Magisk.",
                        color = Color(0xFF888888),
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                isPatching = true
                                statusText = "Идёт патчинг..."
                                AppLogger.info("r", "I_3r7", "Патч boot.img запущен")
                                val patched = MagiskBoot.patchBootImage(context, selectedFilePath)
                                isPatching = false
                                if (patched != null) {
                                    patchedFilePath = patched
                                    statusText = "Готово!"
                                    patchDone = true
                                    AppLogger.info("r", "I_3r7", "Патч завершён успешно", patched)
                                } else {
                                    statusText = "Ошибка патча. Проверьте boot.img"
                                    patchDone = false
                                }
                            }
                        },
                        enabled = selectedFilePath.isNotEmpty() && !isPatching && !patchDone,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Green,
                            disabledContainerColor = Green.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (isPatching) {
                            CircularProgressIndicator(
                                color = Color.Black,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Патчинг...", color = Color.Black, fontWeight = FontWeight.Bold)
                        } else if (patchDone) {
                            Icon(Icons.Default.CheckCircle, null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Успешно", color = Color.Black, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.PlayArrow, null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Пропатчить", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (patchDone && patchedFilePath.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Сохранён: $patchedFilePath",
                            color = Color(0xFFAAAAAA),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Инструкция по прошивке (видна только после успеха)
            if (patchDone) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Divider),
                    colors = CardDefaults.cardColors(containerColor = Surface)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "3. Прошивка",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Скопируйте пропатченный образ на компьютер и выполните в fastboot:",
                            color = Color(0xFF888888),
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "fastboot flash boot new-boot.img",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}