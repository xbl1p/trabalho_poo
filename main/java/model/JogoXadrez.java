package model;

public class JogoXadrez {
    private static JogoXadrez instancia = null;
    private Tabuleiro tabuleiro;
    private Cor jogadorDaVez;
    private Peca pecaSelecionada;

    private JogoXadrez() {
        tabuleiro = new Tabuleiro();
        jogadorDaVez = Cor.BRANCO;
    }

    public static JogoXadrez getInstancia() {
        if (instancia == null) instancia = new JogoXadrez();
        return instancia;
    }

    public boolean selecionaPeca(int linha, int coluna) {
        Peca p = tabuleiro.getPeca(linha, coluna);
        if (p != null && p.getCor() == jogadorDaVez) {
            pecaSelecionada = p;
            return true;
        }
        return false;
    }

    public boolean selecionaCasa(int linha, int coluna) {
        if (pecaSelecionada == null) return false;
        boolean podeMover = pecaSelecionada.podeMoverPara(tabuleiro, linha, coluna);
        if (podeMover) {
            tabuleiro.moverPeca(pecaSelecionada, linha, coluna);
            
         // Verifica promoção
            if (pecaSelecionada instanceof Peao) {
                boolean chegouNoFim =
                    (pecaSelecionada.getCor() == Cor.BRANCO && linha == 0) ||
                    (pecaSelecionada.getCor() == Cor.PRETO && linha == 7);

                if (chegouNoFim) {
                    // substitui diretamente por rainha
                    Peca novaRainha = new Rainha(pecaSelecionada.getCor(), linha, coluna);
                    tabuleiro.moverPeca(novaRainha, linha, coluna);
                }
            }
            
            alternarJogador();
        }
        pecaSelecionada = null;
        return podeMover;
    }

    private void alternarJogador() {
        jogadorDaVez = (jogadorDaVez == Cor.BRANCO) ? Cor.PRETO : Cor.BRANCO;
    }
}
