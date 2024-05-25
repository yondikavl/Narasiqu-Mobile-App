package com.yondikavl.narasiqu

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.yondikavl.narasiqu.api.BaseApi
import com.yondikavl.narasiqu.data.UserPreference
import com.yondikavl.narasiqu.data.dataStore
import com.yondikavl.narasiqu.databinding.ActivityRegisterBinding
import com.yondikavl.narasiqu.models.RequestRegister
import com.yondikavl.narasiqu.models.ResponseRegister
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
                dataName.isEmpty() -> pesanError("Masukkan nama ...")
                dataEmail.isEmpty() -> pesanError("Masukkan email ...")
                dataPassword.isEmpty() -> pesanError("Masukkan password ...")
                else -> {
                    hideOrshowLoading(View.VISIBLE)
                    disableBtn()
                    handleRegister(dataName, dataEmail, dataPassword)
                }
            }
        }
    }

    private fun disableBtn() {
        bind.btnSignup.isEnabled = !bind.pbLoading.isVisible
        bind.btnSignup.isClickable = !bind.pbLoading.isVisible
    }

    private fun handleRegister(dataNama: String, dataEmail: String, dataPass: String) {
        val dataRegister = RequestRegister(dataNama, dataEmail, dataPass)
        val callApi = BaseApi().getApiService(pref = UserPreference.getInstance(dataStore)).postRegister(dataRegister)

        callApi.enqueue(object : Callback<ResponseRegister> {
            override fun onResponse(
                call: Call<ResponseRegister>,
                response: Response<ResponseRegister>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    when {
                        responseBody == null -> {
                            hideOrshowLoading(View.GONE)
                            disableBtn()
                            pesanError("Data masih kosong...")
                        }
                        responseBody.error == true -> {
                            hideOrshowLoading(View.GONE)
                            disableBtn()
                            val errorMessage = responseBody.message ?: "404 Error"
                            pesanError("Pesan Error = $errorMessage")
                        }
                        else -> {
                            hideOrshowLoading(View.GONE)
                            pesanError(responseBody.message!!)
                            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                            finish()
                        }
                    }
                } else {
                    hideOrshowLoading(View.GONE)
                    disableBtn()
                    pesanError("Pesan Error = ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ResponseRegister>, t: Throwable) {
                println("Error ${t.message}")
            }
        })
    }

    private fun hideOrshowLoading(i: Int) {
        bind.pbLoading.visibility = i
    }

    private fun pesanError(s: String) {
        Toast.makeText(this@RegisterActivity, s, Toast.LENGTH_SHORT).show()
    }

    private fun animation() {
        ObjectAnimator.ofFloat(bind.imgLogo, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val tvTitle = ObjectAnimator.ofFloat(bind.tvTitle, View.ALPHA, 1f).setDuration(200)
        val tvName = ObjectAnimator.ofFloat(bind.tvName, View.ALPHA, 1f).setDuration(200)
        val txtInputName = ObjectAnimator.ofFloat(bind.txtInputName, View.ALPHA, 1f).setDuration(200)
        val tvEmail = ObjectAnimator.ofFloat(bind.tvEmail, View.ALPHA, 1f).setDuration(200)
        val txtInputEmail = ObjectAnimator.ofFloat(bind.txtInputEmail, View.ALPHA, 1f).setDuration(200)
        val tvPassword = ObjectAnimator.ofFloat(bind.tvPassword, View.ALPHA, 1f).setDuration(200)
        val txtInputPassword = ObjectAnimator.ofFloat(bind.txtInputPassword, View.ALPHA, 1f).setDuration(200)
        val btnSignup = ObjectAnimator.ofFloat(bind.btnSignup, View.ALPHA, 1f).setDuration(200)

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
            startDelay = 200
        }.start()
    }
}