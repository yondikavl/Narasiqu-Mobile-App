package com.yondikavl.narasiqu.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.yondikavl.narasiqu.network.BaseApi
import com.yondikavl.narasiqu.data.local.UserPreference
import com.yondikavl.narasiqu.data.local.dataStore
import com.yondikavl.narasiqu.databinding.ActivityRegisterBinding
import com.yondikavl.narasiqu.data.remote.request.RegisterRequest
import com.yondikavl.narasiqu.data.remote.response.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private lateinit var bind: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(bind.root)

        registerData()
        animation()
    }

    private fun registerData() {
        bind.btnSignup.setOnClickListener {
            val dataName = bind.etName.text.toString()
            val dataEmail = bind.etEmail.text.toString()
            val dataPassword = bind.etPassword.text.toString()

            when {
                dataName.isEmpty() -> errorMessage("Masukkan nama ...")
                dataEmail.isEmpty() -> errorMessage("Masukkan email ...")
                dataPassword.isEmpty() -> errorMessage("Masukkan password ...")
                else -> {
                    hideOrshowLoading(View.VISIBLE)
                    disableButton()
                    handleRegister(dataName, dataEmail, dataPassword)
                }
            }
        }
    }

    private fun disableButton() {
        bind.btnSignup.isEnabled = !bind.pbLoading.isVisible
        bind.btnSignup.isClickable = !bind.pbLoading.isVisible
    }

    private fun handleRegister(dataNama: String, dataEmail: String, dataPass: String) {
        val dataRegister = RegisterRequest(dataNama, dataEmail, dataPass)
        val callApi = BaseApi().getApiService(pref = UserPreference.getInstance(dataStore)).postRegister(dataRegister)

        callApi.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    when {
                        responseBody == null -> {
                            hideOrshowLoading(View.GONE)
                            disableButton()
                            errorMessage("Data masih kosong...")
                        }
                        responseBody.error == true -> {
                            hideOrshowLoading(View.GONE)
                            disableButton()
                            val errorMessage = responseBody.message ?: "404 Error"
                            errorMessage("Pesan Error = $errorMessage")
                        }
                        else -> {
                            hideOrshowLoading(View.GONE)
                            errorMessage(responseBody.message!!)
                            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                            finish()
                        }
                    }
                } else {
                    hideOrshowLoading(View.GONE)
                    disableButton()
                    errorMessage("Pesan Error = ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                println("Error ${t.message}")
            }
        })
    }

    private fun hideOrshowLoading(i: Int) {
        bind.pbLoading.visibility = i
    }

    private fun errorMessage(s: String) {
        Toast.makeText(this@RegisterActivity, s, Toast.LENGTH_SHORT).show()
    }

    private fun animation() {
        ObjectAnimator.ofFloat(bind.imgLogo, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 5000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val tvTitle = ObjectAnimator.ofFloat(bind.tvTitle, View.ALPHA, 1f).setDuration(250)
        val tvName = ObjectAnimator.ofFloat(bind.tvName, View.ALPHA, 1f).setDuration(250)
        val txtInputName = ObjectAnimator.ofFloat(bind.txtInputName, View.ALPHA, 1f).setDuration(250)
        val tvEmail = ObjectAnimator.ofFloat(bind.tvEmail, View.ALPHA, 1f).setDuration(250)
        val txtInputEmail = ObjectAnimator.ofFloat(bind.txtInputEmail, View.ALPHA, 1f).setDuration(250)
        val tvPassword = ObjectAnimator.ofFloat(bind.tvPassword, View.ALPHA, 1f).setDuration(250)
        val txtInputPassword = ObjectAnimator.ofFloat(bind.txtInputPassword, View.ALPHA, 1f).setDuration(250)
        val btnSignup = ObjectAnimator.ofFloat(bind.btnSignup, View.ALPHA, 1f).setDuration(250)

        AnimatorSet().apply {
            playSequentially(
                tvTitle,
                tvName,
                txtInputName,
                tvEmail,
                txtInputEmail,
                tvPassword,
                txtInputPassword,
                btnSignup
            )
            startDelay = 250
        }.start()
    }
}