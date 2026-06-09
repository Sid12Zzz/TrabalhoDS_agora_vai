package Aplicacao;

import Modelo.Endereco;
import Modelo.Pessoa;
import repository.RepositorioEndereco;
import repository.RepositorioPessoa;
import Utilitario.DesignUI;
import java.util.List;
import Utilitario.Teclado;

public class ControladorPessoa {

    private final RepositorioPessoa pRepo   = RepositorioPessoa.getInstancia();
    private final RepositorioEndereco eRepo = RepositorioEndereco.getInstancia();

    public void cadastrar() {
        DesignUI.subtitulo("Novo Cadastro de Pessoa");
        int codigo = Teclado.lerIntPositivo("Código Identificador:");

        if (pRepo.codigoExiste(codigo)) {
            DesignUI.erro("O código " + codigo + " já está em uso.");
            return;
        }

        String nome = Teclado.lerNome("Nome Completo:");
        String tipo = Teclado.lerOpcao("Vínculo (CLIENTE / FORNECEDOR / AMBOS):",
                new String[]{"CLIENTE", "FORNECEDOR", "AMBOS"});

        Pessoa pessoa = new Pessoa(codigo, nome, tipo);

        if (!pessoa.isValido()) {
            DesignUI.erro(pessoa.getMensagemErro());
            return;
        }

        String continuar = "S";
        while (continuar.equals("S")) {
            String cep = Teclado.lerCep("CEP (apenas números):");
            if (eRepo.cepJaExiste(codigo, cep)) {
                DesignUI.erro("Este CEP já está registrado para esta pessoa.");
            } else {
                String logradouro  = Teclado.lerTextoMin("Logradouro:", 3);
                String numero      = Teclado.lerTexto("Número:");
                String complemento = Teclado.lerTexto("Complemento (deixe vazio se não houver):");
                String tipoEnd     = Teclado.lerOpcao(
                        "Finalidade (COMERCIAL / RESIDENCIAL / ENTREGA / CORRESPONDENCIA):",
                        new String[]{"COMERCIAL", "RESIDENCIAL", "ENTREGA", "CORRESPONDENCIA"});

                Endereco end = new Endereco(cep, logradouro, numero, complemento, tipoEnd);
                pessoa.adicionarEndereco(end);
                try {
                    eRepo.salvar(codigo, end);
                } catch (RuntimeException e) {
                    DesignUI.erro(e.getMessage());
                }
            }
            continuar = Teclado.lerOpcao("Adicionar outro endereço? (S/N):", new String[]{"S", "N"});
        }

        try {
            pRepo.salvar(pessoa);
            DesignUI.sucesso("Pessoa salva com sucesso!");
        } catch (RuntimeException e) {
            DesignUI.erro(e.getMessage());
        }
    }

