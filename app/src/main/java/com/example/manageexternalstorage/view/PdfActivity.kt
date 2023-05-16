package com.example.manageexternalstorage.view


import android.content.ActivityNotFoundException
import android.content.Intent
import android.media.MediaScannerConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.manageexternalstorage.R
import com.example.manageexternalstorage.adapters.PdfAdapter
import com.example.manageexternalstorage.data.PdfFile
import com.example.manageexternalstorage.databinding.RenameDialogBinding
import com.example.manageexternalstorage.interfaces.onPdfClickListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File

class PdfActivity : AppCompatActivity(), onPdfClickListener {

    private var pdfRecyclerView: RecyclerView? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf)

        pdfRecyclerView = findViewById(R.id.pdfRecyclerView)
        pdfRecyclerView?.layoutManager = LinearLayoutManager(this)

        val pdfFiles = getPdfFiles()
        pdfRecyclerView?.adapter = PdfAdapter(pdfFiles, this)
    }

    private fun getPdfFiles(): List<PdfFile> {
        val pdfFiles = mutableListOf<PdfFile>()
        val uri = MediaStore.Files.getContentUri("external")
        val projection = arrayOf(
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.DATA)
        val selection = "${MediaStore.Files.FileColumns.MIME_TYPE} = ?"
        val selectionArgs = arrayOf("application/pdf")
        val sortOrder = "${MediaStore.Files.FileColumns.DISPLAY_NAME} ASC"
        contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)?.use { cursor ->
            val nameColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)
            val pathColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)
            while (cursor.moveToNext()) {
                val name = cursor.getString(nameColumn)
                val path = cursor.getString(pathColumn)
                pdfFiles.add(PdfFile(name, path.toUri()))
            }
        }

        return pdfFiles
    }

    override fun onRenamePdfClickListener(filePdf: PdfFile) {
        val customDialog = LayoutInflater.from(this).inflate(R.layout.rename_dialog, null)
        val bindingRF = RenameDialogBinding.bind(customDialog)
        val dialog = MaterialAlertDialogBuilder(this).setView(customDialog)
            .setCancelable(false)
            .setPositiveButton("Rename") { self, _ ->
                self.dismiss()
                val currenFile = File(filePdf.uri.toString())
                val newName = bindingRF.renameField.text
                if (newName != null && currenFile.exists() && newName.toString()
                        .isNotEmpty()
                ) {
                    val newFile = File(
                        currenFile.parentFile,
                        newName.toString() + "." + currenFile.extension
                    )
                    Log.d("IsFileRename", "popUpMenu: $newName")

                    if (currenFile.renameTo(newFile)) {
                        Toast.makeText(this, newName.toString(), Toast.LENGTH_SHORT).show()
                        MediaScannerConnection.scanFile(
                            this,
                            arrayOf(newFile.toString()),
                            arrayOf("pdf/*"),
                            null
                        )
                        filePdf.name = newFile.name
                        filePdf.uri = newFile.path.toString().toUri()

                    } else {
                        Toast.makeText(this, "Error Occurred", Toast.LENGTH_SHORT)
                            .show()
                    }

                } else {
                    Toast.makeText(this, "Access Denied", Toast.LENGTH_SHORT).show()
                }

            }
            .setNegativeButton("Cancel") { self, _ ->
                self.dismiss()
            }
            .create()
        dialog.show()
        bindingRF.renameField.text = SpannableStringBuilder(filePdf.name)

    }

    override fun onCopyPdfClickListener(filePdf: PdfFile) {
        val imageUri = filePdf.uri
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "pdf/*"
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
        try {
            val chooser = Intent.createChooser(shareIntent, "Share pdf using")
            startActivity(chooser)
        } catch (e: ActivityNotFoundException) {
            // Handle the exception if no activity is available to handle the intent
        }
        Toast.makeText(this, "Copied:${filePdf.name}?", Toast.LENGTH_SHORT).show()

    }

    override fun onDeletePdfClickListener(filePdf: PdfFile) {
        val builder = AlertDialog.Builder(this)
            .setTitle("Delete Alert")
            .setMessage("Are you sure you want to delete:${filePdf.name}?")
            .setPositiveButton("OK") { dialog, which ->
                val filePath = filePdf.uri.toString()
                val isDeleted = deleteFileFromStorage2(filePath)
                if (isDeleted) {
                    Toast.makeText(this, "${filePdf.name} is Deleted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error Occurred", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel") { dialog, which ->
                // Do something when the Cancel button is clicked
            }
            .show()
    }

    override fun onLongItemClickListener(filePdf: PdfFile) {

    }

    private fun deleteFileFromStorage2(filePath: String): Boolean {
        val file = File(filePath)
        if (file.exists()) {
            return file.delete()
        }
        return false
    }


}