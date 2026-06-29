package com.petshop.subsistema.servicios.service;

import com.petshop.dto.ClienteRequest;
import com.petshop.exception.RecursoNoEncontradoException;
import com.petshop.model.Cliente;
import com.petshop.subsistema.servicios.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public Cliente registrar(ClienteRequest request) {
        Cliente cliente = Cliente.builder()
                .nombre(request.getNombre())
                .telefono(request.getTelefono())
                .direccion(request.getDireccion())
                .correo(request.getCorreo())
                .build();
        return clienteRepository.save(cliente);
    }

    public Cliente actualizar(Long id, ClienteRequest request) {
        Cliente cliente = buscar(id);
        cliente.setNombre(request.getNombre());
        cliente.setTelefono(request.getTelefono());
        cliente.setDireccion(request.getDireccion());
        cliente.setCorreo(request.getCorreo());
        return clienteRepository.save(cliente);
    }

    @Transactional(readOnly = true)
    public Cliente buscar(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    public void eliminar(Long id) {
        clienteRepository.delete(buscar(id));
    }
}
