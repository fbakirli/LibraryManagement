package libraryproject.service;

import libraryproject.entity.Book;
import libraryproject.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // Fetch all books
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    // Find a book by ID
    public Book findById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    // Save or update a book
    public Book save(Book book) {
        return bookRepository.save(book);
    }

    // Delete a book by ID
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    // New method to update the image path only
    public void updateBookImage(Long bookId, String imagePath) {
        Book book = findBookByIdOrThrow(bookId);
        book.setImagePath(imagePath);
        bookRepository.save(book);
    }

    // New method to find books by category ID
    public List<Book> findBooksByCategoryId(Long categoryId) {
        return bookRepository.findByCategoryId(categoryId);
    }

    // Utility method to get book or throw an exception
    private Book findBookByIdOrThrow(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with ID: " + bookId));
    }
}