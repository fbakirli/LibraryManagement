package libraryproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import libraryproject.entity.Author;
import libraryproject.entity.Book;
import libraryproject.entity.Category;
import libraryproject.service.AuthorService;
import libraryproject.service.BookService;
import libraryproject.service.CategoryService;
import libraryproject.service.S3Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/books")
@Tag(name = "Book Controller", description = "APIs for managing books")
public class BookController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final CategoryService categoryService;
    private final S3Service s3Service;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    public BookController(BookService bookService, AuthorService authorService, CategoryService categoryService, S3Service s3Service) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.categoryService = categoryService;
        this.s3Service = s3Service;
    }

    @GetMapping
    @Operation(summary = "List all books", description = "Fetches all books with author details")
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
    @Operation(summary = "Show create book form")
    public String createBookForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "books/create";
    }

    @PostMapping("/create")
    @Operation(summary = "Create a new book", description = "Creates a new book with selected authors, category, and uploaded image")
    public String createBook(@ModelAttribute Book book,
                             @RequestParam("file") MultipartFile file,
                             @RequestParam("authorIds") List<Long> authorIds,
                             @RequestParam("categoryId") Long categoryId) {
        if (!file.isEmpty()) {
            String s3Url = s3Service.uploadFile(file);
            book.setImagePath(s3Url);
        }

        Set<Author> authors = new HashSet<>(authorService.findAuthorsByIds(authorIds));
        if (authors.isEmpty()) {
            return "redirect:/books/create?error=No authors selected";
        }
        book.setAuthors(authors);

        Category category = categoryService.getCategoryById(categoryId).orElse(null);
        if (category == null) {
            return "redirect:/books/create?error=Category not found";
        }
        book.setCategory(category);

        bookService.save(book);
        return "redirect:/books";
    }

    @GetMapping("/edit/{id}")
    @Operation(summary = "Show edit book form")
    public String editBookForm(@PathVariable Long id, Model model) {
        Book book = bookService.findById(id);
        if (book == null) {
            return "redirect:/books";
        }
        model.addAttribute("book", book);
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "books/edit";
    }

    @PostMapping("/edit/{id}")
    @Operation(summary = "Edit an existing book")
    public String editBook(@PathVariable Long id,
                           @ModelAttribute Book book,
                           @RequestParam("authorIds") List<Long> authorIds,
                           @RequestParam("categoryId") Long categoryId,
                           @RequestParam("file") MultipartFile file) {

        Book existingBook = bookService.findById(id);
        if (existingBook == null) {
            return "redirect:/books";
        }

        if (!file.isEmpty()) {
            String s3Url = s3Service.uploadFile(file);
            book.setImagePath(s3Url);
        } else {
            book.setImagePath(existingBook.getImagePath());
        }

        Set<Author> authors = new HashSet<>(authorService.findAuthorsByIds(authorIds));
        if (authors.isEmpty()) {
            return "redirect:/books/edit/" + id + "?error=No authors selected";
        }
        book.setAuthors(authors);

        Category category = categoryService.getCategoryById(categoryId).orElse(null);
        if (category == null) {
            return "redirect:/books/edit/" + id + "?error=Category not found";
        }
        book.setCategory(category);

        book.setId(id);
        bookService.save(book);
        return "redirect:/books";
    }

    @GetMapping("/{id}")
    @Operation(summary = "View a book's details")
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
    @Operation(summary = "Delete a book by ID")
    public String deleteBook(@PathVariable Long id) {
        bookService.deleteById(id);
        return "redirect:/books";
    }

    @PostMapping("/upload-image/{id}")
    @Operation(summary = "Upload an image for a book")
    public String uploadBookImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        String fileName = s3Service.uploadFile(file);
        bookService.updateBookImage(id, fileName);
        return "redirect:/books";
    }
}
