package model;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Testes unitários da peça Peão (regras básicas — sem en passant nem promoção).
 *
 * · Peão branco parte de D2 (linha 6, col 3).  
 * · Peão preto  parte de E7 (linha 1, col 4).  
 * · Tabuleiro é limpo antes de cada caso.
 */
public class PeaoTest {

    private Tabuleiro tabuleiro;
    private Peao peaoBranco;
    private Peao peaoPreto;

    /*------------------------------------------------------------
     * FIXTURE
     *----------------------------------------------------------*/
    @Before
    public void setUp() {
        tabuleiro = new Tabuleiro();
        tabuleiro.limpar();                      // torna o tabuleiro vazio

        peaoBranco = new Peao(Cor.BRANCO, 6, 3); // D2
        tabuleiro.moverPeca(peaoBranco, 6, 3);

        peaoPreto  = new Peao(Cor.PRETO , 1, 4); // E7
        tabuleiro.moverPeca(peaoPreto , 1, 4);
    }

    /*============================================================
     *  A. AVANÇO SIMPLES
     *==========================================================*/

    @Test public void brancoAvancaUmaCasa() {         // D2 → D3
        assertTrue(peaoBranco.podeMoverPara(tabuleiro, 5, 3));
    }

    @Test public void pretoAvancaUmaCasa() {          // E7 → E6
        assertTrue(peaoPreto.podeMoverPara(tabuleiro, 2, 4));
    }

    @Test public void naoAvancaCasaOcupada() {        // D3 ocupado
        tabuleiro.moverPeca(new Peao(Cor.PRETO, 5, 3), 5, 3);
        assertFalse(peaoBranco.podeMoverPara(tabuleiro, 5, 3));
    }

    /*============================================================
     *  B. AVANÇO DUPLO NA PRIMEIRA MOVIDA
     *==========================================================*/

    @Test public void avancarDuasCasasPrimeiraJogadaBranco() { // D2 → D4
        assertTrue(peaoBranco.podeMoverPara(tabuleiro, 4, 3));
    }

    @Test public void avancarDuasCasasPrimeiraJogadaPreto() {  // E7 → E5
        assertTrue(peaoPreto.podeMoverPara(tabuleiro, 3, 4));
    }

    @Test public void avancarDuasComIntermediariaOcupada() {   // D2 → D4 com D3 bloqueado
        tabuleiro.moverPeca(new Peao(Cor.PRETO, 5, 3), 5, 3);  // bloqueia D3
        assertFalse(peaoBranco.podeMoverPara(tabuleiro, 4, 3));
    }

    // [NOVO] destino ocupado no avanço duplo
    @Test public void avancarDuasComDestinoOcupado() {         // D2 → D4 mas D4 ocupada
        tabuleiro.moverPeca(new Peao(Cor.PRETO, 4, 3), 4, 3);  // bloqueia D4
        assertFalse(peaoBranco.podeMoverPara(tabuleiro, 4, 3));
    }

    @Test public void avancarDuasForaDaLinhaInicial() {        // branco já em D3 tenta D5
        tabuleiro.moverPeca(peaoBranco, 5, 3);                 // move para D3
        assertFalse(peaoBranco.podeMoverPara(tabuleiro, 3, 3));
    }

    // [NOVO] tenta avanço-duplo após já ter andado uma casa
    @Test public void avancarDuasAposMovimentoParcial() {      // D3 → D5 (ilegal)
        tabuleiro.moverPeca(peaoBranco, 5, 3);                 // D2 → D3
        assertFalse(peaoBranco.podeMoverPara(tabuleiro, 3, 3));
    }

    /*============================================================
     *  C. CAPTURAS DIAGONAIS
     *==========================================================*/

    @Test public void capturaDiagonalDireitaBranco() { // D2 captura peça preta em E3
        tabuleiro.moverPeca(new Torre(Cor.PRETO, 5, 4), 5, 4);
        assertTrue(peaoBranco.podeMoverPara(tabuleiro, 5, 4));
    }

