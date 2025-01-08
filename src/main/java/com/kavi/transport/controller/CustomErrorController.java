package com.kavi.transport.controller;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@Controller
public class CustomErrorController implements ErrorController {

    // Inject ErrorAttributes
    private final ErrorAttributes errorAttributes;

    public CustomErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        WebRequest webRequest = new ServletWebRequest(request);
        Map<String, Object> errorAttributes = this.errorAttributes.getErrorAttributes(
                webRequest,
                ErrorAttributeOptions.defaults()
        );

        // Get status code from error attributes
        Integer statusCode = (Integer) errorAttributes.get("status");

        if (statusCode == 404) {
            model.addAttribute("errorMessage", "Page not found.");
            return "error-404";
        } else if (statusCode == 500) {
            model.addAttribute("errorMessage", "Internal server error.");
            return "error-500";
        }

        model.addAttribute("errorMessage", "An unexpected error occurred.");
        return "error";
    }
}
