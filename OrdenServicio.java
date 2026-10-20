import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class OrdenServicio {

    private final String codigo;
    private final String cliente;
    private final String direccion;
    private final BigDecimal horasEstimadas;
    private final Zona zona;

    private EstadoOrden estado = EstadoOrden.PENDIENTE;
    private int visitas = 0;

    private BigDecimal ingresoRegistro = BigDecimal.ZERO;
    private BigDecimal ingresoVisitas = BigDecimal.ZERO;

    protected OrdenServicio(
            String codigo,
            String cliente,
            String direccion,
            BigDecimal horasEstimadas,
            Zona zona) {

        this.codigo = Validacion.codigo(codigo);
        this.cliente = Validacion.texto(cliente, "Cliente");
        this.direccion = Validacion.texto(direccion, "Direccion");

        this.horasEstimadas = Validacion.positivo(
            horasEstimadas,
            "Horas estimadas"
        );

        Validacion.exigir(zona != null, "Zona obligatoria.");
        this.zona = zona;
    }

    public String getCodigo() {
        return codigo;
    }

    public BigDecimal getHorasEstimadas() {
        return horasEstimadas;
    }

    public Zona getZona() {
        return zona;
    }

    public EstadoOrden getEstado() {
        return estado;
    }

    public int getVisitas() {
        return visitas;
    }

    public BigDecimal getIngresoRegistro() {
        return ingresoRegistro;
    }

    public BigDecimal getIngresoVisitas() {
        return ingresoVisitas;
    }

    public abstract String categoria();

    public abstract BigDecimal calcularCosto();

    public abstract int diasAtencion();

    public int maxVisitas() {
        return 3;
    }

    public boolean compatible(Cuadrilla cuadrilla) {
        return true;
    }

    protected String detalle() {
        return "";
    }

    protected BigDecimal tarifaBase() {
        return new BigDecimal(
            zona == Zona.METROPOLITANA ? "100" : "175"
        );
    }

    // Acceso de paquete: estas operaciones son coordinadas por el sistema.
    void cobrarRegistro() {
        exigirEstado(EstadoOrden.PENDIENTE);

        Validacion.exigir(
            ingresoRegistro.signum() == 0,
            "El registro ya fue cobrado."
        );

        ingresoRegistro = calcularCosto()
                .setScale(2, RoundingMode.HALF_UP);
    }

    void asignar() {
        exigirEstado(EstadoOrden.PENDIENTE);

        Validacion.exigir(
            visitas < maxVisitas(),
            "La orden agoto sus visitas."
        );

        estado = EstadoOrden.ASIGNADA;
    }

    void retirar() {
        exigirEstado(EstadoOrden.ASIGNADA);
        estado = EstadoOrden.PENDIENTE;
    }

    void validarInicio() {
        exigirEstado(EstadoOrden.ASIGNADA);

        Validacion.exigir(
            visitas < maxVisitas(),
            "La orden agoto sus visitas."
        );
    }

    void iniciarVisita() {
        validarInicio();

        if (visitas > 0) {
            ingresoVisitas = ingresoVisitas.add(
                new BigDecimal("40")
            );
        }

        visitas++;
        estado = EstadoOrden.EN_ATENCION;
    }

    void registrarResultado(ResultadoVisita resultado) {
        exigirEstado(EstadoOrden.EN_ATENCION);

        Validacion.exigir(
            resultado != null,
            "Resultado obligatorio."
        );

        if (resultado == ResultadoVisita.COMPLETADA) {
            estado = EstadoOrden.COMPLETADA;
        } else {
            estado = visitas >= maxVisitas()
                    ? EstadoOrden.CANCELADA
                    : EstadoOrden.PENDIENTE;
        }
    }

    private void exigirEstado(EstadoOrden esperado) {
        if (estado != esperado) {
            throw new IllegalStateException(
                "Orden " + codigo
                + ": se requiere " + esperado
                + ", estado actual " + estado + "."
            );
        }
    }

    @Override
    public String toString() {
        return codigo
                + " | " + categoria()
                + " | " + cliente
                + " | " + direccion
                + " | " + zona
                + " | Horas: " + horasEstimadas.toPlainString()
                + detalle()
                + " | " + estado
                + " | Visitas: " + visitas + "/" + maxVisitas()
                + " | Costo: " + Validacion.dinero(calcularCosto())
                + " | Atencion: " + diasAtencion() + " dias habiles"
                + " | Cobrado registro: "
                + Validacion.dinero(ingresoRegistro)
                + " | Cobrado visitas: "
                + Validacion.dinero(ingresoVisitas);
    }
}