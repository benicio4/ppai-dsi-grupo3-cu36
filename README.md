# PPAI 2026 – DSI · CU 36 Consultar seguimiento de bolsines

Proyecto Práctico de Aplicación Integrador de **Diseño de Sistemas de Información**
(UTN FRC – Ing. en Sistemas de Información), ciclo lectivo 2026.

**Curso:** 3K__ · **Grupo:** G__ · **Docente tutor:** ________

## Integrantes

| Nombre y apellido | Legajo | Usuario de GitHub |
|---|---|---|
|  |  |  |
|  |  |  |
|  |  |  |
|  |  |  |

## Qué hace

Un Encargado de Bolsines consulta en un mapa la ubicación de los bolsines en estado
*Enviado* cuya comisión médica (CM) de origen es la de su usuario, y puede notificar la
ubicación por correo al Gerente de la CM destino (CU 31 incluido).

## Tecnologías

| Capa | Tecnología |
|---|---|
| Backend | Java 25 (LTS), Spring Boot, Spring Data JPA |
| Base de datos | H2 (en archivo) |
| Frontend | React + Vite |
| GPS Tracker | API mock de la cátedra: https://github.com/LiberaeMentis/dsi-api-bolsines (modelo XTR-4500L) |
| Gestión | Jira (Scrum) + GitHub |

## Requisitos previos

- JDK 25, Node.js (LTS), Git y VS Code.
- Al abrir la carpeta en VS Code, aceptá instalar las **extensiones recomendadas**.
- La API mock de la cátedra está hecha para Java 17: si no arranca con el JDK 25, usar un JDK 17 solo para ella.

## Cómo levantar el proyecto (3 procesos)

| Proceso | Puerto | Dónde |
|---|---|---|
| API mock del GPS | 8080 | Repo aparte de la cátedra (clonarlo **fuera** de esta carpeta) |
| Backend Spring Boot | 8081 | `backend/` |
| Frontend React | 5173 | `frontend/` |

1. **API mock:** clonar el repo de la cátedra y ejecutar `./mvnw spring-boot:run`
   (en Windows `.\mvnw.cmd spring-boot:run`). Verificar en `http://localhost:8080/health`.
2. **Backend:** ver [`backend/README.md`](backend/README.md).
3. **Frontend:** ver [`frontend/README.md`](frontend/README.md).

## Usuarios de demostración

_A completar cuando esté cargado el `DataLoader`._

## Estructura del repositorio

```
.
├── backend/    Spring Boot (API REST, Gestor, entidades, persistencia)
├── frontend/   React (Pantalla de seguimiento de bolsines)
├── docs/       Diagramas (.drawio), descripción del CU y decisiones
├── .github/    Plantilla de Pull Request
└── CONTRIBUTING.md   Cómo trabajamos (ramas, commits, revisiones)
```

## Cómo trabajamos

Leer [`CONTRIBUTING.md`](CONTRIBUTING.md) antes de tocar código.
