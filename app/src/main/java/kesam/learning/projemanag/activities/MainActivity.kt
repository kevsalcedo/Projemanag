package kesam.learning.projemanag.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowInsets
import android.view.WindowManager
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kesam.learning.projemanag.R
import kesam.learning.projemanag.databinding.ActivityMainBinding
import kesam.learning.projemanag.databinding.NavHeaderMainBinding
import kesam.learning.projemanag.firebase.FirestoreClass
import kesam.learning.projemanag.models.User


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener{

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
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

        binding?.navView?.setNavigationItemSelectedListener(this)

        binding?.appBarMainLayout?.fabCreateBoard?.setOnClickListener {
            val intent = Intent(this, CreateBoardActivity::class.java)
            startActivity(intent)
        }

        // Get the current logged in user details.
        FirestoreClass().loadUserData(this@MainActivity)
    }

    /**
     * A function for actionBar Setup.
     */
    private fun setupActionBar() {

        setSupportActionBar(binding?.appBarMainLayout?.toolbarMainActivity)
        binding?.appBarMainLayout?.toolbarMainActivity?.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        binding?.appBarMainLayout?.toolbarMainActivity?.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    /**
     * A function for opening and closing the Navigation Drawer.
     */
    private fun toggleDrawer() {

        if (binding?.drawerLayout?.isDrawerOpen(GravityCompat.START) == true){
            binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        }else{
            binding?.drawerLayout?.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if (binding?.drawerLayout?.isDrawerOpen(GravityCompat.START) == true){
            binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        } else {
            // A double back press function is added in Base Activity.
            doubleBackToExit()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        // Add the click events of navigation menu items
        when (item.itemId) {
            R.id.nav_my_profile -> {

                // Launch the MyProfileActivity Screen
                val intent = Intent(this, MyProfileActivity::class.java)
                startActivity(intent)
            }

            R.id.nav_sign_out -> {
                // Here sign outs the user from firebase in this device.
                FirebaseAuth.getInstance().signOut()

                // Send the user to the intro screen of the application.
                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        return true
    }

    fun updateNavigationUserDetails(user: User){
        // The instance of the header view of the navigation view.
        val headerView = binding?.navView?.getHeaderView(0)
        //val headerView = nav_view.getHeaderView(0)

        // The instance of the user image of the navigation view.
        val headerBinding  = headerView?.let { NavHeaderMainBinding.bind(it) }
        //val navUserImage = headerView?.findViewById<ImageView>(R.id.iv_user_image)

        headerBinding?.ivUserImage?.let {
            Glide
                .with(this)
                .load(user.image) // URL of the image
                .centerCrop() // Scale type of the image.
                .placeholder(R.drawable.ic_user_place_holder) // A default place holder
                .into(it)
        }


        /*
        // Load the user image in the ImageView.
        Glide
            .with(this@MainActivity)
            .load(user.image) // URL of the image
            .centerCrop() // Scale type of the image.
            .placeholder(R.drawable.ic_user_place_holder) // A default place holder
            .into(headerBinding) // the view in which the image will be loaded.

         */



        headerBinding?.tvUsername?.text = user.name

        // The instance of the user name TextView of the navigation view.
        //val navUsername = headerView?.findViewById<TextView>(R.id.tv_username)
        // Set the user name
        //navUsername?.text = user.name
    }


}