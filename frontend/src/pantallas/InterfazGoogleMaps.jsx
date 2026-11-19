import { importLibrary, setOptions } from '@googlemaps/js-api-loader'
import { useEffect, useMemo, useRef, useState } from 'react'

const apiKey = import.meta.env.VITE_GOOGLE_MAPS_API_KEY?.trim()
let bibliotecasMaps

function cargarGoogleMaps() {
  if (!bibliotecasMaps) {
    setOptions({ key: apiKey, v: 'weekly' })
    bibliotecasMaps = Promise.all([
      importLibrary('maps'),
      importLibrary('marker'),
      importLibrary('core'),
    ])
  }
  return bibliotecasMaps
}

const formatoFecha = new Intl.DateTimeFormat('es-AR', { dateStyle: 'short', timeStyle: 'short' })

function describirUbicacion({ numeroBolsin, fechaHoraUltimaActualizacion, bolsin }) {
  const partes = [`Bolsín ${numeroBolsin}`]
  if (bolsin) partes.push(`Precinto ${bolsin.numeroPrecinto}`, `Destino: ${bolsin.cmDestino}`)
  else partes.push('No figura entre los bolsines enviados de la CM')
  const fecha = new Date(fechaHoraUltimaActualizacion)
  if (!Number.isNaN(fecha.getTime())) partes.push(`Última actualización: ${formatoFecha.format(fecha)}`)
  return partes.join('\n')
}

/**
 * <<interfaz>> InterfazGoogleMaps - recibe el mapa armado por GestorSeguimientoBolsines.armarMapa()
 * (un elemento por bolsín que devolvió el GPS Tracker) y dibuja un marcador por cada uno.
 */
export default function InterfazGoogleMaps({ ubicacionesBolsines, tomarSeleccionBolsin }) {
  const ubicados = useMemo(() => ubicacionesBolsines.filter(({ latitud, longitud }) =>
    Number.isFinite(latitud) && Math.abs(latitud) <= 90 &&
    Number.isFinite(longitud) && Math.abs(longitud) <= 180
  ), [ubicacionesBolsines])

  if (!apiKey) {
    return <p className="mapa__aviso">Configurá la clave de Google Maps en frontend/.env.local.</p>
  }
  if (ubicados.length === 0) {
    return <p className="mapa__aviso">Ubicación GPS no disponible para los bolsines de esta consulta.</p>
  }
  return <MapaGoogle ubicados={ubicados} tomarSeleccionBolsin={tomarSeleccionBolsin} />
}

function MapaGoogle({ ubicados, tomarSeleccionBolsin }) {
  const contenedor = useRef(null)
  const [googleMaps, setGoogleMaps] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    let activo = true
    const elemento = contenedor.current
    const antes = window.gm_authFailure
    const errorAutenticacion = () => {
      setError('Google Maps rechazó la clave. Revisá sus restricciones en Google Cloud.')
      antes?.()
    }
    window.gm_authFailure = errorAutenticacion

    cargarGoogleMaps()
      .then(([{ Map }, { AdvancedMarkerElement, PinElement }, { LatLngBounds }]) => {
        if (!activo) return
        const mapa = new Map(elemento, { mapId: 'DEMO_MAP_ID', streetViewControl: false })
        setGoogleMaps({ mapa, AdvancedMarkerElement, PinElement, LatLngBounds })
      })
      .catch(() => {
        if (activo) setError('No se pudo cargar Google Maps.')
      })

    return () => {
      activo = false
      if (window.gm_authFailure === errorAutenticacion) window.gm_authFailure = antes
      elemento.replaceChildren()
    }
  }, [])

  useEffect(() => {
    if (!googleMaps || error) return
    return construirMapaConUbicacionesBolsines(googleMaps, ubicados, tomarSeleccionBolsin)
  }, [googleMaps, error, ubicados, tomarSeleccionBolsin])

  return (
    <>
      {error && <p className="mapa__aviso" role="status">{error}</p>}
      {!error && !googleMaps && <p className="mapa__aviso" role="status">Cargando mapa…</p>}
      <div ref={contenedor} className="mapa__google" aria-label="Mapa de bolsines" hidden={Boolean(error)} />
    </>
  )
}

// Dibuja un marcador por cada bolsín ubicado y encuadra el mapa para que se vean todos.
// Devuelve una función que quita los marcadores (para cuando cambian las ubicaciones).
function construirMapaConUbicacionesBolsines(
  { mapa, AdvancedMarkerElement, PinElement, LatLngBounds },
  ubicados,
  tomarSeleccionBolsin,
) {
  const limites = new LatLngBounds()
  const marcadores = ubicados.map((ubicacion) => {
    const { numeroBolsin, latitud, longitud, bolsin } = ubicacion
    const position = { lat: latitud, lng: longitud }
    limites.extend(position)

    // Solo se pueden seleccionar los bolsines enviados de la CM del usuario (paso 7)
    const seleccionable = Boolean(bolsin)
    const pin = new PinElement({
      glyphText: String(numeroBolsin),
      scale: 1.15,
      ...(seleccionable ? {} : { background: '#9aa0a6', borderColor: '#5f6368', glyphColor: '#ffffff' }),
    })
    const marcador = new AdvancedMarkerElement({
      map: mapa,
      position,
      title: describirUbicacion(ubicacion),
      content: pin,
      gmpClickable: seleccionable,
    })
    const seleccionar = () => tomarSeleccionBolsin(numeroBolsin)
    if (seleccionable) marcador.addEventListener('gmp-click', seleccionar)
    return { marcador, seleccionar }
  })

  if (limites.getNorthEast().equals(limites.getSouthWest())) {
    mapa.setCenter(limites.getCenter())
    mapa.setZoom(13)
  } else {
    mapa.fitBounds(limites, 36)
  }

  return () => {
    for (const { marcador, seleccionar } of marcadores) {
      marcador.removeEventListener('gmp-click', seleccionar)
      marcador.map = null
    }
  }
}
