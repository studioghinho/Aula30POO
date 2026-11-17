package br.com.starlog.model;

/**
 * Entidade de domínio Produto do Almoxarifado StarLog.
 * Garante as invariantes de negócio no construtor e nos setters.
 */
public class Produto {

    private Long id;
    private String nome;
    private double preco;
    private int estoque;

    // Construtor de persistência (com id) - usado ao hidratar registros do banco
    public Produto(Long id, String nome, double preco, int estoque) {
        validarNome(nome);
        validarPreco(preco);
        validarEstoque(estoque);
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.estoque = estoque;
    }

    // Construtor de negócio (sem id) - o id é gerado pelo banco (AUTOINCREMENT)
    public Produto(String nome, double preco, int estoque) {
        this(null, nome, preco, estoque);
    }

    private static void validarNome(String nome) {
        if (nome == null || nome.trim().length() < 3) {
            throw new IllegalArgumentException("O nome deve ter pelo menos 3 caracteres.");
        }
    }

    private static void validarPreco(double preco) {
        if (preco <= 0.0) {
            throw new IllegalArgumentException("O preço deve ser maior que 0.0.");
        }
    }

    private static void validarEstoque(int estoque) {
        if (estoque < 0) {
            throw new IllegalArgumentException("O estoque não pode ser negativo.");
        }
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public double getPreco() { return preco; }
    public int getEstoque() { return estoque; }

    public void setId(Long id) { this.id = id; }

    public void setPreco(double preco) {
        validarPreco(preco);
        this.preco = preco;
    }

    public void setEstoque(int estoque) {
        validarEstoque(estoque);
        this.estoque = estoque;
    }

    @Override
    public String toString() {
        return String.format("Produto{id=%d, nome='%s', preco=R$ %.2f, estoque=%d}",
                id, nome, preco, estoque);
    }
}
