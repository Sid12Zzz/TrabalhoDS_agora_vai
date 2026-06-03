package Utilitario;
// resto do arquivo permanece igual

import java.util.Locale;

/**
 * UI: CORPORATE AZURE COMPACT (FIXED)
 * Design monolítico e contínuo, com correção de buffer de teclado.
 */
public class DesignUI {

    public static final String ESC = "\u001B[";
    public static final String RESET = ESC + "0m";
    public static final String BOLD = ESC + "1m";
    public static final String DIM = ESC + "2m";

    public static final String AZURE_VIBRANTE = ESC + "38;5;39m";
    public static final String AZURE_SKY      = ESC + "38;5;117m";
    public static final String AZURE_DEEP     = ESC + "38;5;25m";
    public static final String SLATE_DARK     = ESC + "38;5;236m";

    public static final String BRANCO_PURO    = ESC + "38;5;255m";
    public static final String TEXTO_CLARO    = ESC + "38;5;252m";
    public static final String TEXTO_MUTED    = ESC + "38;5;244m";
    public static final String TEXTO_SUBTIL   = ESC + "38;5;239m";

    public static final String SUCESSO = ESC + "38;5;121m";
    public static final String ERRO    = ESC + "38;5;204m";
    public static final String AVISO   = ESC + "38;5;222m";

    public static final String BG_AZURE = ESC + "48;5;26m" + ESC + "38;5;255m";

    public static final int W = 64;
    private static final String INDENT = " ";
    private static final String ANSI_REGEX = "\u001B\\[[0-9;]*m";
    private static final String MARK = "✦";

    // ─── UTILITÁRIO DE MOEDA ───────────────────────────────────────
    /** Formata double como "R$ 1.234,56" */
    public static String formatarMoeda(double valor) {
        return String.format(new java.util.Locale("pt", "BR"), "R$ %,.2f", valor);
    }

    // ─── COMPONENTES ──────────────────────────────────────────────

    public static void cabecalho() {
        println(AZURE_DEEP + "╔" + repeat("═", W) + "╗");
        println(AZURE_DEEP + "║" + center(BOLD + BRANCO_PURO + "  S I S T E M A   D E   G E S T Ã O  " + RESET, W) + AZURE_DEEP + "║");
        println(AZURE_DEEP + "║" + center(DIM + AZURE_SKY + "ADMINISTRAÇÃO INTEGRADA E CONTROLE DE DADOS" + RESET, W) + AZURE_DEEP + "║");
        println(AZURE_DEEP + "╚" + repeat("═", W) + "╝");
    }

    public static void blocoMenu(String icone, String cor, String titulo, String[][] itens, String[][] secundario) {
        String head = "  " + MARK + "  " + titulo.toUpperCase() + "  ";
        int tracos = Math.max(0, W - visibleLength(head) - 2);

        println(AZURE_VIBRANTE + "┌─" + RESET + BOLD + BRANCO_PURO + head + RESET + AZURE_VIBRANTE + repeat("─", tracos) + "┐");

        for (String[] item : normalizar(itens)) {
            if (item == null || item.length < 2) continue;
            String linha = "  " + BOLD + AZURE_SKY + left(item[0], 3) + RESET + "  " + TEXTO_CLARO + item[1];
            println(AZURE_VIBRANTE + "│" + RESET + left(linha, W) + AZURE_VIBRANTE + "│");
        }

        if (secundario != null && secundario.length > 0) {
            println(AZURE_VIBRANTE + "├" + repeat("─", W) + "┤");
            for (String[] item : normalizar(secundario)) {
                if (item == null || item.length < 2) continue;
                String linha = "  " + BOLD + AZURE_SKY + left(item[0], 3) + RESET + "  " + TEXTO_CLARO + item[1];
                println(AZURE_VIBRANTE + "│" + RESET + left(linha, W) + AZURE_VIBRANTE + "│");
            }
        }
        println(AZURE_VIBRANTE + "└" + repeat("─", W) + "┘");
    }

    public static void rodape() {
        println(AZURE_DEEP + "╒" + repeat("═", W) + "╕");
        String sair = "  " + BOLD + ERRO + "0" + RESET + "  " + TEXTO_CLARO + "ENCERRAR SESSÃO" + RESET;
        println(AZURE_DEEP + "│" + RESET + left(sair, W) + AZURE_DEEP + "│");
        println(AZURE_DEEP + "╘" + repeat("═", W) + "╛");
    }

