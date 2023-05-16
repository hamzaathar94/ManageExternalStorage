package com.example.manageexternalstorage.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.manageexternalstorage.data.PdfFile
import com.example.manageexternalstorage.databinding.ItemPdfBinding
import com.example.manageexternalstorage.interfaces.onPdfClickListener


class PdfAdapter(private val pdfFiles: List<PdfFile>, val onPdfClickListener: onPdfClickListener) :
    RecyclerView.Adapter<PdfAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPdfBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pdfFile = pdfFiles[position]
        holder.binding.fileName.text = pdfFile.name

        holder.itemView.setOnLongClickListener {
            onPdfClickListener.onLongItemClickListener(pdfFile)
            true
        }

        holder.binding.renameButton.setOnClickListener {
            onPdfClickListener.onRenamePdfClickListener(pdfFile)
        }
        holder.binding.copyButton.setOnClickListener {
            onPdfClickListener.onCopyPdfClickListener(pdfFile)
        }
        holder.binding.deleteButton.setOnClickListener {
            onPdfClickListener.onDeletePdfClickListener(pdfFile)
        }

    }

    override fun getItemCount(): Int {
        return pdfFiles.size
    }

    class ViewHolder(var binding: ItemPdfBinding) : RecyclerView.ViewHolder(binding.root) {}
}
