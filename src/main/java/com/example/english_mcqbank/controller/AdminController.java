package com.example.english_mcqbank.controller;


import com.example.english_mcqbank.model.Topic;
import com.example.english_mcqbank.model.UserEntity;
import com.example.english_mcqbank.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminController {
    final UserDetailsServiceImpl userService;
    final ILogService logService;
    final ITopicService topicService;
    final IQuestionService questionService;
    final PasswordEncoder passwordEncoder;
    final IExamService examService;
    final IResultService resultService;
    final ISessionService sessionService;

    @Autowired
    HttpSession session;

    @RequestMapping("/admin")
    public ModelAndView admin() {
        ModelAndView adminModelAndView = new ModelAndView("admin");
        adminModelAndView.addObject("admin", "admin");
        //List<UserEntity> users = userService.getAllUsers();
        //adminModelAndView.addObject("users", users);
        UserEntity user = sessionService.getLoggedInUser();
        adminModelAndView.addObject("user", user);
        adminModelAndView.addObject("type", 1);
        return adminModelAndView; // Trả về admin.jsp
    }

    @RequestMapping("/admin/profile")
    public ModelAndView adminProfile() {
        ModelAndView view = new ModelAndView("profile");
        UserEntity user = sessionService.getLoggedInUser();
        sessionService.setAttribute("type3", 1);
        view.addObject("user", user);
        //view.addObject("loggedInUser", user);
        view.addObject("successMessage", null);
        view.addObject("errorMessage", null);
        view.addObject("type", 1);

        return view; // Trả về admin.jsp
    }

    @RequestMapping("/admin/test1")
    public ModelAndView test1(HttpSession session1, Authentication authentication) {
        ModelAndView view = new ModelAndView("test1");
        List<Topic> topics = topicService.getAllTopics();
        view.addObject("topics", topics);
        session.setAttribute("topics", topics);
        UserEntity user1 = (UserEntity) session1.getAttribute("loggedInUser");
        UserEntity user2 = (UserEntity) session.getAttribute("loggedInUser");
        view.addObject("user1", user1);
        view.addObject("user2", user2);
        //TopicsWrapper topicsWrapper = new TopicsWrapper();
        //topicsWrapper.setTopics(topics);
        //model.addAttribute("topicsWrapper", topicsWrapper);
        //model.addAttribute("topics", topics);
        return view; // Trả về admin.jsp
    }
}
