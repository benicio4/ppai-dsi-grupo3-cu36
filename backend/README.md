# Backend (Spring Boot)

API REST del CU 36. Puerto **8081**.

> Esta carpeta todavía no tiene el proyecto Spring Boot. Lo genera **una sola persona**,
> lo sube a `main` y el resto lo clona (Sprint 0).

## Crear el proyecto

En [start.spring.io](https://start.spring.io) (o con la extensión *Spring Initializr* de VS Code):

| Campo | Valor |
|---|---|
| Project | Maven |
| Language | Java |
| Spring Boot | la última versión estable que ofrezca (verificar que soporte Java 25) |
| Group | `ar.edu.utn.frc.dsi` |
| Artifact | `ppai` |
| Package name | `ar.edu.utn.frc.dsi.ppai` |
| Java | 25 |
| Dependencias | Spring Web, Spring Data JPA, H2 Database, Validation, Spring Boot DevTools |

Descomprimí el `.zip` **dentro de `backend/`** para que `pom.xml` quede en `backend/pom.xml`.
**No agregues Lombok.**

Después sumá a mano en el `pom.xml` (solo para encriptar contraseñas con BCrypt, sin Spring Security completo):

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-crypto</artifactId>
</dependency>
```

## Configuración

Pegá esto en `src/main/resources/application.properties`
(la clave del GPS está en el README de la API mock de la cátedra):

```properties
server.port=8081

spring.datasource.url=jdbc:h2:file:./data/ppai
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true

gps.api.base-url=http://localhost:8080
gps.api.xtr.key=<clave del modelo XTR-4500L, ver README de la API mock>
```

- Consola de la base: <http://localhost:8081/h2-console> (JDBC URL: `jdbc:h2:file:./data/ppai`, usuario `sa`, sin contraseña).
- La carpeta `data/` está en `.gitignore`: cada integrante tiene su propia base, que se carga sola al arrancar.

## Cómo ejecutarlo

```
cd backend
./mvnw spring-boot:run          # Windows: .\mvnw.cmd spring-boot:run
```

Antes, la API mock del GPS tiene que estar corriendo en el puerto 8080 (ver README de la raíz).

## Paquetes previstos

```
ar.edu.utn.frc.dsi.ppai
 ├─ boundary/      AuthController, SeguimientoBolsinesController, dto/
 ├─ gestor/        GestorSeguimientoBolsines, GestorNotificarUbicacionBolsin (CU 31)
 ├─ entidades/     Bolsin, Remito, Estado, CambioEstadoBolsin, ComisionMedica,
 │                 Empleado, Rol, Usuario, Sesion
 ├─ gps/           InterfazGPSTracker, UbicacionBolsin, XtrGpsTracker, GpsTrackerException
 ├─ correo/        InterfazCorreo, CorreoSimulado
 ├─ repositorio/   interfaces Spring Data
 ├─ seguridad/     AuthInterceptor, WebConfig
 └─ config/        DataLoader (datos de prueba)
```

Los métodos de los diagramas viven en las **entidades** (`bolsin.sosEnviado()`, `empleado.sosGCM()`, ...);
los repositorios solo traen datos. Los nombres de clases y métodos son los de `docs/`.

## Endpoints previstos

| Método y ruta | Qué hace |
|---|---|
| `POST /api/auth/login` | Valida usuario y contraseña, crea la `Sesion` y devuelve el token |
| `GET /api/seguimiento-bolsines` | Pasos 1-6: CM del usuario, bolsines enviados con su ubicación y avisos |
| `POST /api/seguimiento-bolsines/{numeroBolsin}/notificacion` | Pasos 8-11: notifica la ubicación al Gerente de la CM destino (CU 31) |