    @Test public void capturaDiagonalEsquerdaPreto() { // E7 captura peça branca em D6
        tabuleiro.moverPeca(new Bispo(Cor.BRANCO, 2, 3), 2, 3);
        assertTrue(peaoPreto.podeMoverPara(tabuleiro, 2, 3));
    }

    @Test public void diagonalVaziaNaoPermiteMovimento() { // D2 → C3 vazio
        assertFalse(peaoBranco.podeMoverPara(tabuleiro, 5, 2));
    }

    @Test public void naoCapturaAliadoDiagonal() { // peça branca em E3
        tabuleiro.moverPeca(new Peao(Cor.BRANCO, 5, 4), 5, 4);
        assertFalse(peaoBranco.podeMoverPara(tabuleiro, 5, 4));
    }

    // [NOVO] captura no canto do tabuleiro
    @Test public void capturaNoCantoBranco() { // A2 captura B3
        Peao peaoBorda = new Peao(Cor.BRANCO, 6, 0); // A2
        tabuleiro.moverPeca(peaoBorda, 6, 0);
        tabuleiro.moverPeca(new Torre(Cor.PRETO, 5, 1), 5, 1); // B3
        assertTrue(peaoBorda.podeMoverPara(tabuleiro, 5, 1));
    }

    /*============================================================
     *  D. DIREÇÃO INVERSA E MOVIMENTOS ILEGAIS
     *==========================================================*/

    @Test public void brancoNaoMoveParaBaixo() { // D2 → D1
        assertFalse(peaoBranco.podeMoverPara(tabuleiro, 7, 3));
    }

    @Test public void pretoNaoMoveParaCima() {   // E7 → E8
        assertFalse(peaoPreto.podeMoverPara(tabuleiro, 0, 4));
    }

    @Test public void movimentoLateralInvalido() { // D2 → C2
        assertFalse(peaoBranco.podeMoverPara(tabuleiro, 6, 2));
    }

    @Test public void movimentoVerticalTresCasas() { // D2 → D5
        assertFalse(peaoBranco.podeMoverPara(tabuleiro, 3, 3));
    }

    /*============================================================
     *  E. BLOQUEIOS IMEDIATOS
     *==========================================================*/

    @Test public void peaoBloqueadoPorAliadoNaFrente() { // peao branco em D3
        tabuleiro.moverPeca(new Peao(Cor.BRANCO, 5, 3), 5, 3);
        assertFalse(peaoBranco.podeMoverPara(tabuleiro, 5, 3));
    }

    /*============================================================
     *  F. LIMITES DO TABULEIRO
     *==========================================================*/

    @Test public void foraDoTabuleiro() {
        assertFalse(peaoBranco.podeMoverPara(tabuleiro, -1, 3)); // linha -1
        assertFalse(peaoBranco.podeMoverPara(tabuleiro,  8, 3)); // linha 8
        assertFalse(peaoBranco.podeMoverPara(tabuleiro,  5,-1)); // col -1
        assertFalse(peaoBranco.podeMoverPara(tabuleiro,  5, 8)); // col  8
    }

    /*============================================================
     *  G. ESTADO APÓS MOVIMENTO / CAPTURA
     *==========================================================*/

    @Test public void capturaAtualizaEstado() { // branco captura em E3
        Torre alvo = new Torre(Cor.PRETO, 5, 4);
        tabuleiro.moverPeca(alvo, 5, 4);

        assertTrue(peaoBranco.podeMoverPara(tabuleiro, 5, 4));
        tabuleiro.moverPeca(peaoBranco, 5, 4);

        assertEquals(peaoBranco, tabuleiro.getPeca(5, 4));
        assertNull(tabuleiro.getPeca(6, 3));
    }

    @Test public void avançoAtualizaCoordenadas() { // D2 → D3
        assertTrue(peaoBranco.podeMoverPara(tabuleiro, 5, 3));
        tabuleiro.moverPeca(peaoBranco, 5, 3);

        assertEquals(5, peaoBranco.getLinha());
        assertEquals(3, peaoBranco.getColuna());
    }
}
