import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

public class TecniHogar {

    private final Map<String, OrdenServicio> ordenes =
            new LinkedHashMap<>();

    private final Map<String, Cuadrilla> cuadrillas =
            new LinkedHashMap<>();

    private final Map<String, Jornada> jornadas =
            new LinkedHashMap<>();

    public TecniHogar() {
        cargarIniciales();
    }

    public void registrarOrden(OrdenServicio orden) {
        guardarOrden(orden, true);
    }

    private void guardarOrden(
            OrdenServicio orden,
            boolean cobrar) {

        Validacion.exigir(
            orden != null,
            "Orden obligatoria."
        );

        Validacion.exigir(
            !ordenes.containsKey(orden.getCodigo()),
            "Codigo de orden repetido."
        );

        Validacion.exigir(
            orden.getEstado() == EstadoOrden.PENDIENTE
                    && orden.getVisitas() == 0,
            "Solo se registran ordenes nuevas y pendientes."
        );

        if (cobrar) {
            orden.cobrarRegistro();
        }

        ordenes.put(orden.getCodigo(), orden);
    }

    public void registrarCuadrilla(Cuadrilla cuadrilla) {
        Validacion.exigir(
            cuadrilla != null,
            "Cuadrilla obligatoria."
        );

        Validacion.exigir(
            !cuadrillas.containsKey(cuadrilla.getCodigo()),
            "Codigo de cuadrilla repetido."
        );

        cuadrillas.put(cuadrilla.getCodigo(), cuadrilla);
    }

    public Jornada crearJornada(
            String codigo,
            String codigoCuadrilla) {

        String clave = Validacion.codigo(codigo);

        Validacion.exigir(
            !jornadas.containsKey(clave),
            "Codigo de jornada repetido."
        );

        Cuadrilla cuadrilla = buscarCuadrilla(codigoCuadrilla);

        for (Jornada jornada : jornadas.values()) {
            Validacion.exigir(
                jornada.getCuadrilla() != cuadrilla
                        || jornada.getEstado() == EstadoJornada.CERRADA,
                "La cuadrilla ya tiene una jornada sin cerrar."
            );
        }

        Jornada nueva = new Jornada(clave, cuadrilla);
        jornadas.put(clave, nueva);

        return nueva;
    }

    public void asignarOrden(
            String codigoJornada,
            String codigoOrden) {

        buscarJornada(codigoJornada)
                .asignar(buscarOrden(codigoOrden));
    }

    public void retirarOrden(
            String codigoJornada,
            String codigoOrden) {

        buscarJornada(codigoJornada).retirar(codigoOrden);
    }

    public void iniciarJornada(String codigoJornada) {
        buscarJornada(codigoJornada).iniciar();
    }

    public void registrarResultado(
            String codigoJornada,
            String codigoOrden,
            ResultadoVisita resultado) {

        buscarJornada(codigoJornada)
                .registrarResultado(codigoOrden, resultado);
    }

    public OrdenServicio buscarOrden(String codigo) {
        OrdenServicio orden = ordenes.get(
            Validacion.codigo(codigo)
        );

        Validacion.exigir(
            orden != null,
            "Orden inexistente."
        );

        return orden;
    }

    public Cuadrilla buscarCuadrilla(String codigo) {
        Cuadrilla cuadrilla = cuadrillas.get(
            Validacion.codigo(codigo)
        );

        Validacion.exigir(
            cuadrilla != null,
            "Cuadrilla inexistente."
        );

        return cuadrilla;
    }

    public Jornada buscarJornada(String codigo) {
        Jornada jornada = jornadas.get(
            Validacion.codigo(codigo)
        );

        Validacion.exigir(
            jornada != null,
            "Jornada inexistente."
        );

        return jornada;
    }

    public String consultarOrdenes() {
        StringBuilder texto = new StringBuilder();

        for (OrdenServicio orden : ordenes.values()) {
            texto.append(orden).append('\n');
        }

        return texto.toString();
    }

    public String consultarCuadrillas() {
        StringBuilder texto = new StringBuilder();

        for (Cuadrilla cuadrilla : cuadrillas.values()) {
            texto.append(cuadrilla).append('\n');
        }

        return texto.toString();
    }

    public String reporteEstados() {
        Map<String, int[]> conteos = new LinkedHashMap<>();

        for (OrdenServicio orden : ordenes.values()) {
            conteos.putIfAbsent(
                orden.categoria(),
                new int[EstadoOrden.values().length]
            );

            conteos.get(orden.categoria())
                    [orden.getEstado().ordinal()]++;
        }

        StringBuilder texto = new StringBuilder(
            "ORDENES POR CATEGORIA Y ESTADO\n"
        );

        int[] totales = new int[EstadoOrden.values().length];

        for (Map.Entry<String, int[]> fila : conteos.entrySet()) {
            texto.append(fila.getKey()).append(": ");

            int total = 0;

            for (EstadoOrden estado : EstadoOrden.values()) {
                int cantidad = fila.getValue()[estado.ordinal()];

                texto.append(estado)
                        .append('=')
                        .append(cantidad)
                        .append(" | ");

                total += cantidad;
                totales[estado.ordinal()] += cantidad;
            }

            texto.append("TOTAL=")
                    .append(total)
                    .append('\n');
        }

        texto.append("TOTALES: ");

        for (EstadoOrden estado : EstadoOrden.values()) {
            texto.append(estado)
                    .append('=')
                    .append(totales[estado.ordinal()])
                    .append(" | ");
        }

        return texto.append("TOTAL=")
                .append(ordenes.size())
                .toString();
    }

