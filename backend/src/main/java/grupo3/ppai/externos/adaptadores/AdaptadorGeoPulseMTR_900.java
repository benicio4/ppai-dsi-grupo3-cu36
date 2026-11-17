package grupo3.ppai.externos.adaptadores;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import grupo3.ppai.externos.servicios.ServicioGeoPulseMTR_900;

@Component
@Scope("prototype")
public class AdaptadorGeoPulseMTR_900 implements IAdaptadorCoordenadasBolsines {

    private final String apiKey;
    private final ServicioGeoPulseMTR_900 servicioGPS;

    public AdaptadorGeoPulseMTR_900(@Value("${gps.api.keys.geopulse-mtr-900}") String apiKey,
            ServicioGeoPulseMTR_900 servicioGPS) {
        this.apiKey = apiKey;
        this.servicioGPS = servicioGPS;
    }

    @Override
	public List<ArrayList<Object>> recibirDatosGPS(List<Integer> listaNrosBolsines, String codigoCMOrigen, String[] codigosCMDestino) {
        throw new UnsupportedOperationException("Pendiente de implementar");
	};

	@Override
	public void solicitarDatosGPS(Integer nroBolsin, String codigoCMOrigen, String codigoCMDestino) {
        throw new UnsupportedOperationException("Pendiente de implementar");
	};

	@Override
	public List<Object> adaptarDatosGPS() {
        throw new UnsupportedOperationException("Pendiente de implementar");
    }
}
