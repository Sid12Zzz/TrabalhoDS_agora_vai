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

    private final RepositorioPedido pedRepo   = RepositorioPedido.getInstancia();
    private final RepositorioPessoa pRepo     = RepositorioPessoa.getInstancia();
    private final RepositorioProduto prodRepo = RepositorioProduto.getInstancia();
    private final RepositorioEndereco eRepo   = RepositorioEndereco.getInstancia();

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

        String cep = null;
        while (true) {
            String entrada = Teclado.lerCep("CEP para entrega (apenas números ou 0 para cancelar):");
            if (entrada.equals("0")) {
                DesignUI.info("Operação cancelada.");
                return;
            }
            if (eRepo.cepJaExiste(codCli, entrada)) {
                cep = entrada;
                break;
            }
            DesignUI.erro("CEP não cadastrado para este cliente. Tente novamente ou digite 0 para cancelar.");
        }

        Pedido pedido = new Pedido(numero, cliente, cep);

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

        if (!pedido.isValido()) {
            DesignUI.erro(pedido.getMensagemErro());
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

        Pedido pedido = pedRepo.buscarPorNumero(numero);
        int codCliente = pedido.getCliente().getCodigo();
        DesignUI.info("Pedido de: " + pedido.getCliente().getNome());

        String novoCep = null;
        while (true) {
            String entrada = Teclado.lerCep("Novo CEP de entrega (apenas números ou 0 para cancelar):");
            if (entrada.equals("0")) {
                DesignUI.info("Operação cancelada.");
                return;
            }
            if (eRepo.cepJaExiste(codCliente, entrada)) {
                novoCep = entrada;
                break;
            }
            DesignUI.erro("CEP não cadastrado para este cliente. Tente novamente ou digite 0 para cancelar.");
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