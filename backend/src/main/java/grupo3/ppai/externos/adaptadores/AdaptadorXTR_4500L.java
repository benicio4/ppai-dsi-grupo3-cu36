package grupo3.ppai.externos.adaptadores;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import grupo3.ppai.externos.servicios.ServicioXTR_4500L;
import lombok.Data;


@Component
@Data // incluye el @RequiredArgsConstructor
public class AdaptadorXTR_4500L implements IAdaptadorCoordenadasBolsines {

	private final String apiKey;
	private final ServicioXTR_4500L servicioGPS; //aca asumimos que busca al servicio y lo sabe encontrar
	private String jsonDevuelto;

	@Override
	public List<ArrayList<Object>> recibirDatosGPS(int[] listaNrosBolsines, String codigoCMOrigen, String codigoCMDestino) {

		List<ArrayList<Object>> arrayDeBolsines = new ArrayList<ArrayList<Object>>();

		for (int num : listaNrosBolsines) {

    		this.solicitarDatosGPS(num, codigoCMOrigen, null);

    		arrayDeBolsines.add((ArrayList<Object>) this.adaptarDatosGPS());

		};

		return arrayDeBolsines;
	};

	@Override
	public void solicitarDatosGPS(int nroBolsin, String codigoCMOrigen, String codigoCMDestino) {

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
        arrayDatosBolsin.set(0, jsonDatosBolsin.getInt("numeroBolsin"));
        arrayDatosBolsin.set(1, jsonDatosBolsin.getDouble("latitud"));
        arrayDatosBolsin.set(2, jsonDatosBolsin.getDouble("longitud"));
        arrayDatosBolsin.set(3, LocalDateTime.parse(jsonDatosBolsin.getString("fechaHoraUltimaActualizacion")));

		return arrayDatosBolsin;
	};
};

