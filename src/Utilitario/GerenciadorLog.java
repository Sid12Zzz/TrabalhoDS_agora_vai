package Utilitario;
// resto do arquivo permanece igual

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class GerenciadorLog {

    private static final String ARQUIVO = "./data/log.txt";

    public static void registrar(String operacao) {
        gravar("INFO", operacao);
    }

    public static void registrarErro(String operacao, String detalhe) {
        gravar("ERRO", operacao + " | " + detalhe);
    }

    private static void gravar(String nivel, String mensagem) {
        try {
            FileWriter fw  = new FileWriter(ARQUIVO, true);
            PrintWriter pw = new PrintWriter(fw);
            pw.println("[" + nivel + "] " + LocalDateTime.now() + " - " + mensagem);
            pw.close();
            fw.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}