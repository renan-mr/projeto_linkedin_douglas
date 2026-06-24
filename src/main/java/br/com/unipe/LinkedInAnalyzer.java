package br.com.unipe; // mesmo pacote das classes de grafo

import java.util.ArrayList; // listas dinâmicas usadas nos resultados e na BFS
import java.util.Comparator; // ordenação das sugestões por amigos em comum
import java.util.HashSet; // conjunto para marcar visitados (busca rápida, sem repetição)
import java.util.LinkedList; // usada como fila (FIFO) na busca em largura
import java.util.List; // tipo de retorno das missões 2 e 5
import java.util.Queue; // interface da fila da BFS
import java.util.Set; // conjuntos de vizinhos e de visitados

/**
 * Motor de análises e recomendações da rede social profissional.
 * Recebe um {@link Grafo} não-direcionado e ponderado (perfis = vértices,
 * conexões = arestas, afinidade = pesos) e responde às 5 missões do projeto.
 */
public class LinkedInAnalyzer {

    private final Grafo redeSocial; // instância do grafo que representa toda a rede de contatos

    // ----------------------------------------------------------------------------------
    // MISSÃO 1 - Construtor da Análise
    // ----------------------------------------------------------------------------------

    /**
     * Guarda a instância do grafo para que as demais missões possam consultá-la.
     *
     * @param redeSocial o grafo já montado com perfis e conexões.
     */
    public LinkedInAnalyzer(Grafo redeSocial) {
        this.redeSocial = redeSocial; // armazena a referência da rede para uso nas análises
    }

    // ----------------------------------------------------------------------------------
    // MISSÃO 2 - Sugestão de Conexões (amigos de 2º grau)
    // ----------------------------------------------------------------------------------

    /**
     * Sugere "amigos de amigos" que o usuário ainda não tem como contato direto,
     * ordenados do maior para o menor número de amigos em comum.
     *
     * @param nomeUsuario nome da pessoa para quem queremos gerar sugestões.
     * @return lista de {@link Sugestao} (nome + amigos em comum), já ordenada.
     */
    public List<Sugestao> sugerirConexoes(String nomeUsuario) {
        // Localiza o perfil do usuário; erro claro caso o nome não exista na rede.
        Vertice usuario = redeSocial.encontraVertice(nomeUsuario).orElseThrow(
                () -> new IllegalArgumentException("Vertice " + nomeUsuario + " não encontrado."));

        // Contatos diretos (1º grau) do usuário: não podem virar sugestão.
        Set<Vertice> amigosDiretos = new HashSet<>(usuario.getAdjacencias());

        // Acumula, para cada candidato (amigo de amigo), quantos amigos em comum tem.
        List<Sugestao> sugestoes = new ArrayList<>();

        // Varre os amigos de 2º grau: para cada amigo direto, olha os amigos DELE.
        for (Vertice amigoDireto : usuario.getAdjacencias()) {
            for (Vertice candidato : amigoDireto.getAdjacencias()) {
                boolean eOProprioUsuario = candidato.equals(usuario); // regra 2: não sugerir a si mesmo
                boolean jaEAmigoDireto = amigosDiretos.contains(candidato); // regra 1: não sugerir contato de 1º grau
                boolean jaFoiContabilizado = contemNome(sugestoes, candidato.getNome()); // evita duplicar candidato

                if (eOProprioUsuario || jaEAmigoDireto || jaFoiContabilizado) {
                    continue; // candidato inválido ou já tratado: pula
                }

                // Conta os amigos em comum entre o usuário e este candidato (regra 3 = critério de ordenação).
                int amigosEmComum = contaAmigosEmComum(amigosDiretos, candidato);
                sugestoes.add(new Sugestao(candidato.getNome(), amigosEmComum));
            }
        }

        // Ordena do maior para o menor número de amigos em comum (decrescente).
        sugestoes.sort(Comparator.comparingInt(Sugestao::amigosEmComum).reversed());
        return sugestoes; // estrutura final com nomes sugeridos e amigos em comum
    }

    /**
     * Conta quantos contatos diretos do usuário também são amigos do candidato.
     */
    private int contaAmigosEmComum(Set<Vertice> amigosDoUsuario, Vertice candidato) {
        int total = 0; // contador de amizades compartilhadas
        for (Vertice amigoDoCandidato : candidato.getAdjacencias()) { // olha cada amigo do candidato
            if (amigosDoUsuario.contains(amigoDoCandidato)) { // esse amigo também é amigo do usuário?
                total++; // sim: é um amigo em comum
            }
        }
        return total; // quantidade de amigos em comum
    }

    /**
     * Verifica se um candidato (por nome) já está presente na lista de sugestões.
     */
    private boolean contemNome(List<Sugestao> sugestoes, String nome) {
        return sugestoes.stream().anyMatch(sugestao -> sugestao.nome().equals(nome)); // true se já houver o nome
    }
}