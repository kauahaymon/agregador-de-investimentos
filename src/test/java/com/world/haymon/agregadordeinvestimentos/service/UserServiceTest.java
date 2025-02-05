package com.world.haymon.agregadordeinvestimentos.service;

import com.world.haymon.agregadordeinvestimentos.dto.CreateUserDTO;
import com.world.haymon.agregadordeinvestimentos.entity.User;
import com.world.haymon.agregadordeinvestimentos.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userArgumentcaptor;

    @Nested
    class createUser {

        @Test
        @DisplayName("Should create a name successfully")
        void shouldCreateAUser() {

            // arrange
            User user = new User(
                    UUID.randomUUID(),
                    "username",
                    "email@email.com",
                    "123",
                    Instant.now(),
                    null
            );

            // act
            doReturn(user).when(userRepository).save(userArgumentcaptor.capture());

            CreateUserDTO input = new CreateUserDTO(
                    "username",
                    "email@email.com",
                    "password");

            UUID output = userService.createUser(input);

            // assert
            assertNotNull(output);
            assertEquals(input.username(), userArgumentcaptor.getValue().getUsername());
            assertEquals(input.email(), userArgumentcaptor.getValue().getEmail());
            assertEquals(input.password(), userArgumentcaptor.getValue().getPassword());
        }

        @Test
        @DisplayName("Should throw exception when error occurs")
        void shouldThrowExceptionWhenErrorOccurs() {

            // arrange
            doThrow(new RuntimeException()).when(userRepository).save(any());

            CreateUserDTO input = new CreateUserDTO(
                    "username",
                    "email@email.com",
                    "password");

            // act & assert
            assertThrows(RuntimeException.class, () -> userService.createUser(input));
        }
    }
}