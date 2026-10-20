import java.math.BigDecimal;

public class Cuadrilla {

    private final String codigo;
    private final BigDecimal capacidadHoras;
    private final boolean certificada;

    public Cuadrilla(
            String codigo,
            BigDecimal capacidadHoras,
            boolean certificada) {

        this.codigo = Validacion.codigo(codigo);

        this.capacidadHoras = Validacion.positivo(
            capacidadHoras,
            "Capacidad de horas"
        );

        this.certificada = certificada;
    }

    public String getCodigo() {
        return codigo;
    }

    public BigDecimal getCapacidadHoras() {
        return capacidadHoras;
    }

    public boolean isCertificada() {
        return certificada;
    }

    @Override
    public String toString() {
        return codigo
                + " | Capacidad: " + capacidadHoras.toPlainString()
                + " h | Certificada en refrigeracion: "
                + (certificada ? "Si" : "No");
    }
}