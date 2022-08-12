// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import data.UserInputData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
@Preview
fun App() {
    val defaultPaddingDp = 20.dp;
    var isStateActive by remember { mutableStateOf(StateActive.START) }
    var failReasonState by remember { mutableStateOf("") }

    val title by remember { mutableStateOf("自动抢课插件") }

    var sessionValue by remember { mutableStateOf("lhjjfg7sn65do1msujrj8pg7oimku57g") }
    var classTableId by remember { mutableStateOf("3673161") }
    var startTime by remember { mutableStateOf("17:30") }

    // 按钮是否可以点击
    var startButtonDisable by remember { mutableStateOf(false) }
    startButtonDisable = true

    MaterialTheme {

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {

            Column {

                Column(modifier = Modifier.align(Alignment.CenterHorizontally)) {

                    // title text
                    Text(
                        text = title,
                        modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                            .padding(top = defaultPaddingDp, bottom = defaultPaddingDp),
                        fontSize = 47.sp
                    )

                    Column {

                        // input with session value
                        TextField(
                            value = sessionValue,
                            textStyle = TextStyle(fontSize = 15.sp),
                            label = { Text("session") },
                            placeholder = { Text("在可以抢一小时时间里问我要") },
                            onValueChange = { sessionValue = it },
                            modifier = Modifier.padding(top = defaultPaddingDp)
                        )

                        // input with class table id
                        TextField(
                            value = classTableId,
                            textStyle = TextStyle(fontSize = 15.sp),
                            label = { Text("课程ID") },
                            placeholder = { Text("在可以抢一小时时间里问我要") },
                            onValueChange = { classTableId = it },
                            modifier = Modifier.padding(top = defaultPaddingDp)
                        )

                        // input with class table id
                        TextField(
                            value = startTime,
                            textStyle = TextStyle(fontSize = 15.sp),
                            label = { Text("当天开始抢的小时跟分钟") },
                            placeholder = { Text("eg 17:00 格式") },
                            onValueChange = { startTime = it },
                            modifier = Modifier.padding(top = defaultPaddingDp)
                        )

                        Row(modifier = Modifier.align(alignment = Alignment.End)) {

                            val executorsPool = ExecutorsPool();

                            // end button
                            Button(modifier = Modifier.align(alignment = Alignment.Bottom)
                                .padding(top = defaultPaddingDp, end = defaultPaddingDp),
                                colors = ButtonDefaults.buttonColors(MaterialTheme.colors.surface),
                                onClick = {

                                    // 停止
                                    executorsPool.stop()

                                    // 重置状态
                                    isStateActive = StateActive.START
                                    failReasonState = ""

                                    // 抢课按钮变成可点击
                                    startButtonDisable = true

                                }) { Text("⏹停止") }

                            // start button
                            Button(modifier = Modifier.align(alignment = Alignment.Bottom)
                                .padding(top = defaultPaddingDp),
                                enabled = startButtonDisable,
                                onClick = {

                                    CoroutineScope(Dispatchers.IO).launch {
                                        startButtonDisable = false
                                        isStateActive = StateActive.ING
                                        executorsPool.setState(StateActive.ING)
                                        println("isStateActive: ${isStateActive.name}, state: ${executorsPool.getState()}")

                                        // 启动线程池
                                        executorsPool.start(UserInputData(sessionValue, classTableId, startTime))

                                        do {
                                            isStateActive = executorsPool.getState()
                                            startButtonDisable = true
                                            if (isStateActive == StateActive.FAIL) {
                                                failReasonState = executorsPool.getStateMessage()
                                            }
                                        } while (StateActive.SUCCESS != executorsPool.getState() && StateActive.FAIL != executorsPool.getState())

                                    }

                                }) { Text(if (startButtonDisable) "💪🏻开始抢课" else "🫡正在抢啦") }
                        }
                    }

                }

                // status area
                Column {

                    Divider(modifier = Modifier.padding(top = defaultPaddingDp * 2, bottom = defaultPaddingDp))

                    val defaultStateFontSize = 14.sp
                    val defaultFailReasonStateFontSIze = 11.sp

                    // status
                    Column(
                        modifier = Modifier.align(alignment = Alignment.CenterHorizontally).padding(top = 30.dp)
                            .fillMaxWidth()
                    ) {

                        Text(
                            text = "程序准备",
                            modifier = Modifier.padding(bottom = defaultPaddingDp / 2).fillMaxWidth(),
                            fontSize = defaultStateFontSize,
                            color = if (isStateActive == StateActive.START) {
                                Color.Black
                            } else {
                                Color.Gray
                            },
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "正在抢课",
                            modifier = Modifier.padding(bottom = defaultPaddingDp / 2).fillMaxWidth(),
                            fontSize = defaultStateFontSize,
                            color = if (isStateActive == StateActive.ING) {
                                Color.Black
                            } else {
                                Color.Gray
                            },
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "抢课成功",
                            modifier = Modifier.padding(bottom = defaultPaddingDp / 2).fillMaxWidth(),
                            fontSize = defaultStateFontSize,
                            color = if (isStateActive == StateActive.SUCCESS) {
                                Color.Green
                            } else {
                                Color.Gray
                            },
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "抢课失败",
                            modifier = Modifier.padding(bottom = defaultPaddingDp / 2).fillMaxWidth(),
                            fontSize = defaultStateFontSize,
                            color = if (isStateActive == StateActive.FAIL) {
                                Color.Red
                            } else {
                                Color.Gray
                            },
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = failReasonState,
                            modifier = Modifier.padding(bottom = defaultPaddingDp / 2).fillMaxWidth(),
                            fontSize = defaultFailReasonStateFontSIze,
                            color = if (isStateActive == StateActive.FAIL) {
                                Color.Gray
                            } else {
                                Color.Gray
                            },
                            textAlign = TextAlign.Center
                        )

                    }

                }

            }


        }
    }
}


fun main() = application {
    val defaultWindowWidth = 400.dp
    val defaultWindowHeight = 700.dp;
    Window(
        title = "抢课插件-Bodysoul舞蹈",
        state = WindowState(size = DpSize(defaultWindowWidth, defaultWindowHeight)),
        onCloseRequest = ::exitApplication,
//        icon = BitmapPainter(
//            ImageIO.read(File("/default-icon.icns")).toComposeImageBitmap(),
//            IntOffset(0, 0),
//            IntSize(0, 0)
//        )
    ) {
        App()
    }
}
