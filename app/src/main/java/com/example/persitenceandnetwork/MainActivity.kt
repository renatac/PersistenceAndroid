package com.example.persitenceandnetwork

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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

        allocateInternalMemoryForUseAsExternalStorage()

        /*Para fins de teste, em dispositivos sem armazenamento externo removível, utilize o comando abaixo para habilitar um disco virtual*/
        //adb shell sm set-virtual-disk true

        storesMediaContentInSpecificAppDirectoriesOnExternalStorage()
    }

    private fun storesMediaContentInSpecificAppDirectoriesOnExternalStorage() {
        /*Conteúdo de mídia - para armazenar arquivos de mídia como documentos, imagens, aúdio, vídeo etc, que sejam úteis apenas dentro
        *do app, é recomendável armazená-los em diretórios específicos do app no armazenamento externo. */
        val albumName = "albumName"
        val file = File(
            applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "albumName"
        )
        if (!file.mkdirs()) {
            Log.e("HSS", "Directory not created")
        }
        //Devo usar nomes de diretórios fornecidos por constantes da API, como DIRECTORY_PICTURES, DIRECTORY_DOCUMENTS, DOCUMENTS_MOVIES
        //Esses nomes de diretório garantem que os arquivos sejam tratados de forma adequada pelo sistema.
    }

    private fun allocateInternalMemoryForUseAsExternalStorage() {
        //Um dispositivo alocando uma partição da própria memória interna para uso como armazenamento externo e também um slot
        //para cartão SD. O primeiro elemento retorna o volume de armazenamento externo principal.
        val externalStorageVolumes: Array<out File> =
            ContextCompat.getExternalFilesDirs(applicationContext, null)
        val primaryExternalStorage = externalStorageVolumes[0]
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