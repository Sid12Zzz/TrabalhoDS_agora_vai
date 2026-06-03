package Aplicacao;

import Modelo.Pessoa;
import Modelo.Produto;
import repository.RepositorioPedido;
import repository.RepositorioPessoa;
import repository.RepositorioProduto;
import Utilitario.DesignUI;
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
        int codigo = Teclado.lerIntPositivo("Código do Produto:");

        if (prodRepo.codigoExiste(codigo)) {
            DesignUI.erro("Código já existente.");
            return;
        }

        String desc  = Teclado.lerTextoMin("Descrição/Nome:", 3);
        double custo = Teclado.lerDouble("Preço de Custo (R$):");
        double venda = Teclado.lerDouble("Preço de Venda (R$):");
        int fornCod  = Teclado.lerIntPositivo("Código do Fornecedor:");

        Pessoa fornecedor = pRepo.buscarPorCodigo(fornCod);
        if (fornecedor == null || fornecedor.getTipoPessoa() == Modelo.TipoPessoa.CLIENTE) {
            DesignUI.erro("Fornecedor inválido ou não cadastrado.");
            return;
        }

        if (venda < custo) DesignUI.aviso("Atenção: Preço de venda abaixo do custo.");
        if (venda == 0)    DesignUI.aviso("Atenção: Preço de venda definido como zero.");

        try {
            prodRepo.salvar(new Produto(codigo, desc, custo, venda, fornCod));
            DesignUI.sucesso("Produto cadastrado com sucesso!");
        } catch (RuntimeException e) {
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
            DesignUI.linhaCaixa("Custo     ", DesignUI.formatarMoeda(p.getCusto()));
            DesignUI.linhaCaixa("Venda     ", DesignUI.formatarMoeda(p.getPrecoVenda()));
            DesignUI.linhaCaixa("Fornecedor", "cód. " + p.getCodigoFornecedor());
            DesignUI.fecharCaixa();
            DesignUI.espaco();
        }
    }

    public void listar() {
        DesignUI.subtitulo("Catálogo de Produtos");
        DesignUI.prompt("Filtrar por descrição (vazio para todos):");
        String filtro = Teclado.lerLinha();

        try {
            List<Produto> lista = filtro.isEmpty() ? prodRepo.listar() : prodRepo.buscarPorDescricao(filtro);
            imprimirProdutos(lista);
        } catch (RuntimeException e) {
            DesignUI.erro(e.getMessage());
        }
    }

    public void alterar() {
        DesignUI.subtitulo("Alterar Produto");
        int codigo = Teclado.lerIntPositivo("Código do Produto:");

        // BUG CORRIGIDO: verifica existência ANTES de pedir novos dados
        if (!prodRepo.codigoExiste(codigo)) {
            DesignUI.erro("Produto com código " + codigo + " não encontrado.");
            return;
        }

        String desc  = Teclado.lerTextoMin("Nova Descrição:", 3);
        double custo = Teclado.lerDouble("Novo Custo (R$):");
        double venda = Teclado.lerDouble("Novo Preço de Venda (R$):");
        int fornCod  = Teclado.lerIntPositivo("Código do Fornecedor:");

        Pessoa forn = pRepo.buscarPorCodigo(fornCod);
        if (forn == null || forn.getTipoPessoa() == Modelo.TipoPessoa.CLIENTE) {
            DesignUI.erro("Fornecedor inválido ou não cadastrado.");
            return;
        }

        if (venda < custo) DesignUI.aviso("Atenção: Preço de venda abaixo do custo.");

        try {
            prodRepo.alterar(codigo, desc, custo, venda, fornCod);
            DesignUI.sucesso("Produto alterado com sucesso!");
        } catch (RuntimeException e) {
            DesignUI.erro(e.getMessage());
        }
    }

    public void excluir() {
        DesignUI.subtitulo("Excluir Produto");
        int codigo = Teclado.lerIntPositivo("Código do Produto:");

        if (!prodRepo.codigoExiste(codigo)) {
            DesignUI.erro("Produto com código " + codigo + " não encontrado.");
            return;
        }
        if (pedRepo.produtoEstaEmPedido(codigo)) {
            DesignUI.erro("Produto vinculado a pedido(s) existente(s). Exclusão bloqueada.");
            return;
        }
        if (Teclado.lerOpcao("Confirmar exclusão? (S/N):", new String[]{"S", "N"}).equals("S")) {
            try {
                prodRepo.excluir(codigo);
                DesignUI.sucesso("Produto excluído com sucesso.");
            } catch (RuntimeException e) {
                DesignUI.erro(e.getMessage());
            }
        } else {
            DesignUI.info("Operação cancelada.");
        }
    }
}