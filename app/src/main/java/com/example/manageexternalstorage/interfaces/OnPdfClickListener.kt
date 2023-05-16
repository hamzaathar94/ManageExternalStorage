package com.example.manageexternalstorage.interfaces


import com.example.manageexternalstorage.data.PdfFile

interface onPdfClickListener {
   fun onRenamePdfClickListener(filePdf: PdfFile)
    fun onCopyPdfClickListener(filePdf: PdfFile)
    fun onDeletePdfClickListener(filePdf: PdfFile)
    fun onLongItemClickListener(filePdf: PdfFile)
}