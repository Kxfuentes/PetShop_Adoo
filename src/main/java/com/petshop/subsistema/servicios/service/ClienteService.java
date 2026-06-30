package com.petshop.subsistema.servicios.service;

import com.petshop.dto.ClienteRequest;
import com.petshop.exception.OperacionInvalidaException;
import com.petshop.exception.RecursoNoEncontradoException;
import com.petshop.model.Cliente;
import com.petshop.subsistema.comercial.repository.VentaRepository;
import com.petshop.subsistema.servicios.repository.ClienteRepository;
import com.petshop.subsistema.servicios.repository.CitaServicioRepository;
import com.petshop.subsistema.servicios.repository.MascotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final MascotaRepository mascotaRepository;
    private final CitaServicioRepository citaServicioRepository;
    private final VentaRepository ventaRepository;

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
        Cliente cliente = buscar(id);
        if (ventaRepository.existsByClienteId(id)) {
            throw new OperacionInvalidaException(
                    "No se puede eliminar el cliente '" + cliente.getNombre()
                            + "' porque tiene ventas registradas.");
        }
        if (citaServicioRepository.existsByMascotaClienteId(id)) {
            throw new OperacionInvalidaException(
                    "No se puede eliminar el cliente '" + cliente.getNombre()
                            + "' porque una de sus mascotas tiene citas registradas.");
        }
        if (mascotaRepository.existsByClienteId(id)) {
            throw new OperacionInvalidaException(
                    "No se puede eliminar el cliente '" + cliente.getNombre()
                            + "' porque tiene mascotas asociadas. Elimina o reasigna esas mascotas primero.");
        }
        clienteRepository.delete(cliente);
    }
}
