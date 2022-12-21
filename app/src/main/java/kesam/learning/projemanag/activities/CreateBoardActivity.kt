package kesam.learning.projemanag.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kesam.learning.projemanag.R
import kesam.learning.projemanag.databinding.ActivityCreateBoardBinding
import kesam.learning.projemanag.databinding.ActivityMainBinding
import kesam.learning.projemanag.firebase.FirestoreClass
import kesam.learning.projemanag.models.Board
import kesam.learning.projemanag.utils.Constants
import java.io.IOException

class CreateBoardActivity : BaseActivity() {

    // Add a global variable for URI of a selected image from phone storage.
    private var mSelectedImageFileUri : Uri? = null

    private var binding: ActivityCreateBoardBinding? = null

    // Create a global variable for User name
    private lateinit var mUserName: String

    // A global variable for a board image URL
    private var mBoardImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // This is used to hide the status bar and make the splash screen as a full screen activity.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        setupActionBar()

        // Get the username from the intent.
        if (intent.hasExtra(Constants.NAME)) {
            mUserName = intent.getStringExtra(Constants.NAME)!!
        }

        binding?.ivBoardImage?.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this@CreateBoardActivity)
            }else{
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        binding?.btnCreate?.setOnClickListener {
            if (mSelectedImageFileUri != null){
                uploadBoardImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                createBoard()
            }
        }

    }

    /**
     * A function for actionBar Setup.
     */
    private fun setupActionBar() {

        setSupportActionBar(binding?.toolbarCreateBoardActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        binding?.toolbarCreateBoardActivity?.setNavigationOnClickListener {
            onBackPressed() }

        actionBar?.title = resources.getString(R.string.create_board_title)
    }

    // Check the result of runtime permission after the user allows or deny based on the unique code.
    /**
     * This function will identify the result of runtime permission after the user allows or deny permission based on the unique code.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            // Call the image chooser function.
            Constants.showImageChooser(this@CreateBoardActivity)
        }else{
            //Displaying another toast if permission is not granted
            Toast.makeText(
                this,
                "Oops, you just denied the permission for storage. You can also allow it from settings.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // Get the result of the image selection based on the constant code.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && requestCode == Constants.PICK_IMAGE_REQUEST_CODE
            && data!!.data != null
        ) {
            // The uri of selection image from phone storage.
            mSelectedImageFileUri = data.data

            try {
                // Load the user image in the ImageView.
                binding?.ivBoardImage?.let {
                    Glide
                        .with(this@CreateBoardActivity)
                        .load(Uri.parse(mSelectedImageFileUri.toString())) // URI of the image
                        .centerCrop() // Scale type of the image.
                        .placeholder(R.drawable.ic_board_place_holder) // A default place holder
                        .into(it) // the view in which the image will be loaded. (Context: it = ivBoardImage)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * A function for notifying the board is created successfully.
     */
    fun boardCreatedSuccessfully() {

        hideProgressDialog()

        setResult(Activity.RESULT_OK)

        finish()
    }

    private fun createBoard() {
        //  A list is created to add the assigned members.
        //  This can be modified later on as of now the user itself will be the member of the board.
        val assignedUsersArrayList: ArrayList<String> = ArrayList()
        assignedUsersArrayList.add(getCurrentUserID()) // adding the current user id.

        // Creating the instance of the Board and adding the values as per parameters.
        val board = Board(
            binding?.etBoardName?.text.toString(),
            mBoardImageURL,
            mUserName,
            assignedUsersArrayList
        )

        FirestoreClass().createBoard(this@CreateBoardActivity, board)
    }

    private fun uploadBoardImage() {
        showProgressDialog(resources.getString(R.string.please_wait))

        //getting the storage reference
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            "BOARD_IMAGE" + System.currentTimeMillis() + "."
                    + Constants.getFileExtension(this@CreateBoardActivity, mSelectedImageFileUri)
        )

        //adding the file to reference
        sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener { taskSnapshot ->
            // The image upload is success
            Log.e(
                "Board Image URL",
                taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
            )

            // Get the downloadable url from the task snapshot
            taskSnapshot.metadata!!.reference!!.downloadUrl
                .addOnSuccessListener { uri ->
                    Log.e("Downloadable Image URL", uri.toString())

                    // assign the image url to the variable.
                    mBoardImageURL = uri.toString()

                    // Call a function to create the board.
                    createBoard()
                }
        }.addOnFailureListener { exception ->
            Toast.makeText(
                this@CreateBoardActivity,
                exception.message,
                Toast.LENGTH_LONG
                ).show()

            hideProgressDialog()
        }
    }


}