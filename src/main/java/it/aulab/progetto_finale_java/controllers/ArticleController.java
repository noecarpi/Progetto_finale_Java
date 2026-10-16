package it.aulab.progetto_finale_java.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.aulab.progetto_finale_java.dtos.ArticleDto;
import it.aulab.progetto_finale_java.dtos.CategoryDto;
import it.aulab.progetto_finale_java.models.Article;
import it.aulab.progetto_finale_java.models.Category;
import it.aulab.progetto_finale_java.repositories.ArticleRepository;
import it.aulab.progetto_finale_java.services.ArticleService;
import it.aulab.progetto_finale_java.services.CrudService;
import jakarta.validation.Valid;

@Controller 
@RequestMapping ("/articles")
public class ArticleController {

    @Autowired 
    @Qualifier ("categoryService")
    private CrudService<CategoryDto, Category, Long> categoryService;

    @Autowired 
    private ArticleService articleService;

    @Autowired 
    private ArticleRepository articleRepository;

    @Autowired 
    private ModelMapper modelMapper;

    // vista tutti articoli
    @GetMapping 
    public String articlesIndex(Model viewModel){
        viewModel.addAttribute("title", "All articles");

        List<ArticleDto> articles = new ArrayList<>();
        for (Article article : articleRepository.findByIsAcceptedTrue()) {
            articles.add(modelMapper.map(article, ArticleDto.class));
        }

        Collections.sort(articles, Comparator.comparing(ArticleDto::getPublishDate).reversed());
        viewModel.addAttribute("articles", articles);
        
        // System.out.println("SONO ENTRATA IN ARTICLES INDEX");
        return "article/articles";
    }

    // vista detail articolo
    @GetMapping ("detail/{id}")
    public String detailArticle(@PathVariable("id") Long id, Model viewModel){
        viewModel.addAttribute("title", "Article detail");

        ArticleDto article = articleService.read(id);
        viewModel.addAttribute("article", article);

        // solo per fare funzionare la colonna related articles che ho voluto aggiungere nella pagina di detail
        List<ArticleDto> articles = articleService.searchByCategory(
            modelMapper.map(article.getCategory(), Category.class)
        );

        List<ArticleDto> relatedArticles = articles.stream()
            .filter(a -> Boolean.TRUE.equals(a.getIsAccepted()))
            .filter(a -> !a.getId().equals(id))
            .collect(Collectors.toList());

            Collections.sort(articles, Comparator.comparing(ArticleDto::getPublishDate).reversed());
            
            viewModel.addAttribute("articles", relatedArticles);

        return "article/detail";
    }

    // vista edit article
    @GetMapping ("/edit/{id}")
    public String editArticle(@PathVariable("id") Long id, Model viewModel){
        viewModel.addAttribute("title", "Edit article");
        viewModel.addAttribute("article", articleService.read(id));
        viewModel.addAttribute("categories", categoryService.readAll());
        return "article/edit";
    }

    // vista detail ma da revisor
    @GetMapping ("revisor/detail/{id}")
    public String revisorDetailArticle(@PathVariable("id") Long id, Model viewModel){
        viewModel.addAttribute("title", "Article detail");
        viewModel.addAttribute("article", articleService.read(id));
        return "article/detail";
    }
    
    // vista create articolo
    @GetMapping ("create")
    public String articleCreate(Model viewModel){
        viewModel.addAttribute("title", "Create article");
        viewModel.addAttribute("article", new Article());
        viewModel.addAttribute("categories", categoryService.readAll());
        return "article/create";
    }

    // ricerca articolo
    @GetMapping("/search")
    public String articleSearch(@Param("keyword") String keyword, Model viewModel){
        viewModel.addAttribute("title", "Found articles");

        List<ArticleDto> articles = articleService.search(keyword);
        List<ArticleDto> acceptedArticles = articles.stream().filter(article -> Boolean.TRUE.equals(article.getIsAccepted())).collect(Collectors.toList());

        viewModel.addAttribute("articles", acceptedArticles);

        return "article/articles";
    }

    // delete article
    @GetMapping ("/delete/{id}")
    public String articleDelete(@PathVariable ("id") Long id, RedirectAttributes redirectAttributes){
        articleService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Article deleted successfully");

        return "redirect:/writer/dashboard";
    }

    // save articolo
    @PostMapping 
    public String articleStore(@Valid @ModelAttribute ("article") Article article,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                Principal principal,
                                MultipartFile file,
                                Model viewModel){
        
        if (result.hasErrors()) {
            
            // result.getAllErrors().forEach(error ->
            // System.out.println(error));
            viewModel.addAttribute("title", "Create article");
            viewModel.addAttribute("article", article);
            viewModel.addAttribute("categories", categoryService.readAll());
            return "article/create";
        }

        articleService.create(article, principal, file);
        redirectAttributes.addFlashAttribute("successMessage", "Article added successfully");

        return "redirect:/writer/dashboard";
    }

    // edit articolo
    @PostMapping ("/update/{id}")
    public String articleUpdate(@PathVariable("id") Long id,
                                @Valid @ModelAttribute ("article") Article article,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                Principal principal,
                                MultipartFile file,
                                Model viewModel){

        // System.out.println("ERRORI VALIDAZIONE: " + result.getAllErrors());
        if (result.hasErrors()) {
            viewModel.addAttribute("title", "Edit article");
            article.setImage(articleService.read(id).getImage());
            viewModel.addAttribute("article", article);
            viewModel.addAttribute("categories", categoryService.readAll());
            return "article/edit";
        }

        // System.out.println("SONO ARRIVATA ALL'UPDATE");
        articleService.update(id, article, file);
        redirectAttributes.addFlashAttribute("successMessage", "Article updated successfully");

        return "redirect:/writer/dashboard";
    }

    // accetta o rifiuta da revisor
    @PostMapping ("/accept")
    public String articleSetAccepted(@RequestParam("action") String action, @RequestParam("articleId") Long articleId, RedirectAttributes redirectAttributes){
        if (action.equals("accept")) {
            articleService.setIsAccepted(true, articleId);
            redirectAttributes.addFlashAttribute("resultMessage", "Article accepted!");
        } else if (action.equals("reject")) {
            articleService.setIsAccepted(false, articleId);
            redirectAttributes.addFlashAttribute("resultMessage", "Article rejected!");
        } else {
            redirectAttributes.addFlashAttribute("resultMessage", "Wrong action!");
        }

        return "redirect:/revisor/dashboard";
    }

}
