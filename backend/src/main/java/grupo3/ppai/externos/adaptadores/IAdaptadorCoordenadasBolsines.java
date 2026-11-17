package grupo3.ppai.externos.adaptadores;

import java.util.ArrayList;
import java.util.List;

public interface IAdaptadorCoordenadasBolsines {
    public void solicitarDatosGPS(int nroBolsin, String codigoCMOrigen, String codigoCMDestino);
    public List<ArrayList<Object>> recibirDatosGPS(int[] listaNrosBolsines, String codigoCMOrigen, String codigoCMDestino);
    public List<Object> adaptarDatosGPS();
}
