package Aplicacao;

import Modelo.Endereco;
import Modelo.Pessoa;
import repository.RepositorioEndereco;
import repository.RepositorioPessoa;
import util.DesignUI;
import java.util.List;
import Utilitario.Teclado;

public class ControladorPessoa {

    private final RepositorioPessoa pRepo;
    private final RepositorioEndereco eRepo;

    public ControladorPessoa(RepositorioPessoa pRepo, RepositorioEndereco eRepo) {
        this.pRepo = pRepo;
        this.eRepo = eRepo;
    }

    public void cadastrar() {
        DesignUI.subtitulo("Novo Cadastro de Pessoa");
        int codigo = Utilitario.Teclado.lerIntPositivo("Código Identificador:");

        if (pRepo.codigoExiste(codigo)) {
            DesignUI.erro("O código " + codigo + " já está em uso.");
            return;
        }

        String nome = Utilitario.Teclado.lerNome("Nome Completo:");
        String tipo = Utilitario.Teclado.lerOpcao("Vínculo (CLIENTE / FORNECEDOR / AMBOS):", new String[]{"CLIENTE", "FORNECEDOR", "AMBOS"});
        Pessoa pessoa = new Pessoa(codigo, nome, tipo);

        String continuar = "S";
        while (continuar.equals("S")) {
            String cep = Utilitario.Teclado.lerCep("CEP (apenas números):");
            if (eRepo.cepJaExiste(codigo, cep)) {
                DesignUI.erro("Este CEP já está registrado para esta pessoa.");
            } else {
                String logradouro  = Utilitario.Teclado.lerTextoMin("Logradouro:", 3);
                String numero      = Utilitario.Teclado.lerTexto("Número:");
                String complemento = Utilitario.Teclado.lerTexto("Complemento:");
                String tipoEnd     = Utilitario.Teclado.lerOpcao("Finalidade (COMERCIAL / RESIDENCIAL / ENTREGA / CORRESPONDENCIA):",
                        new String[]{"COMERCIAL", "RESIDENCIAL", "ENTREGA", "CORRESPONDENCIA"});

                Endereco end = new Endereco(cep, logradouro, numero, complemento, tipoEnd);
                pessoa.adicionarEndereco(end);

                try {
                    eRepo.salvar(codigo, end);
                } catch ( RuntimeException e) {
                    DesignUI.erro(e.getMessage());
                }
            }
            continuar = Utilitario.Teclado.lerOpcao("Adicionar outro endereço? (S/N):", new String[]{"S", "N"});
        }

        try {
            pRepo.salvar(pessoa);
            DesignUI.sucesso("Pessoa salva com sucesso!");
        } catch ( RuntimeException e) {
            DesignUI.erro(e.getMessage());
        }
    }

    public void listar() {
        DesignUI.subtitulo("Listagem de Pessoas");
        DesignUI.prompt("Filtro de nome (vazio para todos):");
        String filtro = Utilitario.Teclado.lerLinha();

        try {
            List<Pessoa> lista = filtro.isEmpty() ? pRepo.listar() : pRepo.buscarPorNome(filtro);
            if (lista.isEmpty()) {
                DesignUI.vazio("Nenhuma pessoa encontrada.");
                return;
            }
            for (Pessoa p : lista) {
                DesignUI.abrirCaixa("Pessoa");
                DesignUI.linhaCaixa("Código", String.valueOf(p.getCodigo()));
                DesignUI.linhaCaixa("Nome  ", p.getNome());
                DesignUI.linhaCaixa("Tipo  ", p.getTipoPessoa());
                DesignUI.fecharCaixa();
                DesignUI.espaco();
            }
        } catch (RuntimeException e) {
            DesignUI.erro(e.getMessage());
        }
    }

