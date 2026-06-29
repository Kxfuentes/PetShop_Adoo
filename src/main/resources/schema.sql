-- Script de referencia del modelo relacional (PostgreSQL)
-- Hibernate crea/actualiza las tablas con ddl-auto: update

CREATE TABLE IF NOT EXISTS usuarios (
    id          BIGSERIAL PRIMARY KEY,
    nombre      VARCHAR(255) NOT NULL,
    correo      VARCHAR(255) NOT NULL UNIQUE,
    contrasena  VARCHAR(255) NOT NULL,
    rol         VARCHAR(20)  NOT NULL,
    estado      BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS clientes (
    id        BIGSERIAL PRIMARY KEY,
    nombre    VARCHAR(255) NOT NULL,
    telefono  VARCHAR(50),
    direccion VARCHAR(255),
    correo    VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS mascotas (
    id             BIGSERIAL PRIMARY KEY,
    nombre         VARCHAR(255) NOT NULL,
    especie        VARCHAR(100),
    raza           VARCHAR(100),
    edad           INTEGER,
    peso           NUMERIC(8, 2),
    observaciones  TEXT,
    cliente_id     BIGINT NOT NULL REFERENCES clientes(id)
);

CREATE TABLE IF NOT EXISTS categorias (
    id          BIGSERIAL PRIMARY KEY,
    nombre      VARCHAR(255) NOT NULL UNIQUE,
    descripcion VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS productos (
    id           BIGSERIAL PRIMARY KEY,
    nombre       VARCHAR(255) NOT NULL,
    descripcion  VARCHAR(500),
    precio       NUMERIC(12, 2) NOT NULL,
    stock        INTEGER NOT NULL DEFAULT 0 CHECK (stock >= 0),
    stock_minimo INTEGER NOT NULL DEFAULT 0,
    categoria_id BIGINT REFERENCES categorias(id)
);

CREATE TABLE IF NOT EXISTS ventas (
    id         BIGSERIAL PRIMARY KEY,
    fecha      TIMESTAMP NOT NULL,
    total      NUMERIC(14, 2) NOT NULL,
    estado     VARCHAR(15) NOT NULL,
    cliente_id BIGINT NOT NULL REFERENCES clientes(id),
    usuario_id BIGINT REFERENCES usuarios(id)
);

CREATE TABLE IF NOT EXISTS detalle_ventas (
    id              BIGSERIAL PRIMARY KEY,
    venta_id        BIGINT NOT NULL REFERENCES ventas(id) ON DELETE CASCADE,
    producto_id     BIGINT NOT NULL REFERENCES productos(id),
    cantidad        INTEGER NOT NULL CHECK (cantidad > 0),
    precio_unitario NUMERIC(12, 2) NOT NULL,
    subtotal        NUMERIC(14, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS servicios (
    id               BIGSERIAL PRIMARY KEY,
    nombre           VARCHAR(255) NOT NULL,
    precio           NUMERIC(12, 2) NOT NULL,
    duracion_minutos INTEGER NOT NULL,
    descripcion      VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS citas_servicio (
    id          BIGSERIAL PRIMARY KEY,
    fecha_hora  TIMESTAMP NOT NULL,
    estado      VARCHAR(15) NOT NULL,
    observacion TEXT,
    mascota_id  BIGINT NOT NULL REFERENCES mascotas(id),
    servicio_id BIGINT NOT NULL REFERENCES servicios(id),
    usuario_id  BIGINT REFERENCES usuarios(id)
);

ALTER TABLE citas_servicio DROP CONSTRAINT IF EXISTS citas_servicio_estado_check;

ALTER TABLE citas_servicio
    ADD CONSTRAINT citas_servicio_estado_check
    CHECK (estado IN ('AGENDADA', 'REPROGRAMADA', 'CONFIRMADA', 'EN_ATENCION', 'FINALIZADA', 'CANCELADA'));

CREATE TABLE IF NOT EXISTS movimientos_inventario (
    id              BIGSERIAL PRIMARY KEY,
    producto_id     BIGINT NOT NULL REFERENCES productos(id),
    tipo_movimiento VARCHAR(10) NOT NULL,
    cantidad        INTEGER NOT NULL CHECK (cantidad > 0),
    fecha           TIMESTAMP NOT NULL,
    motivo          VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS notificaciones_stock (
    id          BIGSERIAL PRIMARY KEY,
    producto_id BIGINT NOT NULL REFERENCES productos(id),
    producto    VARCHAR(255) NOT NULL,
    stock       INTEGER NOT NULL,
    stock_minimo INTEGER NOT NULL,
    tipo        VARCHAR(20) NOT NULL,
    mensaje     VARCHAR(500) NOT NULL,
    leida       BOOLEAN NOT NULL DEFAULT FALSE,
    fecha       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_mascotas_cliente ON mascotas(cliente_id);
CREATE INDEX IF NOT EXISTS idx_ventas_cliente ON ventas(cliente_id);
CREATE INDEX IF NOT EXISTS idx_ventas_fecha ON ventas(fecha);
CREATE INDEX IF NOT EXISTS idx_citas_mascota ON citas_servicio(mascota_id);
CREATE INDEX IF NOT EXISTS idx_citas_fecha ON citas_servicio(fecha_hora);
CREATE INDEX IF NOT EXISTS idx_movimientos_producto ON movimientos_inventario(producto_id);
CREATE INDEX IF NOT EXISTS idx_notificaciones_stock_fecha ON notificaciones_stock(fecha DESC);
CREATE INDEX IF NOT EXISTS idx_notificaciones_stock_leida ON notificaciones_stock(leida);

CREATE OR REPLACE FUNCTION fn_notificar_stock_bajo()
RETURNS TRIGGER
LANGUAGE plpgsql
AS '
DECLARE
    tipo_notificacion VARCHAR(20);
    mensaje_notificacion VARCHAR(500);
    payload TEXT;
BEGIN
    IF TG_OP = ''UPDATE''
       AND OLD.stock <= OLD.stock_minimo
       AND NEW.stock > NEW.stock_minimo THEN

        tipo_notificacion := ''STOCK_RESUELTO'';
        mensaje_notificacion := ''Stock resuelto para '' || NEW.nombre || ''. Disponible: '' || NEW.stock || '', minimo: '' || NEW.stock_minimo;

        UPDATE notificaciones_stock
        SET leida = TRUE
        WHERE producto_id = NEW.id
          AND tipo IN (''STOCK_BAJO'', ''AGOTADO'')
          AND leida = FALSE;

        INSERT INTO notificaciones_stock (
            producto_id,
            producto,
            stock,
            stock_minimo,
            tipo,
            mensaje,
            leida
        ) VALUES (
            NEW.id,
            NEW.nombre,
            NEW.stock,
            NEW.stock_minimo,
            tipo_notificacion,
            mensaje_notificacion,
            TRUE
        );

        payload := json_build_object(
            ''productoId'', NEW.id,
            ''producto'', NEW.nombre,
            ''stock'', NEW.stock,
            ''stockMinimo'', NEW.stock_minimo,
            ''tipo'', tipo_notificacion,
            ''mensaje'', mensaje_notificacion
        )::TEXT;

        PERFORM pg_notify(''stock_bajo'', payload);

    ELSIF NEW.stock <= NEW.stock_minimo
       AND (TG_OP = ''INSERT'' OR OLD.stock IS DISTINCT FROM NEW.stock)
       AND (TG_OP = ''INSERT'' OR OLD.stock > NEW.stock_minimo OR NEW.stock = 0) THEN

        tipo_notificacion := CASE
            WHEN NEW.stock = 0 THEN ''AGOTADO''
            ELSE ''STOCK_BAJO''
        END;

        mensaje_notificacion := CASE
            WHEN NEW.stock = 0 THEN ''Producto agotado: '' || NEW.nombre
            ELSE ''Stock bajo para '' || NEW.nombre || ''. Disponible: '' || NEW.stock || '', minimo: '' || NEW.stock_minimo
        END;

        INSERT INTO notificaciones_stock (
            producto_id,
            producto,
            stock,
            stock_minimo,
            tipo,
            mensaje
        ) VALUES (
            NEW.id,
            NEW.nombre,
            NEW.stock,
            NEW.stock_minimo,
            tipo_notificacion,
            mensaje_notificacion
        );

        payload := json_build_object(
            ''productoId'', NEW.id,
            ''producto'', NEW.nombre,
            ''stock'', NEW.stock,
            ''stockMinimo'', NEW.stock_minimo,
            ''tipo'', tipo_notificacion,
            ''mensaje'', mensaje_notificacion
        )::TEXT;

        PERFORM pg_notify(''stock_bajo'', payload);
    END IF;

    RETURN NEW;
END;
';

DROP TRIGGER IF EXISTS trg_notificar_stock_bajo ON productos;

CREATE TRIGGER trg_notificar_stock_bajo
AFTER INSERT OR UPDATE OF stock, stock_minimo ON productos
FOR EACH ROW
EXECUTE FUNCTION fn_notificar_stock_bajo();
