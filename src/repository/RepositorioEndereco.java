package repository;

import Modelo.Endereco;
import util.GerenciadorLog;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RepositorioEndereco {

    private static final String ARQUIVO = "./data/enderecos.txt";
    private static final String TEMP    = "./data/temp_enderecos.txt";

    public boolean cepJaExiste(int codigoPessoa, String cep) {
        try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] d = linha.split(";");
                if (d.length < 6) continue;
                if (Integer.parseInt(d[0]) == codigoPessoa && d[1].equalsIgnoreCase(cep))
                    return true;
            }
        } catch (Exception ignored) {}
        return false;
    }

    public void salvar(int codigoPessoa, Endereco end) {
        if (end.getCep() == null || end.getCep().trim().isEmpty()) {
            throw new IllegalArgumentException("CEP não pode ser vazio.");
        }
        if (end.getLogradouro() == null || end.getLogradouro().trim().isEmpty()) {
            throw new IllegalArgumentException("Logradouro não pode ser vazio.");
        }
        if (cepJaExiste(codigoPessoa, end.getCep())) {
            throw new IllegalArgumentException("Essa pessoa já possui um endereço com o CEP " + end.getCep());
        }
        try (FileWriter fw = new FileWriter(ARQUIVO, true); PrintWriter pw = new PrintWriter(fw)) {
            pw.println(codigoPessoa + ";" + end.getCep() + ";" +
                    end.getLogradouro() + ";" + end.getNumero() + ";" +
                    end.getComplemento() + ";" + end.getTipo());
            GerenciadorLog.registrar("Endereco cadastrado");
        } catch (Exception e) { throw new RuntimeException("Erro ao salvar endereço."); }
    }

    // Retorna os dados em forma de Array de Strings para o Controlador desenhar
    public List<String[]> listarTodos() {
        List<String[]> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] d = linha.split(";");
                if (d.length >= 6) lista.add(d);
            }
        } catch (Exception e) { throw new RuntimeException("Erro ao listar endereços."); }
        return lista;
    }

    public void alterar(int codigoPessoa, String cepBusca, String novoLogradouro,
                        String novoNumero, String novoComplemento, String novoTipo) {
        if (!cepJaExiste(codigoPessoa, cepBusca)) {
            throw new IllegalArgumentException("Endereço não encontrado.");
        }
        File orig = new File(ARQUIVO);
        File temp = new File(TEMP);
        try (BufferedReader br = new BufferedReader(new FileReader(orig));
             PrintWriter pw = new PrintWriter(new FileWriter(temp))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] d = linha.split(";");
                if (d.length < 6) { pw.println(linha); continue; }
                if (Integer.parseInt(d[0]) == codigoPessoa && d[1].equals(cepBusca)) {
                    pw.println(codigoPessoa + ";" + cepBusca + ";" + novoLogradouro + ";" +
                            novoNumero + ";" + novoComplemento + ";" + novoTipo.toUpperCase());
                } else {
                    pw.println(linha);
                }
            }
        } catch (Exception e) { throw new RuntimeException("Erro ao alterar endereço."); }
        orig.delete(); temp.renameTo(orig);
        GerenciadorLog.registrar("Endereco alterado");
    }

    public void excluir(int codigoPessoa, String cepBusca) {
        if (!cepJaExiste(codigoPessoa, cepBusca)) {
            throw new IllegalArgumentException("Endereço não encontrado.");
        }
        File orig = new File(ARQUIVO);
        File temp = new File(TEMP);
        try (BufferedReader br = new BufferedReader(new FileReader(orig));
             PrintWriter pw = new PrintWriter(new FileWriter(temp))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] d = linha.split(";");
                if (d.length < 6) { pw.println(linha); continue; }
                if (!(Integer.parseInt(d[0]) == codigoPessoa && d[1].equals(cepBusca))) {
                    pw.println(linha);
                }
            }
        } catch (Exception e) { throw new RuntimeException("Erro ao excluir endereço."); }
        orig.delete(); temp.renameTo(orig);
        GerenciadorLog.registrar("Endereco excluído");
    }

    public void excluirPorPessoa(int codigoPessoa) {
        File orig = new File(ARQUIVO);
        File temp = new File(TEMP);
        try (BufferedReader br = new BufferedReader(new FileReader(orig));
             PrintWriter pw = new PrintWriter(new FileWriter(temp))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] d = linha.split(";");
                if (d.length < 6 || Integer.parseInt(d[0]) != codigoPessoa) {
                    pw.println(linha);
                }
            }
        } catch (Exception e) { throw new RuntimeException("Erro ao limpar endereços."); }
        orig.delete(); temp.renameTo(orig);
        GerenciadorLog.registrar("Enderecos excluidos junto com pessoa " + codigoPessoa);
    }
}