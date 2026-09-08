package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.WargaRepository
import com.example.data.model.DuesEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DuesPdfGenerator {

    private const val PAGE_WIDTH = 595 // A4 standard width in points
    private const val PAGE_HEIGHT = 842 // A4 standard height in points
    private const val MARGIN_LEFT = 36f
    private const val MARGIN_RIGHT = 559f
    private const val MARGIN_TOP = 36f
    private const val MARGIN_BOTTOM = 806f
    private const val CONTENT_WIDTH = MARGIN_RIGHT - MARGIN_LEFT // 523f

    data class PdfConfig(
        val periodFilter: String = "Semua Periode",
        val rtFilter: String = "Semua RT",
        val statusFilter: String = "Semua Status",
        val ketuaName: String = "H.Didin jaenudin",
        val bendaharaName: String = "Lahri supriatna",
        val notes: String = "Laporan ini dibuat otomatis melalui Sistem Informasi Warga RW 26."
    )

    fun generateDuesPdf(
        context: Context,
        duesList: List<DuesEntity>,
        config: PdfConfig = PdfConfig()
    ): File {
        val document = PdfDocument()

        // Filter dues based on configuration
        val filteredList = duesList.filter { dues ->
            val matchesRt = config.rtFilter == "Semua RT" || dues.rt.contains(config.rtFilter, ignoreCase = true)
            val matchesStatus = config.statusFilter == "Semua Status" || dues.status.equals(config.statusFilter, ignoreCase = true)
            val matchesPeriod = config.periodFilter == "Semua Periode" || dues.periodMonth.contains(config.periodFilter, ignoreCase = true)
            matchesRt && matchesStatus && matchesPeriod
        }

        val totalCollected = filteredList.filter { it.status.equals("Lunas", ignoreCase = true) }.sumOf { it.amount }
        val totalPending = filteredList.filter { it.status.equals("Belum Lunas", ignoreCase = true) }.sumOf { it.amount }
        val totalAmount = filteredList.sumOf { it.amount }
        val countPaid = filteredList.count { it.status.equals("Lunas", ignoreCase = true) }
        val countUnpaid = filteredList.count { it.status.equals("Belum Lunas", ignoreCase = true) }

        // Paints
        val paintText = Paint().apply {
            color = Color.BLACK
            isAntiAlias = true
        }

        val paintHeader = Paint().apply {
            color = Color.rgb(27, 75, 138) // Deep RW Blue
            isAntiAlias = true
        }

        val paintLine = Paint().apply {
            color = Color.rgb(200, 205, 215)
            strokeWidth = 1f
            isAntiAlias = true
        }

        val paintDividerThick = Paint().apply {
            color = Color.rgb(27, 75, 138)
            strokeWidth = 2.5f
            isAntiAlias = true
        }

        val paintDividerThin = Paint().apply {
            color = Color.rgb(27, 75, 138)
            strokeWidth = 0.8f
            isAntiAlias = true
        }

        val paintCardBg = Paint().apply {
            color = Color.rgb(245, 247, 250)
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val paintGreen = Paint().apply {
            color = Color.rgb(22, 120, 50)
            isAntiAlias = true
        }

        val paintRed = Paint().apply {
            color = Color.rgb(190, 30, 40)
            isAntiAlias = true
        }

        val rowHeight = 22f
        val maxRowsFirstPage = 13
        val maxRowsSubsequentPages = 24

        var remainingDues = filteredList
        var pageNumber = 1

        // Pre-calculate total pages
        val totalItems = filteredList.size
        val totalPages = if (totalItems <= maxRowsFirstPage) {
            1
        } else {
            1 + Math.ceil((totalItems - maxRowsFirstPage).toDouble() / maxRowsSubsequentPages).toInt()
        }

        // Draw Page 1
        var pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
        var page = document.startPage(pageInfo)
        var canvas = page.canvas

        var y = MARGIN_TOP

        // 1. Kop Surat Resmi RW 26
        paintHeader.textSize = 15f
        paintHeader.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paintHeader.textAlign = Paint.Align.CENTER
        canvas.drawText("PENGURUS RUKUN WARGA (RW) 26", PAGE_WIDTH / 2f, y + 15, paintHeader)

        paintText.textSize = 10.5f
        paintText.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paintText.textAlign = Paint.Align.CENTER
        canvas.drawText("KELURAHAN SUKAMENTRI • KECAMATAN GARUTKOTA", PAGE_WIDTH / 2f, y + 30, paintText)

        paintText.textSize = 8f
        paintText.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paintText.color = Color.rgb(100, 100, 100)
        canvas.drawText("Sekretariat: Balai Warga RW 26 • Hotline/WA: 0812-3456-789 • Portal Warga Mandiri", PAGE_WIDTH / 2f, y + 43, paintText)

        // Double Kop line
        canvas.drawLine(MARGIN_LEFT, y + 50, MARGIN_RIGHT, y + 50, paintDividerThick)
        canvas.drawLine(MARGIN_LEFT, y + 53, MARGIN_RIGHT, y + 53, paintDividerThin)

        y += 68f

        // 2. Judul Dokumen & Subtitle
        paintText.color = Color.BLACK
        paintText.textSize = 13f
        paintText.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paintText.textAlign = Paint.Align.CENTER
        canvas.drawText("LAPORAN REKAPITULASI IURAN KAS WARGA", PAGE_WIDTH / 2f, y, paintText)

        y += 14f
        paintText.textSize = 9f
        paintText.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paintText.color = Color.rgb(80, 80, 80)
        val datePrintStr = SimpleDateFormat("dd MMMM yyyy, HH:mm 'WIB'", Locale("id", "ID")).format(Date())
        canvas.drawText("Periode: ${config.periodFilter} • Filter: ${config.rtFilter} • Tanggal Cetak: $datePrintStr", PAGE_WIDTH / 2f, y, paintText)

        y += 18f

        // 3. Ringkasan Keuangan Box (Summary Grid)
        val boxHeight = 54f
        val boxWidth = (CONTENT_WIDTH - 16f) / 3f

        // Box 1: Total Terkumpul
        drawStatCard(
            canvas = canvas,
            left = MARGIN_LEFT,
            top = y,
            width = boxWidth,
            height = boxHeight,
            label = "Total Kas Terkumpul ($countPaid Lunas)",
            value = WargaRepository.formatRupiah(totalCollected),
            valueColor = Color.rgb(22, 120, 50),
            bgPaint = paintCardBg,
            linePaint = paintLine,
            textPaint = paintText
        )

        // Box 2: Total Tunggakan
        drawStatCard(
            canvas = canvas,
            left = MARGIN_LEFT + boxWidth + 8f,
            top = y,
            width = boxWidth,
            height = boxHeight,
            label = "Total Tunggakan ($countUnpaid Belum)",
            value = WargaRepository.formatRupiah(totalPending),
            valueColor = Color.rgb(190, 30, 40),
            bgPaint = paintCardBg,
            linePaint = paintLine,
            textPaint = paintText
        )

        // Box 3: Total Target & % Pelunasan
        val percentage = if (totalAmount > 0) (totalCollected.toDouble() / totalAmount * 100).toInt() else 0
        drawStatCard(
            canvas = canvas,
            left = MARGIN_LEFT + (boxWidth + 8f) * 2f,
            top = y,
            width = boxWidth,
            height = boxHeight,
            label = "Total Target ($percentage% Tercapai)",
            value = WargaRepository.formatRupiah(totalAmount),
            valueColor = Color.rgb(27, 75, 138),
            bgPaint = paintCardBg,
            linePaint = paintLine,
            textPaint = paintText
        )

        y += boxHeight + 14f

        // 4. Tabel Header
        y = drawTableHeader(canvas, y, paintHeader, paintText, paintLine)

        // 5. Draw First Page Rows
        val firstBatch = remainingDues.take(maxRowsFirstPage)
        remainingDues = remainingDues.drop(maxRowsFirstPage)

        y = drawTableRows(canvas, y, firstBatch, 1, rowHeight, paintText, paintGreen, paintRed, paintLine)

        // Footer for Page 1
        drawFooter(canvas, pageNumber, totalPages, config.notes, paintText, paintLine)
        document.finishPage(page)

        // Subsequent Pages
        var rowIndex = firstBatch.size + 1
        while (remainingDues.isNotEmpty()) {
            pageNumber++
            pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
            page = document.startPage(pageInfo)
            canvas = page.canvas

            y = MARGIN_TOP

            // Compact Header for continuation page
            paintText.textSize = 9.5f
            paintText.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paintText.color = Color.rgb(60, 60, 60)
            paintText.textAlign = Paint.Align.LEFT
            canvas.drawText("Laporan Rekapitulasi Iuran Kas RW 26 (Lanjutan)", MARGIN_LEFT, y + 10, paintText)

            paintText.textAlign = Paint.Align.RIGHT
            canvas.drawText("Periode: ${config.periodFilter}", MARGIN_RIGHT, y + 10, paintText)

            canvas.drawLine(MARGIN_LEFT, y + 16, MARGIN_RIGHT, y + 16, paintDividerThin)
            y += 26f

            // Redraw table header
            y = drawTableHeader(canvas, y, paintHeader, paintText, paintLine)

            val currentBatch = remainingDues.take(maxRowsSubsequentPages)
            remainingDues = remainingDues.drop(maxRowsSubsequentPages)

            y = drawTableRows(canvas, y, currentBatch, rowIndex, rowHeight, paintText, paintGreen, paintRed, paintLine)
            rowIndex += currentBatch.size

            // If this is the last page, draw the signatures
            if (remainingDues.isEmpty()) {
                drawSignatureSection(canvas, y + 16f, config, paintText, paintLine)
            }

            drawFooter(canvas, pageNumber, totalPages, config.notes, paintText, paintLine)
            document.finishPage(page)
        }

        // If only 1 page and we have room, draw signature on page 1
        if (totalPages == 1) {
            // Re-open page 1? PdfDocument pages cannot be re-opened once finished.
            // Let's ensure signature was drawn on page 1 if totalPages == 1!
        }

        // Write to application cache/files directory
        val fileName = "Laporan_Iuran_RW26_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.pdf"
        val pdfDir = File(context.cacheDir, "pdf_reports").apply { if (!exists()) mkdirs() }
        val pdfFile = File(pdfDir, fileName)

        // Let's create the final document properly ensuring signatures on last page:
        // Because in the loop above, if totalPages == 1, document was already finished!
        // To make it 100% robust, let's write a single clean generator with multi-page:
        document.close() // Close draft if any, let's write clean structure:

        return generateCleanPdfDocument(context, filteredList, config)
    }

    private fun generateCleanPdfDocument(
        context: Context,
        filteredList: List<DuesEntity>,
        config: PdfConfig
    ): File {
        val document = PdfDocument()

        val totalCollected = filteredList.filter { it.status.equals("Lunas", ignoreCase = true) }.sumOf { it.amount }
        val totalPending = filteredList.filter { it.status.equals("Belum Lunas", ignoreCase = true) }.sumOf { it.amount }
        val totalAmount = filteredList.sumOf { it.amount }
        val countPaid = filteredList.count { it.status.equals("Lunas", ignoreCase = true) }
        val countUnpaid = filteredList.count { it.status.equals("Belum Lunas", ignoreCase = true) }

        val paintText = Paint().apply { color = Color.BLACK; isAntiAlias = true }
        val paintHeader = Paint().apply { color = Color.rgb(27, 75, 138); isAntiAlias = true }
        val paintLine = Paint().apply { color = Color.rgb(210, 215, 225); strokeWidth = 0.8f; isAntiAlias = true }
        val paintDividerThick = Paint().apply { color = Color.rgb(27, 75, 138); strokeWidth = 2.5f; isAntiAlias = true }
        val paintDividerThin = Paint().apply { color = Color.rgb(27, 75, 138); strokeWidth = 0.8f; isAntiAlias = true }
        val paintCardBg = Paint().apply { color = Color.rgb(247, 249, 252); style = Paint.Style.FILL; isAntiAlias = true }
        val paintGreen = Paint().apply { color = Color.rgb(20, 125, 50); isAntiAlias = true }
        val paintRed = Paint().apply { color = Color.rgb(195, 35, 45); isAntiAlias = true }

        val rowHeight = 22f

        // With signatures (height approx 95f), on a single page we can fit up to 9 rows
        val maxRowsPage1WithSignatures = 9
        val maxRowsPage1WithoutSignatures = 14
        val maxRowsSubsequentPages = 23
        val maxRowsSubsequentLastWithSignatures = 15

        val totalItems = filteredList.size
        val totalPages: Int
        if (totalItems <= maxRowsPage1WithSignatures) {
            totalPages = 1
        } else if (totalItems <= maxRowsPage1WithoutSignatures) {
            // Needs 2nd page just for signatures
            totalPages = 2
        } else {
            val remaining = totalItems - maxRowsPage1WithoutSignatures
            totalPages = 1 + Math.ceil(remaining.toDouble() / maxRowsSubsequentLastWithSignatures).toInt().coerceAtLeast(1)
        }

        var remainingDues = filteredList
        var pageNumber = 1
        var globalRowIndex = 1

        while (pageNumber <= totalPages) {
            val isFirstPage = (pageNumber == 1)
            val isLastPage = (pageNumber == totalPages)

            val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas
            var y = MARGIN_TOP

            if (isFirstPage) {
                // Official Kop Surat RW 26
                paintHeader.textSize = 15f
                paintHeader.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                paintHeader.textAlign = Paint.Align.CENTER
                canvas.drawText("PENGURUS RUKUN WARGA (RW) 26", PAGE_WIDTH / 2f, y + 14, paintHeader)

                paintText.textSize = 10f
                paintText.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                paintText.textAlign = Paint.Align.CENTER
                paintText.color = Color.rgb(40, 40, 40)
                canvas.drawText("KELURAHAN SUKAMENTRI • KECAMATAN GARUTKOTA", PAGE_WIDTH / 2f, y + 28, paintText)

                paintText.textSize = 8f
                paintText.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                paintText.color = Color.rgb(110, 110, 110)
                canvas.drawText("Sekretariat: Balai Pertemuan Warga RW 26 • Kontak: 0812-3456-789", PAGE_WIDTH / 2f, y + 41, paintText)

                canvas.drawLine(MARGIN_LEFT, y + 47, MARGIN_RIGHT, y + 47, paintDividerThick)
                canvas.drawLine(MARGIN_LEFT, y + 50, MARGIN_RIGHT, y + 50, paintDividerThin)

                y += 64f

                // Document Title
                paintText.color = Color.BLACK
                paintText.textSize = 12.5f
                paintText.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                paintText.textAlign = Paint.Align.CENTER
                canvas.drawText("LAPORAN REKAPITULASI IURAN KAS WARGA", PAGE_WIDTH / 2f, y, paintText)

                y += 14f
                paintText.textSize = 8.5f
                paintText.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                paintText.color = Color.rgb(90, 90, 90)
                val datePrintStr = SimpleDateFormat("dd MMMM yyyy, HH:mm 'WIB'", Locale("id", "ID")).format(Date())
                canvas.drawText("Periode: ${config.periodFilter}   |   Filter: ${config.rtFilter}   |   Dicetak: $datePrintStr", PAGE_WIDTH / 2f, y, paintText)

                y += 16f

                // Summary Statistics 3 Cards
                val boxHeight = 48f
                val boxWidth = (CONTENT_WIDTH - 16f) / 3f
                val pct = if (totalAmount > 0) (totalCollected.toDouble() / totalAmount * 100).toInt() else 0

                drawStatCard(canvas, MARGIN_LEFT, y, boxWidth, boxHeight, "Kas Terkumpul ($countPaid)", WargaRepository.formatRupiah(totalCollected), paintGreen.color, paintCardBg, paintLine, paintText)
                drawStatCard(canvas, MARGIN_LEFT + boxWidth + 8f, y, boxWidth, boxHeight, "Tunggakan ($countUnpaid)", WargaRepository.formatRupiah(totalPending), paintRed.color, paintCardBg, paintLine, paintText)
                drawStatCard(canvas, MARGIN_LEFT + (boxWidth + 8f) * 2f, y, boxWidth, boxHeight, "Total Target ($pct%)", WargaRepository.formatRupiah(totalAmount), paintHeader.color, paintCardBg, paintLine, paintText)

                y += boxHeight + 14f
            } else {
                // Continuation page mini header
                paintText.textSize = 9.5f
                paintText.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                paintText.color = Color.rgb(60, 60, 60)
                paintText.textAlign = Paint.Align.LEFT
                canvas.drawText("Laporan Rekapitulasi Iuran Kas RW 26 (Lanjutan)", MARGIN_LEFT, y + 10, paintText)

                paintText.textAlign = Paint.Align.RIGHT
                canvas.drawText("Periode: ${config.periodFilter}", MARGIN_RIGHT, y + 10, paintText)

                canvas.drawLine(MARGIN_LEFT, y + 16, MARGIN_RIGHT, y + 16, paintDividerThin)
                y += 24f
            }

            // Draw Table Header
            y = drawTableHeader(canvas, y, paintHeader, paintText, paintLine)

            // Determine capacity for this page
            val rowsCapacity = if (isFirstPage) {
                if (isLastPage) maxRowsPage1WithSignatures else maxRowsPage1WithoutSignatures
            } else {
                if (isLastPage) maxRowsSubsequentLastWithSignatures else maxRowsSubsequentPages
            }

            val currentBatch = remainingDues.take(rowsCapacity)
            remainingDues = remainingDues.drop(rowsCapacity)

            if (currentBatch.isNotEmpty()) {
                y = drawTableRows(canvas, y, currentBatch, globalRowIndex, rowHeight, paintText, paintGreen, paintRed, paintLine)
                globalRowIndex += currentBatch.size
            } else if (isLastPage && totalItems == 0) {
                // Empty state in table
                paintText.textSize = 9f
                paintText.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
                paintText.textAlign = Paint.Align.CENTER
                paintText.color = Color.GRAY
                canvas.drawText("Tidak ada data iuran yang sesuai kriteria filter", PAGE_WIDTH / 2f, y + 25, paintText)
                y += 40f
            }

            // If this is the last page, draw Signatures
            if (isLastPage) {
                val signatureY = (MARGIN_BOTTOM - 110f).coerceAtLeast(y + 12f)
                drawSignatureSection(canvas, signatureY, config, paintText, paintLine)
            }

            // Draw Page Footer
            drawFooter(canvas, pageNumber, totalPages, config.notes, paintText, paintLine)
            document.finishPage(page)

            pageNumber++
        }

        // Save file
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "Laporan_Iuran_RW26_$timeStamp.pdf"
        val pdfDir = File(context.cacheDir, "pdf_reports").apply { if (!exists()) mkdirs() }
        val pdfFile = File(pdfDir, fileName)

        FileOutputStream(pdfFile).use { out ->
            document.writeTo(out)
        }
        document.close()

        return pdfFile
    }

    private fun drawStatCard(
        canvas: Canvas,
        left: Float,
        top: Float,
        width: Float,
        height: Float,
        label: String,
        value: String,
        valueColor: Int,
        bgPaint: Paint,
        linePaint: Paint,
        textPaint: Paint
    ) {
        // Background card with rounded corners
        canvas.drawRoundRect(left, top, left + width, top + height, 6f, 6f, bgPaint)
        canvas.drawRoundRect(left, top, left + width, top + height, 6f, 6f, linePaint)

        // Label
        textPaint.textSize = 7.5f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textPaint.color = Color.rgb(100, 105, 115)
        textPaint.textAlign = Paint.Align.LEFT
        canvas.drawText(label, left + 8f, top + 16f, textPaint)

        // Value
        textPaint.textSize = 10f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.color = valueColor
        canvas.drawText(value, left + 8f, top + 34f, textPaint)
    }

    private fun drawTableHeader(
        canvas: Canvas,
        y: Float,
        headerBgPaint: Paint,
        textPaint: Paint,
        linePaint: Paint
    ): Float {
        val headerHeight = 22f
        val headerBg = Paint().apply {
            color = Color.rgb(238, 242, 248)
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        canvas.drawRect(MARGIN_LEFT, y, MARGIN_RIGHT, y + headerHeight, headerBg)
        canvas.drawLine(MARGIN_LEFT, y, MARGIN_RIGHT, y, linePaint)
        canvas.drawLine(MARGIN_LEFT, y + headerHeight, MARGIN_RIGHT, y + headerHeight, linePaint)

        textPaint.textSize = 8f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.color = Color.rgb(30, 45, 75)

        val textY = y + 14f

        // Columns definition
        // No: 24, Warga: 110, RT/Rumah: 66, Kategori: 115, Periode: 68, Nominal: 70, Status: 70
        var colX = MARGIN_LEFT
        textPaint.textAlign = Paint.Align.CENTER
        canvas.drawText("No", colX + 12f, textY, textPaint)
        colX += 24f

        textPaint.textAlign = Paint.Align.LEFT
        canvas.drawText("Nama Warga / KK", colX + 4f, textY, textPaint)
        colX += 110f

        canvas.drawText("RT / Blok", colX + 4f, textY, textPaint)
        colX += 66f

        canvas.drawText("Jenis Iuran", colX + 4f, textY, textPaint)
        colX += 115f

        canvas.drawText("Periode", colX + 4f, textY, textPaint)
        colX += 68f

        textPaint.textAlign = Paint.Align.RIGHT
        canvas.drawText("Nominal", colX + 66f, textY, textPaint)
        colX += 70f

        textPaint.textAlign = Paint.Align.CENTER
        canvas.drawText("Status & Bayar", colX + 35f, textY, textPaint)

        return y + headerHeight
    }

    private fun drawTableRows(
        canvas: Canvas,
        startY: Float,
        items: List<DuesEntity>,
        startIndex: Int,
        rowHeight: Float,
        textPaint: Paint,
        paintGreen: Paint,
        paintRed: Paint,
        linePaint: Paint
    ): Float {
        var y = startY
        val zebraBg = Paint().apply {
            color = Color.rgb(250, 251, 253)
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        items.forEachIndexed { i, dues ->
            val isEven = (startIndex + i) % 2 == 0
            if (isEven) {
                canvas.drawRect(MARGIN_LEFT, y, MARGIN_RIGHT, y + rowHeight, zebraBg)
            }
            canvas.drawLine(MARGIN_LEFT, y + rowHeight, MARGIN_RIGHT, y + rowHeight, linePaint)

            val textY = y + 14f
            var colX = MARGIN_LEFT

            // No
            textPaint.textSize = 8f
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textPaint.color = Color.rgb(80, 80, 80)
            textPaint.textAlign = Paint.Align.CENTER
            canvas.drawText("${startIndex + i}", colX + 12f, textY, textPaint)
            colX += 24f

            // Nama Warga
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textPaint.color = Color.rgb(20, 25, 35)
            textPaint.textAlign = Paint.Align.LEFT
            val truncatedName = truncateText(dues.citizenName, 22)
            canvas.drawText(truncatedName, colX + 4f, textY, textPaint)
            colX += 110f

            // RT / Rumah
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textPaint.color = Color.rgb(60, 60, 60)
            val rtText = truncateText("${dues.rt} • ${dues.houseNumber}", 14)
            canvas.drawText(rtText, colX + 4f, textY, textPaint)
            colX += 66f

            // Kategori
            val categoryText = truncateText(dues.category, 24)
            canvas.drawText(categoryText, colX + 4f, textY, textPaint)
            colX += 115f

            // Periode
            val periodText = truncateText(dues.periodMonth, 13)
            canvas.drawText(periodText, colX + 4f, textY, textPaint)
            colX += 68f

            // Nominal
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textPaint.color = Color.rgb(20, 20, 20)
            textPaint.textAlign = Paint.Align.RIGHT
            canvas.drawText(WargaRepository.formatRupiah(dues.amount), colX + 66f, textY, textPaint)
            colX += 70f

            // Status & Bayar
            val isPaid = dues.status.equals("Lunas", ignoreCase = true)
            val statusPaint = if (isPaid) paintGreen else paintRed
            statusPaint.textSize = 7.5f
            statusPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            statusPaint.textAlign = Paint.Align.CENTER

            val statusText = if (isPaid) {
                if (dues.paymentMethod.isNotBlank() && dues.paymentMethod != "-") "Lunas (${dues.paymentMethod})" else "Lunas"
            } else {
                "Belum Lunas"
            }
            canvas.drawText(truncateText(statusText, 14), colX + 35f, textY, statusPaint)

            y += rowHeight
        }

        return y
    }

    private fun drawSignatureSection(
        canvas: Canvas,
        y: Float,
        config: PdfConfig,
        textPaint: Paint,
        linePaint: Paint
    ) {
        val dateToday = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date())

        textPaint.textSize = 8.5f
        textPaint.color = Color.rgb(40, 40, 40)
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textPaint.textAlign = Paint.Align.RIGHT
        canvas.drawText("Dikeluarkan di: RW 26 Sukamentri, $dateToday", MARGIN_RIGHT - 10f, y + 10f, textPaint)

        val sigTop = y + 24f

        // Column Left: Ketua RW
        val col1CenterX = MARGIN_LEFT + 90f
        textPaint.textAlign = Paint.Align.CENTER
        canvas.drawText("Mengetahui,", col1CenterX, sigTop, textPaint)
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Ketua RW 26", col1CenterX, sigTop + 12f, textPaint)

        // Space for signature & stamp
        val nameY = sigTop + 60f
        canvas.drawText("( ${config.ketuaName} )", col1CenterX, nameY, textPaint)
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textPaint.textSize = 7.5f
        textPaint.color = Color.rgb(100, 100, 100)
        canvas.drawText("NIP/Pengurus RW 26", col1CenterX, nameY + 10f, textPaint)

        // Column Right: Bendahara RW
        val col2CenterX = MARGIN_RIGHT - 90f
        textPaint.textSize = 8.5f
        textPaint.color = Color.rgb(40, 40, 40)
        textPaint.textAlign = Paint.Align.CENTER
        canvas.drawText("Dibuat & Diverifikasi,", col2CenterX, sigTop, textPaint)
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Bendahara RW 26", col2CenterX, sigTop + 12f, textPaint)

        canvas.drawText("( ${config.bendaharaName} )", col2CenterX, nameY, textPaint)
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textPaint.textSize = 7.5f
        textPaint.color = Color.rgb(100, 100, 100)
        canvas.drawText("Pengelola Kas RW 26", col2CenterX, nameY + 10f, textPaint)
    }

    private fun drawFooter(
        canvas: Canvas,
        pageNumber: Int,
        totalPages: Int,
        notes: String,
        textPaint: Paint,
        linePaint: Paint
    ) {
        val y = MARGIN_BOTTOM

        canvas.drawLine(MARGIN_LEFT, y - 10f, MARGIN_RIGHT, y - 10f, linePaint)

        textPaint.textSize = 7.5f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        textPaint.color = Color.rgb(120, 120, 120)
        textPaint.textAlign = Paint.Align.LEFT
        canvas.drawText(notes, MARGIN_LEFT, y + 4f, textPaint)

        textPaint.textAlign = Paint.Align.RIGHT
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("Halaman $pageNumber dari $totalPages", MARGIN_RIGHT, y + 4f, textPaint)
    }

    private fun truncateText(text: String, maxChars: Int): String {
        return if (text.length > maxChars) {
            text.take(maxChars - 1) + "…"
        } else {
            text
        }
    }

    fun openOrSharePdf(context: Context, file: File, action: String = "view") {
        try {
            val authority = "${context.packageName}.fileprovider"
            val uri: Uri = FileProvider.getUriForFile(context, authority, file)

            if (action == "share") {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_SUBJECT, "Laporan Rekapitulasi Iuran Kas RW 26")
                    putExtra(Intent.EXTRA_TEXT, "Berikut terlampir dokumen resmi Laporan Rekapitulasi Iuran Kas Warga RW 26 dalam format PDF.")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(Intent.createChooser(shareIntent, "Bagikan Laporan PDF RW 26"))
            } else {
                val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/pdf")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                val chooser = Intent.createChooser(viewIntent, "Buka / Cetak Laporan PDF")
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(chooser)
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Gagal membuka file PDF: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }
}
