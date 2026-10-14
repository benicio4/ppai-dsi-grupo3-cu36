# Decisiones técnicas

## Tomadas

| Tema | Decisión |
|---|---|
| Lombok | No (getters y setters generados por el IDE) |
| Persistencia | JPA + Hibernate, con DTOs hacia el front |
| Base de datos | H2 en archivo (`backend/data/`, ignorada por Git; se carga sola con un `DataLoader`) |
| Login | Sí, con `Usuario` y `Sesion` del modelo (token en cada pedido) |
| GPS Tracker | Solo modelo XTR-4500L por ahora, detrás de la interfaz `InterfazGPSTracker` |
| Falla parcial del GPS | Se muestran los demás bolsines y un aviso por cada uno que falle |
| Java | 25 LTS |
| Puertos | API mock 8080 · Backend 8081 · Frontend 5173 |
| Repositorio | Monorepo: `backend/`, `frontend/`, `docs/` |

## Por defecto, a confirmar

- **Estado del Gestor:** una instancia nueva por pedido (`@Scope("prototype")`), de modo que los
  atributos del diagrama siguen siendo campos y nada se comparte entre usuarios.
  Alternativas: Gestor de sesión (`@SessionScope`) o Gestor sin estado.

## Pendientes

1. Mapa real: Google Maps (API key y facturación) u otra opción (Leaflet / OpenStreetMap). Consultar al tutor por la consistencia con el modelo.
2. Correo real: SMTP de Gmail, Mailtrap, MailHog u otro. Por ahora `CorreoSimulado` escribe en el log.
3. Patrón de diseño de Gamma: reunión con el tutor (candidatos: Adapter o Strategy para el GPS, State para la máquina de estados).
4. Otros modelos de GPS (NavTrack QX-7A, GeoPulse MTR-900): ¿se suman después?
5. Unificar nombres distintos entre diagramas:
   `esTuCMOrigenCMUsuario` / `esTuCMOrigenCMDeUsuario`, `getNumeroPrecinto` / `getNroPrecinto`,
   `esTuCMCMDestino` / `esTuCMCMDestinoBolsin`.
6. Cantidad real de integrantes y fechas de entrega del cronograma (para dimensionar los sprints).
7. Seguridad: si el login propio alcanza o se prefiere Spring Security completo.
8. Esquema de la BD: `ddl-auto=update` + `DataLoader` (por defecto) o Flyway.
9. Verificar si la API mock (hecha para Java 17) corre bien con Java 25.
