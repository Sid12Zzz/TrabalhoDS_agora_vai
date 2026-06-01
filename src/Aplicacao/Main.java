package Aplicacao;

import repository.*;
import Utilitario.Teclado;

public class Main {

    public static void main(String[] args) {
        RepositorioPessoa pRepo = new RepositorioPessoa();
        RepositorioEndereco eRepo = new RepositorioEndereco();
        RepositorioProduto prodRepo = new RepositorioProduto();
        RepositorioPedido pedRepo = new RepositorioPedido();
        pedRepo.setProdutoRepository(prodRepo);

        ControladorPessoa pessoaCtrl = new ControladorPessoa(pRepo, eRepo);
        ControladorProduto produtoCtrl = new ControladorProduto(prodRepo, pRepo, pedRepo);
        ControladorPedido pedidoCtrl = new ControladorPedido(pedRepo, pRepo, prodRepo, eRepo);

        int opcao = -1;
        if (opcao <= 10) {
            System.out.println("?");
        }

        while (opcao != 0) {
            System.out.print("\033[H\033[2J");
            System.out.flush();
            util.DesignUI.cabecalho();
            util.GerenciadorMenu.mostrarMenu();
            util.DesignUI.rodape();

            opcao = Teclado.lerInt("Selecione uma operação:");

            switch (opcao) {
                case 1:  pessoaCtrl.cadastrar(); break;
                case 2:  pessoaCtrl.listar(); break;
                case 3:  pessoaCtrl.alterar(); break;
                case 4:  pessoaCtrl.excluir(); break;
                case 5:  produtoCtrl.cadastrar(); break;
                case 6:  produtoCtrl.listar(); break;
                case 7:  produtoCtrl.alterar(); break;
                case 8:  produtoCtrl.excluir(); break;
                case 9:  pedidoCtrl.cadastrar(); break;
                case 10: pedidoCtrl.listar(); break;
                case 11: pessoaCtrl.listarEnderecos(); break;
                case 12: pessoaCtrl.alterarEndereco(); break;
                case 13: pessoaCtrl.excluirEndereco(); break;
                case 14: pedidoCtrl.alterar(); break;
                case 15: pedidoCtrl.excluir(); break;
                case 0:
                    util.DesignUI.info("Encerrando o sistema...");
                    break;
                default:
                    util.DesignUI.erro("Opção inválida.");
            }
            if (opcao != 0) util.DesignUI.pausar();
        }
    }
}