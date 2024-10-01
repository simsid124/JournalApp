package com.example.journalapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.journalapp.databinding.JournalRowBinding

class JournalRecyclerAdapter(val context: Context, val journalList: List<Journal>)
    : RecyclerView.Adapter<JournalRecyclerAdapter.MyViewHolder>() {

        lateinit var binding : JournalRowBinding

        class MyViewHolder(var binding: JournalRowBinding) : RecyclerView.ViewHolder(binding.root){
            fun bind(journal: Journal){
                binding.journal = journal
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        binding = JournalRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = journalList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val journal = journalList[position]
        holder.bind(journal)

        // Load image using Glide
        Glide.with(context)
            .load(journal.imageUrl)
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.error_image)
            .into(holder.binding.journalImageList)

        holder.binding.journalRowShareBtn.setOnClickListener {
            val shareText = "Check out my amazing new journal activity named: ${journal.title}\n${journal.thoughts}"
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Journal"))
        }
    }
}