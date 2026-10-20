import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

public final class Validacion {

    private Validacion() {
    }

    public static void exigir(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new IllegalArgumentException(mensaje);
        }
    }

    public static String texto(String valor, String campo) {
        exigir(
            valor != null && !valor.trim().isEmpty(),
            campo + " no puede estar vacio."
        );

        return valor.trim();
    }

    public static String codigo(String valor) {
        return texto(valor, "Codigo").toUpperCase(Locale.ROOT);
    }

    public static BigDecimal positivo(BigDecimal valor, String campo) {
        exigir(
            valor != null && valor.signum() > 0,
            campo + " debe ser positivo."
        );

        return valor;
    }

    public static String dinero(BigDecimal valor) {
        return "Q" + valor
                .setScale(2, RoundingMode.HALF_UP)
                .toPlainString();
    }
}