package repository;

import Modelo.ItemPedido;
import Modelo.Pedido;
import Modelo.Pessoa;
import Modelo.Produto;
import Utilitario.GerenciadorLog;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Formato da linha no arquivo pedidos.txt:
 * numeroPedido;codCliente;nomeCliente;cepEntrega;cod:qtd:subtotal|...|...;total
 */
public class RepositorioPedido implements Repositorio<Pedido> {

    private static final String ARQUIVO = "./data/pedidos.txt";
    private static final String TEMP    = "./data/temp_pedidos.txt";

    // Singleton
    private static RepositorioPedido instancia;

    private RepositorioProduto produtoRepo;

    private RepositorioPedido() {}

    public static RepositorioPedido getInstancia() {
        if (instancia == null) {
            instancia = new RepositorioPedido();
        }
        return instancia;
    }

    public void setProdutoRepository(RepositorioProduto produtoRepo) {
        this.produtoRepo = produtoRepo;
    }

    public boolean numeroExiste(int numero) {
        try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] d = linha.split(";");
                if (d.length < 1) continue;
                try {
                    if (Integer.parseInt(d[0].trim()) == numero) return true;
                } catch (NumberFormatException ignored) {}
            }
        } catch (IOException ignored) {}
        return false;
    }

    public boolean produtoEstaEmPedido(int codigoProduto) {
        try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] d = linha.split(";");
                if (d.length < 5) continue;
                String[] itens = d[4].split("\\|");
                for (String item : itens) {
                    String[] partes = item.split(":");
                    if (partes.length > 0) {
                        try {
                            if (Integer.parseInt(partes[0].trim()) == codigoProduto) return true;
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }
        } catch (IOException ignored) {}
        return false;
    }

    @Override
    public void salvar(Pedido pedido) {
        if (numeroExiste(pedido.getNumeroPedido())) {
            throw new IllegalArgumentException("Já existe um pedido com o número " + pedido.getNumeroPedido());
        }
        if (pedido.getItens().isEmpty()) {
            throw new IllegalArgumentException("O pedido deve ter pelo menos um item.");
        }
        try (FileWriter fw = new FileWriter(ARQUIVO, true); PrintWriter pw = new PrintWriter(fw)) {
            StringBuilder strItens = new StringBuilder();
            for (int i = 0; i < pedido.getItens().size(); i++) {
                ItemPedido item = pedido.getItens().get(i);
                strItens.append(item.getProduto().getCodigo())
                        .append(":").append(item.getQuantidade())
                        .append(":").append(String.format(Locale.US, "%.2f", item.getSubtotal()));
                if (i < pedido.getItens().size() - 1) strItens.append("|");
            }
            pw.println(pedido.getNumeroPedido() + ";" +
                    pedido.getCliente().getCodigo() + ";" +
                    pedido.getCliente().getNome() + ";" +
                    pedido.getEnderecoEntrega() + ";" +
                    strItens + ";" +
                    String.format(Locale.US, "%.2f", pedido.getTotal()));
            GerenciadorLog.registrar("Pedido cadastrado: num=" + pedido.getNumeroPedido()
                    + " cliente=cod" + pedido.getCliente().getCodigo());
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar pedido: " + e.getMessage());
        }
    }

    @Override
    public List<Pedido> listar() {
        List<Pedido> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] d = linha.split(";");
                if (d.length < 5) continue;

                int numero;
                int codCliente;
                try {
                    numero     = Integer.parseInt(d[0].trim());
                    codCliente = Integer.parseInt(d[1].trim());
                } catch (NumberFormatException e) { continue; }

                String nomeCliente = d[2].trim();
                String endereco    = d[3].trim();

                Pessoa cliente = new Pessoa(codCliente, nomeCliente, Modelo.TipoPessoa.CLIENTE);
                Pedido pedido  = new Pedido(numero, cliente, endereco);

                if (d.length >= 5 && !d[4].trim().isEmpty()) {
                    String[] itensStr = d[4].split("\\|");
                    for (String itemStr : itensStr) {
                        String[] p = itemStr.split(":");
                        if (p.length < 3) continue;
                        int codProduto;
                        int qtd;
                        double subtotal;
                        try {
                            codProduto = Integer.parseInt(p[0].trim());
                            qtd        = Integer.parseInt(p[1].trim());
                            subtotal   = Double.parseDouble(p[2].trim().replace(",", "."));
                        } catch (NumberFormatException e) { continue; }

                        Produto produto = null;
                        if (produtoRepo != null) produto = produtoRepo.buscarPorCodigo(codProduto);
                        if (produto == null) {
                            double precoUnit = qtd > 0 ? subtotal / qtd : 0;
                            produto = new Produto(codProduto, "Produto #" + codProduto, 0, precoUnit, 0);
                        }
                        ItemPedido item = new ItemPedido(produto, qtd);
                        item.setSubtotal(subtotal);
                        pedido.adicionarItem(item);
                    }
                }
                lista.add(pedido);
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao listar pedidos: " + e.getMessage());
        }
        return lista;
    }

    public List<Pedido> buscarPorCliente(String filtro) {
        List<Pedido> filtrados = new ArrayList<>();
        for (Pedido p : listar()) {
            if (p.getCliente().getNome().toLowerCase().contains(filtro.toLowerCase()))
                filtrados.add(p);
        }
        return filtrados;
    }

    public Pedido buscarPorNumero(int numero) {
        for (Pedido p : listar()) {
            if (p.getNumeroPedido() == numero) return p;
        }
        return null;
    }

    public void alterarEntrega(int numeroBusca, String novoEndereco) {
        if (!numeroExiste(numeroBusca)) throw new IllegalArgumentException("Pedido não encontrado.");
        File orig = new File(ARQUIVO);
        File temp = new File(TEMP);
        try (BufferedReader br = new BufferedReader(new FileReader(orig));
             PrintWriter pw = new PrintWriter(new FileWriter(temp))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] d = linha.split(";");
                try {
                    if (Integer.parseInt(d[0].trim()) == numeroBusca) {
                        d[3] = novoEndereco;
                        pw.println(String.join(";", d));
                    } else {
                        pw.println(linha);
                    }
                } catch (NumberFormatException e) { pw.println(linha); }
            }
        } catch (IOException e) { throw new RuntimeException("Erro ao alterar pedido: " + e.getMessage()); }
        orig.delete(); temp.renameTo(orig);
        GerenciadorLog.registrar("Pedido alterado endereço: num=" + numeroBusca + " novoCep=" + novoEndereco);
    }

    @Override
    public void excluir(int numeroBusca) {
        if (!numeroExiste(numeroBusca)) throw new IllegalArgumentException("Pedido não encontrado.");
        File orig = new File(ARQUIVO);
        File temp = new File(TEMP);
        try (BufferedReader br = new BufferedReader(new FileReader(orig));
             PrintWriter pw = new PrintWriter(new FileWriter(temp))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] d = linha.split(";");
                try {
                    if (Integer.parseInt(d[0].trim()) != numeroBusca) pw.println(linha);
                } catch (NumberFormatException e) { pw.println(linha); }
            }
        } catch (IOException e) { throw new RuntimeException("Erro ao excluir pedido: " + e.getMessage()); }
        orig.delete(); temp.renameTo(orig);
        GerenciadorLog.registrar("Pedido excluído: num=" + numeroBusca);
    }
}