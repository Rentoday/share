package com.project.rentoday.domain.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/notice")
    public String notice(Model model) {
        return "noticeModal.html";
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        return "registrationModal.html";
    }

    @GetMapping("/refund")
    public String refund(Model model) {
        return "refund.html";
    }

    @GetMapping("/noticeAdd")
    public String noticeAdd(Model model) {
        return "member/noticeAdd.html";
    }

    @GetMapping("/withdrawal")
    public String withdrawal (Model model) {
        return "withdrawal.html";
    }

    @GetMapping("/verification")
    public String verification (Model model) {
        return "verificationModal.html";
    }

    @GetMapping("/layout")
    public String layout(Model model) {
        return "layout/layout.html";
    }

    @GetMapping("/main")
    public String main() {
        return "layout/main.html";
    }


    @GetMapping("/loginModal")
    public String loginModal() {
        return "member/loginModal.html";
    }
}
