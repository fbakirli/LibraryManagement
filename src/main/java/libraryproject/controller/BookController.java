package libraryproject.controller;

import libraryproject.entity.Author;
import libraryproject.entity.Book;
import libraryproject.service.AuthorService;
import libraryproject.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    public BookController(BookService bookService, AuthorService authorService) {
        this.bookService = bookService;
        this.authorService = authorService;
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
        return "books/list"; // maps to books/list.html
    }

    @GetMapping("/create")
    public String createBookForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("authorIds", new ArrayList<>()); // For multi-select binding
        model.addAttribute("authors", authorService.findAll());
        return "books/create";
    }


    @PostMapping("/create")
    public String createBook(@ModelAttribute Book book, @RequestParam List<Long> authorIds) {
        Set<Author> authors = new HashSet<>(authorService.findAuthorsByIds(authorIds));
        book.setAuthors(authors);
        bookService.save(book);
        return "redirect:/books";
    }



    @GetMapping("/edit/{id}")
    public String editBookForm(@PathVariable Long id, Model model) {
        Book book = bookService.findById(id);
        if (book == null) {
            return "redirect:/books"; // handle book not found
        }
        model.addAttribute("book", book);
        model.addAttribute("authors", authorService.findAll());
        return "books/edit"; // maps to books/edit.html
    }

    @PostMapping("/edit/{id}")
    public String editBook(@PathVariable Long id, @ModelAttribute Book book, @RequestParam List<Long> authorIds) {
        Book existingBook = bookService.findById(id);
        if (existingBook == null) {
            return "redirect:/books"; // Handle case where book does not exist
        }
        Set<Author> authors = new HashSet<>(authorService.findAuthorsByIds(authorIds));
        book.setAuthors(authors);
        book.setId(id); // Ensure the ID remains unchanged
        bookService.save(book);
        return "redirect:/books";
    }


    @GetMapping("/{id}")
    public String viewBook(@PathVariable Long id, Model model) {
        Book book = bookService.findById(id);
        if (book == null) {
            return "redirect:/books"; // handle book not found
        }
        model.addAttribute("book", book);
        return "books/view"; // maps to books/view.html
    }

    @PostMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookService.deleteById(id);
        return "redirect:/books";
    }
}