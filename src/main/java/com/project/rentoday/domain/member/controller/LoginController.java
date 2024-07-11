package com.project.rentoday.domain.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {


    @GetMapping("/login")
    public String login(Model model) {
        return "login.html";
    }

    @GetMapping("/main")
    public String main() {
        return "main.html";
    }

    @GetMapping("/my")
    public String my() {
        return "my.html";
    }

    @GetMapping("/join")
    public String join() {
        return "join.html";
    }
}
