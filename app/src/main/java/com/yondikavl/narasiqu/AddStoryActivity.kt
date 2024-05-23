package com.yondikavl.narasiqu

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.yondikavl.narasiqu.databinding.ActivityAddStoryBinding
import com.yondikavl.narasiqu.viewModels.AddStoryModels
import com.yondikavl.narasiqu.viewModels.ViewModelsFactory
import com.squareup.picasso.Picasso
import id.zelory.compressor.Compressor
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

    private val addStoryModel by viewModels<AddStoryModels> {
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
            file = File(path!!)
            handleContent(file!!)
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
                file = File(path!!)
                handleContent(file!!)
            }
        }
    }

    private fun handleContent(file: File) {
        val compressFile = Compressor(this).compressToFile(file)
        uploadContent(compressFile)
    }

    private fun getPathFromUri(context: Context, it: Uri): String? {
        var realPath: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(it, projection, null, null, null)
        cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            realPath = it.getString(columnIndex)
        }
        return realPath
    }

    private fun getImageUri(context: Context): Uri? {
        var uri: Uri? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "$timeStamp.jpeg")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/MyCamera/")
            }
            uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
        }
        return uri
    }

    private fun pesanError(s: String) {
        Toast.makeText(this@AddStoryActivity, s, Toast.LENGTH_SHORT).show()
    }

    private fun uploadContent(poto: File) {
        bind.btnUpload.setOnClickListener {
            handleUpload(poto)
        }
    }

    private fun handleUpload(poto: File) {

        val dataDesc = bind.etDesc.text.toString()
        when {
            dataDesc.isEmpty() -> pesanError("Masukkan deskripsi...")
            !poto.exists() -> pesanError("Masukkan foto...")
            else -> {
                val desc = dataDesc.toRequestBody("text/plain".toMediaType())
                val photo = poto.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart: MultipartBody.Part
                        = MultipartBody.Part.createFormData("photo", poto.name, photo)

                lifecycleScope.launch {
                    try {
                        addStoryModel.postStory(desc, imageMultipart)

                        pesanError("Berhasil mengunggah...")
                        val i = Intent(this@AddStoryActivity, MainActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(i)
                        finish()
                    } catch (e: Exception){
                        pesanError("Gagal Mengunggah... ${e.message}")
                    }
                }
            }
        }
    }

    companion object {
        private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    }
}