package com.world.haymon.agregadordeinvestimentos.service;

import com.world.haymon.agregadordeinvestimentos.dto.CreateUserDTO;
import com.world.haymon.agregadordeinvestimentos.dto.UpdateUserDTO;
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

import javax.swing.text.html.Option;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userArgumentcaptor;

    @Captor
    private ArgumentCaptor<UUID> uuidArgumentcaptor;

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

    @Nested
    class getUserById {

        @Test
        @DisplayName("Should get user by id when optional is present")
        void shouldGetUserByIdWithSuccessWhenOptionalIsPresent() {

            // arrange
            User user = new User(
                    UUID.randomUUID(),
                    "username",
                    "email@email.com",
                    "123",
                    Instant.now(),
                    null
            );
            doReturn(Optional.of(user)).when(userRepository).findById(uuidArgumentcaptor.capture());

            // act
            Optional<User> output = userService.getUserById(user.getId().toString());

            // assert
            assertTrue(output.isPresent());
            assertEquals(user.getId(), uuidArgumentcaptor.getValue());
        }
        @Test
        @DisplayName("Should get user by id when optional is empty")
        void shouldGetUserByIdWithSuccessWhenOptionalIsEmpty() {

            // arrange
            UUID userId = UUID.randomUUID();

            doReturn(Optional.empty()).when(userRepository).findById(uuidArgumentcaptor.capture());

            // act
            Optional<User> output = userService.getUserById(userId.toString());

            // assert
            assertTrue(output.isEmpty());
            assertEquals(userId, uuidArgumentcaptor.getValue());
        }
    }

    @Nested 
    class getAllUsers {
    	
        @Test
        @DisplayName("Should return a list of users")
        void shouldReturnAllUsersWithSuccess() {
        	
        	// arrange
            User user = new User(
                    UUID.randomUUID(),
                    "username",
                    "email@email.com",
                    "123",
                    Instant.now(),
                    null
            );

            List<User> userList = List.of(user);
            doReturn(userList).when(userRepository).findAll();

            // act
            List<User> output = userService.getAllUsers();

            // assert
            assertEquals(userList.size(), output.size());
        }
    }

    @Nested
    class deleteUserById {

        @Test
        @DisplayName("Should delete a user by id when exists")
        void shouldDeleteUserByIdWhenExists() {

            // arrange
            doReturn(true)
                    .when(userRepository)
                    .existsById(uuidArgumentcaptor.capture());

            doNothing()
                    .when(userRepository)
                    .deleteById(uuidArgumentcaptor.capture());

            UUID userId = UUID.randomUUID();

            // act
            userService.deleteUserById(userId.toString());

            // assert
            var idList = uuidArgumentcaptor.getAllValues();
            assertEquals(userId, idList.get(0));
            assertEquals(userId, idList.get(1));
        }

        @Test
        @DisplayName("Should not delete a user by id when Not exists")
        void shouldDeleteUserByIdWhenNotExists() {

            // arrange
            doReturn(true)
                    .when(userRepository)
                    .existsById(uuidArgumentcaptor.capture());

            UUID userId = UUID.randomUUID();

            // act
            userService.deleteUserById(userId.toString());

            // assert
            assertEquals(userId, uuidArgumentcaptor.getValue());
        }
    }

    @Nested
    class updateUserById {

        @Test
        @DisplayName("Should delete a user by id when username and password is filled")
        void shouldDeleteUserByIdWhenUsernameAndPasswordIsFilled() {

            // arrange
            UpdateUserDTO updateUserDTO = new UpdateUserDTO(
                    "newUsername",
                    "newPassword"
            );

            User user = new User(
                    UUID.randomUUID(),
                    "username",
                    "email@email.com",
                    "123",
                    Instant.now(),
                    null
            );
            doReturn(Optional.of(user))
                    .when(userRepository)
                    .findById(uuidArgumentcaptor.capture());

            doReturn(user)
                    .when(userRepository)
                    .save(userArgumentcaptor.capture());

            // act
            userService.updateUserById(user.getId().toString(), updateUserDTO);

            // assert
            assertEquals(user.getId(), uuidArgumentcaptor.getValue());

            User userCaptured = userArgumentcaptor.getValue();

            assertEquals(updateUserDTO.username(), userCaptured.getUsername());
            assertEquals(updateUserDTO.password(), userCaptured.getPassword());

            verify(userRepository, times(1))
                    .findById(uuidArgumentcaptor.capture());
            verify(userRepository, times(1))
                    .save(user);
        }

        @Test
        @DisplayName("Should not delete a user when NOT exists")
        void shouldNotDeleteUserWhenNotExists() {

            // arrange
            UpdateUserDTO updateUserDTO = new UpdateUserDTO(
                    "newUsername",
                    "newPassword"
            );

            UUID userId = UUID.randomUUID();

            doReturn(Optional.empty())
                    .when(userRepository)
                    .findById(uuidArgumentcaptor.capture());

            // act
            userService.updateUserById(userId.toString(), updateUserDTO);

            // assert
            assertEquals(userId, uuidArgumentcaptor.getValue());

            verify(userRepository, times(1))
                    .findById(uuidArgumentcaptor.capture());
            verify(userRepository, times(0))
                    .save(any());
        }
    }
}