package it.aulab.progetto_finale_java.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.aulab.progetto_finale_java.dtos.ArticleDto;
import it.aulab.progetto_finale_java.dtos.UserDto;
import it.aulab.progetto_finale_java.models.Article;
import it.aulab.progetto_finale_java.models.User;
import it.aulab.progetto_finale_java.repositories.ArticleRepository;
import it.aulab.progetto_finale_java.repositories.CareerRequestRepository;
import it.aulab.progetto_finale_java.services.ArticleService;
import it.aulab.progetto_finale_java.services.CategoryService;
import it.aulab.progetto_finale_java.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller 
public class UserController {

    @Autowired 
    private UserService userService;

    @Autowired 
    private ArticleService articleService;

    @Autowired 
    private ArticleRepository articleRepository;

    @Autowired 
    private CareerRequestRepository careerRequestRepository;

    @Autowired 
    private CategoryService categoryService;

    @Autowired 
    private ModelMapper modelMapper;

    @GetMapping ("/")
    public String home(Model viewModel){
        List<ArticleDto> articles = new ArrayList<>();
        for (Article article : articleRepository.findByIsAcceptedTrue()) {
            articles.add(modelMapper.map(article, ArticleDto.class));
        }


        Collections.sort(articles, Comparator.comparing(ArticleDto::getPublishDate).reversed());
        
        List<ArticleDto> lastThreeArticles = articles.stream().limit(3).collect(Collectors.toList());
        
        viewModel.addAttribute("articles",lastThreeArticles);

        return "home";
    }

    @GetMapping ("/register")
    public String register(Model model){
        model.addAttribute("title", "Register");
        model.addAttribute("user", new UserDto());
        return "auth/register";
    }

    @GetMapping ("/login")
    public String login(Model model){
        model.addAttribute("title", "Login");
        return "auth/login";
    }

    @GetMapping ("/search/{id}")
    public String userArticlesSearch(@PathVariable("id") Long id, Model viewModel){
        User user = userService.find(id);
        viewModel.addAttribute("title", user.getUsername());

        List<ArticleDto> articles = articleService.searchByAuthor(user);
        Collections.sort(articles, Comparator.comparing(ArticleDto::getPublishDate).reversed());
        List<ArticleDto> acceptedArticles = articles.stream().filter(article -> Boolean.TRUE.equals(article.getIsAccepted())).collect(Collectors.toList());

        viewModel.addAttribute("articles", acceptedArticles);

        return "article/articles";
    }

    @GetMapping ("/admin/dashboard") 
    public String adminDashboard(Model viewModel){
        viewModel.addAttribute("title", "Admin Dashboard");
        viewModel.addAttribute("requests", careerRequestRepository.findByIsCheckedFalse());
        viewModel.addAttribute("categories", categoryService.readAll());

        return "admin/dashboard";
    }

    @GetMapping ("/revisor/dashboard") 
    public String revisorDashboard(Model viewModel){
        viewModel.addAttribute("title", "Revisor Dashboard");
        viewModel.addAttribute("articles", articleRepository.findByIsAcceptedIsNull());

        return "revisor/dashboard";
    }

    @GetMapping ("/writer/dashboard")
    public String writerDashboard(Model viewModel, Principal principal) {
        viewModel.addAttribute("title", "Writer Dashboard");

        List<ArticleDto> userArticles = articleService.readAll().stream()
                                        .filter(article -> article.getUser().getEmail().equals(principal.getName()))
                                        .toList();

        viewModel.addAttribute("articles", userArticles);

        return "writer/dashboard";
    }


    @PostMapping ("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto userDto,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes,
                                HttpServletRequest request, HttpServletResponse response){

        // System.out.println(">>> REGISTRAZIONE RICEVUTA");
        User existingUser = userService.findUserByEmail(userDto.getEmail());

        if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()) {
            result.rejectValue("email", null, "There's already an account registered with the same email");
        }

        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "auth/register";
        }

        // System.out.println(">>> VALIDAZIONE SUPERATA, SALVO L'UTENTE");
        userService.saveUser(userDto, redirectAttributes, request, response);

        redirectAttributes.addFlashAttribute("successMessage", "Account registered successfully");

        return "redirect:/";
    }

}
