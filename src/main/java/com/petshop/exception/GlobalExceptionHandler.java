package com.petshop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleRecursoNoEncontrado(RecursoNoEncontradoException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<Map<String, Object>> handleStockInsuficiente(StockInsuficienteException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(OperacionInvalidaException.class)
    public ResponseEntity<Map<String, Object>> handleOperacionInvalida(OperacionInvalidaException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, "No tiene permisos para realizar esta operación");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errores.put(error.getField(), error.getDefaultMessage());
        }
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Error de validación");
        body.put("detalles", errores);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        return buildResponse(HttpStatus.CONFLICT, mensajeIntegridad(ex));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "No fue posible completar la operación. Intenta de nuevo o revisa los datos relacionados.");
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", message);
        return ResponseEntity.status(status).body(body);
    }

    private String mensajeIntegridad(DataIntegrityViolationException ex) {
        String detalle = rootMessage(ex).toLowerCase();

        if (detalle.contains("mascotas") && detalle.contains("citas_servicio")) {
            return "No se puede eliminar la mascota porque tiene citas registradas.";
        }
        if (detalle.contains("clientes") && detalle.contains("mascotas")) {
            return "No se puede eliminar el cliente porque tiene mascotas asociadas.";
        }
        if (detalle.contains("clientes") && detalle.contains("ventas")) {
            return "No se puede eliminar el cliente porque tiene ventas registradas.";
        }
        if (detalle.contains("servicios") && detalle.contains("citas_servicio")) {
            return "No se puede eliminar el servicio porque tiene citas registradas.";
        }
        if (detalle.contains("categorias") && detalle.contains("productos")) {
            return "No se puede eliminar la categoría porque tiene productos asociados.";
        }
        if (detalle.contains("productos") && detalle.contains("detalle_ventas")) {
            return "No se puede eliminar el producto porque ya aparece en ventas registradas.";
        }
        if (detalle.contains("productos") && detalle.contains("movimientos_inventario")) {
            return "No se puede eliminar el producto porque tiene movimientos de inventario registrados.";
        }

        return "No se puede eliminar este registro porque está relacionado con otros datos del sistema.";
    }

    private String rootMessage(Throwable throwable) {
        Throwable cursor = throwable;
        while (cursor.getCause() != null) {
            cursor = cursor.getCause();
        }
        return cursor.getMessage() != null ? cursor.getMessage() : throwable.getMessage();
    }
}
