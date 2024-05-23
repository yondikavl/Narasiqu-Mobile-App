package com.yondikavl.narasiqu

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.yondikavl.narasiqu.databinding.ActivityDetailStoryBinding
import com.yondikavl.narasiqu.models.Story
import com.yondikavl.narasiqu.viewModels.DetailStoryModels
import com.yondikavl.narasiqu.viewModels.ViewModelsFactory
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

class DetailStoryActivity : AppCompatActivity() {

    private val detailStoryModels by viewModels<DetailStoryModels> {
        ViewModelsFactory.getInstance(this)
    }

    private lateinit var bind: ActivityDetailStoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(bind.root)

        lifecycleScope.launch {
            getDataFromIntent()
        }

        bind.btnBack.setOnClickListener {
            startActivity(Intent(this@DetailStoryActivity, MainActivity::class.java))
        }
    }

    private suspend fun getDataFromIntent() {
        val dataId = intent.getStringExtra("id")

        if (dataId != null) {
            detailStoryModels.getDetailStory(dataId).observe(this){
                fetchDataToBind(it)
            }
        }
    }

    private fun fetchDataToBind(data: Story?) {
        if (data != null){
            bind.tvStoryTitleDetail.text = data.name
            Picasso.get().load(data.photoUrl).fit().into(bind.ivStoryPhoto)
            bind.tvStoryDescription.text = data.description
        }
    }
}