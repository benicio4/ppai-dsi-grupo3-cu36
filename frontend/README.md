# Frontend (React + Vite)

Pantalla del CU 36. Puerto **5173**.

# React + Vite

This template provides a minimal setup to get React working in Vite with HMR and some ESLint rules.

Currently, two official plugins are available:

- [@vitejs/plugin-react](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react) uses [Oxc](https://oxc.rs)
- [@vitejs/plugin-react-swc](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react-swc) uses [SWC](https://swc.rs/)

## React Compiler

The React Compiler is not enabled on this template because of its impact on dev & build performances. To add it, see [this documentation](https://react.dev/learn/react-compiler/installation).

## Expanding the ESLint configuration

If you are developing a production application, we recommend using TypeScript with type-aware lint rules enabled. Check out the [TS template](https://github.com/vitejs/vite/tree/main/packages/create-vite/template-react-ts) for information on how to integrate TypeScript and [`typescript-eslint`](https://typescript-eslint.io) in your project.

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