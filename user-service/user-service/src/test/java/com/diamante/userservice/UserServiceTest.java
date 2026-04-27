package com.diamante.userservice;

import com.diamante.userservice.dto.UserPreferenceDTO;
import com.diamante.userservice.dto.UserRequestDTO;
import com.diamante.userservice.dto.UserResponseDTO;
import com.diamante.userservice.exception.UserAlreadyExistsException;
import com.diamante.userservice.exception.UserNotFoundException;
import com.diamante.userservice.model.FaixaPreco;
import com.diamante.userservice.model.TipoComida;
import com.diamante.userservice.model.User;
import com.diamante.userservice.repository.UserRepository;
import com.diamante.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private UserRequestDTO requestDTO;
    private User user;

    @BeforeEach
    void setUp() {
        requestDTO = UserRequestDTO.builder()
                .nome("João Silva")
                .email("joao@email.com")
                .senha("senha123")
                .tiposComidaPreferidos(List.of(TipoComida.ITALIANA))
                .faixaPrecoPreferida(FaixaPreco.MODERADO)
                .localizacao("São Paulo, SP")
                .build();

        user = User.builder()
                .id(1L)
                .nome("João Silva")
                .email("joao@email.com")
                .senha("senha123")
                .tiposComidaPreferidos(List.of(TipoComida.ITALIANA))
                .faixaPrecoPreferida(FaixaPreco.MODERADO)
                .localizacao("São Paulo, SP")
                .build();
    }

    @Test
    void deveCriarUsuarioComSucesso() {
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(user);

        UserResponseDTO response = userService.criarUsuario(requestDTO);

        assertThat(response).isNotNull();
        assertThat(response.getNome()).isEqualTo("João Silva");
        assertThat(response.getEmail()).isEqualTo("joao@email.com");
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaExiste() {
        when(userRepository.existsByEmail(any())).thenReturn(true);

        assertThatThrownBy(() -> userService.criarUsuario(requestDTO))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("já existe");
    }

    @Test
    void deveBuscarUsuarioPorIdComSucesso() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponseDTO response = userService.buscarPorId(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNome()).isEqualTo("João Silva");
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.buscarPorId(99L))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void deveBuscarPreferenciasDoUsuario() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserPreferenceDTO prefs = userService.buscarPreferencias(1L);

        assertThat(prefs.getLocalizacao()).isEqualTo("São Paulo, SP");
        assertThat(prefs.getFaixaPrecoPreferida()).isEqualTo(FaixaPreco.MODERADO);
        assertThat(prefs.getTiposComidaPreferidos()).contains(TipoComida.ITALIANA);
    }
}
