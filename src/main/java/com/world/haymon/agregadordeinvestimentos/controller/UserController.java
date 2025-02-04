package com.world.haymon.agregadordeinvestimentos.controller;

import com.world.haymon.agregadordeinvestimentos.dto.CreateUserDTO;
import com.world.haymon.agregadordeinvestimentos.dto.UpdateUserDTO;
import com.world.haymon.agregadordeinvestimentos.entity.User;
import com.world.haymon.agregadordeinvestimentos.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody CreateUserDTO createUserDTO) {
        var userId = userService.createUser(createUserDTO);

        return ResponseEntity.created(URI.create("/v1/users/" + userId.toString())).build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable("userId") String userId) {
        var user = userService.getUserById(userId);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        var users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Void> updateUserById(@PathVariable("userId") String id, @RequestBody UpdateUserDTO updateUserDTO) {
        userService.updateUserById(id, updateUserDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserById(@PathVariable("userId") String userId) {
        userService.deleteUserById(userId);
        return ResponseEntity.noContent().build();
    }
}
