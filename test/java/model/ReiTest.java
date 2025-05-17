package model;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Testes unitários da peça Rei.
 *
 * · Rei branco parte de D4 (linha 3, coluna 3)  
 * · Rei preto parte de E8 (linha 7, coluna 4)  
 * · Tabuleiro é limpo antes de cada teste
 */
public class ReiTest {

    private Tabuleiro tabuleiro;
    private Rei reiBranco;
    private Rei reiPreto;

    @Before
    public void setUp() {
        tabuleiro = new Tabuleiro();
        tabuleiro.limpar();

        reiBranco = new Rei(Cor.BRANCO, 3, 3); // D4
        tabuleiro.moverPeca(reiBranco, 3, 3);

        reiPreto = new Rei(Cor.PRETO, 7, 4);   // E8
        tabuleiro.moverPeca(reiPreto, 7, 4);
    }

    /*============================================================
     *  A. MOVIMENTOS VÁLIDOS (1 casa em qualquer direção)
     *==========================================================*/

    @Test public void moveUmaCasaDireita() { assertTrue(reiBranco.podeMoverPara(tabuleiro, 3, 4)); }   // E4
    @Test public void moveUmaCasaEsquerda() { assertTrue(reiBranco.podeMoverPara(tabuleiro, 3, 2)); }  // C4
    @Test public void moveUmaCasaCima() { assertTrue(reiBranco.podeMoverPara(tabuleiro, 4, 3)); }      // D5
    @Test public void moveUmaCasaBaixo() { assertTrue(reiBranco.podeMoverPara(tabuleiro, 2, 3)); }     // D3
    @Test public void moveDiagonalNE() { assertTrue(reiBranco.podeMoverPara(tabuleiro, 4, 4)); }       // E5
    @Test public void moveDiagonalNO() { assertTrue(reiBranco.podeMoverPara(tabuleiro, 4, 2)); }       // C5
    @Test public void moveDiagonalSE() { assertTrue(reiBranco.podeMoverPara(tabuleiro, 2, 4)); }       // E3
    @Test public void moveDiagonalSO() { assertTrue(reiBranco.podeMoverPara(tabuleiro, 2, 2)); }       // C3

    /*============================================================
     *  B. MOVIMENTOS INVÁLIDOS
     *==========================================================*/

    @Test public void naoMoveDuasCasasHorizontal() { assertFalse(reiBranco.podeMoverPara(tabuleiro, 3, 5)); }  // F4
    @Test public void naoMoveDuasCasasVertical() { assertFalse(reiBranco.podeMoverPara(tabuleiro, 5, 3)); }    // D6
    @Test public void naoMoveEmL() { assertFalse(reiBranco.podeMoverPara(tabuleiro, 5, 4)); }                 // E6 (estilo cavalo)
    @Test public void naoPermiteMesmaCasa() { assertFalse(reiBranco.podeMoverPara(tabuleiro, 3, 3)); }

    /*============================================================
     *  C. CAPTURAS
     *==========================================================*/

    @Test public void capturaPeaoPretoAdjacente() {
        tabuleiro.moverPeca(new Peao(Cor.PRETO, 4, 4), 4, 4); // E5
        assertTrue(reiBranco.podeMoverPara(tabuleiro, 4, 4));
    }

    @Test public void naoCapturaAliado() {
        tabuleiro.moverPeca(new Torre(Cor.BRANCO, 3, 4), 3, 4); // E4
        assertFalse(reiBranco.podeMoverPara(tabuleiro, 3, 4));
    }

    /*============================================================
     *  D. LIMITES DO TABULEIRO
     *==========================================================*/

    @Test public void naoSaiDoTabuleiro() {
        // Rei preto em E8 (7,4)
        
        // Casos INVÁLIDOS (fora do tabuleiro)
        assertFalse(reiPreto.podeMoverPara(tabuleiro, 8, 4));  // linha 8 (inválida)
        assertFalse(reiPreto.podeMoverPara(tabuleiro, 7, 8));  // coluna 8 (inválida)

        // Caso VÁLIDO (dentro do tabuleiro)
        assertTrue(reiPreto.podeMoverPara(tabuleiro, 7, 5));   // F8 (válido)
    }
    
