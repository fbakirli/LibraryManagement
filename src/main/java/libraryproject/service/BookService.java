package libraryproject.service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import libraryproject.entity.Book;
import libraryproject.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Tag(name = "Book Service", description = "Service for managing books")
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Operation(summary = "Fetch all books", description = "Retrieves a list of all books in the library")
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Operation(summary = "Find a book by ID", description = "Retrieves a book using its unique ID")
    public Book findById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    @Operation(summary = "Save or update a book", description = "Saves a new book or updates an existing one")
    public Book save(Book book) {
        return bookRepository.save(book);
    }

    @Operation(summary = "Delete a book by ID", description = "Removes a book from the library using its ID")
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    @Operation(summary = "Update book image", description = "Updates the image path of a book")
    public void updateBookImage(Long bookId, String imagePath) {
        Book book = findBookByIdOrThrow(bookId);
        book.setImagePath(imagePath);
        bookRepository.save(book);
    }

    @Operation(summary = "Find books by category ID", description = "Retrieves books that belong to a specific category")
    public List<Book> findBooksByCategoryId(Long categoryId) {
        return bookRepository.findByCategoryId(categoryId);
    }

    private Book findBookByIdOrThrow(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with ID: " + bookId));
    }
}