package edu.temple.flossplayer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton

class BookControlFragment : Fragment() {

    lateinit var bookPlay: Button
    lateinit var bookPause: Button
    private lateinit var bookViewModel : BookViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_book_control, container, false).apply {

            bookViewModel = ViewModelProvider(requireActivity())[BookViewModel::class.java]

            bookPause.findViewById<Button>(R.id.buttonPause)
            bookPlay.findViewById<Button>(R.id.buttonPlay)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bookPlay.setOnClickListener()
    }
}