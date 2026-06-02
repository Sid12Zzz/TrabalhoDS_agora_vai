package Modelo;

public class Endereco {

    private String cep;
    private String logradouro;
    private String numero;
    private String complemento;
    private TipoEndereco tipo; // COMERCIAL, RESIDENCIAL, ENTREGA, CORRESPONDENCIA

    public Endereco() {}

    public Endereco(String cep, String logradouro, String numero, String complemento, String tipo) {
        this.cep = formatar(cep);
        this.logradouro = formatar(logradouro);
        this.numero = formatar(numero);
        this.complemento = formatar(complemento);
        this.tipo = TipoEndereco.fromString(tipo);
    }

    public Endereco(String cep, String logradouro, String numero, String complemento, TipoEndereco tipo) {
        this.cep = formatar(cep);
        this.logradouro = formatar(logradouro);
        this.numero = formatar(numero);
        this.complemento = formatar(complemento);
        this.tipo = tipo;
    }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = formatar(cep); }

    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String l) { this.logradouro = formatar(l); }

    public String getNumero() { return numero; }
    public void setNumero(String n) { this.numero = formatar(n); }

    public String getComplemento() { return complemento; }
    public void setComplemento(String c) { this.complemento = formatar(c); }

    public TipoEndereco getTipo() { return tipo; }
    public void setTipo(String t) { this.tipo = TipoEndereco.fromString(t); }
    public void setTipo(TipoEndereco t) { this.tipo = t; }

    public String resumo() {
        return String.format("%s, %s (%s) - %s", logradouro, numero, complemento, cep);
    }

    private String formatar(String s) {
        return (s == null) ? "" : s.trim();
    }

    @Override
    public String toString() {
        return String.format("%s;%s;%s;%s;%s", cep, logradouro, numero, complemento,
                tipo != null ? tipo.name() : "");
    }
}
