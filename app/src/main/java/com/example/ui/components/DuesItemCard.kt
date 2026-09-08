package com.example.ui.components

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.WargaRepository
import com.example.data.model.DuesEntity
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.CardBorderStroke
import com.example.ui.theme.StatusPaidBadgeContainer
import com.example.ui.theme.StatusPaidBadgeIcon
import com.example.ui.theme.StatusPaidColor
import com.example.ui.theme.StatusUnpaidBg
import com.example.ui.theme.StatusUnpaidColor

@Composable
fun DuesItemCard(
    dues: DuesEntity,
    onViewReceipt: (DuesEntity) -> Unit,
    onQuickPay: (DuesEntity) -> Unit,
    onDelete: (DuesEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val isPaid = dues.status.equals("Lunas", ignoreCase = true)
    val statusColor = if (isPaid) StatusPaidColor else StatusUnpaidColor
    val statusBg = if (isPaid) Color(0xFFDCFCE7) else StatusUnpaidBg
    val badgeBg = if (isPaid) StatusPaidBadgeContainer else Color(0xFFFAD2CF)
    val badgeIconTint = if (isPaid) StatusPaidBadgeIcon else Color(0xFF3F0001)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("dues_card_${dues.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, CardBorderStroke),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Row: Category badge & status chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(badgeBg, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPaid) Icons.Default.CheckCircle else Icons.Default.HourglassTop,
                            contentDescription = null,
                            tint = badgeIconTint,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = dues.category,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Periode: ${dues.periodMonth}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Status Chip
                Box(
                    modifier = Modifier
                        .background(statusBg, RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = dues.status,
                        color = statusColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Resident Name & Address + Amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = dues.citizenName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${dues.houseNumber} • ${dues.rt}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (isPaid) "+${WargaRepository.formatRupiah(dues.amount)}" else WargaRepository.formatRupiah(dues.amount),
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
                        fontWeight = FontWeight.Bold,
                        color = if (isPaid) StatusPaidColor else StatusUnpaidColor
                    )
                    Text(
                        text = dues.paymentMethod,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (dues.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Catatan: ${dues.notes}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isPaid) "Tgl: ${WargaRepository.formatDate(dues.paymentDate)}" else "Jatuh Tempo Bulanan",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { onDelete(dues) },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("delete_dues_${dues.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Hapus Data Iuran",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    if (isPaid) {
                        OutlinedButton(
                            onClick = { onViewReceipt(dues) },
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, CardBorderStroke),
                            modifier = Modifier.testTag("receipt_button_${dues.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Kuitansi", style = MaterialTheme.typography.labelMedium)
                        }
                    } else {
                        ElevatedButton(
                            onClick = { onQuickPay(dues) },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = BluePrimary,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.testTag("pay_button_${dues.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Bayar Sekarang", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }
}
