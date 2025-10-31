package com.example.demo.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.Map;

@Controller
public class AppErrorController implements ErrorController {
    private final ErrorAttributes errorAttributes;

    public AppErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        ServletWebRequest webRequest = new ServletWebRequest(request);
        Map<String, Object> attrs = errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.of(
                ErrorAttributeOptions.Include.MESSAGE,
                ErrorAttributeOptions.Include.STATUS,
                ErrorAttributeOptions.Include.EXCEPTION
        ));
        
        Object status = attrs.get("status");
        Object message = attrs.get("message");
        Object exception = attrs.get("exception");
        
        model.addAttribute("status", status != null ? status : "Неизвестный");
        model.addAttribute("message", message != null ? message : "Произошла неизвестная ошибка");
        model.addAttribute("exception", exception);
        
        return "error";
    }
}
