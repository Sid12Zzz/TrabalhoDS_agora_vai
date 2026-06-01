package Aplicacao;

import Modelo.ItemPedido;
import Modelo.Pedido;
import Modelo.Pessoa;
import Modelo.Produto;
import repository.RepositorioEndereco;
import repository.RepositorioPedido;
import repository.RepositorioPessoa;
import repository.RepositorioProduto;
import util.DesignUI;
import java.util.List;
import Utilitario.Teclado;

public class ControladorPedido {

    private final RepositorioPedido pedRepo;
    private final RepositorioPessoa pRepo;
    private final RepositorioProduto prodRepo;
    private final RepositorioEndereco eRepo;

    public ControladorPedido(RepositorioPedido pedRepo, RepositorioPessoa pRepo,
                             RepositorioProduto prodRepo, RepositorioEndereco eRepo) {
        this.pedRepo  = pedRepo;
        this.pRepo    = pRepo;
        this.prodRepo = prodRepo;
        this.eRepo    = eRepo;
    }

    public void cadastrar() {
        DesignUI.subtitulo("Abertura de Novo Pedido");
        int numero = Utilitario.Teclado.lerIntPositivo("Número do Pedido:");
        if (pedRepo.numeroExiste(numero)) {
            DesignUI.erro("Pedido já existente.");
            return;
        }

        int codCli = Utilitario.Teclado.lerIntPositivo("Código do Cliente:");
        Pessoa cliente = pRepo.buscarPorCodigo(codCli);
        if (cliente == null || cliente.getTipoPessoa().equalsIgnoreCase("FORNECEDOR")) {
            DesignUI.erro("Cliente inválido.");
            return;
        }

        DesignUI.info("Cliente identificado: " + cliente.getNome());
        String cep = Utilitario.Teclado.lerCep("CEP para entrega (apenas números):");

        try {
            if(!eRepo.cepJaExiste(codCli, cep)) {
                DesignUI.erro("CEP não cadastrado para este cliente.");
                return;
            }
        } catch (RuntimeException e) {
            DesignUI.erro("Erro ao validar endereço."); return;
        }

        Pedido pedido = new Pedido(numero, cliente, cep);
        int itensQtd  = Utilitario.Teclado.lerIntPositivo("Quantidade de itens diferentes:");

        for (int i = 0; i < itensQtd; i++) {
            int pCod  = Utilitario.Teclado.lerIntPositivo("Cód. Produto " + (i + 1) + ":");
            Produto p = prodRepo.buscarPorCodigo(pCod);
            if (p != null) {
                int q = Utilitario.Teclado.lerIntPositivo("Qtd:");
                pedido.adicionarItem(new ItemPedido(p, q));
            } else {
                DesignUI.erro("Produto não encontrado.");
            }
        }

        try {
            pedRepo.salvar(pedido);
            DesignUI.sucesso("Pedido registrado com sucesso!");
        } catch (RuntimeException e) {
            DesignUI.erro(e.getMessage());
        }
    }

    private void desenharPedido(Pedido pedido) {
        // CORREÇÃO AQUI: getNumeroPedido()
        DesignUI.abrirCaixa("Pedido nº " + pedido.getNumeroPedido());
        DesignUI.linhaCaixa("Cliente ", pedido.getCliente().getNome());
        DesignUI.linhaCaixa("Entrega ", pedido.getEnderecoEntrega());
        DesignUI.separadorCaixa();

        for (ItemPedido item : pedido.getItens()) {
            String nomeProduto = item.getProduto().getDescricao();
            DesignUI.linhaCaixa(nomeProduto, "qtd: " + item.getQuantidade() + "  |  R$ " + item.getSubtotal());
        }

        DesignUI.separadorCaixa();
        DesignUI.linhaCaixa("TOTAL   ", "R$ " + pedido.getTotal());
        DesignUI.fecharCaixa();
        DesignUI.espaco();
    }

    public void listar() {
        DesignUI.subtitulo("Listagem de Pedidos");
        String modo = Utilitario.Teclado.lerOpcao("Buscar por (NUMERO / CLIENTE / TODOS):", new String[]{"NUMERO", "CLIENTE", "TODOS"});

        try {
            if (modo.equals("NUMERO")) {
                int num = Utilitario.Teclado.lerIntPositivo("Número do pedido:");
                Pedido p = pedRepo.buscarPorNumero(num);
                if (p != null) desenharPedido(p);
                else DesignUI.vazio("Pedido não encontrado.");

            } else if (modo.equals("CLIENTE")) {
                DesignUI.prompt("Nome do cliente:");
                String filtro = Utilitario.Teclado.lerLinha();
                List<Pedido> lista = pedRepo.buscarPorCliente(filtro);
                if (lista.isEmpty()) DesignUI.vazio("Nenhum pedido para este cliente.");
                else for (Pedido p : lista) desenharPedido(p);

            } else {
                List<Pedido> lista = pedRepo.listar();
                if (lista.isEmpty()) DesignUI.vazio("Nenhum pedido cadastrado.");
                else for (Pedido p : lista) desenharPedido(p);
            }
        } catch (RuntimeException e) {
            DesignUI.erro(e.getMessage());
        }
    }

    public void alterar() {
        DesignUI.subtitulo("Alterar Entrega do Pedido");
        int numero = Utilitario.Teclado.lerIntPositivo("Número do Pedido:");
        String novoCep = Utilitario.Teclado.lerCep("Novo CEP de entrega (apenas números):");
        try {
            pedRepo.alterarEntrega(numero, novoCep);
            DesignUI.sucesso("Endereço de entrega atualizado.");
        } catch (RuntimeException e) {
            DesignUI.erro(e.getMessage());
        }
    }

    public void excluir() {
        DesignUI.subtitulo("Excluir Pedido");
        int numero = Utilitario.Teclado.lerIntPositivo("Número do Pedido:");

        if (Utilitario.Teclado.lerOpcao("Confirmar exclusão? (S/N):", new String[]{"S", "N"}).equals("S")) {
            try {
                pedRepo.excluir(numero);
                DesignUI.sucesso("Pedido apagado do sistema.");
            } catch (RuntimeException e) {
                DesignUI.erro(e.getMessage());
            }
        } else {
            DesignUI.info("Operação cancelada.");
        }
    }
}