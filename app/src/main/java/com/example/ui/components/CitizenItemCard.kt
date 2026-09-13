package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.CitizenEntity
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.CardBorderStroke
import com.example.ui.theme.StatusPaidColor
import com.example.ui.theme.WarmAmberTertiary

@Composable
fun CitizenItemCard(
    citizen: CitizenEntity,
    onToggleLock: (CitizenEntity) -> Unit,
    onDelete: (CitizenEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isTetap = citizen.statusDomisili == "Tetap"
    var showLockedAlert by remember { mutableStateOf(false) }

    if (showLockedAlert) {
        AlertDialog(
            onDismissRequest = { showLockedAlert = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = BluePrimary,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "Nama Warga Terkunci",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Text(
                    text = "Data nama warga \"${citizen.name}\" saat ini berstatus terkunci untuk mengamankan data dan mencegah perubahan/penghapusan tidak sengaja. Buka kunci terlebih dahulu jika Anda ingin menghapus data warga ini.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onToggleLock(citizen)
                        showLockedAlert = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Buka Kunci Sekarang")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showLockedAlert = false },
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Tutup")
                }
            }
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("citizen_card_${citizen.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (citizen.isLocked) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            if (citizen.isLocked) 1.5.dp else 1.dp,
            if (citizen.isLocked) Color(0xFF93C5FD) else CardBorderStroke
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                if (citizen.isLocked) BluePrimary else MaterialTheme.colorScheme.surfaceVariant,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = if (citizen.isLocked) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = citizen.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (citizen.isLocked) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Data Terkunci",
                                    tint = BluePrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "${citizen.houseNumber} • ${citizen.rt}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Lock status badge
                    Box(
                        modifier = Modifier
                            .background(
                                if (citizen.isLocked) Color(0xFFEFF6FF) else Color(0xFFF1F5F9),
                                RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = if (citizen.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                                contentDescription = null,
                                tint = if (citizen.isLocked) BluePrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = if (citizen.isLocked) "Terkunci" else "Terbuka",
                                color = if (citizen.isLocked) BluePrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Domisili badge
                    Box(
                        modifier = Modifier
                            .background(
                                if (isTetap) Color(0xFFDCFCE7) else Color(0xFFFEEFC3),
                                RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = citizen.statusDomisili,
                            color = if (isTetap) StatusPaidColor else WarmAmberTertiary,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Details info row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "${citizen.familyMembersCount} Jiwa / Anggota KK",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = citizen.phoneNumber,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Lock / Unlock Button
                    OutlinedButton(
                        onClick = { onToggleLock(citizen) },
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, if (citizen.isLocked) Color(0xFF93C5FD) else CardBorderStroke),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (citizen.isLocked) Color(0xFFEFF6FF) else Color.Transparent
                        ),
                        modifier = Modifier.testTag("toggle_lock_${citizen.id}")
                    ) {
                        Icon(
                            imageVector = if (citizen.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = if (citizen.isLocked) "Buka Kunci" else "Kunci Nama",
                            tint = if (citizen.isLocked) BluePrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (citizen.isLocked) "Terkunci" else "Kunci",
                            color = if (citizen.isLocked) BluePrimary else MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (citizen.isLocked) FontWeight.Bold else FontWeight.Normal
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            val cleanNumber = citizen.phoneNumber.replace("-", "").replace(" ", "").replace("+", "")
                            val formattedNumber = if (cleanNumber.startsWith("0")) "62${cleanNumber.substring(1)}" else cleanNumber
                            val uri = Uri.parse("https://api.whatsapp.com/send?phone=$formattedNumber&text=Halo%20${Uri.encode(citizen.name)}%2C%20salam%20dari%20Pengurus%20RW%2026.")
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            context.startActivity(intent)
                        },
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, CardBorderStroke)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Chat,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("WA", style = MaterialTheme.typography.labelMedium)
                    }

                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${citizen.phoneNumber}"))
                            context.startActivity(intent)
                        },
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, CardBorderStroke)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Telp", style = MaterialTheme.typography.labelMedium)
                    }
                }

                IconButton(
                    onClick = {
                        if (citizen.isLocked) {
                            showLockedAlert = true
                        } else {
                            onDelete(citizen)
                        }
                    },
                    modifier = Modifier
                        .size(34.dp)
                        .testTag("delete_citizen_${citizen.id}")
                ) {
                    Icon(
                        imageVector = if (citizen.isLocked) Icons.Default.Lock else Icons.Default.DeleteOutline,
                        contentDescription = if (citizen.isLocked) "Data Terkunci (Tidak Dapat Dihapus)" else "Hapus Data Warga",
                        tint = if (citizen.isLocked) BluePrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
