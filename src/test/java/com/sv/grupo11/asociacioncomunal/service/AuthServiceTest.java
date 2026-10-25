package com.sv.grupo11.asociacioncomunal.service;

import com.sv.grupo11.asociacioncomunal.exception.RecursoNoEncontradoException;
import com.sv.grupo11.asociacioncomunal.exception.ReglaNegocioException;
import com.sv.grupo11.asociacioncomunal.exception.ValidacionException;
import com.sv.grupo11.asociacioncomunal.model.Rol;
import com.sv.grupo11.asociacioncomunal.model.Usuario;
import com.sv.grupo11.asociacioncomunal.repository.UsuarioRepository;
import com.sv.grupo11.asociacioncomunal.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    private AuthService service;
    private Usuario admin;

    @BeforeEach
    void setUp() {
        service = new AuthService(usuarioRepository);
        admin = new Usuario(1, "admin", PasswordUtil.hash("admin123"), Rol.ADMINISTRADOR, null);
    }

    @Test
    @DisplayName("Login correcto devuelve el usuario cuando la contrasena coincide con el hash")
    void login_exitoso() {
        when(usuarioRepository.buscarPorNombre("admin")).thenReturn(Optional.of(admin));
        Optional<Usuario> sesion = service.login("admin", "admin123");
        assertTrue(sesion.isPresent());
        assertEquals(Rol.ADMINISTRADOR, sesion.get().getRol());
    }

    @Test
    void login_contrasenaIncorrecta_devuelveVacio() {
        when(usuarioRepository.buscarPorNombre("admin")).thenReturn(Optional.of(admin));
        assertTrue(service.login("admin", "otra").isEmpty());
    }

    @Test
    void login_usuarioInexistente_devuelveVacio() {
        when(usuarioRepository.buscarPorNombre("nadie")).thenReturn(Optional.empty());
        assertTrue(service.login("nadie", "x").isEmpty());
    }

    @Test
    @DisplayName("Crear usuario guarda la contrasena hasheada, nunca en claro")
    void crearUsuario_guardaHash() {
        when(usuarioRepository.buscarPorNombre("nuevo")).thenReturn(Optional.empty());
        when(usuarioRepository.guardar(any(Usuario.class))).thenReturn(new Usuario(9, "nuevo", "hash", Rol.SECRETARIO, null));

        service.crearUsuario("nuevo", "clave123", Rol.SECRETARIO, null);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).guardar(captor.capture());
        String almacenada = captor.getValue().getContrasenaHash();
        assertAll(
                () -> assertNotEquals("clave123", almacenada),
                () -> assertTrue(PasswordUtil.esHash(almacenada)),
                () -> assertTrue(captor.getValue().validarContrasena("clave123")));
    }

    @Test
    void crearUsuario_contrasenaCorta_lanzaValidacion() {
        assertThrows(ValidacionException.class, () -> service.crearUsuario("x", "123", Rol.SOCIO, 1));
        verifyNoInteractions(usuarioRepository);
    }

    @Test
    void crearUsuario_socioSinIdSocio_lanzaValidacion() {
        assertThrows(ValidacionException.class, () -> service.crearUsuario("socio9", "clave123", Rol.SOCIO, null));
    }

    @Test
    void crearUsuario_nombreRepetido_lanzaReglaNegocio() {
        when(usuarioRepository.buscarPorNombre("admin")).thenReturn(Optional.of(admin));
        assertThrows(ReglaNegocioException.class, () -> service.crearUsuario("admin", "clave123", Rol.TESORERO, null));
        verify(usuarioRepository, never()).guardar(any());
    }

    @Test
    @DisplayName("Regla: no puede eliminarse al unico administrador")
    void eliminarUsuario_ultimoAdministrador_lanzaReglaNegocio() {
        when(usuarioRepository.buscarPorId(1)).thenReturn(Optional.of(admin));
        when(usuarioRepository.contarPorRol(Rol.ADMINISTRADOR)).thenReturn(1);

        assertThrows(ReglaNegocioException.class, () -> service.eliminarUsuario(1));
        verify(usuarioRepository, never()).eliminar(1);
    }

    @Test
    void eliminarUsuario_existente_loElimina() {
        Usuario tesorero = new Usuario(3, "tesorero", "h", Rol.TESORERO, null);
        when(usuarioRepository.buscarPorId(3)).thenReturn(Optional.of(tesorero));

        service.eliminarUsuario(3);

        verify(usuarioRepository, times(1)).eliminar(3);
    }

    @Test
    void eliminarUsuario_inexistente_lanzaNoEncontrado() {
        when(usuarioRepository.buscarPorId(77)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> service.eliminarUsuario(77));
    }
}
