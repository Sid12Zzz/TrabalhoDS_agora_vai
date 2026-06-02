package Utilitario;

import java.util.Locale;
import java.util.Scanner;

public class Teclado {

    // O Scanner fica encapsulado e protegido aqui dentro
    private static final Scanner sc = new Scanner(System.in).useLocale(Locale.US);

    public static String lerLinha() {
        return sc.nextLine().trim();
    }

    public static String lerTexto(String msg) {
        DesignUI.prompt(msg);
        return sc.nextLine().trim();
    }

    public static String lerTextoMin(String msg, int min) {
        while (true) {
            String txt = lerTexto(msg);
            if (txt.length() >= min) return txt;
            DesignUI.erro("O texto deve ter pelo menos " + min + " caracteres.");
        }
    }

    public static String lerNome(String msg) {
        while (true) {
            String nome = lerTextoMin(msg, 3);
            if (nome.matches(".*[a-zA-ZÀ-ÿ].*")) return nome;
            DesignUI.erro("Nome inválido. Use pelo menos uma letra.");
        }
    }

    public static int lerInt(String msg) {
        while (true) {
            DesignUI.prompt(msg);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                DesignUI.erro("Entrada inválida. Digite um número inteiro.");
            }
        }
    }

    public static int lerIntPositivo(String msg) {
        while (true) {
            int v = lerInt(msg);
            if (v > 0) return v;
            DesignUI.erro("O valor deve ser maior que zero.");
        }
    }

    public static double lerDouble(String msg) {
        while (true) {
            DesignUI.prompt(msg);
            try {
                double v = Double.parseDouble(sc.nextLine().trim().replace(",", "."));
                if (v < 0) {
                    DesignUI.erro("O valor não pode ser negativo.");
                } else {
                    return v;
                }
            } catch (NumberFormatException e) {
                DesignUI.erro("Entrada inválida. Digite um número decimal válido.");
            }
        }
    }

    public static String lerCep(String msg) {
        while (true) {
            String cep = lerTexto(msg).replaceAll("[^0-9]", "");
            if (cep.length() == 8) return cep;
            DesignUI.erro("CEP inválido. Digite exatamente 8 números.");
        }
    }

    public static String lerOpcao(String msg, String[] opcoes) {
        while (true) {
            String lido = lerTexto(msg).toUpperCase();
            for (String op : opcoes) {
                if (op.toUpperCase().equals(lido)) return op;
            }
            DesignUI.erro("Opção inválida.");
        }
    }
}