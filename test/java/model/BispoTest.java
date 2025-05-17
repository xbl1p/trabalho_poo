package model;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Testes unitários da peça Bispo.
 *
 * · O bispo branco parte de D4 (linha 3, coluna 3) em todos os testes,
 *   salvo indicação em contrário.  
 * · O tabuleiro é limpo antes de cada caso para isolar o cenário.  
 * · A notação algébrica é indicada nos comentários.
 */
public class BispoTest {

    private Tabuleiro tabuleiro;
    private Bispo bispoBranco;
    private Bispo bispoPreto;

    /*------------------------------------------------------------
     * FIXTURE
     *----------------------------------------------------------*/
    @Before
    public void setUp() {
        tabuleiro = new Tabuleiro();
        tabuleiro.limpar();                         // tabuleiro vazio

        bispoBranco = new Bispo(Cor.BRANCO, 3, 3);  // D4
        tabuleiro.moverPeca(bispoBranco, 3, 3);

        bispoPreto  = new Bispo(Cor.PRETO , 6, 6);  // G7
        tabuleiro.moverPeca(bispoPreto , 6, 6);
    }

    /*============================================================
     *  A. MOVIMENTOS DIAGONAIS LIVRES
     *==========================================================*/

    @Test public void moveNoroeste_A7()   { assertTrue(bispoBranco.podeMoverPara(tabuleiro, 6, 0)); }
    @Test public void moveSudeste_G1()    { assertTrue(bispoBranco.podeMoverPara(tabuleiro, 0, 6)); }
    @Test public void moveSudoeste_A1()   { assertTrue(bispoBranco.podeMoverPara(tabuleiro, 0, 0)); }
    @Test public void moveUmaCasaNE_E5()  { assertTrue(bispoBranco.podeMoverPara(tabuleiro, 4, 4)); }
    @Test public void moveNordeste_F6()   { assertTrue(bispoBranco.podeMoverPara(tabuleiro, 5, 5)); }

    /*============================================================
     *  B. MOVIMENTOS INVÁLIDOS
     *==========================================================*/
    
    @Test public void naomoveNordeste_H8()   { assertFalse(bispoBranco.podeMoverPara(tabuleiro, 7, 7)); } // existe um bispo preto no caminho !
    @Test public void naoMoveHorizontal() { assertFalse(bispoBranco.podeMoverPara(tabuleiro, 3, 5)); }
    @Test public void naoMoveVertical()   { assertFalse(bispoBranco.podeMoverPara(tabuleiro, 6, 3)); }
    @Test public void naoMoveEstiloCavalo(){ assertFalse(bispoBranco.podeMoverPara(tabuleiro, 5, 4)); }
    @Test public void naoPermiteMesmaCasa(){ assertFalse(bispoBranco.podeMoverPara(tabuleiro, 3, 3)); }

    /*============================================================
     *  C. BLOQUEIO POR PEÇAS NO CAMINHO
     *==========================================================*/

    @Test public void bloqueioAliadoNoCaminho_NE() {         // peça em E5
        tabuleiro.moverPeca(new Peao(Cor.BRANCO, 4, 4), 4, 4);
        assertFalse(bispoBranco.podeMoverPara(tabuleiro, 7, 7));
    }

    @Test public void bloqueioInimigoNoCaminho_SW() {        // peça em B2
        tabuleiro.moverPeca(new Peao(Cor.PRETO, 1, 1), 1, 1);
        assertFalse(bispoBranco.podeMoverPara(tabuleiro, 0, 0));
    }

    /*============================================================
     *  D. CAPTURA DE PEÇAS ADVERSÁRIAS
     *==========================================================*/

    @Test public void capturaInimigo_NE() {                  // alvo em F6
        tabuleiro.moverPeca(new Torre(Cor.PRETO, 5, 5), 5, 5);
        assertTrue(bispoBranco.podeMoverPara(tabuleiro, 5, 5));
    }

    @Test public void naoCapturaAliado_NE() {                // aliado em F6
        tabuleiro.moverPeca(new Torre(Cor.BRANCO, 5, 5), 5, 5);
        assertFalse(bispoBranco.podeMoverPara(tabuleiro, 5, 5));
    }

    /*============================================================
     *  E. PODE MOVER ATÉ ANTES DO BLOQUEIO
     *==========================================================*/

    @Test public void moveAteAntesDeBloqueio_NE() {          // inimigo em G7
        tabuleiro.moverPeca(new Peao(Cor.PRETO, 6, 6), 6, 6);
        assertTrue(bispoBranco.podeMoverPara(tabuleiro, 5, 5)); // F6
    }

    /*============================================================
     *  F. LIMITES DO TABULEIRO
     *==========================================================*/

    @Test public void foraDoTabuleiro() {
        assertFalse(bispoBranco.podeMoverPara(tabuleiro, -1, -1));
        assertFalse(bispoBranco.podeMoverPara(tabuleiro, 8 ,  8));
        assertFalse(bispoBranco.podeMoverPara(tabuleiro,  4, -1));
        assertFalse(bispoBranco.podeMoverPara(tabuleiro, -1,  4));
    }

    /*============================================================
     *  G. ESTADO APÓS CAPTURA
     *==========================================================*/

    @Test public void capturaAtualizaEstado() {              // D4 captura em B6
        Peao alvo = new Peao(Cor.PRETO, 5, 1);               // B6
        tabuleiro.moverPeca(alvo, 5, 1);

        assertTrue(bispoBranco.podeMoverPara(tabuleiro, 5, 1));
        tabuleiro.moverPeca(bispoBranco, 5, 1);

        assertEquals(bispoBranco, tabuleiro.getPeca(5, 1));
        assertNull(tabuleiro.getPeca(3, 3));
    }

    /*============================================================
     *  H. TESTES BÁSICOS DO BISPO PRETO
     *==========================================================*/

    @Test public void bispoPretoMovimentaECaptura() {        // G7 → B2 captura em F6
        tabuleiro.moverPeca(new Torre(Cor.BRANCO, 5, 5), 5, 5); // F6
        assertTrue(bispoPreto.podeMoverPara(tabuleiro, 5, 5));   // capturar
        assertFalse(bispoPreto.podeMoverPara(tabuleiro, 6, 2));  // movimento reto (inválido)
    }
}
