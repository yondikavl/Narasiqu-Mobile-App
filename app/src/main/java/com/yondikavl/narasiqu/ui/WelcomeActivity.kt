package com.yondikavl.narasiqu.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import com.yondikavl.narasiqu.databinding.ActivityWelcomeBinding
import com.yondikavl.narasiqu.viewModels.MainModels
import com.yondikavl.narasiqu.viewModels.ViewModelsFactory

class WelcomeActivity : AppCompatActivity() {
    private lateinit var bind: ActivityWelcomeBinding
    private val listStoryViewModel by viewModels<MainModels> {
        ViewModelsFactory.getInstance(this)
    }
    override fun onStart() {
        super.onStart()
        listStoryViewModel.getSession().observe(this){
            if (it.isLogin){
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(bind.root)

        fullscreenApp()
        buttonIntent()
        animation()
    }

    private fun animation() {
        ObjectAnimator.ofFloat(bind.imgWelcome, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 1000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(bind.tvTitle, View.ALPHA, 1f).setDuration(300)
        val desc = ObjectAnimator.ofFloat(bind.tvDesc, View.ALPHA, 1f).setDuration(300)
        val login = ObjectAnimator.ofFloat(bind.btnLogin, View.ALPHA, 1f).setDuration(300)
        val signup = ObjectAnimator.ofFloat(bind.btnRegister, View.ALPHA, 1f).setDuration(300)

        val together = AnimatorSet().apply {
            playTogether(login, signup)
        }

        AnimatorSet().apply {
            playSequentially(title, desc, together)
            start()
        }
    }

    private fun buttonIntent() {
        bind.btnLogin.setOnClickListener {
            startActivity(Intent(this@WelcomeActivity, LoginActivity::class.java))
        }
        bind.btnRegister.setOnClickListener {
            startActivity(Intent(this@WelcomeActivity, RegisterActivity::class.java))
        }
    }

    private fun fullscreenApp() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }
}