    public static void secao(String icone, String titulo, String[][] itens) {
        String head = "  " + MARK + "  " + titulo + "  ";
        int tracos = Math.max(0, W - visibleLength(head) - 2);
        println(AZURE_SKY + "╭─" + RESET + BOLD + BRANCO_PURO + head + RESET + AZURE_SKY + repeat("─", tracos) + "╮");
        for (String[] it : normalizar(itens)) {
            if (it == null || it.length < 2) continue;
            String linha = "  " + BOLD + AZURE_VIBRANTE + left(it[0], 3) + RESET + "  " + TEXTO_CLARO + it[1];
            println(AZURE_SKY + "│" + RESET + left(linha, W) + AZURE_SKY + "│");
        }
        println(AZURE_SKY + "╰" + repeat("─", W) + "╯");
    }

    public static void abrirCaixa(String titulo) {
        String head = "  " + MARK + "  " + titulo + "  ";
        int tracos = Math.max(0, W - visibleLength(head) - 2);
        println(AZURE_DEEP + "┌─" + RESET + BOLD + AZURE_SKY + head + RESET + AZURE_DEEP + repeat("─", tracos) + "┐");
    }

    public static void fecharCaixa()      { println(AZURE_DEEP + "└" + repeat("─", W) + "┘"); }
    public static void separadorCaixa()   { println(AZURE_DEEP + "├" + repeat("─", W) + "┤"); }
    public static void linhaCaixa(String k, String v) {
        String linha = "  " + DIM + AZURE_SKY + left(k, 12) + RESET + " " + BRANCO_PURO + v;
        println(AZURE_DEEP + "│" + RESET + left(linha, W) + AZURE_DEEP + "│");
    }

    public static String badge(String texto) {
        String t = textoSeguro(texto).toUpperCase(Locale.ROOT);
        return BG_AZURE + BOLD + " " + (t.isBlank() ? "INFO" : t) + " " + RESET;
    }

    public static void sucesso(String m) { feedback(AZURE_VIBRANTE, SUCESSO, "✓", m); }
    public static void erro(String m)    { feedback(ERRO, ERRO, "✕", m); }
    public static void aviso(String m)   { feedback(AVISO, AVISO, "!", m); }
    public static void info(String m)    { System.out.println(INDENT + AZURE_VIBRANTE + "ℹ " + RESET + TEXTO_MUTED + textoSeguro(m) + RESET); }

    private static void feedback(String corIcone, String corTexto, String ico, String m) {
        System.out.println(INDENT + corIcone + BOLD + ico + RESET + "  " + corTexto + textoSeguro(m) + RESET);
    }

    public static void prompt(String m) {
        System.out.print(INDENT + AZURE_VIBRANTE + "❯ " + RESET + BRANCO_PURO + textoSeguro(m) + RESET + " ");
    }

    public static void subtitulo(String m) {
        System.out.println(INDENT + AZURE_VIBRANTE + MARK + " " + RESET + BOLD + BRANCO_PURO + textoSeguro(m).toUpperCase() + RESET);
    }

    public static void pausar() {
        System.out.print(INDENT + TEXTO_SUBTIL + "  Pressione ENTER para continuar..." + RESET);
        try {
            while (System.in.available() > 0) System.in.read();
            System.in.read();
        } catch (Exception e) {}
    }

    public static void espaco()        { System.out.println(); }
    public static void vazio(String m) { System.out.println(INDENT + TEXTO_SUBTIL + "○ " + textoSeguro(m) + RESET); }

    private static String[][] normalizar(String[][] i) { return i == null ? new String[0][] : i; }
    private static String textoSeguro(String s)        { return s == null ? "" : s; }
    private static void println(String c)              { System.out.println(INDENT + c + RESET); }
    private static String center(String t, int l) {
        int v = visibleLength(t);
        int p = Math.max(0, l - v);
        int e = p / 2;
        return repeat(" ", e) + t + repeat(" ", p - e);
    }
    private static String left(String t, int l) { return t + repeat(" ", Math.max(0, l - visibleLength(t))); }
    private static int visibleLength(String t) {
        String limpo = textoSeguro(t).replaceAll(ANSI_REGEX, "");
        return limpo.codePointCount(0, limpo.length());
    }
    private static String repeat(String t, int v) { return t.repeat(Math.max(0, v)); }
}