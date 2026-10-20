import java.math.BigDecimal;
import java.math.RoundingMode;

public class InstalacionElectricaCertificada extends Instalacion {

    private final BigDecimal valorMateriales;

    public InstalacionElectricaCertificada(
            String codigo,
            String cliente,
            String direccion,
            BigDecimal horas,
            Zona zona,
            int puntos,
            BigDecimal valorMateriales) {

        super(codigo, cliente, direccion, horas, zona, puntos);

        this.valorMateriales = Validacion.positivo(
            valorMateriales,
            "Valor de materiales"
        );
    }

    @Override
    public String categoria() {
        return "Instalacion electrica certificada";
    }

    @Override
    public BigDecimal calcularCosto() {
        BigDecimal garantia = valorMateriales.multiply(
            new BigDecimal("0.05")
        );

        return super.calcularCosto()
                .add(new BigDecimal("120"))
                .add(garantia)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public int diasAtencion() {
        return super.diasAtencion() + 2;
    }

    @Override
    public int maxVisitas() {
        return 2;
    }

    @Override
    protected String detalle() {
        return super.detalle()
                + " | Materiales: "
                + Validacion.dinero(valorMateriales);
    }
}