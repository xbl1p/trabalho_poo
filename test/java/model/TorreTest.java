package model;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Testes unitários da peça Torre.
 *
 * · A torre branca é colocada em D4 (linha 3, coluna 3) antes de cada caso.
 * · O tabuleiro vem preenchido pelas peças padrão do construtor de {@link Tabuleiro}.
 *   –  linhas 1 (peões pretos) e 6 (peões brancos) já contêm peças, portanto
 *     alguns movimentos verticais longos ficarão bloqueados “por natureza”.
 * · Cada teste documenta o **motivo** e a **posição alvo** em notação algébrica.
 */
public class TorreTest {

    private Tabuleiro tabuleiro;
    private Torre torreBranca;
    private Torre torrePreta;

    /*------------------------------------------------------------
     *  FIXTURE
     *----------------------------------------------------------*/
    @Before
    public void setUp() {
        tabuleiro = new Tabuleiro();

        // LIMPA o tabuleiro padrão
        tabuleiro.limpar();  

        // Torre branca em D4 (3,3)
        torreBranca = new Torre(Cor.BRANCO, 3, 3);
        tabuleiro.moverPeca(torreBranca, 3, 3);

        // Torre preta em G7 (6,6)
        torrePreta = new Torre(Cor.PRETO, 6, 6);
        tabuleiro.moverPeca(torrePreta, 6, 6);
    }


    /*============================================================
     *  MOVIMENTOS ORTOGONAIS LIVRES  (path sem bloqueios)
     *==========================================================*/

    @Test public void moveHorizontalDireita_H4() {  // D4 → H4
        assertTrue(torreBranca.podeMoverPara(tabuleiro, 3, 7));
    }

    @Test public void moveHorizontalEsquerda_A4() { // D4 → A4
        assertTrue(torreBranca.podeMoverPara(tabuleiro, 3, 0));
    }

    @Test public void moveVerticalCima_D6() {       // D4 → D6  (linha 5)
        // caminho passa apenas por D5 (4,3) – livre
        assertTrue(torreBranca.podeMoverPara(tabuleiro, 5, 3));
    }

    @Test public void moveVerticalBaixo_D3() {      // D4 → D3 (linha 2)
        assertTrue(torreBranca.podeMoverPara(tabuleiro, 2, 3));
    }

    /*============================================================
     *  MOVIMENTOS INVÁLIDOS  (diagonal, em “L”, aleatório, mesma casa)
     *==========================================================*/

    @Test public void naoMoveDiagonal_F6() {        // D4 → F6
        assertFalse(torreBranca.podeMoverPara(tabuleiro, 5, 5));
    }

    @Test public void naoMoveEstiloCavalo_E6() {    // D4 → E6
        assertFalse(torreBranca.podeMoverPara(tabuleiro, 5, 4));
    }

    @Test public void naoMoveAleatorio_F5() {       // D4 → F5
        assertFalse(torreBranca.podeMoverPara(tabuleiro, 4, 5));
    }

    @Test public void naoPermiteMesmaCasa() {       // D4 → D4
        assertFalse(torreBranca.podeMoverPara(tabuleiro, 3, 3));
    }

    /*============================================================
     *  BLOQUEIO - PEÇAS ENTRE TORRE E DESTINO
     *==========================================================*/

    @Test public void bloqueioAliadoVertical_D6() { // peão branco em D5
        tabuleiro.moverPeca(new Peao(Cor.BRANCO, 4, 3), 4, 3);
        assertFalse(torreBranca.podeMoverPara(tabuleiro, 5, 3));
    }

    @Test public void bloqueioAliadoHorizontal_F4() { // peão branco em E4
        tabuleiro.moverPeca(new Peao(Cor.BRANCO, 3, 4), 3, 4);
        assertFalse(torreBranca.podeMoverPara(tabuleiro, 3, 5));
    }

    @Test public void bloqueioInimigo_EntreTorreEDestino() { // peão preto em D5, alvo D6
        tabuleiro.moverPeca(new Peao(Cor.PRETO, 4, 3), 4, 3);
        assertFalse(torreBranca.podeMoverPara(tabuleiro, 5, 3));
    }

    /*============================================================
     *  PODE MOVER ATÉ A CASA ANTERIOR AO BLOQUEIO
     *==========================================================*/

    @Test public void moveAteAntesDePeaoPreto_D5() { // peão preto em D6
        tabuleiro.moverPeca(new Peao(Cor.PRETO, 5, 3), 5, 3);
        assertTrue(torreBranca.podeMoverPara(tabuleiro, 4, 3)); // D5
    }

    @Test public void moveAteAntesDePeaoPreto_E4() { // peão preto em F4
        tabuleiro.moverPeca(new Peao(Cor.PRETO, 3, 5), 3, 5);
        assertTrue(torreBranca.podeMoverPara(tabuleiro, 3, 4)); // E4
    }

    /*============================================================
     *  CAPTURA DE PEÇAS ADVERSÁRIAS
     *==========================================================*/

