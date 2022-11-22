package edu.temple.flossplayer

import android.os.Message
import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.*
import android.os.Handler
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import edu.temple.audlibplayer.PlayerService
import android.content.ContentValues.TAG
import android.os.IBinder
import android.util.Log

class MainActivity : AppCompatActivity(), BookControlFragment.controlInterface {

    private val searchURL = "https://kamorris.com/lab/flossplayer/search.php?query="
    private lateinit var playerBinder: PlayerService.MediaControlBinder
    lateinit var seekBar: SeekBar
    private var isBound = false
    private var activeBookID = -1
    private var progressTime = 0

    //onReceive
    private val receiver = object : BroadcastReceiver()
    {
        override fun onReceive(context: Context?, intent: Intent?)
        {
            if (intent?.action == "edu.temple.floss-player.SelectedBookProgress")
            {
                activeBookID = intent.getIntExtra("id", -1)
                progressTime = intent.getIntExtra("progress", 0)
            }
        }
    }

    //handleMessage
    private val handler = @SuppressLint("HandlerLeak")
    object : Handler()
    {
        override fun handleMessage(msg: Message)
        {
            val bProgress = (msg.obj as PlayerService.BookProgress)

            Intent().also{
                it.action = "edu.temple.floss-player.SelectedBookProgress"
                it.putExtra("id", (bProgress.book as PlayerService.FlossAudioBook).getBookId())
                it.putExtra("progress", bProgress.progress)
                sendBroadcast(it)
            }
        }
    }

    private val serviceConnection = object : ServiceConnection
    {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?)
        {
            playerBinder = (service as PlayerService.MediaControlBinder)
            playerBinder.setProgressHandler(handler)
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?)
        {
            Log.e(TAG, "onServiceDisconnected")
            isBound = false
        }
    }


    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(this)
    }

    private val isSingleContainer: Boolean by lazy {
        findViewById<View>(R.id.container2) == null
    }

    private val bookViewModel: BookViewModel by lazy {
        ViewModelProvider(this)[BookViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // If we're switching from one container to two containers
        // clear BookPlayerFragment from container1
        if (supportFragmentManager.findFragmentById(R.id.container1) is BookPlayerFragment) {
            supportFragmentManager.popBackStack()
        }

        // If this is the first time the activity is loading, go ahead and add a BookListFragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container1, BookListFragment())
                .commit()
        } else
        // If activity loaded previously, there's already a BookListFragment
        // If we have a single container and a selected book, place it on top
            if (isSingleContainer && bookViewModel.getSelectedBook()?.value != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container1, BookPlayerFragment())
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .commit()
            }

        // If we have two containers but no BookPlayerFragment, add one to container2
        if (!isSingleContainer && supportFragmentManager.findFragmentById(R.id.container2) !is BookPlayerFragment)
            supportFragmentManager.beginTransaction()
                .add(R.id.container2, BookPlayerFragment())
                .commit()


        // Respond to selection in portrait mode using flag stored in ViewModel
        bookViewModel.getSelectedBook()?.observe(this) {
            if (!bookViewModel.hasViewedSelectedBook()) {
                if (isSingleContainer) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container1, BookPlayerFragment())
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit()
                }
                bookViewModel.markSelectedBookViewed()
            }
        }

        findViewById<View>(R.id.searchImageButton).setOnClickListener {
            onSearchRequested()
        }

    }

    override fun onBackPressed() {
        // BackPress clears the selected book
        bookViewModel.clearSelectedBook()
        super.onBackPressed()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if (Intent.ACTION_SEARCH == intent!!.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also {
                searchBooks(it)

                // Unselect previous book selection
                bookViewModel.clearSelectedBook()

                // Remove any unwanted DisplayFragments instances from the stack
                supportFragmentManager.popBackStack()
            }
        }
    }

    private fun searchBooks(searchTerm: String) {
        requestQueue.add(
            JsonArrayRequest(searchURL + searchTerm,
                { bookViewModel.updateBooks(it) },
                { Toast.makeText(this, it.networkResponse.toString(), Toast.LENGTH_SHORT).show() })
        )
    }

    override fun bookPause()
    {
        if (playerBinder.isPlaying)
        {
            playerBinder.pause()
        }
    }

    override fun bookPlay()
    {
        if (bookViewModel.getSelectedBook() != null)
        {
            var currentBook = bookViewModel.getSelectedBook()?.value
            if (activeBookID == -1 || (currentBook as PlayerService.FlossAudioBook).getBookId() != activeBookID)
            {
                playerBinder.play(currentBook as PlayerService.FlossAudioBook)
            }
                else if (!playerBinder.isPlaying)
                {
                    playerBinder.pause()
                }
        }
    }
}