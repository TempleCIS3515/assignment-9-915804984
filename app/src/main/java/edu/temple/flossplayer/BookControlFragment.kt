package edu.temple.flossplayer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider

class BookControlFragment : Fragment() {

    lateinit var bookPlay: Button
    lateinit var bookPause: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_book_control, container, false).apply {
            bookPause = findViewById(R.id.buttonPause)
            bookPlay = findViewById(R.id.buttonPlay)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bookPlay.setOnClickListener {
            (requireActivity() as controlInterface).bookPlay()
        }
        bookPause.setOnClickListener {
            (requireActivity() as controlInterface).bookPause()
        }
    }

    interface controlInterface
    {
        fun bookPause()
        fun bookPlay()
    }
}