    @Test public void capturaVertical_D6() { // alvo: peão preto em D6
        tabuleiro.moverPeca(new Peao(Cor.PRETO, 5, 3), 5, 3);
        assertTrue(torreBranca.podeMoverPara(tabuleiro, 5, 3));
    }

    @Test public void capturaHorizontal_F4() { // alvo: peão preto em F4
        tabuleiro.moverPeca(new Peao(Cor.PRETO, 3, 5), 3, 5);
        assertTrue(torreBranca.podeMoverPara(tabuleiro, 3, 5));
    }

    @Test public void naoCapturaAliado_D6() { // peão branco em D6
        tabuleiro.moverPeca(new Peao(Cor.BRANCO, 5, 3), 5, 3);
        assertFalse(torreBranca.podeMoverPara(tabuleiro, 5, 3));
    }

    /*----------------------------------------------------------
     *  Captura + verificação de estado do tabuleiro
     *---------------------------------------------------------*/
    @Test public void capturaAtualizaEstado() { // D4 captura peão em D6
        Peao p = new Peao(Cor.PRETO, 5, 3);
        tabuleiro.moverPeca(p, 5, 3);

        assertTrue(torreBranca.podeMoverPara(tabuleiro, 5, 3));
        tabuleiro.moverPeca(torreBranca, 5, 3);

        assertEquals(torreBranca, tabuleiro.getPeca(5, 3)); // torre na nova casa
        assertNull(tabuleiro.getPeca(3, 3));                // origem vazia
    }

    /*============================================================
     *  LIMITES DO TABULEIRO
     *==========================================================*/

    @Test public void naoSaiDoTabuleiro() {
        assertFalse(torreBranca.podeMoverPara(tabuleiro, 8 , 3)); // linha 8
        assertFalse(torreBranca.podeMoverPara(tabuleiro,-1 , 3)); // linha -1
        assertFalse(torreBranca.podeMoverPara(tabuleiro, 3 , 8)); // col  8
        assertFalse(torreBranca.podeMoverPara(tabuleiro, 3 ,-1)); // col -1
    }

    /*============================================================
     *  TESTES BÁSICOS PARA TORRE PRETA
     *==========================================================*/

    @Test public void torrePretaMovimenta() {
        // G7 (6,6) → C7 (6,2)   — horizontal livre
        assertTrue(torrePreta.podeMoverPara(tabuleiro, 6, 2));

        // G7 → G3 (2,6)         — vertical livre (linha 3-5 estão vazias)
        assertTrue(torrePreta.podeMoverPara(tabuleiro, 2, 6));

        // Movimento ilegal: diagonal G7 → E5
        assertFalse(torrePreta.podeMoverPara(tabuleiro, 4, 4));
    }

    @Test public void torrePretaCapturaTorreBrancaEmC7() {
        // posiciona nova torre branca em C7 (6,2)
        Torre aliadaBranca = new Torre(Cor.BRANCO, 6, 2);
        tabuleiro.moverPeca(aliadaBranca, 6, 2);
        assertTrue(torrePreta.podeMoverPara(tabuleiro, 6, 2));
    }

    /*============================================================
     *  CENÁRIOS DE BLOQUEIO COMBINADO
     *==========================================================*/

    @Test public void bloqueioPorMultiplasPecas() {
        // Peões bloqueando horizontais e verticais
        tabuleiro.moverPeca(new Peao(Cor.PRETO , 3, 5), 3, 5); // F4
        tabuleiro.moverPeca(new Peao(Cor.BRANCO, 5, 3), 5, 3); // D6

        assertFalse("Não passa pelo peão em F4", torreBranca.podeMoverPara(tabuleiro, 3, 6));
        assertFalse("Não passa pelo peão em D6", torreBranca.podeMoverPara(tabuleiro, 6, 3));
    }

    @Test public void caminhoComplexo_QuatroDirecoes() {
        // Peças distribuídas nas quatro direções
        tabuleiro.moverPeca(new Peao  (Cor.BRANCO, 0, 3), 0, 3); // D1
        tabuleiro.moverPeca(new Peao  (Cor.PRETO , 3, 0), 3, 0); // A4
        tabuleiro.moverPeca(new Bispo (Cor.BRANCO, 3, 7), 3, 7); // H4
        tabuleiro.moverPeca(new Cavalo(Cor.PRETO , 7, 3), 7, 3); // D8

        // norte: bloqueada por aliado em D1
        assertFalse(torreBranca.podeMoverPara(tabuleiro, 0, 3));

        // oeste: até A4 (3,0) inclusive (captura inimigo)
        assertTrue(torreBranca.podeMoverPara(tabuleiro, 3, 0));

        // leste: bloqueada por aliado em H4
        assertFalse(torreBranca.podeMoverPara(tabuleiro, 3, 7));

        // sul: até D8 (7,3) inclusive (captura inimigo)
        assertTrue(torreBranca.podeMoverPara(tabuleiro, 7, 3));
    }
}
