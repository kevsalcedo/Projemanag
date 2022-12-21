package kesam.learning.projemanag.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import kesam.learning.projemanag.activities.MyProfileActivity

object Constants{

    // Firebase Constants
    // This  is used for the collection name for USERS.
    const val USERS: String = "users"

    // This  is used for the collection name for USERS.
    const val BOARDS: String = "boards"

    // Firebase database field names
    const val IMAGE: String = "image"
    const val NAME: String = "name"
    const val MOBILE: String = "mobile"

    // Add a field name as assignedTo which we are gonna use later on
    const val ASSIGNED_TO: String = "assignedTo"

    //A unique code for asking the Read Storage Permission using this we will be check and identify in the method onRequestPermissionsResult
    const val READ_STORAGE_PERMISSION_CODE = 1

    // Add a constant for image selection from phone storage
    const val PICK_IMAGE_REQUEST_CODE = 2

    const val CREATE_BOARD_REQUEST_CODE: Int = 12


    /**
     * A function for user profile image selection from phone storage.
     */
    fun showImageChooser(activity: Activity) {
        // An intent for launching the image selection of phone storage.
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        // Launches the image selection of phone storage using the constant code.
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    /**
     * A function to get the extension of selected image.
     */
    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        /*
         * MimeTypeMap: Two-way map that maps MIME-types to file extensions and vice versa.
         *
         * getSingleton(): Get the singleton instance of MimeTypeMap.
         *
         * getExtensionFromMimeType: Return the registered extension for the given MIME type.
         *
         * contentResolver.getType: Return the MIME type of the given content URL.
         */
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}

