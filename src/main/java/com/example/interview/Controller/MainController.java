package com.example.interview.Controller;


import com.example.interview.Entity.User;
import com.example.interview.Service.QuestionService;
import com.example.interview.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.validation.BindingResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final QuestionService questionService;
    private final UserService userService;
    @GetMapping
    public String Main(HttpSession session,
                       Model model){
        User loginUser =(User) session.getAttribute("loginUser");
        System.out.println("login user = "+loginUser);
        model.addAttribute("loginUser",loginUser);
        return "index";
    }

    @PostMapping("/start-interview")
    public String start(@RequestParam("job") String job, HttpSession session, Model model){
        if(session.getAttribute("loginUser") == null){
            return "user/login";
        }
        session.setAttribute("job", job);
        model.addAttribute("job", job);
        return "interview/choice";
    }

    @PostMapping("/start-question")
    public String startQuestion(@RequestParam("type") String type, HttpSession session, Model model){
        String job = (String) session.getAttribute("job");
        if (job == null) {
            return "redirect:/";
        }

        List<String> question = questionService.makeQuestion(job, type, 5);
        model.addAttribute("question", question);
        model.addAttribute("job", job);
        model.addAttribute("type", type);

        session.setAttribute("type", type);

        return "interview/question";
    }
    @GetMapping("/Question/add")
    public String QuestionAdd(HttpSession session){
        User member = (User) session.getAttribute("loginUser");
        if(member!=null){
            return "interview/questionadd";
        }
        return "user/login";
    }

    @GetMapping("/add-question")
    public String addQuestion(HttpSession session) {
        return QuestionAdd(session);
    }

    @PostMapping("/api/questions")
    public ResponseEntity<Map<String, String>> addQuestionApi(
            @RequestBody QuestionRequest request,
            HttpSession session
    ) {
        if (session.getAttribute("loginUser") == null) {
            return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));
        }

        if (request.job() == null || request.type() == null || request.text() == null || request.text().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "직무, 유형, 질문 내용을 모두 입력해주세요."));
        }

        questionService.addQuestion(request.job(), request.type(), request.text().trim());
        return ResponseEntity.ok(Map.of("message", "질문이 등록되었습니다."));
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/login")
    public String login(){
        return "user/login";
    }
    @PostMapping("/login")
    public String logins(HttpServletRequest request, RedirectAttributes redirectAttributes, HttpSession session ,Model model){
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        Optional<User> findEmail = userService.findEmail(email);
        if(findEmail.isEmpty()){
            redirectAttributes.addFlashAttribute("error", "이메일이 존재하지 않습니다.");
            return "redirect:/login";
        }
        User user = findEmail.get();
        if(!user.getPassword().equals(password)){
            redirectAttributes.addFlashAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "redirect:/login";
        }
        model.addAttribute("loginUser",user);
        session.setAttribute("loginUser", user); // 세션에 저장 (중요!)
        return "redirect:/";
    }


    @GetMapping("/signup")
    public String singUp(Model model){
        model.addAttribute("user",new User());
        return"user/signup";
    }
    @PostMapping("/signup")
    public String signUp2(@Valid @ModelAttribute User user, BindingResult bindingResult) {
        // 1. 기본적인 폼 검증 오류가 있으면 다시 회원가입 페이지로
        if (bindingResult.hasErrors()) {
            return "user/signup"; // signup.html 뷰 이름 (템플릿 엔진 기준)
        }

        // 2. 이메일 중복 체크
        if (userService.existsByEmail(user.getEmail())) {
            bindingResult.rejectValue("email", "error.user", "이미 사용 중인 이메일입니다.");
            return "user/signup";
        }

        // 3. 저장 후 성공 페이지로 이동
        userService.save(user);
        return "redirect:/";
    }

    private record QuestionRequest(String job, String type, String text) {
    }
}
