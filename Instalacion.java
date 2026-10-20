import java.math.BigDecimal;
import java.math.RoundingMode;

public class Instalacion extends OrdenServicio {

    private final int puntos;

    public Instalacion(
            String codigo,
            String cliente,
            String direccion,
            BigDecimal horas,
            Zona zona,
            int puntos) {

        super(codigo, cliente, direccion, horas, zona);

        Validacion.exigir(
            puntos > 0,
            "Los puntos deben ser un entero positivo."
        );

        this.puntos = puntos;
    }

    public BigDecimal horasFacturables() {
        BigDecimal horasPorPuntos = BigDecimal
                .valueOf(puntos)
                .multiply(new BigDecimal("0.75"));

        return getHorasEstimadas()
                .max(horasPorPuntos)
                .setScale(0, RoundingMode.CEILING);
    }

    @Override
    public String categoria() {
        return "Instalacion";
    }

    @Override
    public BigDecimal calcularCosto() {
        return tarifaBase().add(
            horasFacturables().multiply(new BigDecimal("90"))
        );
    }

    @Override
    public int diasAtencion() {
        return getZona() == Zona.METROPOLITANA ? 3 : 5;
    }

    @Override
    protected String detalle() {
        return " | Puntos: " + puntos
                + " | Horas facturadas: " + horasFacturables();
    }
}