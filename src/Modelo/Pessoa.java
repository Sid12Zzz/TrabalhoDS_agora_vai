package Modelo;

import java.util.ArrayList;
import java.util.List;

public class Pessoa extends Entidade {

    private String nome;
    private String tipoPessoa; // CLIENTE, FORNECEDOR, AMBOS
    private List<Endereco> enderecos;

    public Pessoa() {
        this.enderecos = new ArrayList<>();
    }

    public Pessoa(int codigo, String nome, String tipoPessoa) {
        super(codigo);
        this.nome = nome;
        this.tipoPessoa = tipoPessoa.toUpperCase();
        this.enderecos = new ArrayList<>();
    }

    public void adicionarEndereco(Endereco endereco) {
        this.enderecos.add(endereco);
    }

    public List<Endereco> getEnderecos() {
        return enderecos;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTipoPessoa() { return tipoPessoa; }
    public void setTipoPessoa(String tipoPessoa) {
        this.tipoPessoa = tipoPessoa.toUpperCase();
    }

    @Override
    public String resumo() {
        return "Pessoa [" + codigo + "] " + nome + " - " + tipoPessoa;
    }
}