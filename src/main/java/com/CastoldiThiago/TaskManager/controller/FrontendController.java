package com.CastoldiThiago.TaskManager.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FrontendController {

    // Captura cualquier ruta que no sea /api/**
    @RequestMapping(value = { "/", "/{path:^(?!api).*$}", "/{path:^(?!api).*$}/**" })
    public String redirectToIndex() {
        return "forward:/index.html";
    }
}
