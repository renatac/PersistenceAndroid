package com.example.persitenceandnetwork

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import androidx.security.crypto.MasterKeys
import java.io.ByteArrayOutputStream
import java.io.File

class MainActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        recorderFileWithJetpackSecurity()

        readerFileWithJetpackSecurity()

        sharedDirectoryOnAndroid9OrHigher()
    }

    //getExternalStoragePublicDirectory is deprecated, como solucionar isso?        ?????????????????????????????????????
    private fun sharedDirectoryOnAndroid9OrHigher() {
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "albumName"
        )
        if (!file?.mkdirs()) {
            Log.e(MainActivity.TAG, "Directory not created")
        }
    }

    private fun recorderFileWithJetpackSecurity() {
        //Gravando arquivos com o Jetpack Security que faz parte do android Jetpack
        //As chaves mestras são usadas para criptografar chaves de criptografia de dados para criptografar
        //arquivos e preferências.
        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)
        val fileToWrite = "my_sensitive_data.txt"
        val encryptedFile = EncryptedFile.Builder(
            File(Environment.DIRECTORY_DOCUMENTS, fileToWrite),
            applicationContext,
            masterKeyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()
        encryptedFile.openFileOutput().bufferedWriter().use { writer ->
            writer.write("My super secret information")
        }
    }

    private fun readerFileWithJetpackSecurity() {
        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        val masterKeysAlias = MasterKeys.getOrCreate(keyGenParameterSpec)
        val fileToRead = "my_sensitive_data.txt"
        //Por que colocou esse byteStream?          ??????????????????????????????????????????????????????????????
        lateinit var byteStream: ByteArrayOutputStream
        val encryptedFile = EncryptedFile.Builder(
            File(Environment.DIRECTORY_DOWNLOADS, fileToRead),
            applicationContext,
            masterKeysAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()
        val contents = encryptedFile.openFileInput().bufferedReader().useLines { lines ->
            lines.fold("") { working, line ->
                "$working\n$line"
            }
        }
        Log.e("teste", contents)
    }


}