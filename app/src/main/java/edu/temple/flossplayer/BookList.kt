package edu.temple.flossplayer

class BookList {

    private val bookList : ArrayList<Book> by lazy {
        ArrayList()
    }

    fun add(book: Book) {
        bookList.add(book)
    }

    fun remove (book: Book) {
        bookList.remove(book)
    }

    fun clear() {
        bookList.clear()
    }

    operator fun get(index: Int) = bookList[index]

    fun size() = bookList.size
}