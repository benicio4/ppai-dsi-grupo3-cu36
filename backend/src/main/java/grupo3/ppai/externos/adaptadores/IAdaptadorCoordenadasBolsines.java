package grupo3.ppai.externos.adaptadores;

import java.util.ArrayList;
import java.util.List;

public interface IAdaptadorCoordenadasBolsines {
    public void solicitarDatosGPS(Integer nroBolsin, String codigoCMOrigen, String codigoCMDestino);
    public List<ArrayList<Object>> recibirDatosGPS(List<Integer> listaNrosBolsines, String codigoCMOrigen, String[] codigosCMDestino);
    public List<Object> adaptarDatosGPS();
}
