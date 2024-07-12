package com.project.rentoday.global.page;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontController {



    @GetMapping("/myPage")
    public String notification() {
        return "myPage/myPage.html";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin/admin.html";
    }

    @GetMapping("/detail")
    public String detail() {
        return "detail.html";
    }

    @GetMapping("/pay")
    public String pay() {
        return "pay.html";
    }
    @GetMapping("/login")
    public String login(Model model) {
        return "login.html";
    }

    @GetMapping("/index")
    public String index() {
        return "index.html";
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
