package com.petshop.subsistema.servicios.service;

import com.petshop.dto.ServicioRequest;
import com.petshop.exception.RecursoNoEncontradoException;
import com.petshop.model.Servicio;
import com.petshop.subsistema.servicios.repository.ServicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ServicioOfrecidoService {

    private final ServicioRepository servicioRepository;

    public Servicio crear(ServicioRequest request) {
        Servicio servicio = Servicio.builder()
                .nombre(request.getNombre())
                .precio(request.getPrecio())
                .duracionMinutos(request.getDuracionMinutos())
                .descripcion(request.getDescripcion())
                .build();
        return servicioRepository.save(servicio);
    }

    public Servicio modificarPrecio(Long id, ServicioRequest request) {
        Servicio servicio = buscar(id);
        servicio.setNombre(request.getNombre());
        servicio.setPrecio(request.getPrecio());
        servicio.setDuracionMinutos(request.getDuracionMinutos());
        servicio.setDescripcion(request.getDescripcion());
        return servicioRepository.save(servicio);
    }

    @Transactional(readOnly = true)
    public Servicio buscar(Long id) {
        return servicioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Servicio no encontrado con id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Servicio> listar() {
        return servicioRepository.findAll();
    }

    public void eliminar(Long id) {
        servicioRepository.delete(buscar(id));
    }
}
