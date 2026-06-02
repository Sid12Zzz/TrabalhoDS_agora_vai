package repository;

import Modelo.Pessoa;
import Utilitario.GerenciadorLog;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RepositorioPessoa implements Repositorio<Pessoa>, Buscavel<Pessoa> {

    private static final String ARQUIVO = "./data/pessoas.txt";
    private static final String TEMP    = "./data/temp_pessoas.txt";

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
    public Pessoa buscarPorCodigo(int codigoBusca) {
        try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] d = linha.split(";");
                if (Integer.parseInt(d[0]) == codigoBusca)
                    return new Pessoa(Integer.parseInt(d[0]), d[1], d[2]);
            }
        } catch (Exception ignored) {}
        return null;
    }

    public List<Pessoa> buscarPorNome(String filtro) {
        List<Pessoa> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] d = linha.split(";");
                if (d[1].toLowerCase().contains(filtro.toLowerCase())) {
                    lista.add(new Pessoa(Integer.parseInt(d[0]), d[1], d[2]));
                }
            }
        } catch (Exception e) { throw new RuntimeException("Erro ao ler arquivo."); }
        return lista;
    }

    @Override
    public void salvar(Pessoa pessoa) {
        if (codigoExiste(pessoa.getCodigo())) {
            throw new IllegalArgumentException("Já existe uma pessoa com o código " + pessoa.getCodigo());
        }
        try (FileWriter fw = new FileWriter(ARQUIVO, true); PrintWriter pw = new PrintWriter(fw)) {
            pw.println(pessoa.getCodigo() + ";" + pessoa.getNome() + ";" + pessoa.getTipoPessoa());
            GerenciadorLog.registrar("Pessoa cadastrada: " + pessoa.getCodigo());
        } catch (Exception e) { throw new RuntimeException("Erro ao salvar pessoa."); }
    }

    @Override
    public List<Pessoa> listar() {
        List<Pessoa> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] d = linha.split(";");
                lista.add(new Pessoa(Integer.parseInt(d[0]), d[1], d[2]));
            }
        } catch (Exception e) { throw new RuntimeException("Erro ao listar pessoas."); }
        return lista;
    }

    public void alterar(int codigoBusca, String novoNome, String novoTipo) {
        if (!codigoExiste(codigoBusca)) {
            throw new IllegalArgumentException("Pessoa com código " + codigoBusca + " não encontrada.");
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
                    pw.println(codigoBusca + ";" + novoNome + ";" + novoTipo.toUpperCase());
                } else {
                    pw.println(linha);
                }
            }
        } catch (Exception e) { throw new RuntimeException("Erro ao alterar pessoa."); }
        orig.delete(); temp.renameTo(orig);
        GerenciadorLog.registrar("Pessoa alterada: " + codigoBusca);
    }

    @Override
    public void excluir(int codigoBusca) {
        if (!codigoExiste(codigoBusca)) {
            throw new IllegalArgumentException("Pessoa com código " + codigoBusca + " não encontrada.");
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
        } catch (Exception e) { throw new RuntimeException("Erro ao excluir pessoa."); }
        orig.delete(); temp.renameTo(orig);
        GerenciadorLog.registrar("Pessoa excluída: " + codigoBusca);
    }

    public boolean pessoaTemPedido(int codigoPessoa) {
        try (BufferedReader br = new BufferedReader(new FileReader("./data/pedidos.txt"))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] d = linha.split(";");
                if (d.length < 2) continue;
                Pessoa p = buscarPorCodigo(codigoPessoa);
                if (p != null && d[1].equalsIgnoreCase(p.getNome())) return true;
            }
        } catch (Exception ignored) {}
        return false;
    }

    public boolean pessoaEhFornecedorDeProduto(int codigoPessoa) {
        try (BufferedReader br = new BufferedReader(new FileReader("./data/produtos.txt"))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] d = linha.split(";");
                if (d.length < 5) continue;
                if (Integer.parseInt(d[4]) == codigoPessoa) return true;
            }
        } catch (Exception ignored) {}
        return false;
    }
}