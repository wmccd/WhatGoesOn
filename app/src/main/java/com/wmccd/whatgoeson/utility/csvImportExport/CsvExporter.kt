package com.wmccd.whatgoeson.utility.csvImportExport

import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import com.wmccd.whatgoeson.MyApplication
import com.wmccd.whatgoeson.repository.database.AlbumDao
import com.wmccd.whatgoeson.repository.database.Converters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter

class CsvExporter(
    private val albumDao: AlbumDao,
) {

    suspend fun export() {
        withContext(Dispatchers.IO) {
            try {
                // 1. Read Data from Room
                val allDetails =
                    albumDao.getAllDetails().firstOrNull()
                        ?: emptyList()

                // 2. Format Data (CSV)
                val converters = Converters()
                val csvContent = buildString {
                    // CSV Header
                    appendLine(CsvConstants.CSV_FIELD_ORDER)
                    allDetails.forEach {
                        val albumName = it.albumName.replace(",", CsvConstants.COMMA_DELIMITER)
                        val artistName = it.artistName.replace(",", CsvConstants.COMMA_DELIMITER)
                        val mediaType = converters.fromMediaType(it.mediaType)
                        appendLine("${it.albumId},$albumName,${it.albumUrl},${it.albumFavourite},${it.artistId},$artistName,$mediaType,")
                    }
                }

                // 3. Create a File
                val directory =
                    MyApplication.appContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                val file = File(directory, "all_details_export.csv")

                // 4. Write Data to File
                FileWriter(file).use { writer ->
                    writer.write(csvContent)
                }

                // 5. Create an Email Intent
                val emailIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/csv"
                    val fileUri: Uri = FileProvider.getUriForFile(
                        MyApplication.appContext,
                        "${MyApplication.appContext.packageName}.fileprovider",
                        file
                    )
                    putExtra(Intent.EXTRA_STREAM, fileUri)
                    putExtra(Intent.EXTRA_SUBJECT, "What Goes On - Data Export")
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "Here's the exported all details data. Note that commas have been replaced with pipes."
                    )
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                // 6. Start the Email Activity
                val chooserIntent = Intent.createChooser(emailIntent, "Send email...")
                chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                MyApplication.appContext.startActivity(chooserIntent)

                // 7. Delete the temporary file
                file.deleteOnExit()
            } catch (e: Exception) {
                MyApplication.utilities.logger.log(
                    Log.ERROR,
                    TAG, "exportData: Exception", e)
            }
        }
    }

    companion object {
        private val TAG = CsvExporter::class.java.simpleName
    }
}