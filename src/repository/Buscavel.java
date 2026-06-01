package repository;

public interface Buscavel<T> {
    T buscarPorCodigo(int codigo);
    boolean codigoExiste(int codigo);
}