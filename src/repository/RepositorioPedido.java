package repository;

import Modelo.ItemPedido;
import Modelo.Pedido;
import Modelo.Pessoa;
import Modelo.Produto;
import util.GerenciadorLog;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RepositorioPedido implements Repositorio<Pedido> {

    private static final String ARQUIVO = "./data/pedidos.txt";
    private static final String TEMP    = "./data/temp_pedidos.txt";
    private RepositorioProduto produtoRepo;

    public void setProdutoRepository(RepositorioProduto produtoRepo) {
        this.produtoRepo = produtoRepo;
    }

    public boolean numeroExiste(int numero) {
        try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] d = linha.split(";");
                if (Integer.parseInt(d[0]) == numero) return true;
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
                if (d.length < 4) continue;
                String[] itens = d[3].split("\\|");
                for (String item : itens) {
                    String[] partes = item.split(":");
                    if (partes.length > 0 && partes[0].trim().equals(String.valueOf(codigoProduto)))
                        return true;
                }
            }
        } catch (IOException ignored) {}
        return false;
    }

    @Override
    public void salvar(Pedido pedido) {
        // CORREÇÃO AQUI: Usando getNumeroPedido() do código original do seu colega
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
                        .append(":").append(item.getSubtotal());
                if (i < pedido.getItens().size() - 1) strItens.append("|");
            }

            // CORREÇÃO AQUI: getNumeroPedido()
            pw.println(pedido.getNumeroPedido() + ";" +
                    pedido.getCliente().getNome() + ";" +
                    pedido.getEnderecoEntrega() + ";" +
                    strItens.toString() + ";" +
                    pedido.getTotal());
            GerenciadorLog.registrar("Pedido cadastrado: " + pedido.getNumeroPedido());
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

                int numero = Integer.parseInt(d[0]);
                String nomeCliente = d[1];
                String endereco = d[2];

                Pessoa cliente = new Pessoa(0, nomeCliente, "CLIENTE");
                Pedido pedido = new Pedido(numero, cliente, endereco);

                if (d.length >= 4 && !d[3].trim().isEmpty()) {
                    String[] itensStr = d[3].split("\\|");
                    for (String itemStr : itensStr) {
                        String[] p = itemStr.split(":");
                        if (p.length >= 3) {
                            int codProduto = Integer.parseInt(p[0].trim());
                            int qtd = Integer.parseInt(p[1].trim());

                            Produto produto = null;
                            if (produtoRepo != null) {
                                produto = produtoRepo.buscarPorCodigo(codProduto);
                            }
                            if (produto == null) {
                                produto = new Produto(codProduto, "Produto Excluído", 0, 0, 0);
                            }
                            pedido.adicionarItem(new ItemPedido(produto, qtd));
                        }
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
        List<Pedido> todos = listar();
        List<Pedido> filtrados = new ArrayList<>();
        for (Pedido p : todos) {
            if (p.getCliente().getNome().toLowerCase().contains(filtro.toLowerCase())) {
                filtrados.add(p);
            }
        }
        return filtrados;
    }

    public Pedido buscarPorNumero(int numero) {
        List<Pedido> todos = listar();
        for (Pedido p : todos) {
            // CORREÇÃO AQUI: getNumeroPedido()
            if (p.getNumeroPedido() == numero) {
                return p;
            }
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
                if (Integer.parseInt(d[0]) == numeroBusca) {
                    d[2] = novoEndereco;
                    pw.println(String.join(";", d));
                } else {
                    pw.println(linha);
                }
            }
        } catch (IOException e) { throw new RuntimeException("Erro ao alterar pedido: " + e.getMessage()); }
        orig.delete(); temp.renameTo(orig);
        GerenciadorLog.registrar("Pedido alterado: " + numeroBusca);
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
                if (Integer.parseInt(d[0]) != numeroBusca) pw.println(linha);
            }
        } catch (IOException e) { throw new RuntimeException("Erro ao excluir pedido: " + e.getMessage()); }
        orig.delete(); temp.renameTo(orig);
        GerenciadorLog.registrar("Pedido excluído: " + numeroBusca);
    }
}