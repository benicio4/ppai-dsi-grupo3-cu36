# Cómo trabajamos

Reglas simples para que ocho personas puedan tocar el mismo código sin pisarse.

## 1. Ramas

- `main` está **protegida**: nadie sube directo. Todo entra por Pull Request.
- Una rama por tarea de Jira, siempre creada desde `main` actualizada.
- Nombre: `CLAVE-NUMERO-descripcion-corta`, por ejemplo `DSI-12-entidad-bolsin`.
  (Reemplazá `DSI` por la clave real de su proyecto de Jira.)

```
git checkout main
git pull
git checkout -b DSI-12-entidad-bolsin
```

## 2. Commits

- Mensajes cortos, en imperativo, con la clave de Jira adelante:
  `DSI-12 Agrega entidad Bolsin con su estado actual`
- Commits chicos y frecuentes: mejor cinco commits claros que uno gigante.
- No se commitean credenciales, archivos de la base H2 (`backend/data/`) ni carpetas
  de IDE (el `.gitignore` ya los excluye; si algo aparece, avisá).

## 3. Pull Requests

1. Subí tu rama (`git push -u origin DSI-12-entidad-bolsin`) y abrí el PR en GitHub.
2. Completá la plantilla (se carga sola).
3. **El revisor es siempre alguien distinto de quien escribió el código.** Así todos
   terminamos conociendo todas las partes, que es lo que nos van a preguntar en la defensa oral.
4. Hace falta al menos **1 aprobación** para hacer *merge*.
5. Después del merge, borrá la rama y mové la tarea a "Hecho" en Jira.

## 4. Definición de "Hecho"

Una tarea está hecha cuando:

- [ ] Compila y arranca sin errores.
- [ ] La probaste manualmente (y anotaste cómo en el PR).
- [ ] Coincide con los diagramas del modelo (nombres de clases y métodos incluidos).
- [ ] Pasó la revisión del PR.

## 5. Código

- Sin Lombok: getters y setters los genera el IDE.
- Los nombres de clases y métodos son los de los diagramas de `docs/`. Si hay que cambiar
  uno, se cambia **también el diagrama** y se avisa al grupo.
- Las entidades no se devuelven al front: se usan DTOs.
- Formato: lo define `.editorconfig`; instalá la extensión de VS Code para respetarlo.

## 6. Comunicación

- Si algo te bloquea más de 30 minutos, pedí ayuda en el grupo antes de seguir solo.
- Reunión corta semanal para revisar el tablero de Jira.
