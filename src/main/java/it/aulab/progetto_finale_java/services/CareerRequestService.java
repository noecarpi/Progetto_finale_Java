package it.aulab.progetto_finale_java.services;

import it.aulab.progetto_finale_java.models.CareerRequest;
import it.aulab.progetto_finale_java.models.User;

public interface CareerRequestService {
    boolean isRoleAlreadyAssigned(User user, CareerRequest careerRequest);
    void save(CareerRequest careerRequest, User user);
    void careerAccept(Long requestId);
    void careerReject(Long requestId);
    CareerRequest find(Long id);
}
