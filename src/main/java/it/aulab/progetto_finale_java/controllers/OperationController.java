package it.aulab.progetto_finale_java.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.aulab.progetto_finale_java.models.CareerRequest;
import it.aulab.progetto_finale_java.models.Role;
import it.aulab.progetto_finale_java.models.User;
import it.aulab.progetto_finale_java.repositories.RoleRepository;
import it.aulab.progetto_finale_java.repositories.UserRepository;
import it.aulab.progetto_finale_java.services.CareerRequestService;

@Controller 
@RequestMapping ("/operations")
public class OperationController {

    @Autowired 
    private RoleRepository roleRepository;

    @Autowired 
    private  UserRepository userRepository;

    @Autowired 
    private CareerRequestService careerRequestService;

    @GetMapping ("/career/request")
    public String careerRequestCreate(Model viewModel){
        viewModel.addAttribute("title", "Work with us");
        viewModel.addAttribute("careerRequest", new CareerRequest());

        List<Role> roles = roleRepository.findAll();
        roles.removeIf(e -> e.getName().equals("ROLE_USER"));
        viewModel.addAttribute("roles", roles);

        return "career/requestForm";
    }

    @GetMapping ("/career/request/detail/{id}")
    public String careerRequestDetail(@PathVariable("id") Long id, Model viewModel){
        viewModel.addAttribute("title", "Application Detail");
        viewModel.addAttribute("request", careerRequestService.find(id));

        return "career/requestDetail";
    }

    @PostMapping ("/career/request/save")
    public String careerRequestStore(@ModelAttribute("careerRequest") CareerRequest careerRequest,
                                    Principal principal,
                                    RedirectAttributes redirectAttributes){
        User user = userRepository.findByEmail(principal.getName());

        if (careerRequestService.isRoleAlreadyAssigned(user, careerRequest)) {
            redirectAttributes.addFlashAttribute("errorMessage", "You already have this role");
            return "redirect:/";
        }

        careerRequestService.save(careerRequest, user);

        redirectAttributes.addFlashAttribute("successMessage", "Request sent sucessfully");

        return "redirect:/";
    }

    @PostMapping ("/career/request/accept/{requestId}")
    public String careerRequestAccept(@PathVariable Long requestId, RedirectAttributes redirectAttributes){
        careerRequestService.careerAccept(requestId);
        redirectAttributes.addFlashAttribute("successMessage", "Accepted User role");

        return "redirect:/admin/dashboard";
    }

    @PostMapping("/career/request/reject/{requestId}")
    public String careerRequestReject(@PathVariable Long requestId, RedirectAttributes redirectAttributes) {

        careerRequestService.careerReject(requestId);
        redirectAttributes.addFlashAttribute("errorMessage","Richiesta rifiutata");

        return "redirect:/admin/dashboard";
    }
}