    public void listar() {
        DesignUI.subtitulo("Listagem de Pessoas");
        DesignUI.prompt("Filtro de nome (vazio para todos):");
        String filtro = Teclado.lerLinha();

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
                DesignUI.linhaCaixa("Tipo  ", p.getTipoPessoa().name());
                DesignUI.fecharCaixa();
                DesignUI.espaco();
            }
        } catch (RuntimeException e) {
            DesignUI.erro(e.getMessage());
        }
    }

    public void alterar() {
        DesignUI.subtitulo("Atualização de Cadastro");
        int codigo = Teclado.lerIntPositivo("Código da pessoa:");

        if (!pRepo.codigoExiste(codigo)) {
            DesignUI.erro("Pessoa com código " + codigo + " não encontrada.");
            return;
        }

        String nome = Teclado.lerNome("Novo nome:");
        String tipo = Teclado.lerOpcao("Novo vínculo (CLIENTE / FORNECEDOR / AMBOS):",
                new String[]{"CLIENTE", "FORNECEDOR", "AMBOS"});

        Pessoa pessoa = new Pessoa(codigo, nome, tipo);

        if (!pessoa.isValido()) {
            DesignUI.erro(pessoa.getMensagemErro());
            return;
        }

        try {
            pRepo.alterar(codigo, nome, tipo);
            DesignUI.sucesso("Pessoa alterada com sucesso.");
        } catch (RuntimeException e) {
            DesignUI.erro(e.getMessage());
        }
    }

    public void excluir() {
        DesignUI.subtitulo("Exclusão de Registro");
        int codigo = Teclado.lerIntPositivo("Código da pessoa:");

        if (!pRepo.codigoExiste(codigo)) {
            DesignUI.erro("Pessoa com código " + codigo + " não encontrada.");
            return;
        }
        if (pRepo.pessoaTemPedido(codigo)) {
            DesignUI.erro("Pessoa possui pedidos vinculados. Exclusão bloqueada.");
            return;
        }
        if (pRepo.pessoaEhFornecedorDeProduto(codigo)) {
            DesignUI.erro("Pessoa é fornecedor de produto(s). Exclusão bloqueada.");
            return;
        }
        if (Teclado.lerOpcao("Confirmar exclusão definitiva? (S/N):", new String[]{"S", "N"}).equals("S")) {
            try {
                pRepo.excluir(codigo);
                eRepo.excluirPorPessoa(codigo);
                DesignUI.sucesso("Pessoa excluída com sucesso.");
            } catch (RuntimeException e) {
                DesignUI.erro(e.getMessage());
            }
        } else {
            DesignUI.info("Operação cancelada.");
        }
    }

    public void cadastrarEndereco() {
        DesignUI.subtitulo("Cadastrar Endereço");
        int cod = Teclado.lerIntPositivo("Cód. Pessoa:");

        if (!pRepo.codigoExiste(cod)) {
            DesignUI.erro("Pessoa com código " + cod + " não encontrada.");
            return;
        }

        String cep = Teclado.lerCep("CEP (apenas números):");
        if (eRepo.cepJaExiste(cod, cep)) {
            DesignUI.erro("Este CEP já está cadastrado para esta pessoa.");
            return;
        }

        String logradouro  = Teclado.lerTextoMin("Logradouro:", 3);
        String numero      = Teclado.lerTexto("Número:");
        String complemento = Teclado.lerTexto("Complemento (deixe vazio se não houver):");
        String tipo        = Teclado.lerOpcao(
                "Finalidade (COMERCIAL / RESIDENCIAL / ENTREGA / CORRESPONDENCIA):",
                new String[]{"COMERCIAL", "RESIDENCIAL", "ENTREGA", "CORRESPONDENCIA"});

        try {
            eRepo.salvar(cod, new Endereco(cep, logradouro, numero, complemento, tipo));
            DesignUI.sucesso("Endereço cadastrado com sucesso!");
        } catch (RuntimeException e) {
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
                String nomePessoa = "";
                try {
                    Pessoa p = pRepo.buscarPorCodigo(Integer.parseInt(d[0].trim()));
                    if (p != null) nomePessoa = " — " + p.getNome();
                } catch (Exception ignored) {}

                DesignUI.abrirCaixa("Endereço");
                DesignUI.linhaCaixa("Pessoa     ", "cód. " + d[0] + nomePessoa);
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

    public void alterarEndereco() {
        DesignUI.subtitulo("Alterar Endereço");
        int cod = Teclado.lerIntPositivo("Cód. Pessoa:");

        if (!pRepo.codigoExiste(cod)) {
            DesignUI.erro("Pessoa com código " + cod + " não encontrada.");
            return;
        }

        String cep = null;
        while (true) {
            String entrada = Teclado.lerCep("CEP do endereço a alterar (apenas números ou 0 para cancelar):");
            if (entrada.equals("0")) {
                DesignUI.info("Operação cancelada.");
                return;
            }
            if (eRepo.cepJaExiste(cod, entrada)) {
                cep = entrada;
                break;
            }
            DesignUI.erro("Esta pessoa não possui endereço com o CEP " + entrada + ". Tente novamente ou digite 0 para cancelar.");
        }

        try {
            String log  = Teclado.lerTextoMin("Novo Logradouro:", 3);
            String num  = Teclado.lerTexto("Novo Número:");
            String comp = Teclado.lerTexto("Novo Complemento:");
            String tipo = Teclado.lerOpcao(
                    "Novo Tipo (COMERCIAL / RESIDENCIAL / ENTREGA / CORRESPONDENCIA):",
                    new String[]{"COMERCIAL", "RESIDENCIAL", "ENTREGA", "CORRESPONDENCIA"});

            eRepo.alterar(cod, cep, log, num, comp, tipo);
            DesignUI.sucesso("Endereço alterado com sucesso.");
        } catch (RuntimeException e) {
            DesignUI.erro(e.getMessage());
        }
    }

    public void excluirEndereco() {
        DesignUI.subtitulo("Excluir Endereço");
        int cod = Teclado.lerIntPositivo("Cód. Pessoa:");

        if (!pRepo.codigoExiste(cod)) {
            DesignUI.erro("Pessoa com código " + cod + " não encontrada.");
            return;
        }

        String cep = Teclado.lerCep("CEP (apenas números):");

        if (!eRepo.cepJaExiste(cod, cep)) {
            DesignUI.erro("Esta pessoa não possui endereço com o CEP " + cep + ".");
            return;
        }

        if (Teclado.lerOpcao("Confirmar exclusão? (S/N):", new String[]{"S", "N"}).equals("S")) {
            try {
                eRepo.excluir(cod, cep);
                DesignUI.sucesso("Endereço excluído com sucesso.");
            } catch (RuntimeException e) {
                DesignUI.erro(e.getMessage());
            }
        } else {
            DesignUI.info("Operação cancelada.");
        }
    }
}