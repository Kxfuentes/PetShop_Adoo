package com.petshop.subsistema.comercial.service;

import com.petshop.dto.CategoriaRequest;
import com.petshop.exception.RecursoNoEncontradoException;
import com.petshop.model.Categoria;
import com.petshop.subsistema.comercial.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public Categoria crear(CategoriaRequest request) {
        Categoria categoria = Categoria.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .build();
        return categoriaRepository.save(categoria);
    }

    public Categoria modificar(Long id, CategoriaRequest request) {
        Categoria categoria = buscar(id);
        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());
        return categoriaRepository.save(categoria);
    }

    @Transactional(readOnly = true)
    public Categoria buscar(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada con id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Categoria> listar() {
        return categoriaRepository.findAll();
    }

    public void eliminar(Long id) {
        categoriaRepository.delete(buscar(id));
    }
}
