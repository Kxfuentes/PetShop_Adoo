package com.petshop.subsistema.servicios.service;

import com.petshop.dto.MascotaRequest;
import com.petshop.exception.RecursoNoEncontradoException;
import com.petshop.model.Mascota;
import com.petshop.subsistema.servicios.repository.MascotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MascotaService {

    private final MascotaRepository mascotaRepository;
    private final ClienteService clienteService;

    public Mascota registrar(MascotaRequest request) {
        Mascota mascota = Mascota.builder()
                .nombre(request.getNombre())
                .especie(request.getEspecie())
                .raza(request.getRaza())
                .edad(request.getEdad())
                .peso(request.getPeso())
                .observaciones(request.getObservaciones())
                .cliente(clienteService.buscar(request.getClienteId()))
                .build();
        return mascotaRepository.save(mascota);
    }

    public Mascota actualizar(Long id, MascotaRequest request) {
        Mascota mascota = buscar(id);
        mascota.setNombre(request.getNombre());
        mascota.setEspecie(request.getEspecie());
        mascota.setRaza(request.getRaza());
        mascota.setEdad(request.getEdad());
        mascota.setPeso(request.getPeso());
        mascota.setObservaciones(request.getObservaciones());
        if (!mascota.getCliente().getId().equals(request.getClienteId())) {
            mascota.setCliente(clienteService.buscar(request.getClienteId()));
        }
        return mascotaRepository.save(mascota);
    }

    @Transactional(readOnly = true)
    public Mascota buscar(Long id) {
        return mascotaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Mascota no encontrada con id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Mascota> listarPorCliente(Long clienteId) {
        clienteService.buscar(clienteId);
        return mascotaRepository.findByClienteId(clienteId);
    }

    @Transactional(readOnly = true)
    public List<Mascota> listar() {
        return mascotaRepository.findAll();
    }

    public void eliminar(Long id) {
        mascotaRepository.delete(buscar(id));
    }
}
