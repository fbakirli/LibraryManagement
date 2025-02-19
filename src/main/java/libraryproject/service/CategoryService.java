package libraryproject.service;

import libraryproject.entity.Book;
import libraryproject.entity.Category;
import libraryproject.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final BookService bookService; // Inject BookService

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, BookService bookService) {
        this.categoryRepository = categoryRepository;
        this.bookService = bookService; // Initialize BookService
    }

    // Fetch all categories
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Find a category by ID
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    // Save or update a category
    public void saveCategory(Category category) {
        categoryRepository.save(category);
    }

    // Delete a category by ID
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    // New method to find books by category ID
    public List<Book> findBooksByCategoryId(Long categoryId) {
        return bookService.findBooksByCategoryId(categoryId); // Use BookService to fetch books
    }
}