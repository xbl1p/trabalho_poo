package model;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Classe de testes unitários para verificar o comportamento correto da peça Cavalo.
 * Todos os testes assumem que o cavalo branco está na posição D4 (linha 3, coluna 3).
 * Notação padrão usada: (linha, coluna) corresponde à notação algébrica de A1-H8.
 */
public class CavaloTest {
    private Tabuleiro tabuleiro;
    private Cavalo cavaloBranco;

    /**
     * Configuração inicial antes de cada teste.
     * Instancia um tabuleiro limpo e posiciona um cavalo branco em D4 (3,3).
     */
    @Before
    public void setUp() {
        tabuleiro = new Tabuleiro();

        // LIMPA o tabuleiro
        tabuleiro.limpar();  

        cavaloBranco = new Cavalo(Cor.BRANCO, 3, 3); // D4
        tabuleiro.moverPeca(cavaloBranco, 3, 3);
    }

    // ==========================================================================
    // TESTES DE MOVIMENTOS VÁLIDOS (formato em L) - devem retornar true
    // ==========================================================================

    @Test
    public void testMovimentoValido_CimaDireita() {
        // D4 → E6 (5,4): 2 para cima, 1 à direita
        assertTrue("Cavalo deve poder mover-se para E6 (2↑ 1→)", cavaloBranco.podeMoverPara(tabuleiro, 5, 4));
    }

    @Test
    public void testMovimentoValido_CimaEsquerda() {
        // D4 → C6 (5,2): 2 para cima, 1 à esquerda
        assertTrue("Cavalo deve poder mover-se para C6 (2↑ 1←)", cavaloBranco.podeMoverPara(tabuleiro, 5, 2));
    }

    @Test
    public void testMovimentoValido_DireitaCima() {
        // D4 → F5 (4,5): 1 para cima, 2 à direita
        assertTrue("Cavalo deve poder mover-se para F5 (1↑ 2→)", cavaloBranco.podeMoverPara(tabuleiro, 4, 5));
    }

    @Test
    public void testMovimentoValido_DireitaBaixo() {
        // D4 → F3 (2,5): 1 para baixo, 2 à direita
        assertTrue("Cavalo deve poder mover-se para F3 (1↓ 2→)", cavaloBranco.podeMoverPara(tabuleiro, 2, 5));
    }

    @Test
    public void testMovimentoValido_EsquerdaCima() {
        // D4 → B5 (4,1): 1 para cima, 2 à esquerda
        assertTrue("Cavalo deve poder mover-se para B5 (1↑ 2←)", cavaloBranco.podeMoverPara(tabuleiro, 4, 1));
    }

    @Test
    public void testMovimentoValido_EsquerdaBaixo() {
        // D4 → B3 (2,1): 1 para baixo, 2 à esquerda
        assertTrue("Cavalo deve poder mover-se para B3 (1↓ 2←)", cavaloBranco.podeMoverPara(tabuleiro, 2, 1));
    }

    @Test
    public void testMovimentoValido_BaixoDireita() {
        // D4 → E2 (1,4): 2 para baixo, 1 à direita
        assertTrue("Cavalo deve poder mover-se para E2 (2↓ 1→)", cavaloBranco.podeMoverPara(tabuleiro, 1, 4));
    }

    @Test
    public void testMovimentoValido_BaixoEsquerda() {
        // D4 → C2 (1,2): 2 para baixo, 1 à esquerda
        assertTrue("Cavalo deve poder mover-se para C2 (2↓ 1←)", cavaloBranco.podeMoverPara(tabuleiro, 1, 2));
    }

    // ==========================================================================
    // TESTES DE CAPTURA DE PEÇAS ADVERSÁRIAS - devem retornar true
    // ==========================================================================

    @Test
    public void testCapturaValida_PeaoPreto() {
        // Peão preto em E6 (5,4): cavalo deve capturar
        Peao peaoPreto = new Peao(Cor.PRETO, 5, 4);
        tabuleiro.moverPeca(peaoPreto, 5, 4);
        assertTrue("Cavalo deve poder capturar peão em E6", cavaloBranco.podeMoverPara(tabuleiro, 5, 4));
    }

    @Test
    public void testCapturaValida_TorrePreta() {
        // Torre preta em B5 (4,1): cavalo deve capturar
        Torre torrePreta = new Torre(Cor.PRETO, 4, 1);
        tabuleiro.moverPeca(torrePreta, 4, 1);
        assertTrue("Cavalo deve poder capturar torre em B5", cavaloBranco.podeMoverPara(tabuleiro, 4, 1));
    }

    @Test
    public void testCapturaAtualizaTabuleiro() {
        // Verifica se a captura realmente atualiza o tabuleiro
        Peao peaoPreto = new Peao(Cor.PRETO, 5, 4); // E6
        tabuleiro.moverPeca(peaoPreto, 5, 4);
        assertTrue(cavaloBranco.podeMoverPara(tabuleiro, 5, 4));
        tabuleiro.moverPeca(cavaloBranco, 5, 4);

        assertEquals("Cavalo deve estar em E6 após a captura", cavaloBranco, tabuleiro.getPeca(5, 4));
        assertNull("Casa de origem D4 deve estar vazia", tabuleiro.getPeca(3, 3));
    }

