package com.petshop.subsistema.comercial.service;

import com.petshop.dto.ProductoRequest;
import com.petshop.exception.RecursoNoEncontradoException;
import com.petshop.model.Categoria;
import com.petshop.model.Producto;
import com.petshop.subsistema.comercial.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaService categoriaService;

    public Producto crear(ProductoRequest request) {
        Producto producto = Producto.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .precio(request.getPrecio())
                .stock(request.getStock())
                .stockMinimo(request.getStockMinimo())
                .categoria(resolverCategoria(request.getCategoriaId()))
                .build();
        return productoRepository.save(producto);
    }

    public Producto modificar(Long id, ProductoRequest request) {
        Producto producto = buscar(id);
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setStockMinimo(request.getStockMinimo());
        producto.setCategoria(resolverCategoria(request.getCategoriaId()));
        return productoRepository.save(producto);
    }

    @Transactional(readOnly = true)
    public Producto buscar(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Producto> listar() {
        return productoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Producto> listarConAlertaStock() {
        return productoRepository.findAll().stream()
                .filter(Producto::requiereAlertaStock)
                .toList();
    }

    public void eliminar(Long id) {
        productoRepository.delete(buscar(id));
    }

    private Categoria resolverCategoria(Long categoriaId) {
        if (categoriaId == null) {
            return null;
        }
        return categoriaService.buscar(categoriaId);
    }
}
