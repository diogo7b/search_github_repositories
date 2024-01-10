package com.example.github_repositories.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.github_repositories.R
import com.example.github_repositories.domain.Repos


class ReposAdapter(private val reposList: List<Repos>, ContextApp: Context) :
    RecyclerView.Adapter<ReposAdapter.ViewHolder>() {
    val contextApp = ContextApp

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameRepo: TextView
        val urlRepo: ImageView

        init {
            view.apply {
                nameRepo = findViewById(R.id.tv_name_repository)
                urlRepo = findViewById(R.id.iv_btn_share)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_rc_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.nameRepo.text = reposList[position].name
        holder.urlRepo.setOnClickListener {
            openUrl(reposList[position].html_url)
        }
    }

    override fun getItemCount(): Int = reposList.size

    fun openUrl(url: String) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, url)
            type = "text/plain"
        }
        val chooser: Intent = Intent.createChooser(intent, "Abrir link:")

        startActivity(contextApp, chooser, null)
    }

}