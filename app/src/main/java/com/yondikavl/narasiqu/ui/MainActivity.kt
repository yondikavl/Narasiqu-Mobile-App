package com.yondikavl.narasiqu.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.yondikavl.narasiqu.adapter.LoadingStateAdapter
import com.yondikavl.narasiqu.adapter.PagingStoryAdapter
import com.yondikavl.narasiqu.databinding.ActivityMainBinding
import com.yondikavl.narasiqu.viewModels.MainModels
import com.yondikavl.narasiqu.viewModels.ViewModelsFactory
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val listStoryViewModel by viewModels<MainModels> {
        ViewModelsFactory.getInstance(this)
    }

    private lateinit var bind: ActivityMainBinding
    private val pagingStoryAdapter = PagingStoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)

        bind.btnMap.setOnClickListener {
            startActivity(Intent(this@MainActivity, MapsActivity::class.java))
        }
        bind.btnAddStory.setOnClickListener {
            startActivity(Intent(this@MainActivity, AddStoryActivity::class.java))
        }
        listStoryViewModel.getSession().observe(this) {
            val token = it.token
            if (token.isNotEmpty()){
                setAdapter()
            } else {
                startActivity(Intent(this@MainActivity, WelcomeActivity::class.java))
                finish()
            }
        }
        bind.btnLogout.setOnClickListener {
            listStoryViewModel.logout()
            val i = Intent(this@MainActivity, WelcomeActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
            finish()
        }
    }

    private fun setAdapter() {
        bind.rvStories.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = pagingStoryAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    pagingStoryAdapter.retry()
                }
            )
        }

        lifecycleScope.launch {
            listStoryViewModel.getAllStory().observe(this@MainActivity) {
                pagingStoryAdapter.submitData(lifecycle, it)
            }
        }
    }
}