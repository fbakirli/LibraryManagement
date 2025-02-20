package libraryproject.controller;

import libraryproject.entity.Book;
import libraryproject.entity.Category;
import libraryproject.service.BookService;
import libraryproject.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/website")
public class WebsiteController {

    @Autowired
    private BookService bookService;

    @Autowired
    private CategoryService categoryService;

    // Show all books
    @GetMapping("/books")
    public String showAllBooks(Model model) {
        List<Book> books = bookService.findAll();
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("books", books);
        model.addAttribute("categories", categories);
        return "website/books"; // Thymeleaf template to display all books
    }

    // Show books by category
    @GetMapping("/books/category/{id}")
    public String showBooksByCategory(@PathVariable Long id, Model model) {
        List<Book> books = bookService.findBooksByCategoryId(id);
        List<Category> categories = categoryService.getAllCategories();
        Category selectedCategory = categoryService.getCategoryById(id).orElse(null);

        model.addAttribute("books", books);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategory", selectedCategory);
        return "website/books"; // Thymeleaf template to display books by category
    }

    // Show book details
    @GetMapping("/books/{id}")
    public String showBookDetails(@PathVariable Long id, Model model) {
        Book book = bookService.findById(id);
        if (book == null) {
            return "redirect:/website/books";
        }
        model.addAttribute("book", book);
        return "website/bookDetails"; // Thymeleaf template to display book details
    }
}