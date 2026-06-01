package repository;

import java.util.List;

public interface Repositorio<T> {
    void salvar(T obj);
    List<T> listar();
    void excluir(int codigo);
}