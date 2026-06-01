package Modelo;

import java.util.ArrayList;
import java.util.List;

public class Pedido {

    private int numeroPedido;
    private Pessoa cliente;
    private String enderecoEntrega;
    private List<ItemPedido> itens;
    private double total;

    public Pedido() {
        this.itens = new ArrayList<>();
    }

    public Pedido(int numeroPedido, Pessoa cliente, String enderecoEntrega) {
        this.numeroPedido = numeroPedido;
        this.cliente = cliente;
        this.enderecoEntrega = enderecoEntrega;
        this.itens = new ArrayList<>();
    }

    public void adicionarItem(ItemPedido item) {
        itens.add(item);
        calcularTotal();
    }

    public void calcularTotal() {
        double soma = 0;
        for (ItemPedido item : itens) {
            soma += item.getSubtotal();
        }
        this.total = soma;
    }

    public int getNumeroPedido()                      { return numeroPedido; }
    public void setNumeroPedido(int n)                { this.numeroPedido = n; }

    public Pessoa getCliente()                        { return cliente; }
    public void setCliente(Pessoa cliente)            { this.cliente = cliente; }

    public String getEnderecoEntrega()                { return enderecoEntrega; }
    public void setEnderecoEntrega(String e)          { this.enderecoEntrega = e; }

    public List<ItemPedido> getItens()                { return itens; }

    public double getTotal()                          { return total; }
}