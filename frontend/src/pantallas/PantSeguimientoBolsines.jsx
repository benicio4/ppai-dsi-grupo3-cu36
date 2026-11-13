import { useMemo, useState } from 'react'
import './PantSeguimientoBolsines.css'

const API = '/api/seguimiento-bolsines'

async function pedir(url, cuerpo) {
  const respuesta = await fetch(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: cuerpo ? JSON.stringify(cuerpo) : undefined,
  })
  const datos = await respuesta.json().catch(() => ({}))
  if (!respuesta.ok) throw new Error(datos.mensaje || 'Ocurrió un error inesperado.')
  return datos
}

/**
 * <<boundary>> PantSeguimientoBolsines - CU 36 Consultar seguimiento de bolsines.
 * Los atributos del diagrama son estado/elementos de la pantalla y los métodos
 * son funciones con el mismo nombre.
 */
export default function PantSeguimientoBolsines() {
  const [habilitada, setHabilitada] = useState(false)
  const [cargando, setCargando] = useState(false)
  const [infoCMlogeado, setInfoCMlogeado] = useState('')
  const [bolsines, setBolsines] = useState([])
  const [checkboxFiltro, setCheckboxFiltro] = useState(false)
  const [txtNumPrecinto, setTxtNumPrecinto] = useState('')
  const [txtCMdestino, setTxtCMdestino] = useState('')
  const [comboSeleccionBolsin, setComboSeleccionBolsin] = useState('')
  const [consultandoEnvioMail, setConsultandoEnvioMail] = useState(false)
  const [mensaje, setMensaje] = useState(null) // { tipo: 'exito' | 'aviso' | 'error', texto }

  // ---- Paso 1: el EB selecciona la opción ----
  async function opcionConsultarUbiBolsines() {
    habilitarPantalla()
    setCargando(true)
    try {
      const datos = await pedir(`${API}/consulta`)
      mostrarNombreCM(datos.nombreCM)
      if (datos.bolsines.length === 0) {
        // A1: no se encuentran bolsines en estado Enviado
        setMensaje({ tipo: 'aviso', texto: datos.mensaje })
        return
      }
      mostrarMapaBolsines(datos.bolsines)
      habilitarOpcionDeSeleccion()
      habilitarOpcionesDeFiltrado()
    } catch (error) {
      setMensaje({ tipo: 'error', texto: error.message })
    } finally {
      setCargando(false)
    }
  }

  function habilitarPantalla() {
    setHabilitada(true)
    setMensaje(null)
    setBolsines([])
    setComboSeleccionBolsin('')
    setConsultandoEnvioMail(false)
  }

  function mostrarNombreCM(nombreCM) {
    setInfoCMlogeado(nombreCM)
  }

  // PENDIENTE: dibujar el mapa con InterfazGoogleMaps; por ahora se listan los bolsines
  function mostrarMapaBolsines(bolsinesEnviados) {
    setBolsines(bolsinesEnviados)
  }

  function habilitarOpcionDeSeleccion() {
    setComboSeleccionBolsin('')
  }

  function habilitarOpcionesDeFiltrado() {
    setCheckboxFiltro(false)
    setTxtNumPrecinto('')
    setTxtCMdestino('')
  }

  // ---- Paso 7: el EB selecciona un bolsín ----
  function tomarSeleccionBolsin(numeroBolsin) {
    setComboSeleccionBolsin(numeroBolsin)
    setMensaje(null)
    consultarEnvioMailGCM()
  }

  // Paso 8: se ofrece enviar el correo al GCM destino
  function consultarEnvioMailGCM() {
    setConsultandoEnvioMail(true)
  }

  // ---- Paso 9: el EB confirma (o no, A5) el envío del correo ----
  async function tomarSeleccionConfirmacion(confirmacion) {
    setCargando(true)
    try {
      const datos = await pedir(`${API}/confirmacion`, {
        numeroBolsin: Number(comboSeleccionBolsin),
        confirmacion,
      })
      if (confirmacion) mostrarMensajeEjecucionExitosa(datos.mensaje)
      else setMensaje({ tipo: 'aviso', texto: datos.mensaje })
      setConsultandoEnvioMail(false)
    } catch (error) {
      setMensaje({ tipo: 'error', texto: error.message })
    } finally {
      setCargando(false)
    }
  }

  function mostrarMensajeEjecucionExitosa(texto) {
    setMensaje({ tipo: 'exito', texto })
  }

  // A4: el EB cancela la ejecución del caso de uso
  function cancelarCU() {
    setHabilitada(false)
    setInfoCMlogeado('')
    setBolsines([])
    setComboSeleccionBolsin('')
    setConsultandoEnvioMail(false)
    setMensaje({ tipo: 'aviso', texto: 'Se canceló la consulta de seguimiento de bolsines.' })
  }

  const bolsinesFiltrados = useMemo(() => {
    if (!checkboxFiltro) return bolsines
    return bolsines.filter(
      (b) =>
        String(b.numeroPrecinto).includes(txtNumPrecinto.trim()) &&
        b.cmDestino.toLowerCase().includes(txtCMdestino.trim().toLowerCase()),
    )
  }, [bolsines, checkboxFiltro, txtNumPrecinto, txtCMdestino])

  const bolsinSeleccionado = bolsines.find((b) => String(b.numeroBolsin) === String(comboSeleccionBolsin))

  return (
    <main className="pantalla">
      <header className="pantalla__encabezado">
        <h1>Seguimiento de bolsines</h1>
        {infoCMlogeado && (
          <p className="info-cm">
            Comisión Médica: <strong>{infoCMlogeado}</strong>
          </p>
        )}
      </header>

      {!habilitada && (
        <button className="boton boton--primario" onClick={opcionConsultarUbiBolsines}>
          Consultar ubicación de bolsines
        </button>
      )}

      {mensaje && (
        <p className={`mensaje mensaje--${mensaje.tipo}`} role="status">
          {mensaje.texto}
        </p>
      )}

      {habilitada && cargando && bolsines.length === 0 && <p>Buscando bolsines enviados…</p>}

      {habilitada && bolsines.length > 0 && (
        <section className="pantalla__cuerpo">
          <aside className="filtros">
            <label className="filtros__check">
              <input
                type="checkbox"
                checked={checkboxFiltro}
                onChange={(e) => setCheckboxFiltro(e.target.checked)}
              />
              Filtrar bolsines
            </label>
            <label>
              Número de precinto
              <input
                value={txtNumPrecinto}
                disabled={!checkboxFiltro}
                inputMode="numeric"
                onChange={(e) => setTxtNumPrecinto(e.target.value)}
              />
            </label>
            <label>
              CM destino
              <input
                value={txtCMdestino}
                disabled={!checkboxFiltro}
                onChange={(e) => setTxtCMdestino(e.target.value)}
              />
            </label>
            <label>
              Seleccionar bolsín
              <select
                value={comboSeleccionBolsin}
                onChange={(e) => e.target.value && tomarSeleccionBolsin(e.target.value)}
              >
                <option value="">—</option>
                {bolsinesFiltrados.map((b) => (
                  <option key={b.numeroBolsin} value={b.numeroBolsin}>
                    Bolsín {b.numeroBolsin}
                  </option>
                ))}
              </select>
            </label>
          </aside>

          <div className="mapa" aria-label="Mapa de bolsines">
            <p className="mapa__aviso">Mapa pendiente (InterfazGoogleMaps). Bolsines enviados:</p>
            {bolsinesFiltrados.length === 0 && <p>Ningún bolsín coincide con el filtro.</p>}
            <ul className="mapa__lista">
              {bolsinesFiltrados.map((b) => (
                <li key={b.numeroBolsin}>
                  <button
                    className={`bolsin ${String(b.numeroBolsin) === String(comboSeleccionBolsin) ? 'bolsin--sel' : ''}`}
                    onClick={() => tomarSeleccionBolsin(b.numeroBolsin)}
                  >
                    <strong>Bolsín {b.numeroBolsin}</strong>
                    <span>Precinto {b.numeroPrecinto}</span>
                    <span>Destino: {b.cmDestino}</span>
                  </button>
                </li>
              ))}
            </ul>
          </div>
        </section>
      )}

      {consultandoEnvioMail && bolsinSeleccionado && (
        <section className="confirmacion">
          <p>
            ¿Enviar un correo al Gerente de <strong>{bolsinSeleccionado.cmDestino}</strong> para informar la
            ubicación del bolsín <strong>{bolsinSeleccionado.numeroBolsin}</strong>?
          </p>
          <div className="confirmacion__botones">
            <button className="boton boton--primario" disabled={cargando} onClick={() => tomarSeleccionConfirmacion(true)}>
              Enviar mail
            </button>
            <button className="boton" disabled={cargando} onClick={() => tomarSeleccionConfirmacion(false)}>
              No enviar
            </button>
          </div>
        </section>
      )}

      {habilitada && (
        <footer className="pantalla__pie">
          <button className="boton boton--peligro" onClick={cancelarCU}>
            Cancelar
          </button>
        </footer>
      )}
    </main>
  )
}
