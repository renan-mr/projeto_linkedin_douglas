package br.com.unipe; // mesmo pacote das demais classes do grafo

import java.util.List; // usado para guardar a sequência ordenada de nomes do caminho

/**
 * Estrutura de retorno do menor caminho ponderado (Dijkstra).
 * Unifica em um único objeto a rota encontrada e o custo acumulado dela,
 * evitando precisar de dois métodos separados para a Missão 4.
 *
 * @param nomesDosVertices sequência ordenada de nomes da origem até o destino
 *                         (lista vazia quando o destino é inalcançável).
 * @param custoTotal       soma dos pesos das arestas do caminho
 *                         (-1 quando o destino é inalcançável).
 */
public record Caminho(List<String> nomesDosVertices, int custoTotal) { // record = classe imutável só de dados

    /**
     * Fábrica de conveniência para o caso "sem rota possível":
     * caminho vazio e custo -1, exatamente como pede o enunciado da Missão 4.
     */
    public static Caminho inalcancavel() { // método estático nomeado para deixar a intenção clara
        return new Caminho(List.of(), -1); // List.of() devolve uma lista imutável vazia
    }

    /** Indica se existe rota válida (custo diferente do sentinela -1). */
    public boolean existeRota() { // facilita a leitura no código que consome o resultado
        return custoTotal != -1; // -1 é o valor combinado para "inalcançável"
    }
}
