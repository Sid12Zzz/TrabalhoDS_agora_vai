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
                if (d.length < 3) continue;
                if (Integer.parseInt(d[0].trim()) == codigo) return true;
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
                if (d.length < 3) continue;
                if (Integer.parseInt(d[0].trim()) == codigoBusca)
                    return new Pessoa(Integer.parseInt(d[0].trim()), d[1].trim(), d[2].trim());
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
                if (d.length < 3) continue;
                if (d[1].trim().toLowerCase().contains(filtro.toLowerCase())) {
                    lista.add(new Pessoa(Integer.parseInt(d[0].trim()), d[1].trim(), d[2].trim()));
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
            pw.println(pessoa.getCodigo() + ";" + pessoa.getNome() + ";" + pessoa.getTipoPessoa().name());
            GerenciadorLog.registrar("Pessoa cadastrada: cod=" + pessoa.getCodigo() + " nome=" + pessoa.getNome());
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
                if (d.length < 3) continue;
                lista.add(new Pessoa(Integer.parseInt(d[0].trim()), d[1].trim(), d[2].trim()));
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
                if (d.length < 3) { pw.println(linha); continue; }
                if (Integer.parseInt(d[0].trim()) == codigoBusca) {
                    pw.println(codigoBusca + ";" + novoNome + ";" + novoTipo.toUpperCase());
                } else {
                    pw.println(linha);
                }
            }
        } catch (Exception e) { throw new RuntimeException("Erro ao alterar pessoa."); }
        orig.delete(); temp.renameTo(orig);
        GerenciadorLog.registrar("Pessoa alterada: cod=" + codigoBusca + " novoNome=" + novoNome);
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
                if (d.length < 1) continue;
                if (Integer.parseInt(d[0].trim()) != codigoBusca) pw.println(linha);
            }
        } catch (Exception e) { throw new RuntimeException("Erro ao excluir pessoa."); }
        orig.delete(); temp.renameTo(orig);
        GerenciadorLog.registrar("Pessoa excluída: cod=" + codigoBusca);
    }

    /** Verifica se há pedidos vinculados ao código da pessoa (compara por código, não por nome). */
    public boolean pessoaTemPedido(int codigoPessoa) {
        try (BufferedReader br = new BufferedReader(new FileReader("./data/pedidos.txt"))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] d = linha.split(";");
                if (d.length < 2) continue;
                // formato do pedido: numero;codCliente;cep;itens;total
                try {
                    if (Integer.parseInt(d[1].trim()) == codigoPessoa) return true;
                } catch (NumberFormatException ignored) {}
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
                if (Integer.parseInt(d[4].trim()) == codigoPessoa) return true;
            }
        } catch (Exception ignored) {}
        return false;
    }
}