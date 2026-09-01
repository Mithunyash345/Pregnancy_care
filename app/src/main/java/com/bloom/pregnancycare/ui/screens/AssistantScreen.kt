package com.bloom.pregnancycare.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.border
import com.bloom.pregnancycare.ui.components.BoxDefaults
import com.bloom.pregnancycare.ui.components.CustomTopAppBar
import com.bloom.pregnancycare.ui.theme.*
import com.bloom.pregnancycare.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistantScreen(viewModel: MainViewModel) {
    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Scroll to bottom when message size changes
    LaunchedEffect(viewModel.chatMessages.size) {
        if (viewModel.chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(viewModel.chatMessages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Bloom AI Assistant",
                subtitle = "Trained OB/GYN Core",
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo("home") }) {
                        Text("🏠", fontSize = 18.sp)
                    }
                },
                actions = {
                    TextButton(onClick = { 
                        viewModel.chatMessages.clear()
                        viewModel.chatMessages.add(com.bloom.pregnancycare.data.Message(text = "Hello Elena, how can I help you?", sender = "ai", timestamp = "Just Now"))
                    }) {
                        Text("Reset", color = DarkPink, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            )
        },
        containerColor = OffWhite
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Educational Disclaimer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(WarningAmber.copy(alpha = 0.12f))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Text("⚠️", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Disclaimer: Bloom AI assists with educational information. All diagnostic & medical treatment decisions must be decided by your primary OB/GYN doctor.",
                        fontSize = 10.sp,
                        color = Color(0xFFC05621),
                        lineHeight = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Message list
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(viewModel.chatMessages) { message ->
                    val isUser = message.sender == "user"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                    ) {
                        if (!isUser) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(SoftPink.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🤖", fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Column(
                            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 16.dp,
                                            topEnd = 16.dp,
                                            bottomStart = if (isUser) 16.dp else 4.dp,
                                            bottomEnd = if (isUser) 4.dp else 16.dp
                                        )
                                    )
                                    .background(if (isUser) SoftPink else SnowWhite)
                                    .border(
                                        width = if (isUser) 0.dp else 0.8.dp,
                                        color = if (isUser) Color.Transparent else DividerGray,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = message.text,
                                    fontSize = 13.sp,
                                    color = if (isUser) Color(0xFF2C2C2C) else DarkCharcoal,
                                    lineHeight = 18.sp
                                )
                            }
                            Text(
                                text = message.timestamp,
                                fontSize = 9.sp,
                                color = MutedSage,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            // Voice Listening Soundwave
            if (viewModel.isListeningVoice.value) {
                VoiceRecordingWave(onTextCaptured = { capturedText ->
                    textInput = capturedText
                    viewModel.toggleVoiceListening(false)
                })
            }

            // Input panel
            Card(
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                colors = CardDefaults.cardColors(containerColor = SnowWhite),
                border = BoxDefaults.borderStroke(DividerGray),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Suggested Prompts
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("🍵 Chamomile tea?", "⚡ Back pain?", "🥗 Folic foods").forEach { query ->
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(OffWhite)
                                    .border(0.8.dp, DividerGray, CircleShape)
                                    .clickable {
                                        val question = when (query) {
                                            "🍵 Chamomile tea?" -> "Can I drink chamomile tea?"
                                            "⚡ Back pain?" -> "How can I ease lower back pain?"
                                            else -> "What foods contain gestational folic acid?"
                                        }
                                        viewModel.sendChatMessage(question)
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(query, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = DarkCharcoal)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = textInput,
                            onValueChange = { textInput = it },
                            placeholder = { Text("Ask about symptoms, foods...", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(24.dp),
                            trailingIcon = {
                                IconButton(onClick = { viewModel.toggleVoiceListening(true) }) {
                                    Text("🎤", fontSize = 16.sp)
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SoftPink,
                                unfocusedBorderColor = DividerGray
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (textInput.trim().isNotEmpty()) {
                                    viewModel.sendChatMessage(textInput)
                                    textInput = ""
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(SoftPink)
                        ) {
                            Text("➡️", fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VoiceRecordingWave(onTextCaptured: (String) -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val waveHeight by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = 35f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "waveHeight"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = SoftMint),
        border = BoxDefaults.borderStroke(Color(0xFF319795).copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🔴", fontSize = 12.sp, modifier = Modifier.padding(end = 6.dp))
                Text("Listening... Speak now", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2C2C2C))
            }
            
            Row(
                modifier = Modifier.height(35.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                repeat(5) { index ->
                    val factor = when(index) {
                        0 -> 0.4f
                        1 -> 0.9f
                        2 -> 0.6f
                        3 -> 1.1f
                        else -> 0.3f
                    }
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height((waveHeight * factor).dp)
                            .clip(CircleShape)
                            .background(Color(0xFF319795))
                    )
                }
            }
        }
    }

    // Auto capture mock speech after delay
    LaunchedEffect(Unit) {
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            onTextCaptured("How to improve my sleep score during my second trimester?")
        }, 3000)
    }
}
