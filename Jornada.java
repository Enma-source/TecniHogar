import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

public class Jornada {

    private final String codigo;
    private final Cuadrilla cuadrilla;

    private EstadoJornada estado = EstadoJornada.PREPARACION;

    private final Map<String, OrdenServicio> ordenes =
            new LinkedHashMap<>();

    private final Map<String, ResultadoVisita> resultados =
            new LinkedHashMap<>();

    Jornada(String codigo, Cuadrilla cuadrilla) {
        this.codigo = Validacion.codigo(codigo);

        Validacion.exigir(
            cuadrilla != null,
            "Cuadrilla obligatoria."
        );

        this.cuadrilla = cuadrilla;
    }

    public String getCodigo() {
        return codigo;
    }

    public Cuadrilla getCuadrilla() {
        return cuadrilla;
    }

    public EstadoJornada getEstado() {
        return estado;
    }

    public BigDecimal horasAsignadas() {
        BigDecimal total = BigDecimal.ZERO;

        for (OrdenServicio orden : ordenes.values()) {
            total = total.add(orden.getHorasEstimadas());
        }

        return total;
    }

    public BigDecimal capacidadDisponible() {
        return cuadrilla.getCapacidadHoras()
                .subtract(horasAsignadas());
    }

    void asignar(OrdenServicio orden) {
        exigirEstado(EstadoJornada.PREPARACION);

        Validacion.exigir(
            orden != null,
            "Orden obligatoria."
        );

        Validacion.exigir(
            !ordenes.containsKey(orden.getCodigo()),
            "Orden ya incluida en la jornada."
        );

        Validacion.exigir(
            orden.getEstado() == EstadoOrden.PENDIENTE,
            "La orden debe estar pendiente."
        );

        Validacion.exigir(
            orden.compatible(cuadrilla),
            "Orden incompatible con la certificacion de la cuadrilla."
        );

        Validacion.exigir(
            orden.getHorasEstimadas()
                    .compareTo(capacidadDisponible()) <= 0,
            "La asignacion supera la capacidad de la cuadrilla."
        );

        orden.asignar();
        ordenes.put(orden.getCodigo(), orden);
    }

    void retirar(String codigoOrden) {
        exigirEstado(EstadoJornada.PREPARACION);

        String clave = Validacion.codigo(codigoOrden);
        OrdenServicio orden = ordenes.get(clave);

        Validacion.exigir(
            orden != null,
            "La orden no pertenece a esta jornada."
        );

        orden.retirar();
        ordenes.remove(clave);
    }

    void iniciar() {
        exigirEstado(EstadoJornada.PREPARACION);

        Validacion.exigir(
            !ordenes.isEmpty(),
            "No se puede iniciar una jornada vacia."
        );

        // Todas se validan antes de cambiar cualquier orden.
        for (OrdenServicio orden : ordenes.values()) {
            orden.validarInicio();
        }

        for (OrdenServicio orden : ordenes.values()) {
            orden.iniciarVisita();
        }

        estado = EstadoJornada.EN_CURSO;
    }

    void registrarResultado(
            String codigoOrden,
            ResultadoVisita resultado) {

        exigirEstado(EstadoJornada.EN_CURSO);

        String clave = Validacion.codigo(codigoOrden);
        OrdenServicio orden = ordenes.get(clave);

        Validacion.exigir(
            orden != null,
            "La orden no pertenece a esta jornada."
        );

        Validacion.exigir(
            !resultados.containsKey(clave),
            "La visita ya tiene resultado."
        );

        Validacion.exigir(
            resultado != null,
            "Resultado obligatorio."
        );

        orden.registrarResultado(resultado);
        resultados.put(clave, resultado);

        if (resultados.size() == ordenes.size()) {
            estado = EstadoJornada.CERRADA;
        }
    }

    private void exigirEstado(EstadoJornada esperado) {
        if (estado != esperado) {
            throw new IllegalStateException(
                "Jornada " + codigo
                + ": se requiere " + esperado
                + ", estado actual " + estado + "."
            );
        }
    }

    @Override
    public String toString() {
        return codigo
                + " | Cuadrilla: " + cuadrilla.getCodigo()
                + " | " + estado
                + " | Horas asignadas: "
                + horasAsignadas().toPlainString()
                + " | Capacidad disponible: "
                + capacidadDisponible().toPlainString()
                + " | Ordenes: " + ordenes.keySet()
                + " | Resultados: " + resultados;
    }
}