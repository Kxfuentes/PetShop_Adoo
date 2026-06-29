# PetShop Management System

Sistema de Gestión integral para Tienda de Mascotas, implementado según el informe OOAD (Análisis y Diseño Orientado a Objetos) con arquitectura en tres capas y subsistemas **Comercial** y **Servicios**.

## Interpretación del sistema

El documento modela una tienda de mascotas con dos subsistemas desacoplados:

| Subsistema | Responsabilidad | Entidades principales |
|------------|-----------------|------------------------|
| **Comercial** | Ventas, productos, categorías e inventario | Producto, Categoría, Inventario, Venta, DetalleVenta |
| **Servicios** | Clientes, mascotas, citas y servicios | Cliente, Mascota, Servicio, CitaServicio |

Los **Gestores** (`GestorVentas`, `GestorCitas`) actúan como controladores de lógica de negocio (patrón Controller del documento), orquestando validaciones, persistencia y transiciones de estado.

### Flujos críticos

**UC-01 Registrar Venta:** validar stock → crear venta y detalles → calcular total → descontar inventario → estado `PAGADA`.

**UC-02 Agendar Servicio:** validar cliente/mascota → consultar disponibilidad horaria → crear cita en estado `AGENDADA` sin solapamiento.

## Requisitos

- JDK 21+
- Maven 3.9+
- PostgreSQL 14+
- IntelliJ IDEA Community (recomendado) o VS Code con Extension Pack for Java

## Configuración de PostgreSQL

```sql
CREATE DATABASE petshop_db;
CREATE USER postgres WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE petshop_db TO postgres;
```

Ajuste credenciales en `src/main/resources/application.yml` si es necesario:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/petshop_db
    username: postgres
    password: postgres
```

Hibernate crea/actualiza las tablas automáticamente (`ddl-auto: update`). El script de referencia está en `src/main/resources/schema.sql`.

## Ejecutar en IntelliJ IDEA

1. **File → Open** → seleccionar la carpeta `petshop-management`
2. IntelliJ detectará el proyecto Maven e importará dependencias
3. Configurar **Project SDK** en Java 21
4. Crear la base de datos PostgreSQL `petshop_db`
5. Ejecutar `PetShopApplication.java` (clic derecho → Run)
6. La aplicación inicia en `http://localhost:8080`

## Usuarios demo (carga automática)

| Rol | Correo | Contraseña |
|-----|--------|------------|
| ADMIN | admin@petshop.com | admin123 |
| CAJERO | cajero@petshop.com | cajero123 |
| RECEPCIONISTA | recepcion@petshop.com | recep123 |

## Autenticación

Spring Security con **HTTP Basic Auth**. En Postman o curl:

```
Authorization: Basic <base64(correo:contraseña)>
```

Ejemplo:
```bash
echo -n 'admin@petshop.com:admin123' | base64
# Resultado: YWRtaW5AcGV0c2hvcC5jb206YWRtaW4xMjM=
```

## Estructura de paquetes

```
com.petshop
├── config/              # Inicialización de datos
├── controller/          # Usuarios y reportes
├── service/             # Servicios compartidos
├── repository/          # Repositorio de usuarios
├── model/               # Entidades JPA y enums
├── dto/                 # Objetos de transferencia
├── exception/           # Excepciones y @ControllerAdvice
├── security/            # Spring Security
└── subsistema/
    ├── comercial/       # controller, service, repository
    └── servicios/       # controller, service, repository
```

## Endpoints principales

### Usuarios (ADMIN)
| Método | URL | Descripción |
|--------|-----|-------------|
| POST | `/api/usuarios` | Crear usuario |
| GET | `/api/usuarios` | Listar usuarios |

### Subsistema Comercial (ADMIN, CAJERO)
| Método | URL | Descripción |
|--------|-----|-------------|
| POST | `/api/comercial/categorias` | Crear categoría |
| POST | `/api/comercial/productos` | Crear producto |
| POST | `/api/comercial/inventario/movimientos` | Registrar entrada/salida |
| POST | `/api/comercial/ventas` | **Registrar venta (UC-01)** |
| POST | `/api/comercial/ventas/{id}/anular` | Anular venta |
| GET | `/api/comercial/productos/alertas-stock` | Productos bajo stock mínimo |

### Subsistema Servicios (ADMIN, RECEPCIONISTA)
| Método | URL | Descripción |
|--------|-----|-------------|
| POST | `/api/servicios/clientes` | Registrar cliente |
| POST | `/api/servicios/mascotas` | Registrar mascota |
| POST | `/api/servicios/servicios` | Crear servicio |
| POST | `/api/servicios/citas` | **Agendar cita (UC-02)** |
| POST | `/api/servicios/citas/{id}/confirmar` | Confirmar cita |
| POST | `/api/servicios/citas/{id}/cancelar` | Cancelar cita |
| GET | `/api/servicios/citas/disponibilidad` | Consultar disponibilidad |

### Reportes (todos los roles)
| Método | URL | Descripción |
|--------|-----|-------------|
| GET | `/api/reportes/ventas?inicio=...&fin=...` | Reporte de ventas |
| GET | `/api/reportes/inventario` | Alertas de inventario |
| GET | `/api/reportes/citas` | Resumen de citas |

## Ejemplos de requests (Postman/curl)

### Registrar cliente
```bash
curl -u recepcion@petshop.com:recep123 -X POST http://localhost:8080/api/servicios/clientes \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Juan Pérez","telefono":"8888-9999","correo":"juan@email.com"}'
```

### Registrar mascota
```bash
curl -u recepcion@petshop.com:recep123 -X POST http://localhost:8080/api/servicios/mascotas \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Max","especie":"Perro","raza":"Labrador","edad":3,"clienteId":1}'
```

### Registrar venta (UC-01)
```bash
curl -u cajero@petshop.com:cajero123 -X POST http://localhost:8080/api/comercial/ventas \
  -H "Content-Type: application/json" \
  -d '{"clienteId":1,"detalles":[{"productoId":1,"cantidad":2}]}'
```

### Agendar servicio (UC-02)
```bash
curl -u recepcion@petshop.com:recep123 -X POST http://localhost:8080/api/servicios/citas \
  -H "Content-Type: application/json" \
  -d '{"mascotaId":1,"servicioId":1,"fechaHora":"2026-06-25T10:00:00","observacion":"Primera cita"}'
```

### Movimiento de inventario (entrada)
```bash
curl -u admin@petshop.com:admin123 -X POST http://localhost:8080/api/comercial/inventario/movimientos \
  -H "Content-Type: application/json" \
  -d '{"productoId":1,"tipoMovimiento":"ENTRADA","cantidad":20,"motivo":"Compra proveedor"}'
```

### Reporte de ventas
```bash
curl -u admin@petshop.com:admin123 \
  "http://localhost:8080/api/reportes/ventas?inicio=2026-01-01T00:00:00&fin=2026-12-31T23:59:59"
```

## Reglas de negocio implementadas

- No se vende sin stock suficiente (`StockInsuficienteException`)
- El stock nunca es negativo
- Descuento automático de inventario al confirmar venta (`@Transactional`)
- Validación de disponibilidad antes de agendar cita
- No se permiten citas solapadas para la misma mascota
- Solo ADMIN puede anular ventas en estado `PAGADA`

## Compilar y ejecutar por terminal

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home
cd petshop-management
mvn spring-boot:run
```

## Tecnologías

- Java 21
- Spring Boot 3.3.5
- Spring Data JPA + Hibernate
- Spring Security (Basic Auth + roles)
- PostgreSQL
- Lombok
- Jakarta Bean Validation
