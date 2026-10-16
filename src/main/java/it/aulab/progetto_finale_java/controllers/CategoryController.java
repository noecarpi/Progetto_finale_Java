package it.aulab.progetto_finale_java.controllers;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.aulab.progetto_finale_java.dtos.ArticleDto;
import it.aulab.progetto_finale_java.dtos.CategoryDto;
import it.aulab.progetto_finale_java.models.Category;
import it.aulab.progetto_finale_java.services.ArticleService;
import it.aulab.progetto_finale_java.services.CategoryService;
import jakarta.validation.Valid;

@Controller 
@RequestMapping ("/categories")
public class CategoryController {

    @Autowired 
    private ArticleService articleService;

    @Autowired 
    private CategoryService categoryService;

    @Autowired 
    private ModelMapper modelMapper;

    // ricerca articolo per categoria
    @GetMapping ("/search/{id}")
    public String categorySearch(@PathVariable("id") Long id, Model viewModel){
        
        CategoryDto category = categoryService.read(id);
        viewModel.addAttribute("title", category.getName());

        List<ArticleDto> articles = articleService.searchByCategory(modelMapper.map(category, Category.class));
        List<ArticleDto> acceptedArticles = articles.stream().filter(article -> Boolean.TRUE.equals(article.getIsAccepted())).collect(Collectors.toList());
        viewModel.addAttribute("articles", acceptedArticles);

        return "article/articles";
    }

    // vista create categoria
    @GetMapping ("/create")
    public String categoryCreate(Model viewModel){
        viewModel.addAttribute("title", "Create category");
        viewModel.addAttribute("category", new Category());
        return "category/create";
    }

    // vista edit categoria
    @GetMapping ("/edit/{id}")
    public String categoryEdit(@PathVariable("id") Long id, Model viewModel){
        viewModel.addAttribute("title", "Edit category");
        viewModel.addAttribute("category", categoryService.read(id));
        return "category/update";
    }

    // delete categoria
    @GetMapping ("delete/{id}")
    public String categoryDelete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes){
        categoryService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Category deleted successfully");
        return "redirect:/admin/dashboard";
    }

    // save categoria
    @PostMapping 
    public String categoryStore(@Valid @ModelAttribute("category") Category category, BindingResult result, RedirectAttributes redirectAttributes, Model viewModel){
        if (result.hasErrors()) {
            viewModel.addAttribute("title", "Create category");
            viewModel.addAttribute("category", category);
        return "category/create";
        }

        categoryService.create(category, null, null);
        redirectAttributes.addFlashAttribute("successMessage", "Category added successfully");

        return "redirect:/admin/dashboard";
    }

    // edit categoria
    @PostMapping ("/update/{id}")
    public String categoryUpdate(@PathVariable("id") Long id, @Valid @ModelAttribute("category") Category category, BindingResult result, RedirectAttributes redirectAttributes, Model viewModel){
        if (result.hasErrors()) {
            viewModel.addAttribute("title", "Create category");
            viewModel.addAttribute("category", category);
            return "category/update";
        }

        categoryService.update(id, category, null);
        redirectAttributes.addFlashAttribute("successMessage", "Category updated successfully");

        return "redirect:/admin/dashboard";
    }

}
