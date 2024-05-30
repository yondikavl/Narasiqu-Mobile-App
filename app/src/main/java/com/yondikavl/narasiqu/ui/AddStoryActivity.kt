package com.yondikavl.narasiqu.ui

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.yondikavl.narasiqu.databinding.ActivityAddStoryBinding
import com.yondikavl.narasiqu.viewModels.AddStoryViewModels
import com.yondikavl.narasiqu.viewModels.ViewModelsFactory
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddStoryActivity : AppCompatActivity() {

    private val addStoryModel by viewModels<AddStoryViewModels> {
        ViewModelsFactory.getInstance(this)
    }
    private lateinit var bind: ActivityAddStoryBinding
    private var imageUri: Uri? = null
    private var file: File? = null
    private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(bind.root)

        cameraOrGallery()

        bind.btnBack.setOnClickListener {
            startActivity(Intent(this@AddStoryActivity, MainActivity::class.java))
        }
    }

    private fun cameraOrGallery() {
        bind.btnGallery.setOnClickListener {
            val i = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            resultLauncherGallery.launch(i)
        }

        bind.btnCamera.setOnClickListener {
            imageUri = getImageUri(this)
            resultLauncherCamera.launch(imageUri)
        }
    }

    private val resultLauncherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) {
        if (it != null) {
            Picasso.get().load(it).fit().into(bind.ivPreview)
            val path = getPathFromUri(this, it)
            if (path != null) {
                file = File(path)
                handleContent(file!!)
            } else {
                errorMessage("Gagal mendapatkan jalur file")
                Log.e("AddStoryActivity", "Gagal mendapatkan jalur file dari URI: $it")
            }
        }
    }

    private val resultLauncherCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            imageUri?.let {
                println("Image Uri = $it")
                Picasso.get().load(it).fit().into(bind.ivPreview)
                val path = getPathFromUri(this, it)
                if (path != null) {
                    file = File(path)
                    handleContent(file!!)
                } else {
                    errorMessage("Gagal mendapatkan jalur file")
                    Log.e("AddStoryActivity", "Gagal mendapatkan jalur file dari URI: $it")
                }
            }
        }
    }

    private fun handleContent(file: File) {
        try {
            val compressFile = id.zelory.compressor.Compressor(this).compressToFile(file)
            uploadContent(compressFile)
        } catch (e: Exception) {
            errorMessage("Gagal mengompres file: ${e.message}")
            Log.e("AddStoryActivity", "Gagal mengompres file", e)
        }
    }

    private fun getPathFromUri(context: Context, it: Uri): String? {
        var realPath: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(it, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                realPath = it.getString(columnIndex)
            }
        }
        return realPath
    }

    private fun getImageUri(context: Context): Uri? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "$timeStamp.jpeg")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/MyCamera/")
            }
            context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
        } else {
            null
        }
    }

    private fun uploadContent(poto: File) {
        bind.btnUpload.setOnClickListener {
            handleUpload(poto)
        }
    }

    private fun handleUpload(poto: File) {
        val dataDesc = bind.etDesc.text.toString()
        when {
            dataDesc.isEmpty() -> errorMessage("Masukkan deskripsi...")
            !poto.exists() -> errorMessage("Masukkan foto...")
            else -> {
                val desc = dataDesc.toRequestBody("text/plain".toMediaType())
                val photo = poto.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData("photo", poto.name, photo)

                lifecycleScope.launch {
                    try {
                        addStoryModel.postStory(desc, imageMultipart)

                        errorMessage("Berhasil mengunggah...")
                        val i = Intent(this@AddStoryActivity, MainActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(i)
                        finish()
                    } catch (e: Exception) {
                        errorMessage("Gagal mengunggah... ${e.message}")
                        Log.e("AddStoryActivity", "Gagal mengunggah konten", e)
                    }
                }
            }
        }
    }

    private fun errorMessage(s: String) {
        Toast.makeText(this@AddStoryActivity, s, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    }
}
