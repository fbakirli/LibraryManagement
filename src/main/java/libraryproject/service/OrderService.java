package libraryproject.service;

import libraryproject.entity.Book;
import libraryproject.entity.Order;
import libraryproject.entity.Student;
import libraryproject.repository.BookRepository;
import libraryproject.repository.OrderRepository;
import libraryproject.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final StudentRepository studentRepository;
    private final BookRepository bookRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, StudentRepository studentRepository, BookRepository bookRepository) {
        this.orderRepository = orderRepository;
        this.studentRepository = studentRepository;
        this.bookRepository = bookRepository;
    }

    /**
     * Create a new order for a student and a book.
     * Decreases the book's stock by 1.
     *
     * @param studentId The ID of the student placing the order.
     * @param bookId    The ID of the book being ordered.
     * @throws IllegalArgumentException If the student or book is not found, or if the book is out of stock.
     */
    @Transactional
    public void createOrder(Long studentId, Long bookId) {
        // Find the student by ID
        Optional<Student> studentOptional = studentRepository.findById(studentId);
        if (studentOptional.isEmpty()) {
            throw new IllegalArgumentException("Student not found with ID: " + studentId);
        }
        Student student = studentOptional.get();

        // Find the book by ID
        Optional<Book> bookOptional = bookRepository.findById(bookId);
        if (bookOptional.isEmpty()) {
            throw new IllegalArgumentException("Book not found with ID: " + bookId);
        }
        Book book = bookOptional.get();

        // Check if the book is in stock
        if (book.getStock() <= 0) {
            throw new IllegalArgumentException("Book is out of stock: " + book.getTitle());
        }

        // Decrease the book's stock
        book.setStock(book.getStock() - 1);
        bookRepository.save(book);

        // Create and save the order
        Order order = new Order();
        order.setStudent(student);
        order.setBook(book);
        order.setOrderDate(LocalDateTime.now());
        orderRepository.save(order);
    }

    /**
     * Return a book and increase its stock by 1.
     *
     * @param orderId The ID of the order to return.
     * @throws IllegalArgumentException If the order is not found.
     */
    @Transactional
    public void returnOrder(Long orderId) {
        // Find the order
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isEmpty()) {
            throw new IllegalArgumentException("Order not found with ID: " + orderId);
        }
        Order order = orderOptional.get();

        // Increase the book's stock
        Book book = order.getBook();
        book.setStock(book.getStock() + 1);
        bookRepository.save(book);

        // Set the return date and save the order
        order.setReturnDate(LocalDateTime.now());
        orderRepository.save(order);
    }

    /**
     * Get a list of all orders.
     *
     * @return A list of all orders.
     */
    public List<Order> findAll() {
        return orderRepository.findAll();
    }
}