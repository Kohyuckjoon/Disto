package com.example.yscdisto.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.yscdisto.data.model.ProjectCreate
import com.example.yscdisto.databinding.ItemProjectBinding

class ProjectAdapter (
    private val projects: List<ProjectCreate>
) : RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>(){

    inner class ProjectViewHolder(val binding: ItemProjectBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(project: ProjectCreate) {
            binding.mcProjectName.text = project.name
            binding.mcNumber.text = project.sheetNumber
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val binding = ItemProjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProjectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        holder.bind(projects[position])
    }

    override fun getItemCount(): Int = projects.size
}