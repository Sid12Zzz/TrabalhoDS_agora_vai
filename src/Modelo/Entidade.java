package Modelo;

/**
 * Classe Abstrata: ENTIDADE
 * Define a estrutura base para todos os objetos persistentes do sistema.
 * Implementa Resumivel para garantir compatibilidade com a Interface Visual.
 */
public abstract class Entidade implements Resumivel {

    protected int codigo;

    public Entidade() {}

    public Entidade(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    /**
     * Método obrigatório para representação textual simplificada.
     * Deve ser implementado por todas as subclasses.
     */
    @Override
    public abstract String resumo();
}
