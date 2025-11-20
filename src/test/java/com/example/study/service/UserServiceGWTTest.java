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

// UserService GWT 패턴 테스트
@ExtendWith(MockitoExtension.class)
class UserServiceGWTTest {

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
    void givenValidCommand_whenCreateUser_thenReturnsDto() {
        // Given
        UserCreateCommand command = new UserCreateCommand("GWT", "gwt@example.com", "pw");
        when(userJpaRepository.existsByEmail(command.email())).thenReturn(false);
        User saved = User.builder().name(command.username()).email(command.email()).passwordHash("hash").build();
        ReflectionTestUtils.setField(saved, "id", 20L);
        when(userJpaRepository.save(any(User.class))).thenReturn(saved);

        // When
        UserDto dto = userService.createUser(command);

        // Then
        assertThat(dto.getId()).isEqualTo(20L);
        verify(userJpaRepository).save(any(User.class));
    }

    @Test
    void givenDuplicateEmail_whenCreateUser_thenThrows() {
        // Given
        UserCreateCommand command = new UserCreateCommand("dup", "dup@example.com", "pw");
        when(userJpaRepository.existsByEmail(command.email())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.createUser(command))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining(ServiceExceptionCode.ALREADY_EXISTS_USER.getMessage());
        verify(userJpaRepository, never()).save(any(User.class));
    }

    @Test
    void givenDifferentEmail_whenUpdateUser_thenValidatesAndUpdates() {
        // Given
        when(userJpaRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userJpaRepository.existsByEmailAndIdNot("new@example.com", 1L)).thenReturn(false);
        UserUpdateCommand command = new UserUpdateCommand("수정", "new@example.com");

        // When
        UserDto dto = userService.updateUser(1L, command);

        // Then
        assertThat(dto.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    void givenExistingUser_whenDelete_thenDeletes() {
        // Given
        when(userJpaRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        userService.deleteUser(1L);

        // Then
        verify(userJpaRepository).delete(user);
    }
}
