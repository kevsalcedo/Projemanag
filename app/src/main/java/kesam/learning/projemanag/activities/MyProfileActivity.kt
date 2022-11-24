package kesam.learning.projemanag.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import com.bumptech.glide.Glide
import kesam.learning.projemanag.R
import kesam.learning.projemanag.databinding.ActivityMainBinding
import kesam.learning.projemanag.databinding.ActivityMyProfileBinding
import kesam.learning.projemanag.firebase.FirestoreClass
import kesam.learning.projemanag.models.User

class MyProfileActivity : BaseActivity() {

    private var binding: ActivityMyProfileBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
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

        // Call a function to get the current logged in user details.
        FirestoreClass().loadUserData(this@MyProfileActivity)
    }

    private fun setupActionBar() {

        setSupportActionBar(binding?.toolbarMyProfileActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.my_profile)
        }

        binding?.toolbarMyProfileActivity?.setNavigationOnClickListener {
            onBackPressed() }
    }

    /**
     * A function to set the existing details in UI.
     */
    fun setUserDataInUI(user: User) {
        binding?.ivUserImage?.let {
            Glide
                .with(this@MyProfileActivity)
                .load(user.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(it)
        }

        binding?.etName?.setText(user.name)
        binding?.etEmail?.setText(user.email)
        if (user.mobile != 0L) {
            binding?.etMobile?.setText(user.mobile.toString())
        }
    }


}