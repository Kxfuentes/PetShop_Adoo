package com.petshop.config;

import com.petshop.model.*;
import com.petshop.model.enums.RolUsuario;
import com.petshop.repository.UsuarioRepository;
import com.petshop.subsistema.comercial.repository.CategoriaRepository;
import com.petshop.subsistema.comercial.repository.ProductoRepository;
import com.petshop.subsistema.servicios.repository.ClienteRepository;
import com.petshop.subsistema.servicios.repository.ServicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;
    private final ServicioRepository servicioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${petshop.security.default-admin-email}")
    private String adminEmail;

    @Value("${petshop.security.default-admin-password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            usuarioRepository.save(Usuario.builder()
                    .nombre("Administrador")
                    .correo(adminEmail)
                    .contrasena(passwordEncoder.encode(adminPassword))
                    .rol(RolUsuario.ADMIN)
                    .estado(true)
                    .build());

            usuarioRepository.save(Usuario.builder()
                    .nombre("Cajero Demo")
                    .correo("cajero@petshop.com")
                    .contrasena(passwordEncoder.encode("cajero123"))
                    .rol(RolUsuario.CAJERO)
                    .estado(true)
                    .build());

            usuarioRepository.save(Usuario.builder()
                    .nombre("Recepcionista Demo")
                    .correo("recepcion@petshop.com")
                    .contrasena(passwordEncoder.encode("recep123"))
                    .rol(RolUsuario.RECEPCIONISTA)
                    .estado(true)
                    .build());
        }

        if (categoriaRepository.count() == 0) {
            Categoria alimentos = categoriaRepository.save(Categoria.builder()
                    .nombre("Alimentos")
                    .descripcion("Alimentos para mascotas")
                    .build());

            productoRepository.save(Producto.builder()
                    .nombre("Croquetas Premium 15kg")
                    .descripcion("Alimento balanceado para perros adultos")
                    .precio(new BigDecimal("45.99"))
                    .stock(50)
                    .stockMinimo(10)
                    .categoria(alimentos)
                    .build());

            productoRepository.save(Producto.builder()
                    .nombre("Arena para Gatos 10kg")
                    .descripcion("Arena aglomerante")
                    .precio(new BigDecimal("18.50"))
                    .stock(30)
                    .stockMinimo(5)
                    .categoria(alimentos)
                    .build());
        }

        if (clienteRepository.count() == 0) {
            clienteRepository.save(Cliente.builder()
                    .nombre("María López")
                    .telefono("8888-1234")
                    .direccion("Managua, Nicaragua")
                    .correo("maria.lopez@email.com")
                    .build());
        }

        if (servicioRepository.count() == 0) {
            servicioRepository.save(Servicio.builder()
                    .nombre("Baño y Corte")
                    .precio(new BigDecimal("25.00"))
                    .duracionMinutos(60)
                    .descripcion("Baño completo y corte de pelo")
                    .build());

            servicioRepository.save(Servicio.builder()
                    .nombre("Consulta Veterinaria")
                    .precio(new BigDecimal("35.00"))
                    .duracionMinutos(30)
                    .descripcion("Consulta general veterinaria")
                    .build());
        }
    }
}
