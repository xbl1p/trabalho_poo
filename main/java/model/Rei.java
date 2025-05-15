package model;

class Rei extends Peca {

    Rei(Cor cor, int linha, int coluna) {
        super(cor, linha, coluna);
    }

    @Override
    boolean podeMoverPara(Tabuleiro tabuleiro, int l, int c) {
        int dx = Math.abs(linha - l);
        int dy = Math.abs(coluna - c);
        if (dx > 1 || dy > 1) return false;

        Peca destino = tabuleiro.getPeca(l, c);
        return destino == null || destino.getCor() != cor;
    }
}
