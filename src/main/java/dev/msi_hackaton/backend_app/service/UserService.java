package dev.msi_hackaton.backend_app.service;

import dev.msi_hackaton.backend_app.dao.entities.User;
import dev.msi_hackaton.backend_app.dao.repository.UserRepository;
import dev.msi_hackaton.backend_app.dto.UserDto;
import dev.msi_hackaton.backend_app.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final String userNotFoundMessage = "Пользователь не найден.";

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(userNotFoundMessage));
        return userMapper.toDto(user);
    }

    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserDto getUserByEmail(String email){
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException(userNotFoundMessage));
        return userMapper.toDto(user);
    } 

    @Transactional(readOnly = true)
    public UserDto getUserByPhone(String phone){
        User user = userRepository.findByPhone(phone)
            .orElseThrow(() -> new RuntimeException(userNotFoundMessage));
        return userMapper.toDto(user);
    } 
}