package com.example.ui.dialogs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.WargaRepository
import com.example.data.model.DuesEntity
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.CardBorderStroke
import com.example.ui.theme.StatusPaidColor

@Composable
fun ReceiptDialog(
    dues: DuesEntity,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val receiptText = WargaRepository.generateReceiptText(dues)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ReceiptLong,
                    contentDescription = null,
                    tint = BluePrimary
                )
                Text(
                    text = "Kuitansi Digital RW 26",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Receipt Card Style
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF7F9FC), RoundedCornerShape(16.dp))
                        .border(1.dp, CardBorderStroke, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "PENGURUS RUKUN WARGA 26",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = BluePrimary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "Tanda Terima Pembayaran Iuran Warga Digital",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        HorizontalDivider(
                            color = CardBorderStroke,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        ReceiptRow(label = "No. Kuitansi", value = dues.receiptNumber.ifEmpty { "KW-RW26-OFFLINE" })
                        ReceiptRow(label = "Warga / KK", value = dues.citizenName)
                        ReceiptRow(label = "Alamat", value = "${dues.houseNumber}, ${dues.rt}")
                        ReceiptRow(label = "Jenis Iuran", value = dues.category)
                        ReceiptRow(label = "Periode", value = dues.periodMonth)
                        ReceiptRow(label = "Jumlah", value = WargaRepository.formatRupiah(dues.amount), isHighlight = true)
                        ReceiptRow(label = "Metode Bayar", value = dues.paymentMethod)
                        ReceiptRow(label = "Tanggal", value = WargaRepository.formatDate(dues.paymentDate))

                        if (dues.notes.isNotBlank()) {
                            ReceiptRow(label = "Catatan", value = dues.notes)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // LUNAS Stamp
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                                .border(1.5.dp, StatusPaidColor, RoundedCornerShape(8.dp))
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = StatusPaidColor,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "STATUS : LUNAS / TERVERIFIKASI",
                                    color = StatusPaidColor,
                                    fontWeight = FontWeight.ExtraBold,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Kuitansi RW 26", receiptText)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Kuitansi disalin ke clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("copy_receipt_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Salin")
                }

                Button(
                    onClick = {
                        val encodedMsg = Uri.encode(receiptText)
                        val sendIntent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("https://api.whatsapp.com/send?text=$encodedMsg")
                        }
                        context.startActivity(sendIntent)
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    modifier = Modifier.testTag("share_whatsapp_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Chat,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Kirim WA")
                }
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Tutup")
            }
        }
    )
}

@Composable
private fun ReceiptRow(label: String, value: String, isHighlight: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
        Text(
            text = value,
            style = if (isHighlight) MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp) else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.SemiBold,
            color = if (isHighlight) BluePrimary else MaterialTheme.colorScheme.onSurface
        )
    }
}