    public BigDecimal ingresoRegistroTotal() {
        BigDecimal total = BigDecimal.ZERO;

        for (OrdenServicio orden : ordenes.values()) {
            total = total.add(orden.getIngresoRegistro());
        }

        return total;
    }

    public BigDecimal ingresoVisitasTotal() {
        BigDecimal total = BigDecimal.ZERO;

        for (OrdenServicio orden : ordenes.values()) {
            total = total.add(orden.getIngresoVisitas());
        }

        return total;
    }

    public String reporteIngresos() {
        Map<String, BigDecimal[]> ingresos = new LinkedHashMap<>();

        for (OrdenServicio orden : ordenes.values()) {
            ingresos.putIfAbsent(
                orden.categoria(),
                new BigDecimal[]{
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
                }
            );

            BigDecimal[] fila = ingresos.get(orden.categoria());

            fila[0] = fila[0].add(orden.getIngresoRegistro());
            fila[1] = fila[1].add(orden.getIngresoVisitas());
        }

        StringBuilder texto = new StringBuilder(
            "INGRESOS POR CATEGORIA\n"
        );

        for (Map.Entry<String, BigDecimal[]> fila
                : ingresos.entrySet()) {

            BigDecimal[] valores = fila.getValue();

            texto.append(fila.getKey())
                    .append(" | Registro: ")
                    .append(Validacion.dinero(valores[0]))
                    .append(" | Visitas adicionales: ")
                    .append(Validacion.dinero(valores[1]))
                    .append(" | Total: ")
                    .append(
                        Validacion.dinero(
                            valores[0].add(valores[1])
                        )
                    )
                    .append('\n');
        }

        return texto
                .append("Registro total: ")
                .append(Validacion.dinero(ingresoRegistroTotal()))
                .append("\nVisitas adicionales total: ")
                .append(Validacion.dinero(ingresoVisitasTotal()))
                .append("\nINGRESO TOTAL: ")
                .append(
                    Validacion.dinero(
                        ingresoRegistroTotal()
                                .add(ingresoVisitasTotal())
                    )
                )
                .toString();
    }

    public String reporteJornadas() {
        StringBuilder texto = new StringBuilder();

        for (Jornada jornada : jornadas.values()) {
            if (jornada.getEstado() != EstadoJornada.CERRADA) {
                texto.append(jornada).append('\n');
            }
        }

        return texto.length() == 0
                ? "No hay jornadas abiertas."
                : texto.toString();
    }

    private void cargarIniciales() {
        guardarOrden(
            new Diagnostico(
                "D1", "Ana", "Zona 1",
                new BigDecimal("1"),
                Zona.METROPOLITANA
            ),
            false
        );

        guardarOrden(
            new Diagnostico(
                "D2", "Luis", "Villa Canales",
                new BigDecimal("1.5"),
                Zona.PERIFERICA
            ),
            false
        );

        guardarOrden(
            new Instalacion(
                "I1", "Marta", "Zona 7",
                new BigDecimal("2.5"),
                Zona.METROPOLITANA,
                5
            ),
            false
        );

        guardarOrden(
            new Instalacion(
                "I2", "Pedro", "San Lucas",
                new BigDecimal("2.1"),
                Zona.PERIFERICA,
                2
            ),
            false
        );

        guardarOrden(
            new InstalacionElectricaCertificada(
                "E1", "Rosa", "Zona 10",
                new BigDecimal("2.5"),
                Zona.METROPOLITANA,
                5,
                new BigDecimal("2000")
            ),
            false
        );

        guardarOrden(
            new InstalacionElectricaCertificada(
                "E2", "Mario", "Fraijanes",
                new BigDecimal("1.2"),
                Zona.PERIFERICA,
                2,
                new BigDecimal("1000")
            ),
            false
        );

        guardarOrden(
            new MantenimientoAire(
                "A1", "Sofia", "Zona 11",
                new BigDecimal("1.5"),
                Zona.METROPOLITANA,
                9000
            ),
            false
        );

        guardarOrden(
            new MantenimientoAire(
                "A2", "Carlos", "Zona 14",
                new BigDecimal("2"),
                Zona.METROPOLITANA,
                60000
            ),
            false
        );

        registrarCuadrilla(
            new Cuadrilla("C1", new BigDecimal("8"), true)
        );

        registrarCuadrilla(
            new Cuadrilla("C2", new BigDecimal("6"), false)
        );

        registrarCuadrilla(
            new Cuadrilla("C3", new BigDecimal("3"), true)
        );
    }
}