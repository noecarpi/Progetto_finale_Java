package it.aulab.progetto_finale_java.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.aulab.progetto_finale_java.models.CareerRequest;
import it.aulab.progetto_finale_java.models.Role;
import it.aulab.progetto_finale_java.models.User;
import it.aulab.progetto_finale_java.repositories.CareerRequestRepository;
import it.aulab.progetto_finale_java.repositories.RoleRepository;
import it.aulab.progetto_finale_java.repositories.UserRepository;

@Service 
public class CareerRequestServiceImpl implements CareerRequestService {

    @Autowired 
    private CareerRequestRepository careerRequestRepository;

    @Autowired 
    private RoleRepository roleRepository;

    @Autowired 
    private UserRepository userRepository;

    @Autowired 
    private EmailService emailService;


    @Transactional 
    public boolean isRoleAlreadyAssigned(User user, CareerRequest careerRequest) {
        List<Long> allUserIds = careerRequestRepository.findAllUserIds();

        if(!allUserIds.contains(user.getId())){
            return false;
        }

        List<Long> requests = careerRequestRepository.findByUserId(user.getId());

        return requests.stream().anyMatch((roleId -> roleId.equals(careerRequest.getRole().getId())));
    }

    @Override
    public void save(CareerRequest careerRequest, User user) {
        Role role = roleRepository.findById(careerRequest.getRole().getId())
            .orElseThrow();

        careerRequest.setRole(role);
        careerRequest.setUser(user);
        careerRequest.setIsChecked(false);

        careerRequestRepository.save(careerRequest);

        emailService.sendSimpleEmail("adminAulabchronicle@admin.com", 
                                        "Role request: "+ role.getName(), 
                                        "There's a newApplication from " + user.getUsername());
    }

    @Override
    public void careerAccept(Long requestId) {
        CareerRequest request = careerRequestRepository.findById(requestId).get();

        User user = request.getUser();
        Role role = request.getRole();

        List<Role> rolesUser = user.getRoles();
        Role newRole = roleRepository.findByName(role.getName());
        rolesUser.add(newRole);

        user.setRoles(rolesUser);
        userRepository.save(user);
        request.setIsChecked(true);
        careerRequestRepository.save(request);

        emailService.sendSimpleEmail(user.getEmail(), "Role accepted", "Hello, your application has been accepted by our admin. Welcome aboard!");
    }

    @Override
    @Transactional
    // rifiuta la richiesta e la marca come già gestita
    public void careerReject(Long requestId) {
        CareerRequest request = careerRequestRepository.findById(requestId).get();
        request.setIsChecked(true);
        careerRequestRepository.save(request);

        // comunica all'utente che la richiesta non è stata accettata
        emailService.sendSimpleEmail(
                request.getUser().getEmail(),"Role rejected","Hello, your application has been rejected by our admin. Better luck next time!");
    }

    @Override
    public CareerRequest find(Long id) {
        return careerRequestRepository.findById(id).get();
    }

}
