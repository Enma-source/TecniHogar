import java.math.BigDecimal;
import java.util.Scanner;

public final class Entrada {

    private final Scanner scanner;

    public Entrada() {
        scanner = new Scanner(System.in);
    }

    public String texto(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }

    public int entero(String mensaje) {
        try {
            return Integer.parseInt(texto(mensaje));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                "Debe ingresar un entero dentro del rango de int."
            );
        }
    }

    public BigDecimal decimal(String mensaje) {
        String valor = texto(mensaje).replace(',', '.');

        Validacion.exigir(
            valor.matches("[+-]?[0-9]+(\\.[0-9]+)?"),
            "Ingrese un decimal valido, sin separadores de miles. Ejemplo: 2.5."
        );

        return new BigDecimal(valor);
    }
}