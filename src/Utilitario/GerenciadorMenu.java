package Utilitario;

import java.io.BufferedReader;
import java.io.FileReader;

public class GerenciadorMenu {

    private static final String ARQUIVO = "./data/menus.txt";

    public static void mostrarMenu() {
        String[][] pessoasItens  = new String[4][2];
        String[][] produtosItens = new String[4][2];
        String[][] pedidosItens  = new String[4][2];
        String[][] endItens      = new String[4][2];  // era 3, agora 4 (inclui Cadastrar Endereço)

        int pi = 0, pri = 0, pei = 0, ei = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                linha = linha.trim();
                if (linha.isEmpty() || linha.startsWith("=")) continue;

                int sep = linha.indexOf(" - ");
                if (sep == -1) continue;

                String num   = linha.substring(0, sep).trim();
                String descr = linha.substring(sep + 3).trim();
                int opcao;
                try { opcao = Integer.parseInt(num); }
                catch (NumberFormatException e) { continue; }

                if      (opcao >= 1  && opcao <= 4  && pi  < 4) { pessoasItens [pi++]  = new String[]{num, descr}; }
                else if (opcao >= 5  && opcao <= 8  && pri < 4) { produtosItens[pri++] = new String[]{num, descr}; }
                else if (opcao >= 9  && opcao <= 12 && pei < 4) { pedidosItens [pei++] = new String[]{num, descr}; }
                else if (opcao >= 13 && opcao <= 16 && ei  < 4) { endItens     [ei++]  = new String[]{num, descr}; }
            }
        } catch (Exception e) {
            DesignUI.erro("Erro ao carregar menus.txt: " + e.getMessage());
            return;
        }

        DesignUI.blocoMenu("✦", DesignUI.AZURE_VIBRANTE, "PESSOAS",   pessoasItens,  null);
        DesignUI.blocoMenu("✦", DesignUI.AZURE_VIBRANTE, "PRODUTOS",  produtosItens, null);
        DesignUI.blocoMenu("✦", DesignUI.AZURE_VIBRANTE, "PEDIDOS",   pedidosItens,  null);
        DesignUI.blocoMenu("✦", DesignUI.AZURE_VIBRANTE, "ENDEREÇOS", endItens,      null);
    }
}