package Modelo;

public class ItemPedido {

    private Produto produto;
    private int quantidade;
    private double subtotal;

    public ItemPedido() {}

    public ItemPedido(Produto produto, int quantidade) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.subtotal = produto.getPrecoVenda() * quantidade;
    }

    public Produto getProduto()                { return produto; }
    public void setProduto(Produto produto)    { this.produto = produto; }

    public int getQuantidade()                 { return quantidade; }
    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
        // CORRIGIDO: recalcula subtotal sempre que a quantidade mudar
        if (this.produto != null) {
            this.subtotal = this.produto.getPrecoVenda() * quantidade;
        }
    }

    public double getSubtotal() { return subtotal; }
}