package nl.tudelft.sem.template.services;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.database.UserRepository;
import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.enums.Certificate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final transient UserRepository userRepo;

    @Autowired
    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    /**
     * Identify user by id.
     *
     * @param id the id of the user
     * @return information about the user
     */
    public Optional<User> getById(Long id) {
        if (id < 0 || !userRepo.existsById(id)) {
            return Optional.empty();
        } else {
            return userRepo.findById(id);
        }
    }

    /**
     * Add user to the database.
     *
     * @param user full information about a user
     * @return the information of the added user
     */
    public User insert(User user) {
        if (user == null || user.getName().isEmpty()) {
            return null;
        }
        //We are currently not checking if the user already exists
        return userRepo.save(user);
    }

    /**
     * Delete a specific user found by id.
     *
     * @param id the id of the user
     * @return whether the user was deleted or not
     */
    public boolean deleteById(Long id) {
        if (id < 0 || !userRepo.existsById(id)) {
            return false;
        } else {
            userRepo.deleteById(id);
        }
        return true;
    }

    /**
     * Update information about a user inside the database.
     *
     * @param id the id of the user we want to update
     *
     * @param name the netId of the user
     * @param organization the organization the user is part of
     * @param email the email the profile is registered with
     * @param gender the gender of the rower
     * @param certificate the biggest certificate a user holds
     * @param positions the list of position they can fill
     * @return the new profile
     */
    public Optional<User> updateById(Long id, String name, String organization, String email, String gender,
                                     Certificate certificate, List<Position> positions) {

        Optional<User> toUpdate = getById(id);

        if (toUpdate.isPresent()) {
            toUpdate.get().setName(name);
            toUpdate.get().setOrganization(organization);
            toUpdate.get().setEmail(email);
            toUpdate.get().setGender(gender);
            toUpdate.get().setCertificate(certificate);
            toUpdate.get().setPositions(positions);

            userRepo.save(toUpdate.get());
        }
        return toUpdate;
    }

    public Optional<User> addRecurringTimeSlot(Long id, Day day, Pair<Integer, Integer> time) {
        Optional<User> user = getById(id);

        if(user.isPresent()) {
            user.get().addRecurringSlot(day, time);
            userRepo.save(user.get());
        }
        return user;
    }

    public Optional<User> removeRecurringTimeSlot(Long id, Day day, Pair<Integer, Integer> time) {
        Optional<User> user = getById(id);

        if(user.isPresent()) {
            user.get().removeRecurringSlot(day, time);
            userRepo.save(user.get());
        }
        return user;
    }

    public Optional<User> addTimeSlot(Long id, TimeSlot timeSlot) {
        Optional<User> user = getById(id);

        if(user.isPresent()) {
            user.get().addSlot(timeSlot);
            userRepo.save(user.get());
        }
        return user;
    }

    public Optional<User> removeTimeSlot(Long id, TimeSlot timeSlot) {
        Optional<User> user = getById(id);

        if(user.isPresent()) {
            user.get().removeSlot(timeSlot);
            userRepo.save(user.get());
        }
        return user;
    }

    public Optional<List<String>> getNotifications(Long id) {
        Optional<User> user = getById(id);
        return user.map(User::getNotifications);
    }

    public Optional<User> addNotification(Long id, String notification) {
        Optional<User> user = getById(id);

        if(user.isPresent()) {
            user.get().addNotification(notification);
            userRepo.save(user.get());
        }
        return user;
    }

    public Optional<User> setName(Long id, String name) {

        Optional<User> user = getById(id);

        if (user.isPresent()) {
            user.get().setEmail(name);
            userRepo.save(user.get());
        }
        return user;
    }

    public Optional<User> setOrganization(Long id, String organization) {

        Optional<User> user = getById(id);

        if (user.isPresent()) {
            user.get().setOrganization(organization);
            userRepo.save(user.get());
        }
        return user;
    }


    public Optional<User> setGender(Long id, String gender) {

        Optional<User> user = getById(id);

        if (user.isPresent()) {
            user.get().setGender(gender);
            userRepo.save(user.get());
        }
        return user;
    }

    public Optional<User> setCertificate(Long id, Certificate certificate) {

        Optional<User> user = getById(id);

        if (user.isPresent()) {
            user.get().setCertificate(certificate);
            userRepo.save(user.get());
        }
        return user;
    }

    public Optional<User> setPositions(Long id, List<Position> positions) {

        Optional<User> user = getById(id);

        if (user.isPresent()) {
            user.get().setPositions(positions);
            userRepo.save(user.get());
        }
        return user;
    }
}