    @Test 
    public void naoSaiDoTabuleiroH8() {
        // 1. PREPARAÇÃO DO CENÁRIO DE CANTO SUPERIOR DIREITO (H8)
        // ------------------------------------------------------------
        // Posiciona o rei preto no canto H8 (coordenadas 7,7)
        // O tabuleiro em xadrez usa índices de 0 a 7 para linhas e colunas:
        // - Linha 7 = Última linha (8ª linha na notação tradicional)
        // - Coluna 7 = Última coluna (coluna H)
        tabuleiro.moverPeca(reiPreto, 7, 7); 

        // 2. TESTES DE MOVIMENTOS INVÁLIDOS (FORA DOS LIMITES)
        // ------------------------------------------------------------
        // Verifica três cenários de violação de limites físicos do tabuleiro:

        // a. Movimento para LINHA 8 (inválida)
        // - O rei está na linha 7 (última linha válida)
        // - Tentativa de mover para linha 8 (inexistente)
        // - Objetivo: Garantir que a lógica bloqueie movimentos para linhas > 7
        assertFalse(reiPreto.podeMoverPara(tabuleiro, 8, 7)); 

        // b. Movimento para COLUNA 8 (inválida)
        // - O rei está na coluna 7 (última coluna válida)
        // - Tentativa de mover para coluna 8 (inexistente)
        // - Objetivo: Garantir que a lógica bloqueie movimentos para colunas > 7
        assertFalse(reiPreto.podeMoverPara(tabuleiro, 7, 8)); 

        // c. Movimento para LINHA 8 E COLUNA 8 (inválidas)
        // - Cenário extremo: ambas coordenadas fora do tabuleiro
        // - Objetivo: Testar combinação de limites inválidos
        assertFalse(reiPreto.podeMoverPara(tabuleiro, 8, 8)); 
    }
    
    
    @Test public void movimentoValidoNoCanto() {
        tabuleiro.moverPeca(reiBranco, 0, 0); // A1
        assertTrue(reiBranco.podeMoverPara(tabuleiro, 0, 1)); // B1
        assertTrue(reiBranco.podeMoverPara(tabuleiro, 1, 0)); // A2
    }
    

	@Test 
	public void movimentoValidoNoCantoInferiorEsquerdo() {
	    // Posiciona rei branco em A1 (0,0)
	    tabuleiro.moverPeca(reiBranco, 0, 0);
	    
	    // Movimentos válidos
	    assertTrue(reiBranco.podeMoverPara(tabuleiro, 0, 1));  // B1 (direita)
	    assertTrue(reiBranco.podeMoverPara(tabuleiro, 1, 0));  // A2 (baixo)
	    assertTrue(reiBranco.podeMoverPara(tabuleiro, 1, 1));  // B2 (diagonal SE)
	    
	    // Movimento inválido (fora do tabuleiro)
	    assertFalse(reiBranco.podeMoverPara(tabuleiro, -1, 0)); 
	}
	
	@Test 
	public void movimentoValidoNoCantoSuperiorDireito() {
	    // Posiciona rei preto em H8 (7,7)
	    tabuleiro.moverPeca(reiPreto, 7, 7);
	    
	    // Movimentos válidos
	    assertTrue(reiPreto.podeMoverPara(tabuleiro, 7, 6));  // G8 (esquerda)
	    assertTrue(reiPreto.podeMoverPara(tabuleiro, 6, 7));  // H7 (baixo)
	    assertTrue(reiPreto.podeMoverPara(tabuleiro, 6, 6));  // G7 (diagonal SO)
	    
	    // Movimento inválido (fora do tabuleiro)
	    assertFalse(reiPreto.podeMoverPara(tabuleiro, 8, 7)); 
	}
	    

    /*============================================================
     *  E. ESTADO APÓS CAPTURA
     *==========================================================*/

    @Test public void capturaAtualizaTabuleiro() {
        Peao alvo = new Peao(Cor.PRETO, 4, 4); // E5
        tabuleiro.moverPeca(alvo, 4, 4);

        assertTrue(reiBranco.podeMoverPara(tabuleiro, 4, 4));
        tabuleiro.moverPeca(reiBranco, 4, 4);

        assertEquals(reiBranco, tabuleiro.getPeca(4, 4));
        assertNull(tabuleiro.getPeca(3, 3));
    }

    /*============================================================
     *  F. TESTES PARA REI PRETO
     *==========================================================*/

    @Test public void reiPretoMovimentaECaptura() {
        tabuleiro.moverPeca(new Bispo(Cor.BRANCO, 6, 5), 6, 5); // F7
        assertTrue(reiPreto.podeMoverPara(tabuleiro, 6, 5));     // captura
        assertFalse(reiPreto.podeMoverPara(tabuleiro, 7, 6));    // movimento inválido (2 casas na diagonal)
    }
}