    // ==========================================================================
    // TESTES DE MOVIMENTOS INVÁLIDOS (não seguem padrão em L)
    // ==========================================================================

    @Test public void testMovimentoInvalido_Vertical3Casas() {
        // D4 → D7 (6,3): não permitido
        assertFalse("Movimento vertical direto inválido", cavaloBranco.podeMoverPara(tabuleiro, 6, 3));
    }

    @Test public void testMovimentoInvalido_Horizontal2Casas() {
        // D4 → F4 (3,5): movimento reto inválido
        assertFalse("Movimento horizontal direto inválido", cavaloBranco.podeMoverPara(tabuleiro, 3, 5));
    }

    @Test public void testMovimentoInvalido_Diagonal() {
        // D4 → F6 (5,5): movimento diagonal inválido
        assertFalse("Movimento diagonal inválido", cavaloBranco.podeMoverPara(tabuleiro, 5, 5));
    }

    @Test public void testMovimentoInvalido_MesmaCasa() {
        // D4 → D4: cavalo não pode ficar parado
        assertFalse("Cavalo não pode mover para a própria casa", cavaloBranco.podeMoverPara(tabuleiro, 3, 3));
    }

    // ==========================================================================
    // TESTES DE BLOQUEIO POR PEÇAS ALIADAS - devem retornar false
    // ==========================================================================

    @Test
    public void testMovimentoBloqueado_PeaoBranco() {
        // Peão branco em E6 (4,4): cavalo não pode capturar aliado
        Peao peaoBranco = new Peao(Cor.BRANCO, 4, 4);
        tabuleiro.moverPeca(peaoBranco, 4, 4);
        assertFalse("Cavalo não pode capturar peão aliado em E5", cavaloBranco.podeMoverPara(tabuleiro, 4, 4));
    }

    @Test
    public void testMovimentoBloqueado_BispoBranco() {
        // Bispo branco em F5 (4,5)
        Bispo bispoBranco = new Bispo(Cor.BRANCO, 4, 5);
        tabuleiro.moverPeca(bispoBranco, 4, 5);
        assertFalse("Cavalo não pode capturar bispo aliado em F5", cavaloBranco.podeMoverPara(tabuleiro, 4, 5));
    }

    @Test
    public void testMovimentoBloqueado_CapturaAliado() {
        // Tentativa explícita de captura de aliado em E6
        Torre aliada = new Torre(Cor.BRANCO, 5, 4);
        tabuleiro.moverPeca(aliada, 5, 4);
        assertFalse("Cavalo não pode capturar torre aliada em E6", cavaloBranco.podeMoverPara(tabuleiro, 5, 4));
    }

    // ==========================================================================
    // TESTES DE MOVIMENTOS FORA DOS LIMITES DO TABULEIRO - devem retornar false
    // ==========================================================================

    @Test public void testMovimentoInvalido_LinhaNegativa() {
        // Posição inválida: linha -1
        assertFalse("Movimento para linha -1 deve ser inválido", cavaloBranco.podeMoverPara(tabuleiro, -1, 2));
    }

    @Test public void testMovimentoInvalido_LinhaMaiorQue7() {
        // Linha inválida: 8 (além da 8ª linha)
        assertFalse("Movimento para linha 8 deve ser inválido", cavaloBranco.podeMoverPara(tabuleiro, 8, 2));
    }

    @Test public void testMovimentoInvalido_ColunaNegativa() {
        // Coluna inválida: -1
        assertFalse("Movimento para coluna -1 deve ser inválido", cavaloBranco.podeMoverPara(tabuleiro, 4, -1));
    }

    @Test public void testMovimentoInvalido_ColunaMaiorQue7() {
        // Coluna inválida: 8
        assertFalse("Movimento para coluna 8 deve ser inválido", cavaloBranco.podeMoverPara(tabuleiro, 4, 8));
    }

    // ==========================================================================
    // TESTES DE SALTO SOBRE PEÇAS - cavalo pode pular qualquer peça no caminho
    // ==========================================================================

    @Test
    public void testMovimentoValido_SaltoSobrePeaoBranco() {
        // D4 → E6 (5,4) pulando peão em D5 (4,3)
        Peao peaoBranco = new Peao(Cor.BRANCO, 4, 3);
        tabuleiro.moverPeca(peaoBranco, 4, 3);
        assertTrue("Cavalo deve pular sobre peão aliado em D5", cavaloBranco.podeMoverPara(tabuleiro, 5, 4));
    }

    @Test
    public void testMovimentoValido_SaltoSobreTorrePreta() {
        // D4 → E6 (5,4) pulando torre em D5
        Torre torrePreta = new Torre(Cor.PRETO, 4, 3);
        tabuleiro.moverPeca(torrePreta, 4, 3);
        assertTrue("Cavalo deve pular sobre torre inimiga em D5", cavaloBranco.podeMoverPara(tabuleiro, 5, 4));
    }
}
