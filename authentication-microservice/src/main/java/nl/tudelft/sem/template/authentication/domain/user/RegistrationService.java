package nl.tudelft.sem.template.authentication.domain.user;

import org.springframework.stereotype.Service;

/**
 * A DDD service for registering a new user.
 */
@Service
public class RegistrationService {
    private final transient UserRepository userRepository;
    private final transient PasswordHashingService passwordHashingService;

    /**
     * Instantiates a new UserService.
     *
     * @param userRepository         the user repository
     * @param passwordHashingService the password encoder
     */
    public RegistrationService(UserRepository userRepository, PasswordHashingService passwordHashingService) {
        this.userRepository = userRepository;
        this.passwordHashingService = passwordHashingService;
    }

    /**
     * Register a new user.
     *
     * @param netId    The NetID of the user
     * @param password The password of the user
     * @throws Exception if the user already exists
     */
    public AppUser registerUser(NetId netId, Password password, Email email) throws Exception {

        if (checkNetIdIsUnique(netId) && checkEmailIsUnique(email)) {
            // Hash password
            HashedPassword hashedPassword = passwordHashingService.hash(password);

            // Create new account
            AppUser user = new AppUser(netId, hashedPassword, email);

            userRepository.save(user);

            return user;
        }
        if (!checkNetIdIsUnique(netId)) {
            throw new NetIdAlreadyInUseException(netId);
        }
        throw new EmailAlreadyInUseException(email);

    }

    public boolean checkNetIdIsUnique(NetId netId) {
        return !userRepository.existsByNetId(netId);
    }

    public boolean checkEmailIsUnique(Email email) {
        return !userRepository.existsByEmail(email);
    }
}
