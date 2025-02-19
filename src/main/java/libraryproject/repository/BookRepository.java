package libraryproject.repository;

import libraryproject.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    // New method to find books by category ID
    List<Book> findByCategoryId(Long categoryId);
}