package com.bluestaq.challenge.notesvault;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

// Decorate the class with @Controller to indicate that it is a Spring MVC controller
// This class will handle HTTP requests and return views (HTML pages) to the client
@Controller
public class HomeController {

    // Decorate the method with @RequestMapping to specify the URL path it will handle
    @RequestMapping(value = "/")
    public String index() {
        // Return the name of the view (HTML page) to be rendered
        // In this case, it will look for a file named "index.html" in the templates directory
        return "index.html";
    }
}