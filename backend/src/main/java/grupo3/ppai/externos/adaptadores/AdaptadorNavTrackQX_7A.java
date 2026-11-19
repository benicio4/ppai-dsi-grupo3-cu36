package grupo3.ppai.externos.adaptadores;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


import grupo3.ppai.externos.servicios.ServicioNavTrackQX_7A;

@Component
@Scope("prototype")
public class AdaptadorNavTrackQX_7A implements IAdaptadorCoordenadasBolsines {

    private final String apiKey;
    private final ServicioNavTrackQX_7A servicioGPS;

    public AdaptadorNavTrackQX_7A(@Value("${gps.api.keys.navtrack-qx-7a}") String apiKey,
            ServicioNavTrackQX_7A servicioGPS) {
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
	public List<ArrayList<Object>> adaptarDatosGPS() {
        throw new UnsupportedOperationException("Pendiente de implementar");

        // El adaptado seria algo asi
        // String[] stringHechoArray = this.getStringDevuelto().split("",-1);

        // arrayDatosBolsin.add(Integer.parseInt(stringHechoArray[0]));
        // arrayDatosBolsin.add(Double.parseDouble(stringHechoArray[1]));
        // arrayDatosBolsin.add(Double.parseDouble(stringHechoArray[2]));
        // arrayDatosBolsin.add(LocalDateTime.parse(stringHechoArray[3]));
	};

}
