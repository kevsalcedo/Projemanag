package kesam.learning.projemanag.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowInsets
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import kesam.learning.projemanag.R
import kesam.learning.projemanag.adapters.TaskListItemsAdapter
import kesam.learning.projemanag.databinding.ActivityMyProfileBinding
import kesam.learning.projemanag.databinding.ActivityTaskListBinding
import kesam.learning.projemanag.firebase.FirestoreClass
import kesam.learning.projemanag.models.Board
import kesam.learning.projemanag.models.Card
import kesam.learning.projemanag.models.Task
import kesam.learning.projemanag.utils.Constants

class TaskListActivity : BaseActivity() {

    private var binding: ActivityTaskListBinding? = null

    // A global variable for Board Details.
    private lateinit var mBoardDetails: Board

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // Get the board documentId through intent.
        var boardDocumentId = ""
        if (intent.hasExtra(Constants.DOCUMENT_ID)) {
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID)!!
        }

        // Call the function to get the Board Details.
        // START
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this@TaskListActivity, boardDocumentId)

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
    }

    // Inflate the action menu for TaskListScreen and also launch the MembersActivity Screen on item selection
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu to use in the action bar
        menuInflater.inflate(R.menu.menu_members, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.action_members -> {

                startActivity(Intent(this@TaskListActivity, MembersActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupActionBar() {

        setSupportActionBar(binding?.toolbarTaskListActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = mBoardDetails.name
        }

        binding?.toolbarTaskListActivity?.setNavigationOnClickListener {
            onBackPressed() }
    }

    /**
     * A function to get the result of Board Detail.
     */
    fun boardDetails(board: Board) {

        // Initialize and Assign the value to the global variable for Board Details.
        // After replace the parameter variable with global so from onwards the global variable will be used.
        mBoardDetails = board

        hideProgressDialog()

        // Call the function to setup action bar.
        setupActionBar()


        // Here we are appending an item view for adding a list task list for the board.
        val addTaskList = Task(resources.getString(R.string.add_list))
        board.taskList.add(addTaskList)

        binding?.rvTaskList?.layoutManager =
            LinearLayoutManager(this@TaskListActivity, LinearLayoutManager.HORIZONTAL, false)
        binding?.rvTaskList?.setHasFixedSize(true)

        // Create an instance of TaskListItemsAdapter and pass the task list to it.
        val adapter = TaskListItemsAdapter(this@TaskListActivity, board.taskList)
        binding?.rvTaskList?.adapter = adapter // Attach the adapter to the recyclerView.
    }

    /**
     * A function to update the taskList
     */
    fun updateTaskList(position: Int, listName: String, model: Task) {

        val task = Task(listName, model.createdBy)

        mBoardDetails.taskList[position] = task
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@TaskListActivity, mBoardDetails)
    }

    /**
     * A function to delete the task list from database.
     */
    fun deleteTaskList(position: Int){

        mBoardDetails.taskList.removeAt(position)

        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@TaskListActivity, mBoardDetails)
    }

    /**
     * A function to get the result of add or updating the task list.
     */
    fun addUpdateTaskListSuccess() {

        hideProgressDialog()

        // Here get the updated board details.
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this@TaskListActivity, mBoardDetails.documentId)
    }

    fun createTaskList(taskListName: String) {

        Log.e("Task List Name", taskListName)

        // Create and Assign the task details
        val task = Task(taskListName, FirestoreClass().getCurrentUserID())

        mBoardDetails.taskList.add(0, task) // Add task to the first position of ArrayList

        // Remove the last position as we have added the item manually for adding the TaskList.
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().addUpdateTaskList(this@TaskListActivity, mBoardDetails)
    }

    /**
     * A function to create a card and update it in the task list.
     */
    fun addCardToTaskList(position: Int, cardName: String) {

        // Remove the last item
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        val cardAssignedUsersList: ArrayList<String> = ArrayList()
        cardAssignedUsersList.add(FirestoreClass().getCurrentUserID())

        val card = Card(cardName, FirestoreClass().getCurrentUserID(), cardAssignedUsersList)

        val cardsList = mBoardDetails.taskList[position].cards
        cardsList.add(card)

        val task = Task(
            mBoardDetails.taskList[position].title,
            mBoardDetails.taskList[position].createdBy,
            cardsList
        )

        mBoardDetails.taskList[position] = task

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@TaskListActivity, mBoardDetails)
    }


}