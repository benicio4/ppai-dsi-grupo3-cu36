# Frontend (React + Vite)

Pantalla del CU 36. Puerto **5173**.

> Esta carpeta todavía no tiene el proyecto React. Lo genera **una sola persona**,
> lo sube a `main` y el resto lo clona (Sprint 0).

## Crear el proyecto

Con Node.js (LTS) instalado:

```
cd frontend
npm create vite@latest . -- --template react
npm install
npm run dev
```

Abre en <http://localhost:5173>.

## Proxy hacia el backend

Para no configurar CORS en desarrollo, Vite reenvía `/api` al backend (puerto 8081).
En `vite.config.js`:

```js
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': 'http://localhost:8081',
    },
  },
})
```

Así el front llama a `/api/...` sin escribir el puerto. El front **nunca** llama a la API del GPS:
eso lo hace el Gestor del backend.

## Estructura prevista

```
src/
 ├─ api/          funciones que llaman al backend (login, seguimiento, notificar)
 ├─ auth/         AuthContext: guarda el token y lo manda en cada pedido
 ├─ pages/        LoginPage, PantSeguimientoBolsines
 └─ components/   MapaBolsines, filtros, diálogo de confirmación
```

## Pantalla ↔ modelo

`PantSeguimientoBolsines` es la clase Pantalla del diagrama. Sus métodos se corresponden así:

| Método del modelo | En React |
|---|---|
| `opcionConsultarUbiBolsines()` | Botón o menú "Consultar ubicación de bolsines" |
| `habilitarPantalla()` / `mostrarNombreCM()` | Render de la página y encabezado con la CM |
| `mostrarMapaBolsines()` | Componente `MapaBolsines` (por ahora una lista de coordenadas) |
| `habilitarOpcionDeSeleccion()` | Clic en un marcador o combo de bolsín |
| `habilitarOpcionesDeFiltrado()` | Filtros por precinto y por CM destino |
| `consultarEnvioMailGCM()` | Diálogo "¿Enviar correo al Gerente de la CM destino?" |
| `tomarSeleccionConfirmacion()` | Botones Confirmar / Cancelar |
| `mostrarMensajeEjecucionExitosa()` | Mensaje de éxito |
