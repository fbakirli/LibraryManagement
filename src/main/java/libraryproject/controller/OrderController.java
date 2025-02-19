package libraryproject.controller;

import libraryproject.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Display a list of all orders.
     *
     * @param model The model to pass data to the view.
     * @return The view name for the orders list.
     */
    @GetMapping
    public String listOrders(Model model) {
        model.addAttribute("orders", orderService.findAll());
        return "admin/orders/list";
    }

    /**
     * Show the form for creating a new order.
     *
     * @return The view name for the order creation form.
     */
    @GetMapping("/create")
    public String showCreateForm() {
        return "admin/orders/create";
    }

    /**
     * Handle the creation of a new order.
     *
     * @param studentId The ID of the student placing the order.
     * @param bookId    The ID of the book being ordered.
     * @return A redirect to the orders list page.
     */
    @PostMapping("/create")
    public String createOrder(@RequestParam Long studentId, @RequestParam Long bookId) {
        orderService.createOrder(studentId, bookId);
        return "redirect:/admin/orders";
    }

    /**
     * Handle the return of a book.
     *
     * @param orderId The ID of the order to return.
     * @return A redirect to the orders list page.
     */
    @GetMapping("/return/{id}")
    public String returnOrder(@PathVariable("id") Long orderId) {
        orderService.returnOrder(orderId);
        return "redirect:/admin/orders";
    }
}