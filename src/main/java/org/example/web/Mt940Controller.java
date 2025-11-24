package org.example.web;

import jakarta.validation.constraints.NotBlank;
import org.example.model.Statement;
import org.example.util.Mt940Parser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Validated
public class Mt940Controller {

    private final Mt940Parser parser = new Mt940Parser();

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/parse")
    public String parse(@RequestParam("mt940") @NotBlank String mt940, Model model) {
        Statement statement = parser.parse(mt940);
        model.addAttribute("statement", statement);
        return "index";
    }
}
