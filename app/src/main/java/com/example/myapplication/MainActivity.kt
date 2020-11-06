package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createAndDeleteFiles()

        useStreamToCreateFileInInternalDirectory()

        useStreamToAccessFileInInternalDirectory()

        useStreamToCreateFileInExternalDirectory()

        useStreamToAccessFileInExternalDirectory()

        isExternalStorageWritable()
        isExternalStorageReadable()
    }

    /* Verifica se o armazenamento externo está disponível para leitura e escrita */
    fun isExternalStorageWritable() = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

    /* Verifica se o armazenamento externo está disponível para, pelo menos, leitura */
    fun isExternalStorageReadable() = Environment.getExternalStorageState() in
            setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)


    private fun useStreamToAccessFileInExternalDirectory() {
        //Acessar arquivo no diretório externo com FileInputStream
        val externalStreamFileName = "externalStreamFileName"
        val bytes = ByteArray(13)

        val externalFile = File(applicationContext.getExternalFilesDir(null), externalStreamFileName)
        val inputStream = FileInputStream(externalFile)
        inputStream.use {
            it.read(bytes)
        }
        val fileContents = String(bytes)
    }

    private fun useStreamToCreateFileInExternalDirectory() {
        //Criar arquivos no diretório externo com FileOutPutStream
        val externalStreamFileName = "fileName"
        val fileContents = "Hello wolrd"

        val externalStreamFile = File(applicationContext.getExternalFilesDir(null), externalStreamFileName)
        val outputStream = FileOutputStream(externalStreamFile)
        outputStream.use { stream ->
            stream.write(fileContents.toByteArray())
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun useStreamToAccessFileInInternalDirectory() {
        //Acessar arquivo no diretório interno com openFileInput()
        val streamFileName = "streamFileName"
        var fileContent = ""
        applicationContext.openFileInput(streamFileName).use {
            fileContent = it.readBytes().decodeToString()
        }
        Log.d("teste", "Content file is $fileContent")

    }

    private fun useStreamToCreateFileInInternalDirectory() {
        //Criando arquivos no diretório interno com openFileOutput()
        val streamFileName = "streamFileName"
        val fileContents = "Hello wolrd"

        applicationContext.openFileOutput(streamFileName, Context.MODE_PRIVATE).use {
            it.write(fileContents.toByteArray())
        }
    }

    private fun createAndDeleteFiles() {
        /*Armazenamento interno - data/data/app_name*/
        /*Armazenamento externo - sdcard*/

        val internalFileName = "internalFileName.txt"
        val externalFileName = "externalFileName.txt"
        val cacheInternalFileName = "cacheInternalFileName.txt"
        val cacheExternalFileName = "cacheExternalFileName.txt"

        //Acessando diretórios
        //Criando arquivos

        //Diretório Interno
        val internalFile = File(applicationContext.filesDir, internalFileName)
        internalFile.createNewFile()
        internalFile.delete()
        //applicationContext.deleteFile(internalFileName)

        //Diretório Externo
        val externalFile = File(applicationContext.getExternalFilesDir(null), externalFileName)
        externalFile.createNewFile()
        externalFile.delete()
        //applicationContext.deleteFile(externalFileName)

        //Diretório de Cache interno
        val cacheInternalFile = File(applicationContext.cacheDir, cacheInternalFileName)
        cacheInternalFile.createNewFile()
        cacheInternalFile.delete()
        //applicationContext.deleteFile(cacheInternalFileName)

        //Dúvida como apagar um diretório cache criado desta forma?
        //Diretório de Cache interno
        File.createTempFile(cacheInternalFileName, null, applicationContext.cacheDir)
        //cacheInternalFile.delete()
        //applicationContext.deleteFile(cacheInternalFile)

        //Diretório de Cache externo
        val cacheExternalFile = File(applicationContext.externalCacheDir, cacheExternalFileName)
        cacheExternalFile.createNewFile()
        cacheExternalFile.delete()
        //applicationContext.deleteFile(cacheExternalFileName)

        //Dúvida como apagar um diretório cache criado desta forma?
        File.createTempFile(cacheExternalFileName, null, applicationContext.cacheDir)
        //cacheInternalFile.delete()
        //applicationContext.deleteFile(cacheInternalFile)

        //Ver lista de arquivos do diretório interno
        val internalDirectoryFilesList: Array<String> = applicationContext.fileList()
        internalDirectoryFilesList.forEachIndexed { index, it ->
            Log.i("teste", "$index - $it}")
        }
    }
}