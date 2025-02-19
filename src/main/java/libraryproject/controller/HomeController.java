package libraryproject.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home() {
        // Get the currently logged-in user's authentication details
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check the user's role and redirect accordingly
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "admin-home"; // Redirect to admin home page
        } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"))) {
            return "student-home"; // Redirect to student home page
        }

        // Default fallback (e.g., for other roles or unauthenticated users)
        return "redirect:/auth/login";
    }
}