package Modelo;

public enum TipoEndereco {
    COMERCIAL,
    RESIDENCIAL,
    ENTREGA,
    CORRESPONDENCIA;

    public static TipoEndereco fromString(String valor) {
        return TipoEndereco.valueOf(valor.trim().toUpperCase());
    }
}
