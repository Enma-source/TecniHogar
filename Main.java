import java.math.BigDecimal;
import java.util.NoSuchElementException;

public class Main {

    public static void main(String[] args) {
        TecniHogar sistema = new TecniHogar();
        Entrada entrada = new Entrada();

        boolean continuar = true;

        while (continuar) {
            System.out.println("\n--- TECNIHOGAR ---");
            System.out.println("1. Cotizar orden");
            System.out.println("2. Registrar orden");
            System.out.println("3. Registrar cuadrilla");
            System.out.println("4. Crear jornada");
            System.out.println("5. Asignar orden");
            System.out.println("6. Retirar orden");
            System.out.println("7. Iniciar jornada");
            System.out.println("8. Registrar resultado de visita");
            System.out.println("9. Consultar ordenes");
            System.out.println("10. Consultar cuadrillas");
            System.out.println("11. Reporte por categoria y estado");
            System.out.println("12. Reporte de ingresos");
            System.out.println("13. Jornadas no cerradas");
            System.out.println("0. Salir");

            try {
                int opcion = entrada.entero("Opcion: ");

                switch (opcion) {
                    case 1:
                    case 2: {
                        System.out.println("1. Diagnostico");
                        System.out.println("2. Instalacion");
                        System.out.println(
                            "3. Instalacion electrica certificada"
                        );
                        System.out.println("4. Mantenimiento de aire");

                        int tipo = entrada.entero("Categoria: ");

                        Validacion.exigir(
                            tipo >= 1 && tipo <= 4,
                            "Categoria inexistente."
                        );

                        String codigo = entrada.texto("Codigo: ");
                        String cliente = entrada.texto("Cliente: ");
                        String direccion = entrada.texto("Direccion: ");

                        BigDecimal horas = entrada.decimal(
                            "Horas estimadas: "
                        );

                        int numeroZona = entrada.entero(
                            "Zona (1 metropolitana, 2 periferica): "
                        );

                        Validacion.exigir(
                            numeroZona == 1 || numeroZona == 2,
                            "Zona invalida."
                        );

                        Zona zona = numeroZona == 1
                                ? Zona.METROPOLITANA
                                : Zona.PERIFERICA;

                        OrdenServicio orden;

                        // Construye objetos; no calcula sus reglas.
                        switch (tipo) {
                            case 1:
                                orden = new Diagnostico(
                                    codigo,
                                    cliente,
                                    direccion,
                                    horas,
                                    zona
                                );
                                break;

                            case 2:
                                orden = new Instalacion(
                                    codigo,
                                    cliente,
                                    direccion,
                                    horas,
                                    zona,
                                    entrada.entero("Cantidad de puntos: ")
                                );
                                break;

                            case 3:
                                orden = new InstalacionElectricaCertificada(
                                    codigo,
                                    cliente,
                                    direccion,
                                    horas,
                                    zona,
                                    entrada.entero("Cantidad de puntos: "),
                                    entrada.decimal("Valor de materiales (Q): ")
                                );
                                break;

                            case 4:
                                orden = new MantenimientoAire(
                                    codigo,
                                    cliente,
                                    direccion,
                                    horas,
                                    zona,
                                    entrada.entero("Capacidad del equipo (BTU): ")
                                );
                                break;

                            default:
                                throw new IllegalArgumentException(
                                    "Categoria inexistente."
                                );
                        }

                        System.out.println(
                            "Costo: "
                            + Validacion.dinero(orden.calcularCosto())
                            + " | Atencion estimada: "
                            + orden.diasAtencion()
                            + " dias habiles."
                        );

                        switch (opcion) {
                            case 1:
                                System.out.println(
                                    "Cotizacion sin registro ni cobro."
                                );
                                break;

                            case 2:
                                int aceptar = entrada.entero(
                                    "Aceptar y cobrar (1 si, 2 no): "
                                );

                                switch (aceptar) {
                                    case 1:
                                        sistema.registrarOrden(orden);

                                        System.out.println(
                                            "Orden registrada, cobrada y pendiente."
                                        );
                                        break;

                                    case 2:
                                        System.out.println(
                                            "No se registro la orden ni se genero ingreso."
                                        );
                                        break;

                                    default:
                                        throw new IllegalArgumentException(
                                            "Respuesta invalida."
                                        );
                                }
                                break;
                        }

                        break;
                    }

                    case 3: {
                        String codigo = entrada.texto(
                            "Codigo de cuadrilla: "
                        );

                        BigDecimal capacidad = entrada.decimal(
                            "Capacidad de horas por jornada: "
                        );

                        int certificada = entrada.entero(
                            "Certificada en refrigeracion (1 si, 2 no): "
                        );

                        Validacion.exigir(
                            certificada == 1 || certificada == 2,
                            "Respuesta invalida."
                        );

                        sistema.registrarCuadrilla(
                            new Cuadrilla(
                                codigo,
                                capacidad,
                                certificada == 1
                            )
                        );

                        System.out.println("Cuadrilla registrada.");
                        break;
                    }

                    case 4: {
                        String codigo = entrada.texto(
                            "Codigo de la nueva jornada: "
                        );

                        sistema.crearJornada(
                            codigo,
                            entrada.texto("Codigo de cuadrilla: ")
                        );

                        System.out.println(
                            "Jornada creada en preparacion."
                        );
                        break;
                    }

                    case 5: {
                        String jornada = entrada.texto(
                            "Codigo de jornada: "
                        );

                        sistema.asignarOrden(
                            jornada,
                            entrada.texto("Codigo de orden: ")
                        );

                        System.out.println(
                            "Orden asignada, sin cargo adicional."
                        );
                        break;
                    }

                    case 6: {
                        String jornada = entrada.texto(
                            "Codigo de jornada: "
                        );

                        sistema.retirarOrden(
                            jornada,
                            entrada.texto("Codigo de orden: ")
                        );

                        System.out.println(
                            "Orden retirada y pendiente."
                        );
                        break;
                    }

                    case 7: {
                        sistema.iniciarJornada(
                            entrada.texto("Codigo de jornada: ")
                        );

                        System.out.println(
                            "Jornada iniciada. Se cobraron las visitas adicionales aplicables."
                        );
                        break;
                    }

                    case 8: {
                        String jornada = entrada.texto(
                            "Codigo de jornada: "
                        );

                        String codigo = entrada.texto(
                            "Codigo de orden: "
                        );

                        int resultado = entrada.entero(
                            "Resultado (1 completada, 2 fallida): "
                        );

                        Validacion.exigir(
                            resultado == 1 || resultado == 2,
                            "Resultado invalido."
                        );

                        sistema.registrarResultado(
                            jornada,
                            codigo,
                            resultado == 1
                                ? ResultadoVisita.COMPLETADA
                                : ResultadoVisita.FALLIDA
                        );

                        System.out.println(
                            "Resultado registrado. Jornada: "
                            + sistema.buscarJornada(jornada).getEstado()
                        );
                        break;
                    }

                    case 9:
                        System.out.println(
                            sistema.consultarOrdenes()
                        );
                        break;

                    case 10:
                        System.out.println(
                            sistema.consultarCuadrillas()
                        );
                        break;

                    case 11:
                        System.out.println(
                            sistema.reporteEstados()
                        );
                        break;

                    case 12:
                        System.out.println(
                            sistema.reporteIngresos()
                        );
                        break;

                    case 13:
                        System.out.println(
                            sistema.reporteJornadas()
                        );
                        break;

                    case 0:
                        continuar = false;
                        System.out.println("Programa finalizado.");
                        break;

                    default:
                        System.out.println("Opcion inexistente.");
                }

            } catch (IllegalArgumentException | IllegalStateException ex) {
                System.out.println(
                    "Operacion rechazada: " + ex.getMessage()
                );

            } catch (NoSuchElementException ex) {
                System.out.println(
                    "Fin de la entrada. Programa finalizado."
                );

                continuar = false;
            }
        }
    }
}