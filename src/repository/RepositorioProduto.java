package repository;

import Modelo.Produto;
import Utilitario.GerenciadorLog;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RepositorioProduto implements Repositorio<Produto>, Buscavel<Produto> {

    private static final String ARQUIVO = "./data/produtos.txt";
    private static final String TEMP    = "./data/temp_produtos.txt";

    // Singleton
    private static RepositorioProduto instancia;

    private RepositorioProduto() {}

    public static RepositorioProduto getInstancia() {
        if (instancia == null) {
            instancia = new RepositorioProduto();
        }
        return instancia;
    }

    @Override
    public boolean codigoExiste(int codigo) {
        try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] d = linha.split(";");
                if (Integer.parseInt(d[0]) == codigo) return true;
            }
        } catch (Exception ignored) {}
        return false;
    }

    @Override
    public Produto buscarPorCodigo(int codigoBusca) {
        try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] d = linha.split(";");
                if (Integer.parseInt(d[0]) == codigoBusca)
                    return new Produto(Integer.parseInt(d[0]), d[1],
                            Double.parseDouble(d[2].replace(",", ".")),
                            Double.parseDouble(d[3].replace(",", ".")),
                            Integer.parseInt(d[4]));
            }
        } catch (Exception ignored) {}
        return null;
    }

    public List<Produto> buscarPorDescricao(String filtro) {
        List<Produto> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] d = linha.split(";");
                if (d[1].toLowerCase().contains(filtro.toLowerCase())) {
                    lista.add(new Produto(Integer.parseInt(d[0]), d[1],
                            Double.parseDouble(d[2].replace(",", ".")),
                            Double.parseDouble(d[3].replace(",", ".")),
                            Integer.parseInt(d[4])));
                }
            }
        } catch (Exception e) { throw new RuntimeException("Erro ao ler produtos."); }
        return lista;
    }

    @Override
    public void salvar(Produto produto) {
        if (codigoExiste(produto.getCodigo())) {
            throw new IllegalArgumentException("Já existe um produto com o código " + produto.getCodigo());
        }
        if (produto.getDescricao() == null || produto.getDescricao().trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição não pode ser vazia.");
        }
        if (produto.getCusto() < 0 || produto.getPrecoVenda() < 0) {
            throw new IllegalArgumentException("Custo e preço não podem ser negativos.");
        }
        try (FileWriter fw = new FileWriter(ARQUIVO, true); PrintWriter pw = new PrintWriter(fw)) {
            pw.println(produto.getCodigo() + ";" + produto.getDescricao() + ";" +
                    String.format(Locale.US, "%.2f", produto.getCusto()) + ";" +
                    String.format(Locale.US, "%.2f", produto.getPrecoVenda()) + ";" +
                    produto.getCodigoFornecedor());
            GerenciadorLog.registrar("Produto cadastrado: " + produto.getCodigo());
        } catch (Exception e) { throw new RuntimeException("Erro ao salvar produto."); }
    }

    @Override
    public List<Produto> listar() {
        List<Produto> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] d = linha.split(";");
                lista.add(new Produto(Integer.parseInt(d[0]), d[1],
                        Double.parseDouble(d[2].replace(",", ".")),
                        Double.parseDouble(d[3].replace(",", ".")),
                        Integer.parseInt(d[4])));
            }
        } catch (Exception e) { throw new RuntimeException("Erro ao listar produtos."); }
        return lista;
    }

    public void alterar(int codigoBusca, String novaDesc, double novoCusto, double novoPreco, int novoForn) {
        if (!codigoExiste(codigoBusca)) {
            throw new IllegalArgumentException("Produto com código " + codigoBusca + " não encontrado.");
        }
        File orig = new File(ARQUIVO);
        File temp = new File(TEMP);
        try (BufferedReader br = new BufferedReader(new FileReader(orig));
             PrintWriter pw = new PrintWriter(new FileWriter(temp))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] d = linha.split(";");
                if (Integer.parseInt(d[0]) == codigoBusca) {
                    pw.println(codigoBusca + ";" + novaDesc + ";" +
                            String.format(Locale.US, "%.2f", novoCusto) + ";" +
                            String.format(Locale.US, "%.2f", novoPreco) + ";" + novoForn);
                } else {
                    pw.println(linha);
                }
            }
        } catch (Exception e) { throw new RuntimeException("Erro ao alterar produto."); }
        orig.delete(); temp.renameTo(orig);
        GerenciadorLog.registrar("Produto alterado: " + codigoBusca);
    }

    @Override
    public void excluir(int codigoBusca) {
        if (!codigoExiste(codigoBusca)) {
            throw new IllegalArgumentException("Produto com código " + codigoBusca + " não encontrado.");
        }
        File orig = new File(ARQUIVO);
        File temp = new File(TEMP);
        try (BufferedReader br = new BufferedReader(new FileReader(orig));
             PrintWriter pw = new PrintWriter(new FileWriter(temp))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] d = linha.split(";");
                if (Integer.parseInt(d[0]) != codigoBusca) pw.println(linha);
            }
        } catch (Exception e) { throw new RuntimeException("Erro ao excluir produto."); }
        orig.delete(); temp.renameTo(orig);
        GerenciadorLog.registrar("Produto excluído: " + codigoBusca);
    }
}