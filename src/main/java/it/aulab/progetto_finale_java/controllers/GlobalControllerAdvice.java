package it.aulab.progetto_finale_java.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import it.aulab.progetto_finale_java.dtos.CategoryDto;
import it.aulab.progetto_finale_java.services.CategoryService;

@ControllerAdvice 
public class GlobalControllerAdvice {

    @Autowired 
    private CategoryService categoryService;

    @ModelAttribute 
    public void addCategories(Model viewModel){
        List<CategoryDto> categories = categoryService.readAll();
        viewModel.addAttribute("categories", categories);
    }
}

// ho fatto un minimo di ricerca perche volevo che il dropdown della navbar con la lista di categorie funzionasse sempre e mi embrava controproducente dover ricordarmi di aggiungere questo per ogni pagina anche futura