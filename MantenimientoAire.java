import java.math.BigDecimal;
import java.math.RoundingMode;

public class MantenimientoAire extends OrdenServicio {

    private final int btu;

    public MantenimientoAire(
            String codigo,
            String cliente,
            String direccion,
            BigDecimal horas,
            Zona zona,
            int btu) {

        super(codigo, cliente, direccion, horas, zona);

        Validacion.exigir(
            btu >= 9000 && btu <= 60000,
            "Los BTU deben estar entre 9000 y 60000."
        );

        Validacion.exigir(
            zona == Zona.METROPOLITANA,
            "El mantenimiento de aire solo se acepta en zona metropolitana."
        );

        this.btu = btu;
    }

    @Override
    public String categoria() {
        return "Mantenimiento de aire";
    }

    @Override
    public BigDecimal calcularCosto() {
        return tarifaBase()
                .add(
                    getHorasEstimadas().multiply(
                        new BigDecimal("110")
                    )
                )
                .add(new BigDecimal("80"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public int diasAtencion() {
        return 2;
    }

    @Override
    public int maxVisitas() {
        return 1;
    }

    @Override
    public boolean compatible(Cuadrilla cuadrilla) {
        return cuadrilla.isCertificada();
    }

    @Override
    protected String detalle() {
        return " | BTU: " + btu;
    }
}