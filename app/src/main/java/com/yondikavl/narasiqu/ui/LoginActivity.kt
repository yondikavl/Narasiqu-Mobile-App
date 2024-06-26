package com.yondikavl.narasiqu.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.yondikavl.narasiqu.databinding.ActivityLoginBinding
import com.yondikavl.narasiqu.data.remote.request.LoginRequest
import com.yondikavl.narasiqu.viewModels.LoginViewModels
import com.yondikavl.narasiqu.viewModels.ViewModelsFactory
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var bind: ActivityLoginBinding
    private val loginViewModel by viewModels<LoginViewModels> {
        ViewModelsFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(bind.root)

        getLoginData()
        animation()
    }

    private fun getLoginData() {
        bind.btnLogin.setOnClickListener {
            val dataEmail = bind.etEmail.text.toString()
            val dataPassword = bind.etPassword.text.toString()

            when {
                dataEmail.isEmpty() -> errorMessage("Masukkan email ...")
                dataPassword.isEmpty() -> errorMessage("Masukkan password ...")
                else -> {
                    hideOrshowLoading(true)
                    disableButton()
                    getResponse(dataEmail, dataPassword)
                }
            }
        }
    }

    private fun disableButton() {
        bind.btnLogin.isEnabled = !bind.pbLoading.isVisible
        bind.btnLogin.isClickable = !bind.pbLoading.isVisible
    }

    private fun getResponse(dataEmail: String, dataPass: String) {
        val inputLogin = LoginRequest(dataEmail, dataPass)
        lifecycleScope.launch {
            try {
                loginViewModel.postLogin(inputLogin)
                loginViewModel.getSession().observe(this@LoginActivity){
                    if (it.isLogin){
                        hideOrshowLoading(false)
                        notifSuccess()
                    } else {
                        hideOrshowLoading(false)
                        disableButton()
                        notifFailed()
                    }
                }
            } catch (e: Exception) {
                hideOrshowLoading(false)
                disableButton()
                notifFailed()
            }
        }
    }

    private fun hideOrshowLoading(i: Boolean) {
        bind.pbLoading.isVisible = i
    }

    private fun notifFailed() {
        AlertDialog.Builder(this).apply {
            setTitle("Gagal")
            setMessage("Kamu gagal login:(")
            setPositiveButton("Kembali") { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }
    }

    private fun notifSuccess() {
        AlertDialog.Builder(this).apply {
            setTitle("Berhasil")
            setMessage("Kamu berhasil login:)")
            setPositiveButton("Lanjut") { _, _ ->
                val i = Intent(context, MainActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(i)
                finish()
            }
            create()
            show()
        }
    }

    private fun errorMessage(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }

    private fun animation() {
        ObjectAnimator.ofFloat(bind.imgLoginLogo, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 1000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(bind.tvTitleLogin, View.ALPHA, 1f).setDuration(250)
        val tvEmailLabel =
            ObjectAnimator.ofFloat(bind.tvEmailLabel, View.ALPHA, 1f).setDuration(250)
        val txtInputEmail =
            ObjectAnimator.ofFloat(bind.txtInputEmail, View.ALPHA, 1f).setDuration(250)
        val tvPasswordLabel =
            ObjectAnimator.ofFloat(bind.tvPasswordLabel, View.ALPHA, 1f).setDuration(250)
        val txtInputPassword =
            ObjectAnimator.ofFloat(bind.txtInputPassword, View.ALPHA, 1f).setDuration(250)
        val btnLogin = ObjectAnimator.ofFloat(bind.btnLogin, View.ALPHA, 1f).setDuration(250)

        AnimatorSet().apply {
            playSequentially(
                title,
                tvEmailLabel,
                txtInputEmail,
                tvPasswordLabel,
                txtInputPassword,
                btnLogin
            )
            startDelay = 250
        }.start()
    }
}