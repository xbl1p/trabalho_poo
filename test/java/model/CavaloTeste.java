package model;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class CavaloTest {
    private Tabuleiro tabuleiro;
    private Cavalo cavaloBranco;

    // Configuração inicial antes de cada teste
    @Before
    public void setUp() {
        tabuleiro = new Tabuleiro();
        // Posiciona um cavalo branco em D4 (linha 3, coluna 3)
        cavaloBranco = new Cavalo(Cor.BRANCO, 3, 3);
        tabuleiro.moverPeca(cavaloBranco, 3, 3); // Remove peças padrão e coloca o cavalo
    }

    // =============================================
    //   TESTES PARA MOVIMENTOS VÁLIDOS EM "L"
    // =============================================

    @Test
    public void testMovimentoValido_CimaDireita() {
        // Movimento: 2 casas para cima, 1 para a direita (C6 -> E5)
        assertTrue("Cavalo deve mover 2↑1→", cavaloBranco.podeMoverPara(tabuleiro, 5, 4));
    }

    @Test
    public void testMovimentoValido_CimaEsquerda() {
        // Movimento: 2 casas para cima, 1 para a esquerda (C6 -> A5)
        assertTrue("Cavalo deve mover 2↑1←", cavaloBranco.podeMoverPara(tabuleiro, 5, 2));
    }

    @Test
    public void testMovimentoValido_DireitaCima() {
        // Movimento: 1 casa para cima, 2 para a direita (D4 -> F5)
        assertTrue("Cavalo deve mover 1↑2→", cavaloBranco.podeMoverPara(tabuleiro, 4, 5));
    }

    @Test
    public void testMovimentoValido_DireitaBaixo() {
        // Movimento: 1 casa para baixo, 2 para a direita (D4 -> F3)
        assertTrue("Cavalo deve mover 1↓2→", cavaloBranco.podeMoverPara(tabuleiro, 2, 5));
    }

    @Test
    public void testMovimentoValido_EsquerdaCima() {
        // Movimento: 1 casa para cima, 2 para a esquerda (D4 -> B5)
        assertTrue("Cavalo deve mover 1↑2←", cavaloBranco.podeMoverPara(tabuleiro, 4, 1));
    }

    @Test
    public void testMovimentoValido_EsquerdaBaixo() {
        // Movimento: 1 casa para baixo, 2 para a esquerda (D4 -> B3)
        assertTrue("Cavalo deve mover 1↓2←", cavaloBranco.podeMoverPara(tabuleiro, 2, 1));
    }

    @Test
    public void testMovimentoValido_BaixoDireita() {
        // Movimento: 2 casas para baixo, 1 para a direita (D4 -> E2)
        assertTrue("Cavalo deve mover 2↓1→", cavaloBranco.podeMoverPara(tabuleiro, 1, 4));
    }

    @Test
    public void testMovimentoValido_BaixoEsquerda() {
        // Movimento: 2 casas para baixo, 1 para a esquerda (D4 -> C2)
        assertTrue("Cavalo deve mover 2↓1←", cavaloBranco.podeMoverPara(tabuleiro, 1, 2));
    }

    // =============================================
    //   TESTES PARA CAPTURA DE PEÇAS ADVERSÁRIAS
    // =============================================

    @Test
    public void testCapturaValida_PeaoPreto() {
        // Posiciona um peão preto em uma posição válida (E5)
        Peao peaoPreto = new Peao(Cor.PRETO, 4, 4);
        tabuleiro.moverPeca(peaoPreto, 4, 4);
        assertTrue("Cavalo deve capturar peão adversário", cavaloBranco.podeMoverPara(tabuleiro, 4, 4));
    }

    @Test
    public void testCapturaValida_TorrePreta() {
        // Posiciona uma torre preta em uma posição válida (B5)
        Torre torrePreta = new Torre(Cor.PRETO, 4, 1);
        tabuleiro.moverPeca(torrePreta, 4, 1);
        assertTrue("Cavalo deve capturar torre adversária", cavaloBranco.podeMoverPara(tabuleiro, 4, 1));
    }

    // =============================================
    //   TESTES PARA MOVIMENTOS INVÁLIDOS
    // =============================================

    @Test
    public void testMovimentoInvalido_Vertical3Casas() {
        // Movimento: 3 casas para cima (não é em "L")
        assertFalse("Movimento vertical inválido", cavaloBranco.podeMoverPara(tabuleiro, 6, 3));
    }

    @Test
    public void testMovimentoInvalido_Horizontal2Casas() {
        // Movimento: 2 casas para a direita (não é em "L")
        assertFalse("Movimento horizontal inválido", cavaloBranco.podeMoverPara(tabuleiro, 3, 5));
    }

    @Test
    public void testMovimentoInvalido_Diagonal() {
        / Movimento: 2 casas na diagonal (não é em "L")
        assertFalse("Movimento diagonal inválido", cavaloBranco.podeMoverPara(tabuleiro, 5, 5));
    }

    // =============================================
    //   TESTES PARA BLOQUEIO POR PEÇAS ALIADAS
    // =============================================

    @Test
    public void testMovimentoBloqueado_PeaoBranco() {
        // Posiciona um peão branco em E5 (posição válida)
        Peao peaoBranco = new Peao(Cor.BRANCO, 4, 4);
        tabuleiro.moverPeca(peaoBranco, 4, 4);
        assertFalse("Cavalo não pode mover para casa ocupada por aliado", cavaloBranco.podeMoverPara(tabuleiro, 4, 4));
    }

    @Test
    public void testMovimentoBloqueado_BispoBranco() {
        // Posiciona um bispo branco em F5 (posição válida)
        Bispo bispoBranco = new Bispo(Cor.BRANCO, 4, 5);
        tabuleiro.moverPeca(bispoBranco, 4, 5);
        assertFalse("Cavalo não pode mover para casa ocupada por aliado", cavaloBranco.podeMoverPara(tabuleiro, 4, 5));
    }

    // =============================================
    //   TESTES PARA MOVIMENTOS FORA DO TABULEIRO
    // =============================================

    @Test
    public void testMovimentoInvalido_LinhaNegativa() {
        // Tenta mover para linha -1 (fora do tabuleiro)
        assertFalse("Movimento para linha negativa inválido", cavaloBranco.podeMoverPara(tabuleiro, -1, 2));
    }

    @Test
    public void testMovimentoInvalido_ColunaMaiorQue7() {
        // Tenta mover para coluna 8 (fora do tabuleiro)
        assertFalse("Movimento para coluna 8 inválido", cavaloBranco.podeMoverPara(tabuleiro, 4, 8));
    }

    // =============================================
    //   TESTES PARA SALTO SOBRE PEÇAS
    // =============================================

    @Test
    public void testMovimentoValido_SaltoSobrePeaoBranco() {
        // Posiciona um peão branco no caminho (D5)
        Peao peaoBranco = new Peao(Cor.BRANCO, 4, 3);
        tabuleiro.moverPeca(peaoBranco, 4, 3);
        assertTrue("Cavalo deve pular sobre peão aliado", cavaloBranco.podeMoverPara(tabuleiro, 5, 4));
    }

    @Test
    public void testMovimentoValido_SaltoSobreTorrePreta() {
        // Posiciona uma torre preta no caminho (D5)
        Torre torrePreta = new Torre(Cor.PRETO, 4, 3);
        tabuleiro.moverPeca(torrePreta, 4, 3);
        assertTrue("Cavalo deve pular sobre peça adversária", cavaloBranco.podeMoverPara(tabuleiro, 5, 4));
    }
}
