package com.world.haymon.agregadordeinvestimentos.service;

import com.world.haymon.agregadordeinvestimentos.dto.CreateUserDTO;
import com.world.haymon.agregadordeinvestimentos.dto.UpdateUserDTO;
import com.world.haymon.agregadordeinvestimentos.entity.User;
import com.world.haymon.agregadordeinvestimentos.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

   @Autowired
   private UserRepository userRepository;

    public UUID createUser(CreateUserDTO createUserDto) {

        // DTO -> ENTITY
        var entity = new User(
                null,
                createUserDto.username(),
                createUserDto.email(),
                createUserDto.password(),
                Instant.now(),
                null);

        var userSaved = userRepository.save(entity);

        return userSaved.getId();
    }

    public Optional<User> getUserById(String id) {
       return userRepository.findById(UUID.fromString(id));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void updateUserById(String id, UpdateUserDTO updateUserDTO) {

        var userId = UUID.fromString(id);
        var userEntity = userRepository.findById(userId);

        if (userEntity.isPresent()) {
            var user = userEntity.get();
            if (updateUserDTO.username() != null) {
                user.setUsername(updateUserDTO.username());
            }
            if (updateUserDTO.password() != null) {
                user.setPassword(updateUserDTO.password());
            }
            userRepository.save(user);
        }
    }

    public void deleteUserById(String id) {
        var userId = UUID.fromString(id);

        var userExists = userRepository.existsById(userId);
        if(userExists) {
            userRepository.deleteById(userId);
        }
    }
}
