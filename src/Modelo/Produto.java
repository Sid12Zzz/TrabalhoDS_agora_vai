package Modelo;

public class Produto extends Entidade {

    private String descricao;
    private double custo;
    private double precoVenda;
    private int codigoFornecedor;

    public Produto() {}

    public Produto(int codigo, String descricao, double custo,
                   double precoVenda, int codigoFornecedor) {
        super(codigo);
        this.descricao = descricao;
        this.custo = custo;
        this.precoVenda = precoVenda;
        this.codigoFornecedor = codigoFornecedor;
    }

    public String getDescricao()               { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public double getCusto()                   { return custo; }
    public void setCusto(double custo)         { this.custo = custo; }

    public double getPrecoVenda()              { return precoVenda; }
    public void setPrecoVenda(double p)        { this.precoVenda = p; }

    public int getCodigoFornecedor()           { return codigoFornecedor; }
    public void setCodigoFornecedor(int c)     { this.codigoFornecedor = c; }

    @Override
    public String resumo() {
        return "Produto [" + codigo + "] " + descricao +
                " - Custo: R$ " + custo + " | Venda: R$ " + precoVenda;
    }
}