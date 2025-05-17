package model;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Testes unitários da peça Rainha.
 *
 *   · A rainha branca parte de D4 (linha 3, coluna 3) salvo indicação em contrário.
 *   · O tabuleiro é esvaziado em cada {@code @Before} para isolar cenários.
 *   · Comentários indicam a notação algébrica ou o propósito do teste.
 *
 * Cobertura:
 *   A. Movimentos ortogonais (torre)            – 4 testes
 *   B. Movimentos diagonais   (bispo)           – 4 testes
 *   C. Movimentos inválidos                     – 4 testes
 *   D. Bloqueios (aliado / inimigo)             – 4 testes
 *   E. Capturas                                 – 3 testes
 *   F. Movimento até antes de bloqueio          – 2 testes
 *   G. Limites do tabuleiro                     – 1 teste
 *   H. Estado do tabuleiro após captura         – 1 teste
 *   I. Sanidade com rainha preta                – 1 teste
 * -----------------------------------------------  TOTAL = 24 métodos
 */
public class RainhaTest {

    private Tabuleiro tabuleiro;
    private Rainha rainhaBranca;
    private Rainha rainhaPreta;

    /*------------------------------------------------------------
     *  FIXTURE
     *----------------------------------------------------------*/
    @Before
    public void setUp() {
        tabuleiro = new Tabuleiro();
        tabuleiro.limpar();                         // tabuleiro vazio

        rainhaBranca = new Rainha(Cor.BRANCO, 3, 3); // D4
        tabuleiro.moverPeca(rainhaBranca, 3, 3);

        rainhaPreta  = new Rainha(Cor.PRETO , 0, 0); // A8  (usada em último teste)
        tabuleiro.moverPeca(rainhaPreta , 0, 0);
    }

    /*============================================================
     *  A. MOVIMENTOS ORTOGONAIS LIVRES  (estilo TORRE)
     *==========================================================*/

    @Test public void moveHorizontalDireita_H4() { assertTrue(rainhaBranca.podeMoverPara(tabuleiro, 3, 7)); }
    @Test public void moveHorizontalEsquerda_A4() { assertTrue(rainhaBranca.podeMoverPara(tabuleiro, 3, 0)); }
    @Test public void moveVerticalCima_D8()       { assertTrue(rainhaBranca.podeMoverPara(tabuleiro, 7, 3)); }
    @Test public void moveVerticalBaixo_D1()      { assertTrue(rainhaBranca.podeMoverPara(tabuleiro, 0, 3)); }

    /*============================================================
     *  B. MOVIMENTOS DIAGONAIS LIVRES  (estilo BISPO)
     *==========================================================*/

    @Test public void moveNordeste_H8() { assertTrue(rainhaBranca.podeMoverPara(tabuleiro, 7, 7)); }
    @Test public void moveNoroeste_A7() { assertTrue(rainhaBranca.podeMoverPara(tabuleiro, 6, 0)); }
    @Test public void moveSudeste_G1()  { assertTrue(rainhaBranca.podeMoverPara(tabuleiro, 0, 6)); }
    @Test public void moveSudoeste_A1() { assertTrue(rainhaBranca.podeMoverPara(tabuleiro, 0, 0)); }

    /*============================================================
     *  C. MOVIMENTOS INVÁLIDOS
     *==========================================================*/

    @Test public void naoMoveComoCavalo_E6()  { assertFalse(rainhaBranca.podeMoverPara(tabuleiro, 5, 4)); }
    @Test public void naoMoveLateralUmaDiagonal() { assertFalse(rainhaBranca.podeMoverPara(tabuleiro, 4, 5)); }
    @Test public void naoMoveQualquerAleatorio()  { assertFalse(rainhaBranca.podeMoverPara(tabuleiro, 2, 5)); }
    @Test public void naoPermiteMesmaCasa()       { assertFalse(rainhaBranca.podeMoverPara(tabuleiro, 3, 3)); }

    /*============================================================
     *  D. BLOQUEIOS NO CAMINHO
     *==========================================================*/

    @Test public void bloqueioAliadoHorizontal() {            // aliado em E4
        tabuleiro.moverPeca(new Peao(Cor.BRANCO, 3, 4), 3, 4);
        assertFalse(rainhaBranca.podeMoverPara(tabuleiro, 3, 7));
    }

