package Aplicacao;

import Modelo.ItemPedido;
import Modelo.Pedido;
import Modelo.Pessoa;
import Modelo.Produto;
import repository.RepositorioEndereco;
import repository.RepositorioPedido;
import repository.RepositorioPessoa;
import repository.RepositorioProduto;
import Utilitario.DesignUI;
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
        int numero = Teclado.lerIntPositivo("Número do Pedido:");
        if (pedRepo.numeroExiste(numero)) {
            DesignUI.erro("Pedido já existente.");
            return;
        }

        int codCli = Teclado.lerIntPositivo("Código do Cliente:");
        Pessoa cliente = pRepo.buscarPorCodigo(codCli);
        if (cliente == null || cliente.getTipoPessoa() == Modelo.TipoPessoa.FORNECEDOR) {
            DesignUI.erro("Cliente inválido ou não cadastrado.");
            return;
        }

        DesignUI.info("Cliente identificado: " + cliente.getNome());
        String cep = Teclado.lerCep("CEP para entrega (apenas números):");

        if (!eRepo.cepJaExiste(codCli, cep)) {
            DesignUI.erro("CEP não cadastrado para este cliente.");
            return;
        }

        Pedido pedido = new Pedido(numero, cliente, cep);

        // BUG CORRIGIDO: loop garante pelo menos 1 item válido
        int itensQtd = Teclado.lerIntPositivo("Quantidade de itens diferentes:");
        int itensAdicionados = 0;

        for (int i = 0; i < itensQtd; i++) {
            int pCod  = Teclado.lerIntPositivo("Cód. Produto " + (i + 1) + ":");
            Produto p = prodRepo.buscarPorCodigo(pCod);
            if (p != null) {
                int q = Teclado.lerIntPositivo("Quantidade:");
                pedido.adicionarItem(new ItemPedido(p, q));
                itensAdicionados++;
                DesignUI.info(p.getDescricao() + " — " + DesignUI.formatarMoeda(p.getPrecoVenda()) + " x " + q);
            } else {
                DesignUI.erro("Produto não encontrado. Item ignorado.");
            }
        }

        // BUG CORRIGIDO: bloqueia pedido sem itens válidos
        if (itensAdicionados == 0) {
            DesignUI.erro("Nenhum item válido adicionado. Pedido cancelado.");
            return;
        }

        DesignUI.info("Total do pedido: " + DesignUI.formatarMoeda(pedido.getTotal()));

        try {
            pedRepo.salvar(pedido);
            DesignUI.sucesso("Pedido registrado com sucesso!");
        } catch (RuntimeException e) {
            DesignUI.erro(e.getMessage());
        }
    }

    private void desenharPedido(Pedido pedido) {
        DesignUI.abrirCaixa("Pedido nº " + pedido.getNumeroPedido());
        DesignUI.linhaCaixa("Cliente ", pedido.getCliente().getNome()
                + " (cód. " + pedido.getCliente().getCodigo() + ")");
        DesignUI.linhaCaixa("Entrega ", pedido.getEnderecoEntrega());
        DesignUI.separadorCaixa();

        for (ItemPedido item : pedido.getItens()) {
            DesignUI.linhaCaixa(item.getProduto().getDescricao(),
                    "qtd: " + item.getQuantidade()
                            + "  |  " + DesignUI.formatarMoeda(item.getSubtotal()));
        }

        DesignUI.separadorCaixa();
        DesignUI.linhaCaixa("TOTAL   ", DesignUI.formatarMoeda(pedido.getTotal()));
        DesignUI.fecharCaixa();
        DesignUI.espaco();
    }

    public void listar() {
        DesignUI.subtitulo("Listagem de Pedidos");
        String modo = Teclado.lerOpcao("Buscar por (NUMERO / CLIENTE / TODOS):",
                new String[]{"NUMERO", "CLIENTE", "TODOS"});

        try {
            if (modo.equals("NUMERO")) {
                int num = Teclado.lerIntPositivo("Número do pedido:");
                Pedido p = pedRepo.buscarPorNumero(num);
                if (p != null) desenharPedido(p);
                else DesignUI.vazio("Pedido não encontrado.");

            } else if (modo.equals("CLIENTE")) {
                DesignUI.prompt("Nome do cliente:");
                String filtro = Teclado.lerLinha();
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
        DesignUI.subtitulo("Alterar Endereço de Entrega do Pedido");
        int numero = Teclado.lerIntPositivo("Número do Pedido:");

        if (!pedRepo.numeroExiste(numero)) {
            DesignUI.erro("Pedido não encontrado.");
            return;
        }

        // BUG CORRIGIDO: valida CEP contra endereços cadastrados do cliente
        Pedido pedido = pedRepo.buscarPorNumero(numero);
        int codCliente = pedido.getCliente().getCodigo();

        String novoCep = Teclado.lerCep("Novo CEP de entrega (apenas números):");

        if (!eRepo.cepJaExiste(codCliente, novoCep)) {
            DesignUI.erro("O CEP informado não está cadastrado para o cliente deste pedido.");
            return;
        }

        try {
            pedRepo.alterarEntrega(numero, novoCep);
            DesignUI.sucesso("Endereço de entrega atualizado.");
        } catch (RuntimeException e) {
            DesignUI.erro(e.getMessage());
        }
    }

    public void excluir() {
        DesignUI.subtitulo("Excluir Pedido");
        int numero = Teclado.lerIntPositivo("Número do Pedido:");

        if (!pedRepo.numeroExiste(numero)) {
            DesignUI.erro("Pedido não encontrado.");
            return;
        }

        if (Teclado.lerOpcao("Confirmar exclusão? (S/N):", new String[]{"S", "N"}).equals("S")) {
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