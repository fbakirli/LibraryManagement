package libraryproject.controller;

import libraryproject.entity.Author;
import libraryproject.entity.Book;
import libraryproject.service.AuthorService;
import libraryproject.service.BookService;
import libraryproject.service.S3Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final S3Service s3Service;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    public BookController(BookService bookService, AuthorService authorService, S3Service s3Service) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.s3Service = s3Service;
    }

    @GetMapping
    public String listBooks(Model model) {
        List<Book> books = bookService.findAll();
        books.forEach(book -> {
            String authors = book.getAuthors().stream()
                    .map(Author::getName)
                    .collect(Collectors.joining(", "));
            book.setAuthorNames(authors);
        });
        model.addAttribute("books", books);
        return "books/list";
    }

    @GetMapping("/create")
    public String createBookForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("authors", authorService.findAll());
        return "books/create";
    }

    @PostMapping("/create")
    public String createBook(@ModelAttribute Book book,
                             @RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            String s3Url = s3Service.uploadFile(file); // ✅ Use S3 instead of local storage
            book.setImagePath(s3Url);
        }
        bookService.save(book);
        return "redirect:/books";
    }



    @GetMapping("/edit/{id}")
    public String editBookForm(@PathVariable Long id, Model model) {
        Book book = bookService.findById(id);
        if (book == null) {
            return "redirect:/books";
        }
        System.out.println("Book Image Path: " + book.getImagePath()); // Debugging
        model.addAttribute("book", book);
        model.addAttribute("authors", authorService.findAll());
        return "books/edit";
    }

    @PostMapping("/edit/{id}")
    public String editBook(
            @PathVariable Long id,
            @ModelAttribute Book book,
            @RequestParam("authorIds") List<Long> authorIds, // Ensure this matches the form field name
            @RequestParam("file") MultipartFile file) {

        Book existingBook = bookService.findById(id);
        if (existingBook == null) {
            return "redirect:/books";
        }

        // Update the image if a new file is uploaded
        if (!file.isEmpty()) {
            String s3Url = s3Service.uploadFile(file);
            book.setImagePath(s3Url);
        } else {
            // Retain the existing image if no new file is uploaded
            book.setImagePath(existingBook.getImagePath());
        }

        // Fetch the selected authors and set them in the book
        Set<Author> authors = new HashSet<>(authorService.findAuthorsByIds(authorIds));
        book.setAuthors(authors);

        // Set the ID to ensure the existing book is updated
        book.setId(id);

        // Save the updated book
        bookService.save(book);

        return "redirect:/books";
    }

    @GetMapping("/{id}")
    public String viewBook(@PathVariable Long id, Model model) {
        Book book = bookService.findById(id);
        if (book == null) {
            return "redirect:/books";
        }
        String authors = book.getAuthors().stream()
                .map(Author::getName)
                .collect(Collectors.joining(", "));
        book.setAuthorNames(authors);

        model.addAttribute("bucketName", bucketName);
        model.addAttribute("region", region);
        model.addAttribute("book", book);

        return "books/view";
    }

    @PostMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookService.deleteById(id);
        return "redirect:/books";
    }

    @PostMapping("/upload-image/{id}")
    public String uploadBookImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        String fileName = s3Service.uploadFile(file);
        bookService.updateBookImage(id, fileName);
        return "redirect:/books";
    }
}
