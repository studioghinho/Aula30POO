package br.com.starlog;

import br.com.starlog.dao.ProdutoDAO;
import br.com.starlog.dao.ProdutoDAOSqlite;
import br.com.starlog.model.Produto;

import java.util.List;
import java.util.Optional;

/**
 * Simulação de negócio: ciclo completo CRUD do Almoxarifado StarLog.
 */
public class Main {

    public static void main(String[] args) {
        ProdutoDAO dao = new ProdutoDAOSqlite();

        // Limpa execuções anteriores para o resultado ser reproduzível
        for (Produto p : dao.listarTodos()) {
            dao.excluir(p.getId());
        }

        // ---------- [Create] ----------
        System.out.println("=== [CREATE] Cadastrando produtos ===");
        Produto teclado = new Produto("Teclado Mecânico RGB", 349.90, 10);
        Produto mousepad = new Produto("Mousepad Speed XL", 79.90, 25);
        dao.salvar(teclado);
        dao.salvar(mousepad);
        System.out.println("Teclado salvo com ID: " + teclado.getId());
        System.out.println("Mousepad salvo com ID: " + mousepad.getId());

        // ---------- [Read] ----------
        System.out.println("\n=== [READ] Listagem completa ===");
        imprimirTabela(dao.listarTodos());

        // ---------- [Update] ----------
        System.out.println("\n=== [UPDATE] Atualizando o Teclado ===");
        teclado.setEstoque(15);
        teclado.setPreco(289.90);
        boolean atualizou = dao.atualizar(teclado);
        System.out.println("atualizar() retornou: " + atualizou);
        Optional<Produto> atualizado = dao.buscarPorId(teclado.getId());
        atualizado.ifPresent(p -> System.out.println("Registro atualizado: " + p));

        // ---------- [Delete] ----------
        System.out.println("\n=== [DELETE] Removendo o Mousepad ===");
        boolean removeu = dao.excluir(mousepad.getId());
        System.out.println("excluir(" + mousepad.getId() + ") retornou: " + removeu);

        System.out.println("\nTentando excluir um ID inexistente (888)...");
        boolean removeuInexistente = dao.excluir(888L);
        if (removeuInexistente) {
            System.out.println("Produto 888 removido.");
        } else {
            System.out.println("Nenhum produto encontrado com ID 888. Nada foi removido (retorno false).");
        }

        // ---------- [Final State] ----------
        System.out.println("\n=== [FINAL STATE] Estado final do banco ===");
        List<Produto> restantes = dao.listarTodos();
        imprimirTabela(restantes);
        System.out.println("Total de produtos restantes: " + restantes.size());
    }

    private static void imprimirTabela(List<Produto> produtos) {
        System.out.printf("%-5s | %-25s | %-12s | %-7s%n", "ID", "NOME", "PREÇO", "ESTOQUE");
        System.out.println("------+---------------------------+--------------+--------");
        for (Produto p : produtos) {
            System.out.printf("%-5d | %-25s | R$ %-9.2f | %-7d%n",
                    p.getId(), p.getNome(), p.getPreco(), p.getEstoque());
        }
    }
}
