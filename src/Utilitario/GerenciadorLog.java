package Utilitario;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GerenciadorLog {

    private static final String ARQUIVO = "./data/log.txt";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static void registrar(String operacao) {
        gravar("INFO", operacao);
    }

    public static void registrarErro(String operacao, String detalhe) {
        gravar("ERRO", operacao + " | " + detalhe);
    }

    private static void gravar(String nivel, String mensagem) {
        try (FileWriter fw = new FileWriter(ARQUIVO, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println("[" + nivel + "] " + LocalDateTime.now().format(FMT) + " - " + mensagem);
        } catch (Exception e) {
            System.out.println("Aviso: não foi possível gravar no log. " + e.getMessage());
        }
    }
}