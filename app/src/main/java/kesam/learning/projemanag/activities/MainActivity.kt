package kesam.learning.projemanag.activities

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kesam.learning.projemanag.R
import kesam.learning.projemanag.adapters.BoardItemsAdapter
import kesam.learning.projemanag.databinding.ActivityMainBinding
import kesam.learning.projemanag.databinding.NavHeaderMainBinding
import kesam.learning.projemanag.firebase.FirestoreClass
import kesam.learning.projemanag.models.Board
import kesam.learning.projemanag.models.User
import kesam.learning.projemanag.utils.Constants


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener{

    private var binding: ActivityMainBinding? = null

    // Create a global variable for user name
    private lateinit var mUserName: String

    private val boardLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == Activity.RESULT_OK){
            FirestoreClass().getBoardsList(this)
        }
    }

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

        // Get the current logged in user details.
        FirestoreClass().loadUserData(this@MainActivity, true)

        binding?.appBarMainLayout?.fabCreateBoard?.setOnClickListener {
            val intent = Intent(this, CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME, mUserName)
            //startActivityForResult(intent, Constants.CREATE_BOARD_REQUEST_CODE)
            //startActivity(intent)
            boardLauncher.launch(intent)
        }


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

    fun updateNavigationUserDetails(user: User, readBoardsList: Boolean){

        // Initialize the UserName variable.
        mUserName = user.name

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

        headerBinding?.tvUsername?.text = user.name

        // Here if the isToReadBoardList is TRUE then get the list of boards.
        if (readBoardsList){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardsList(this@MainActivity)
        }

        // The instance of the user name TextView of the navigation view.
        //val navUsername = headerView?.findViewById<TextView>(R.id.tv_username)
        // Set the user name
        //navUsername?.text = user.name
    }

    /**
     * A function to populate the result of BOARDS list in the UI i.e in the recyclerView.
     */
    fun populateBoardsListToUI(boardsList: ArrayList<Board>) {

        hideProgressDialog()

        val recyclerView = binding?.appBarMainLayout?.contentMainLayout?.rvBoardsList
        //val noBoardsAvailable = binding?.appBarMainLayout?.contentMainLayout?.tvNoBoardsAvailable

        if (boardsList.size > 0) {
            recyclerView?.visibility = View.VISIBLE

            // layout manager
            val layoutManager = LinearLayoutManager(this)
            recyclerView?.layoutManager = layoutManager
            recyclerView?.setHasFixedSize(true)

            // adapter
            val adapter = BoardItemsAdapter(this, boardsList)
            recyclerView?.adapter = adapter
            Log.i("POPUI:", "Board adapter size: ${adapter.itemCount}")

            // divider
            val dividerItemDecoration =
                DividerItemDecoration(recyclerView?.context, layoutManager.orientation)
            recyclerView?.addItemDecoration(dividerItemDecoration)

            //noBoardsAvailable?.visibility = View.GONE

            // Add click event for boards item and launch the TaskListActivity
            adapter.setOnClickListener(object :
                BoardItemsAdapter.OnClickListener {
                override fun onClick(position: Int, model: Board) {
                    startActivity(Intent(this@MainActivity, TaskListActivity::class.java))
                }
            })

        } else {
            recyclerView?.visibility = View.GONE
            //noBoardsAvailable?.visibility = View.VISIBLE
        }
    }


}