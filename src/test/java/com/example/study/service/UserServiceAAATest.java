package com.example.study.service;

import com.example.study.common.ServiceException;
import com.example.study.common.ServiceExceptionCode;
import com.example.study.entity.User;
import com.example.study.repository.UserJpaRepository;
import com.example.study.service.dto.UserCreateCommand;
import com.example.study.service.dto.UserDto;
import com.example.study.service.dto.UserUpdateCommand;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// UserService AAA 패턴 테스트
@ExtendWith(MockitoExtension.class)
class UserServiceAAATest {

    @Mock
    private UserJpaRepository userJpaRepository;
    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("테스트 사용자")
                .email("origin@example.com")
                .passwordHash("hash")
                .build();
        ReflectionTestUtils.setField(user, "id", 1L);
    }

    @Test
    void createUser_shouldSaveAndReturnDto() {
        // Arrange
        UserCreateCommand command = new UserCreateCommand("새 사용자", "new@example.com", "pw");
        when(userJpaRepository.existsByEmail(command.email())).thenReturn(false);
        User saved = User.builder()
                .name(command.username())
                .email(command.email())
                .passwordHash("hashed")
                .build();
        ReflectionTestUtils.setField(saved, "id", 10L);
        when(userJpaRepository.save(any(User.class))).thenReturn(saved);

        // Act
        UserDto dto = userService.createUser(command);

        // Assert
        verify(userJpaRepository).save(any(User.class));
        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    void createUser_shouldThrowException_whenEmailExists() {
        // Arrange
        UserCreateCommand command = new UserCreateCommand("dup", "dup@example.com", "pw");
        when(userJpaRepository.existsByEmail(command.email())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.createUser(command))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining(ServiceExceptionCode.ALREADY_EXISTS_USER.getMessage());
        verify(userJpaRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_shouldChangeProfileAndValidateDuplicate() {
        // Arrange
        when(userJpaRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userJpaRepository.existsByEmailAndIdNot("new@example.com", 1L)).thenReturn(false);
        UserUpdateCommand command = new UserUpdateCommand("수정", "new@example.com");

        // Act
        UserDto dto = userService.updateUser(1L, command);

        // Assert
        assertThat(dto.getUsername()).isEqualTo("수정");
        assertThat(dto.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    void deleteUser_shouldRemoveEntity() {
        // Arrange
        when(userJpaRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userJpaRepository).delete(user);
    }
}
