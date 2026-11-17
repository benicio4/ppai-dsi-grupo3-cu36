package grupo3.ppai.externos.adaptadores;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import grupo3.ppai.externos.servicios.ServicioXTR_4500L;
import lombok.Getter;
import lombok.Setter;


@Component
@Scope("prototype") // una instancia por pedido: guarda jsonDevuelto como estado
@Getter
@Setter
public class AdaptadorXTR_4500L implements IAdaptadorCoordenadasBolsines {

    private static final Logger log = LoggerFactory.getLogger(AdaptadorXTR_4500L.class);

	private final String apiKey;
	private final ServicioXTR_4500L servicioGPS;
	private String jsonDevuelto;

    public AdaptadorXTR_4500L(@Value("${gps.api.keys.xtr-4500l}") String apiKey, ServicioXTR_4500L servicioGPS){
        this.apiKey = apiKey;
        this.servicioGPS = servicioGPS;
    }

	@Override
	public List<ArrayList<Object>> recibirDatosGPS(List<Integer> listaNrosBolsines, String codigoCMOrigen, String[] codigosCMDestino) {

		List<ArrayList<Object>> arrayDeBolsines = new ArrayList<ArrayList<Object>>();

        for (Integer num : listaNrosBolsines) {
            try {
                this.solicitarDatosGPS(num, codigoCMOrigen, null);

                arrayDeBolsines.add((ArrayList<Object>) this.adaptarDatosGPS());

            } catch (HttpClientErrorException.NotFound e) {
                // 404: bolsín inexistente o código de CM que no coincide. Se saltea y se sigue con el resto.
                log.warn("El GPS XTR-4500L no encontró el bolsín {}: {}", num, e.getMessage());

            } catch (JSONException e) {
                // Respuesta sin datos o con formato inesperado.
                log.warn("Respuesta inválida del GPS XTR-4500L para el bolsín {}: {}", num, e.getMessage());
            }
        }

		return arrayDeBolsines;
	};

	@Override
	public void solicitarDatosGPS(Integer nroBolsin, String codigoCMOrigen, String codigoCMDestino) {

		String json = this.getServicioGPS().getBolsinLocation(nroBolsin, codigoCMOrigen, this.getApiKey());

		this.setJsonDevuelto(json);

	};

	@Override
	public List<Object> adaptarDatosGPS() {

		List<Object> arrayDatosBolsin = new ArrayList<Object>();

        // String -> JSON
        JSONObject jsonReal = new JSONObject(this.getJsonDevuelto());

        // Saco el JSON de los datos del bolsin ubicado en la primer aposicion de array "items"
        JSONObject jsonDatosBolsin = jsonReal.getJSONArray("items").getJSONObject(0);

        // Nombres tomados del readme.md de la api de bolsines provista por la catedra
        arrayDatosBolsin.add(jsonDatosBolsin.getInt("numeroBolsin"));
        arrayDatosBolsin.add(jsonDatosBolsin.getDouble("latitud"));
        arrayDatosBolsin.add(jsonDatosBolsin.getDouble("longitud"));
        arrayDatosBolsin.add(LocalDateTime.parse(jsonDatosBolsin.getString("fechaHoraUltimaActualizacion")));

		return arrayDatosBolsin;
	};
};
