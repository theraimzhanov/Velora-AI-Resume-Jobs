package com.velora.mobile.data.resume

import android.content.ContentResolver
import android.net.Uri
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import org.apache.poi.xwpf.extractor.XWPFWordExtractor
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.ByteArrayInputStream

object ResumeDocumentTextExtractor {

    fun extractText(
        contentResolver: ContentResolver,
        uri: Uri,
        mimeType: String?,
        fileName: String?
    ): String {
        val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() }
            ?: error("Failed to read file.")

        val safeMime = mimeType.orEmpty().lowercase()
        val safeName = fileName.orEmpty().lowercase()

        return when {
            safeMime.contains("pdf") || safeName.endsWith(".pdf") -> extractPdf(bytes)
            safeMime.contains("officedocument.wordprocessingml.document") || safeName.endsWith(".docx") -> extractDocx(bytes)
            safeName.endsWith(".doc") || safeMime.contains("msword") ->
                error("Legacy .doc is not supported yet. Please upload PDF or DOCX.")
            else -> error("Unsupported file type. Please upload PDF or DOCX.")
        }.trim().ifBlank {
            error("Could not extract readable text from this file.")
        }
    }

    private fun extractPdf(bytes: ByteArray): String {
        ByteArrayInputStream(bytes).use { input ->
            PDDocument.load(input).use { document ->
                return PDFTextStripper().getText(document)
            }
        }
    }

    private fun extractDocx(bytes: ByteArray): String {
        ByteArrayInputStream(bytes).use { input ->
            XWPFDocument(input).use { document ->
                XWPFWordExtractor(document).use { extractor ->
                    return extractor.text.orEmpty()
                }
            }
        }
    }
}