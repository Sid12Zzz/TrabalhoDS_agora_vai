package Aplicacao;

import Modelo.Pessoa;
import Modelo.Produto;
import repository.RepositorioPedido;
import repository.RepositorioPessoa;
import repository.RepositorioProduto;
import util.DesignUI;
import java.util.List;
import Utilitario.Teclado;

public class ControladorProduto {

    private final RepositorioProduto prodRepo;
    private final RepositorioPessoa pRepo;
    private final RepositorioPedido pedRepo;

    public ControladorProduto(RepositorioProduto prodRepo, RepositorioPessoa pRepo, RepositorioPedido pedRepo) {
        this.prodRepo = prodRepo;
        this.pRepo    = pRepo;
        this.pedRepo  = pedRepo;
    }

    public void cadastrar() {
        DesignUI.subtitulo("Novo Cadastro de Produto");
        int codigo = Utilitario.Teclado.lerIntPositivo("Código do Produto:");

        if (prodRepo.codigoExiste(codigo)) {
            DesignUI.erro("Código já existente.");
            return;
        }

        String desc  = Utilitario.Teclado.lerTextoMin("Descrição/Nome:", 3);
        double custo = Utilitario.Teclado.lerDouble("Preço de Custo:");
        double venda = Utilitario.Teclado.lerDouble("Preço de Venda:");
        int fornCod  = Utilitario.Teclado.lerIntPositivo("Código do Fornecedor:");

        Pessoa fornecedor = pRepo.buscarPorCodigo(fornCod);
        if (fornecedor == null || fornecedor.getTipoPessoa().equalsIgnoreCase("CLIENTE")) {
            DesignUI.erro("Fornecedor inválido ou não cadastrado.");
            return;
        }

        try {
            if (venda < custo) DesignUI.aviso("Atenção: Preço de venda abaixo do custo.");
            prodRepo.salvar(new Produto(codigo, desc, custo, venda, fornCod));
            DesignUI.sucesso("Produto cadastrado com sucesso!");
        } catch (  RuntimeException e) {
            DesignUI.erro(e.getMessage());
        }
    }

    private void imprimirProdutos(List<Produto> lista) {
        if (lista.isEmpty()) {
            DesignUI.vazio("Nenhum produto encontrado.");
            return;
        }
        for (Produto p : lista) {
            DesignUI.abrirCaixa("Produto");
            DesignUI.linhaCaixa("Código    ", String.valueOf(p.getCodigo()));
            DesignUI.linhaCaixa("Descrição ", p.getDescricao());
            DesignUI.linhaCaixa("Custo     ", "R$ " + p.getCusto());
            DesignUI.linhaCaixa("Venda     ", "R$ " + p.getPrecoVenda());
            DesignUI.linhaCaixa("Fornecedor", "cód. " + p.getCodigoFornecedor());
            DesignUI.fecharCaixa();
            DesignUI.espaco();
        }
    }

    public void listar() {
        DesignUI.subtitulo("Catálogo de Produtos");
        DesignUI.prompt("Filtrar por descrição (vazio para todos):");
        String filtro = Utilitario.Teclado.lerLinha();

        try {
            List<Produto> lista = filtro.isEmpty() ? prodRepo.listar() : prodRepo.buscarPorDescricao(filtro);
            imprimirProdutos(lista);
        } catch (RuntimeException e) {
            DesignUI.erro(e.getMessage());
        }
    }

    public void alterar() {
        DesignUI.subtitulo("Alterar Produto");
        int codigo = Utilitario.Teclado.lerIntPositivo("Código do Produto:");

        String desc  = Utilitario.Teclado.lerTextoMin("Nova Descrição:", 3);
        double custo = Utilitario.Teclado.lerDouble("Novo Custo (R$):");
        double venda = Utilitario.Teclado.lerDouble("Novo Preço de Venda (R$):");
        int fornCod  = Utilitario.Teclado.lerIntPositivo("Código do Fornecedor:");

        Pessoa forn = pRepo.buscarPorCodigo(fornCod);
        if (forn == null || forn.getTipoPessoa().equalsIgnoreCase("CLIENTE")) {
            DesignUI.erro("Fornecedor inválido ou não cadastrado.");
            return;
        }

        try {
            if (venda < custo) DesignUI.aviso("Atenção: Preço de venda abaixo do custo.");
            prodRepo.alterar(codigo, desc, custo, venda, fornCod);
            DesignUI.sucesso("Produto alterado com sucesso!");
        } catch ( RuntimeException e) {
            DesignUI.erro(e.getMessage());
        }
    }

    public void excluir() {
        DesignUI.subtitulo("Excluir Produto");
        int codigo = Utilitario.Teclado.lerIntPositivo("Código do Produto:");

        if (pedRepo.produtoEstaEmPedido(codigo)) {
            DesignUI.erro("Produto vinculado a pedido(s) existente(s). Exclusão bloqueada.");
            return;
        }
        if (Utilitario.Teclado.lerOpcao("Confirmar exclusão? (S/N):", new String[]{"S", "N"}).equals("S")) {
            try {
                prodRepo.excluir(codigo);
                DesignUI.sucesso("Produto excluído com sucesso.");
            } catch (  RuntimeException e) {
                DesignUI.erro(e.getMessage());
            }
        } else {
            DesignUI.info("Operação cancelada.");
        }
    }
}