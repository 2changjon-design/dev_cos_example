package com.example.study.lecture.cleancode.spring.answer4;

import com.example.study.entity.User;
import com.example.study.repository.UserJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAccountService {

    private final UserJpaRepository userRepository;

    public UserAccountService(UserJpaRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User ensureUser(String name, String email) {
        return userRepository.findByEmail(email)
                .map(user -> updateProfileIfNeeded(user, name))
                .orElseGet(() -> register(name, email));
    }

    private User updateProfileIfNeeded(User user, String name) {
        if (name != null && !name.equals(user.getName())) {
            user.updateProfile(name, user.getEmail());
        }
        return user;
    }

    private User register(String name, String email) {
        String resolvedName = (name == null || name.isBlank()) ? "Guest" : name;
        return userRepository.save(
                User.builder()
                        .name(resolvedName)
                        .email(email)
                        .passwordHash("temp-" + email)
                        .build()
        );
    }
}
