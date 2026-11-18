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

export default function InterfazGoogleMaps({ bolsines, tomarSeleccionBolsin }) {
  const ubicados = useMemo(() => bolsines.filter(({ ubicacion }) =>
    Number.isFinite(ubicacion?.latitud) && Math.abs(ubicacion.latitud) <= 90 &&
    Number.isFinite(ubicacion?.longitud) && Math.abs(ubicacion.longitud) <= 180
  ), [bolsines])

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
      .then(([{ Map }, { AdvancedMarkerElement }, { LatLngBounds }]) => {
        if (!activo) return
        const mapa = new Map(elemento, { mapId: 'DEMO_MAP_ID', streetViewControl: false })
        setGoogleMaps({ mapa, AdvancedMarkerElement, LatLngBounds })
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

    const { mapa, AdvancedMarkerElement, LatLngBounds } = googleMaps
    const limites = new LatLngBounds()
    const marcadores = ubicados.map(({ numeroBolsin, ubicacion }) => {
      const position = { lat: ubicacion.latitud, lng: ubicacion.longitud }
      limites.extend(position)
      const marcador = new AdvancedMarkerElement({
        map: mapa,
        position,
        title: `Bolsín ${numeroBolsin}`,
        gmpClickable: true,
      })
      const seleccionar = () => tomarSeleccionBolsin(numeroBolsin)
      marcador.addEventListener('gmp-click', seleccionar)
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
  }, [googleMaps, error, ubicados, tomarSeleccionBolsin])

  return (
    <>
      {error && <p className="mapa__aviso" role="status">{error}</p>}
      {!error && !googleMaps && <p className="mapa__aviso" role="status">Cargando mapa…</p>}
      <div ref={contenedor} className="mapa__google" aria-label="Mapa de bolsines" hidden={Boolean(error)} />
    </>
  )
}
