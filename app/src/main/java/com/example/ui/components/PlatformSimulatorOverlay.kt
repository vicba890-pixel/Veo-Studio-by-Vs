package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Project

@Composable
fun PlatformSimulatorOverlay(
    activeSimulator: String?,
    project: Project,
    modifier: Modifier = Modifier
) {
    if (activeSimulator == null || activeSimulator == "none") return

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
    ) {
        when (activeSimulator) {
            "tiktok" -> TikTokSimulator(project)
            "instagram" -> InstagramReelsSimulator(project)
            "youtube" -> YouTubeShortsSimulator(project)
            "linkedin" -> LinkedInSimulator(project)
        }

        // Platform Safe Zone outline indicator pill at top
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 8.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black.copy(alpha = 0.65f))
                .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                .padding(horizontal = 10.dp, vertical = 3.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981))
                )
                Text(
                    text = "SIMULADOR NATIVO: ${activeSimulator.uppercase()}",
                    color = Color.White,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun TikTokSimulator(project: Project) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Gradient overlay for bottom readability
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                    )
                )
        )

        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Siguiendo", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.width(16.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Para ti", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(2.dp)
                        .background(Color.White)
                )
            }
        }

        // Right side buttons
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 8.dp, bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Profile avatar
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, Color.White, CircleShape)
                    .background(Color(0xFFFF0050)),
                contentAlignment = Alignment.Center
            ) {
                Text("V", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            // Like
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.Favorite, "Like", tint = Color.White, modifier = Modifier.size(24.dp))
                Text("142.8K", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }

            // Comment
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.Comment, "Comment", tint = Color.White, modifier = Modifier.size(22.dp))
                Text("2.4K", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }

            // Bookmark
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.Bookmark, "Save", tint = Color.White, modifier = Modifier.size(22.dp))
                Text("18.1K", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }

            // Share
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.Share, "Share", tint = Color.White, modifier = Modifier.size(22.dp))
                Text("6.5K", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }

            // Vinyl sound disc
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF222222))
                    .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.MusicNote, "Audio", tint = Color.White, modifier = Modifier.size(14.dp))
            }
        }

        // Bottom title & caption
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.78f)
                .padding(start = 12.dp, bottom = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("@veo_studio_creator", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.CheckCircle, "Verified", tint = Color(0xFF20D5EC), modifier = Modifier.size(12.dp))
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                project.title,
                color = Color.White,
                fontSize = 10.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.MusicNote, "Music", tint = Color.White, modifier = Modifier.size(11.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "Sonido Original • ${project.selectedMood} (Veo 3.1 AI)",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 8.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun InstagramReelsSimulator(project: Project) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, start = 14.dp, end = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Reels", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Icon(Icons.Outlined.CameraAlt, "Camera", tint = Color.White, modifier = Modifier.size(20.dp))
        }

        // Right column actions
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 10.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Outlined.FavoriteBorder, "Heart", tint = Color.White, modifier = Modifier.size(24.dp))
                Text("89.3K", color = Color.White, fontSize = 8.5.sp, fontWeight = FontWeight.SemiBold)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Outlined.ChatBubbleOutline, "Comment", tint = Color.White, modifier = Modifier.size(22.dp))
                Text("1,120", color = Color.White, fontSize = 8.5.sp, fontWeight = FontWeight.SemiBold)
            }
            Icon(Icons.Outlined.Send, "Send", tint = Color.White, modifier = Modifier.size(22.dp))
            Icon(Icons.Default.MoreVert, "Menu", tint = Color.White, modifier = Modifier.size(20.dp))
        }

        // Bottom creator bar
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.80f)
                .padding(start = 12.dp, bottom = 14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE1306C)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("V", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text("veo.studio", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .border(1.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("Seguir", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(project.title, color = Color.White, fontSize = 9.5.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun YouTubeShortsSimulator(project: Project) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Right side
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 10.dp, bottom = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.ThumbUp, "Like", tint = Color.White, modifier = Modifier.size(22.dp))
                Text("210K", color = Color.White, fontSize = 8.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.ThumbDown, "Dislike", tint = Color.White, modifier = Modifier.size(20.dp))
                Text("No me gusta", color = Color.White, fontSize = 7.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.Comment, "Comments", tint = Color.White, modifier = Modifier.size(20.dp))
                Text("4.5K", color = Color.White, fontSize = 8.sp)
            }
            Icon(Icons.Filled.Share, "Share", tint = Color.White, modifier = Modifier.size(20.dp))
            Icon(Icons.Filled.Autorenew, "Remix", tint = Color.White, modifier = Modifier.size(20.dp))
        }

        // Bottom channel info
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.80f)
                .padding(start = 12.dp, bottom = 14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.Red),
                    contentAlignment = Alignment.Center
                ) {
                    Text("YT", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text("@VeoStudioChannel", color = Color.White, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("Suscribirme", color = Color.Black, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(project.title, color = Color.White, fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun LinkedInSimulator(project: Project) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Corporate Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp, start = 12.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF0A66C2)),
                contentAlignment = Alignment.Center
            ) {
                Text("in", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("Veo Enterprise Studio • 1.er", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text("Video corporativo certificado C2PA", color = Color.White.copy(alpha = 0.7f), fontSize = 8.sp)
            }
        }

        // Subtitle box for 85% muted viewing compliance
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Black.copy(alpha = 0.75f))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = "CC: \"${project.scriptText.take(90)}...\"",
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Bottom corporate engagement bar
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.65f))
                .padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("👍 Recomendar", color = Color.White, fontSize = 8.sp)
            Text("💬 Comentar", color = Color.White, fontSize = 8.sp)
            Text("↗️ Difundir", color = Color.White, fontSize = 8.sp)
            Text("✉️ Enviar", color = Color.White, fontSize = 8.sp)
        }
    }
}