    public void alterar() {
        DesignUI.subtitulo("Atualização de Cadastro");
        int codigo = Utilitario.Teclado.lerIntPositivo("Código da pessoa:");

        String nome = Utilitario.Teclado.lerNome("Novo nome:");
        String tipo = Utilitario.Teclado.lerOpcao("Novo vínculo:", new String[]{"CLIENTE", "FORNECEDOR", "AMBOS"});

        try {
            pRepo.alterar(codigo, nome, tipo);
            DesignUI.sucesso("Pessoa alterada com sucesso.");
        } catch ( RuntimeException e) {
            DesignUI.erro(e.getMessage());
        }
    }

    public void excluir() {
        DesignUI.subtitulo("Exclusão de Registro");
        int codigo = Utilitario.Teclado.lerIntPositivo("Código da pessoa:");

        if (pRepo.pessoaTemPedido(codigo)) {
            DesignUI.erro("Pessoa possui pedidos vinculados. Exclusão bloqueada.");
            return;
        }
        if (pRepo.pessoaEhFornecedorDeProduto(codigo)) {
            DesignUI.erro("Pessoa é fornecedor de produto(s). Exclusão bloqueada.");
            return;
        }
        if (Utilitario.Teclado.lerOpcao("Confirmar exclusão definitiva? (S/N):", new String[]{"S", "N"}).equals("S")) {
            try {
                pRepo.excluir(codigo);
                eRepo.excluirPorPessoa(codigo);
                DesignUI.sucesso("Pessoa excluída com sucesso.");
            } catch ( RuntimeException e) {
                DesignUI.erro(e.getMessage());
            }
        } else {
            DesignUI.info("Operação cancelada.");
        }
    }

    public void alterarEndereco() {
        DesignUI.subtitulo("Alterar Endereço");
        int cod    = Utilitario.Teclado.lerIntPositivo("Cód. Pessoa:");
        String cep = Utilitario.Teclado.lerCep("CEP (apenas números):");

        try {
            String log  = Utilitario.Teclado.lerTextoMin("Novo Logradouro:", 3);
            String num  = Utilitario.Teclado.lerTexto("Novo Número:");
            String comp = Utilitario.Teclado.lerTexto("Novo Complemento:");
            String tipo = Utilitario.Teclado.lerOpcao("Novo Tipo:", new String[]{"COMERCIAL", "RESIDENCIAL", "ENTREGA", "CORRESPONDENCIA"});

            eRepo.alterar(cod, cep, log, num, comp, tipo);
            DesignUI.sucesso("Endereço alterado com sucesso.");
        } catch ( RuntimeException e) {
            DesignUI.erro(e.getMessage());
        }
    }

    public void excluirEndereco() {
        DesignUI.subtitulo("Excluir Endereço");
        int cod    = Utilitario.Teclado.lerIntPositivo("Cód. Pessoa:");
        String cep = Utilitario.Teclado.lerCep("CEP (apenas números):");

        try {
            eRepo.excluir(cod, cep);
            DesignUI.sucesso("Endereço excluído com sucesso.");
        } catch ( RuntimeException e) {
            DesignUI.erro(e.getMessage());
        }
    }

    public void listarEnderecos() {
        DesignUI.subtitulo("Listagem de Endereços");
        try {
            List<String[]> enderecos = eRepo.listarTodos();
            if (enderecos.isEmpty()) {
                DesignUI.vazio("Nenhum endereço cadastrado.");
                return;
            }
            for (String[] d : enderecos) {
                DesignUI.abrirCaixa("Endereço");
                DesignUI.linhaCaixa("Pessoa     ", "cód. " + d[0]);
                DesignUI.linhaCaixa("CEP        ", d[1]);
                DesignUI.linhaCaixa("Logradouro ", d[2] + ", " + d[3]);
                DesignUI.linhaCaixa("Complemento", d[4]);
                DesignUI.linhaCaixa("Tipo       ", d[5]);
                DesignUI.fecharCaixa();
                DesignUI.espaco();
            }
        } catch (RuntimeException e) {
            DesignUI.erro(e.getMessage());
        }
    }
}