package Aplicacao;

import repository.*;
import Utilitario.Teclado;
import Utilitario.DesignUI;
import Utilitario.GerenciadorMenu;

public class Main {

    public static void main(String[] args) {
        RepositorioPessoa   pRepo    = new RepositorioPessoa();
        RepositorioEndereco eRepo    = new RepositorioEndereco();
        RepositorioProduto  prodRepo = new RepositorioProduto();
        RepositorioPedido   pedRepo  = new RepositorioPedido();
        pedRepo.setProdutoRepository(prodRepo);

        ControladorPessoa  pessoaCtrl  = new ControladorPessoa(pRepo, eRepo);
        ControladorProduto produtoCtrl = new ControladorProduto(prodRepo, pRepo, pedRepo);
        ControladorPedido  pedidoCtrl  = new ControladorPedido(pedRepo, pRepo, prodRepo, eRepo);

        int opcao = -1;

        while (opcao != 0) {
            System.out.print("\033[H\033[2J");
            System.out.flush();
            DesignUI.cabecalho();
            GerenciadorMenu.mostrarMenu();
            DesignUI.rodape();

            opcao = Teclado.lerInt("Selecione uma operação:");

            switch (opcao) {
                case 1:  pessoaCtrl.cadastrar();         break;
                case 2:  pessoaCtrl.listar();            break;
                case 3:  pessoaCtrl.alterar();           break;
                case 4:  pessoaCtrl.excluir();           break;
                case 5:  produtoCtrl.cadastrar();        break;
                case 6:  produtoCtrl.listar();           break;
                case 7:  produtoCtrl.alterar();          break;
                case 8:  produtoCtrl.excluir();          break;
                case 9:  pedidoCtrl.cadastrar();         break;
                case 10: pedidoCtrl.listar();            break;
                case 11: pedidoCtrl.alterar();           break;
                case 12: pedidoCtrl.excluir();           break;
                case 13: pessoaCtrl.listarEnderecos();   break;
                case 14: pessoaCtrl.cadastrarEndereco(); break;  // NOVO
                case 15: pessoaCtrl.alterarEndereco();   break;
                case 16: pessoaCtrl.excluirEndereco();   break;
                case 0:
                    DesignUI.info("Encerrando o sistema...");
                    break;
                default:
                    DesignUI.erro("Opção inválida. Digite um número entre 0 e 16.");
            }
            if (opcao != 0) DesignUI.pausar();
        }
    }
}