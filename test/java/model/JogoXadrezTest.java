package model;

import static org.junit.Assert.*;
import java.lang.reflect.Field;
import org.junit.Before;
import org.junit.Test;

/**
 * Testes da façade {@link JogoXadrez}.
 *
 * · Cada método reinicia a instância Singleton via reflexão,
 *   obtém o tabuleiro interno e o limpa para isolar cenários.
 *
 * · São verificadas: seleção, movimento, captura, promoção,
 *   alternância de turno e guardas de limites.
 */
public class JogoXadrezTest {

    /* façade sob teste */
    private JogoXadrez jogo;

    /* acesso refletivo ao tabuleiro interno (para inspeção de estado) */
    private Tabuleiro tab;

    /*--------------------------------------------------------------
     * FIXTURE – reinicia singleton e deixa tabuleiro vazio
     *------------------------------------------------------------*/
    @Before
    public void setUp() throws Exception {
        /* 1. Zera o campo estático "instancia" (Singleton) */
        Field inst = JogoXadrez.class.getDeclaredField("instancia");
        inst.setAccessible(true);
        inst.set(null, null);

        /* 2. Cria nova instância fresh */
        jogo = JogoXadrez.getInstancia();

        /* 3. Acessa o tabuleiro interno por reflexão e limpa */
        Field fTab = JogoXadrez.class.getDeclaredField("tabuleiro");
        fTab.setAccessible(true);
        tab = (Tabuleiro) fTab.get(jogo);
        tab.limpar();
    }

    /*============================================================
     *  A. SELEÇÃO DE PEÇAS
     *==========================================================*/
    @Test
    public void selecionaPecaDaVez_ok() {
        tab.moverPeca(new Peao(Cor.BRANCO, 6, 0), 6, 0); // A2
        assertTrue(jogo.selecionaPeca(6, 0));
    }

    @Test
    public void selecionaPecaAdversario_falha() {
        tab.moverPeca(new Peao(Cor.PRETO, 1, 0), 1, 0); // A7
        assertFalse(jogo.selecionaPeca(1, 0));
    }

    /*============================================================
     *  B. MOVIMENTO VÁLIDO + ESTADO FINAL
     *==========================================================*/
    @Test
    public void peaoBrancoAvancaDuasCasas() {
        Peao peao = new Peao(Cor.BRANCO, 6, 1);  // B2
        tab.moverPeca(peao, 6, 1);

        assertTrue(jogo.selecionaPeca(6, 1));
        assertTrue(jogo.selecionaCasa(4, 1));    // B4

        assertEquals(peao, tab.getPeca(4, 1));
        assertNull(tab.getPeca(6, 1));
    }

    /*============================================================
     *  C. MOVIMENTO INVÁLIDO
     *==========================================================*/
    @Test
    public void movimentoInvalido_tresCasas() {
        tab.moverPeca(new Peao(Cor.BRANCO, 6, 2), 6, 2); // C2
        assertTrue (jogo.selecionaPeca(6, 2));
        assertFalse(jogo.selecionaCasa(3, 2));           // C5 – ilegal
        assertNotNull(tab.getPeca(6, 2));                // Peça não saiu do lugar
    }

    /*============================================================
     *  D. ALTERNÂNCIA DE TURNO
     *==========================================================*/
    @Test
    public void alternaJogadorAposMovimento() {
        tab.moverPeca(new Peao(Cor.BRANCO, 6, 0), 6, 0); // A2
        tab.moverPeca(new Peao(Cor.PRETO , 1, 0), 1, 0); // A7

        assertTrue(jogo.selecionaPeca(6, 0)); // branco
        assertTrue(jogo.selecionaCasa(5, 0)); // A3

        /* Agora deve ser a vez do preto */
        assertFalse(jogo.selecionaPeca(6, 1)); // outro peão branco
        assertTrue (jogo.selecionaPeca(1, 0)); // peão preto
    }

    /*============================================================
     *  E. CAPTURA POR MEIO DA FAÇADE
     *==========================================================*/
    @Test
    public void rainhaCapturaPeao() {
        Rainha rainha = new Rainha(Cor.BRANCO, 3, 3); // D4
        Peao   peao   = new Peao  (Cor.PRETO , 5, 3); // D6
        tab.moverPeca(rainha, 3, 3);
        tab.moverPeca(peao  , 5, 3);

        assertTrue(jogo.selecionaPeca(3, 3));
        assertTrue(jogo.selecionaCasa(5, 3));     // captura

        assertEquals(rainha, tab.getPeca(5, 3));
        assertNull(tab.getPeca(3, 3));
    }

    /*============================================================
     *  F. PROMOÇÃO AUTOMÁTICA
     *==========================================================*/
    @Test
    public void promocaoPeaoBrancoParaRainha() {
        Peao peao = new Peao(Cor.BRANCO, 1, 4);  // E2
        tab.moverPeca(peao, 1, 4);

        assertTrue(jogo.selecionaPeca(1, 4));
        assertTrue(jogo.selecionaCasa(0, 4));    // E1 – promoção

        Peca promovida = tab.getPeca(0, 4);
        assertTrue(promovida instanceof Rainha);
        assertEquals(Cor.BRANCO, promovida.getCor());
    }

    /*============================================================
     *  G. GUARDAS E ERROS DE USO
     *==========================================================*/
    @Test
    public void selecionaForaDoTabuleiro_falha() {
        assertFalse(jogo.selecionaPeca(-1, 0));
        assertFalse(jogo.selecionaPeca(8 , 0));
        assertFalse(jogo.selecionaPeca(0 , -1));
        assertFalse(jogo.selecionaPeca(0 ,  8));
    }

    @Test
    public void moveSemPecaSelecionada_falha() {
        assertFalse(jogo.selecionaCasa(4, 0));
    }
}
