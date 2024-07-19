package com.rodrigo.drawing_contest.services;

import com.rodrigo.drawing_contest.exceptions.EntityNotFoundException;
import com.rodrigo.drawing_contest.exceptions.UsernameAlreadyUsedException;
import com.rodrigo.drawing_contest.models.entities.User;
import com.rodrigo.drawing_contest.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User findUserByUsername(String username) {
        return this.userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("user with username {" + username + "} not found")
        );
    }

    @Transactional
    public User createUser(String username, String rawPassword) {
        if (userRepository.existsByUsername(username))
            throw new UsernameAlreadyUsedException("username {" + username + "} is invalid");

        String encryptedPassword = new BCryptPasswordEncoder().encode(rawPassword);
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(encryptedPassword);

        return this.userRepository.save(newUser);
    }
}
