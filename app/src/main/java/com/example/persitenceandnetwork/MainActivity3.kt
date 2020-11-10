package com.example.persitenceandnetwork

import android.content.ContentUris
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity


class MainActivity3 : AppCompatActivity() {

    lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        //Armazenamento compartilhado:
        /*<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
           android:maxSdkVersion="28" />*/

        //<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
        /* Apenas para acesso aos arquivos das coleções MediaStore.Images, MediaStore.Video, MediaStore.Audio.
         Se o app quiser acessar a coleção MediaStore.Downloads que ele criou usar o SAF. */


        //Acesso via ContentResolver: Não usamos File, usamos Uris, cada mídia tem um _ID

        //                                                   MediaStoreDictionary

        // Uri->
        //App <------------------> ContentResolver   <----->    ContentProvider
        // <-Cursor                                     MediaStore
        //\\
        //   |
        //   |
        //  \\//
        // Data source
        // MediaStore
        //ContentResolver - insert, update, delete e query

        //API MediaStore :
        /*  MediaStore.Images;
            MediaStore.Video;
            MediaStore.Audio;
            MediaStore.Downloads.
        */

        //val projection = arrayOf(media-database-columns-to-retrieve)
        //val selection = sql-where-clause-with-placeholder-variables
        //val selectionArgs = values-of-placeholder-variables
        //val sortOrder = sql-order-by-clause
        //applicationContext.contentResolver.query(
        //  MediaStore.media-type.Media.EXTERNAL_CONTENT_URI,
        //  projection,
        //  selection,
        //  selectionArgs,
        //  sortOrder
        //)?.use { cursor ->
        //   while (cursor.moveToNext()) {
        //Use an ID column from the projection to get
        //a URI representing the media item itself.
        //  }
        // }

        getImages()

        takingAnImageViaUri()

        saveOneImagePartOne()

    }

    private fun saveOneImagePartOne() {
        val name = "name"
        val bucketName = "fotosNiver"
        val values = ContentValues().apply { put(MediaStore.Images.Media.DISPLAY_NAME, name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/$bucketName/")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        imageUri = applicationContext.contentResolver.insert(collection, values)!!
        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)

        imageUri?.let {
            applicationContext.contentResolver.openOutputStream(it).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
        }
        values.clear()
        values.put(MediaStore.Images.Media.IS_PENDING, 0)
        imageUri?.let { applicationContext.contentResolver.update(it, values, null, null) }
    }

    private fun takingAnImageViaUri() : Any? {
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, imageUri))
        } else {
            contentResolver.openInputStream(imageUri)?.use { inputStream ->
            }
        }
        return bitmap
    }

    //Consultar uma coleção de mídia
    private fun getImages(): List<Image> {
        val list = mutableListOf<Image>()
        val projection = arrayOf(
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.DISPLAY_NAME
        )
        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null
        ).use {
            it?.let { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID)
                val displayNameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val displayName = cursor.getString(displayNameColumn)
                    val contentUri =
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    list.add(Image(id, displayName, contentUri))
                }
            }
        }
        return list
    }

    data class Image(
        var id: Long,
        var displayName: String,
        var contentUri: Uri
    )

}