    @Test public void bloqueioInimigoHorizontal() {           // inimigo em E4
        tabuleiro.moverPeca(new Peao(Cor.PRETO, 3, 4), 3, 4);
        assertFalse(rainhaBranca.podeMoverPara(tabuleiro, 3, 7));
    }

    @Test public void bloqueioAliadoDiagonal() {              // aliado em E5
        tabuleiro.moverPeca(new Peao(Cor.BRANCO, 4, 4), 4, 4);
        assertFalse(rainhaBranca.podeMoverPara(tabuleiro, 7, 7));
    }

    @Test public void bloqueioInimigoDiagonal() {             // inimigo em F6
        tabuleiro.moverPeca(new Peao(Cor.PRETO, 5, 5), 5, 5);
        assertFalse(rainhaBranca.podeMoverPara(tabuleiro, 7, 7));
    }

    /*============================================================
     *  E. CAPTURAS VÁLIDAS
     *==========================================================*/

    @Test public void capturaHorizontal_E4() {
        tabuleiro.moverPeca(new Torre(Cor.PRETO, 3, 4), 3, 4);
        assertTrue(rainhaBranca.podeMoverPara(tabuleiro, 3, 4));
    }

    @Test public void capturaVertical_D6() {
        tabuleiro.moverPeca(new Bispo(Cor.PRETO, 5, 3), 5, 3);
        assertTrue(rainhaBranca.podeMoverPara(tabuleiro, 5, 3));
    }

    @Test public void capturaDiagonal_F6() {
        tabuleiro.moverPeca(new Peao(Cor.PRETO, 5, 5), 5, 5);
        assertTrue(rainhaBranca.podeMoverPara(tabuleiro, 5, 5));
    }

    /*============================================================
     *  F. MOVER ATÉ ANTES DO BLOQUEIO
     *==========================================================*/

    @Test public void moveAteAntesDeAliadoHorizontal() {
        tabuleiro.moverPeca(new Torre(Cor.BRANCO, 3, 6), 3, 6); // aliado em G4
        assertTrue(rainhaBranca.podeMoverPara(tabuleiro, 3, 5)); // F4
    }

    @Test public void moveAteAntesDeAliadoDiagonal() {
        tabuleiro.moverPeca(new Peao(Cor.BRANCO, 6, 6), 6, 6);  // aliado em G7
        assertTrue(rainhaBranca.podeMoverPara(tabuleiro, 5, 5)); // F6
    }

    /*============================================================
     *  G. LIMITES DO TABULEIRO
     *==========================================================*/

    @Test public void foraDoTabuleiro() {
        assertFalse(rainhaBranca.podeMoverPara(tabuleiro, -1,  3));
        assertFalse(rainhaBranca.podeMoverPara(tabuleiro,  8,  3));
        assertFalse(rainhaBranca.podeMoverPara(tabuleiro,  3, -1));
        assertFalse(rainhaBranca.podeMoverPara(tabuleiro,  3,  8));
    }

    /*============================================================
     *  H. ESTADO APÓS CAPTURA
     *==========================================================*/

    @Test public void capturaAtualizaEstado() { // D4 captura peão em D6
        Peao alvo = new Peao(Cor.PRETO, 5, 3);
        tabuleiro.moverPeca(alvo, 5, 3);

        assertTrue(rainhaBranca.podeMoverPara(tabuleiro, 5, 3));
        tabuleiro.moverPeca(rainhaBranca, 5, 3);

        assertEquals(rainhaBranca, tabuleiro.getPeca(5, 3));
        assertNull(tabuleiro.getPeca(3, 3));
    }

    /*============================================================
     *  I. TESTE BÁSICO PARA RAINHA PRETA
     *==========================================================*/

    @Test public void rainhaPretaMovimentaECaptura() { // A8 → D5 captura em C6
        tabuleiro.moverPeca(new Torre(Cor.BRANCO, 2, 2), 2, 2); // C6
        
        // Posiciona peão preto em A5 (0,3) - BLOQUEIO
        tabuleiro.moverPeca(new Peao(Cor.PRETO, 0, 3), 0, 3); 

        assertTrue(rainhaPreta.podeMoverPara(tabuleiro, 2, 2));  // captura diagonal
        assertFalse(rainhaPreta.podeMoverPara(tabuleiro, 0, 3)); //// bloqueio vertical 
    }
}
