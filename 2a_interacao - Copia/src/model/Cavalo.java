package model;

public class Cavalo extends Peca {  // mudado para public

    public Cavalo(Cor cor, int linha, int coluna) {  //  mudado para public
        super(cor, linha, coluna);
    }

    @Override
    public boolean podeMoverPara(Tabuleiro tabuleiro, int l, int c) {  // mudado para public
        if (l < 0 || l > 7 || c < 0 || c > 7) return false; 

        int dx = Math.abs(linha - l);
        int dy = Math.abs(coluna - c);
        if (!((dx == 2 && dy == 1) || (dx == 1 && dy == 2))) return false;

        Peca destino = tabuleiro.getPeca(l, c);
        return destino == null || destino.getCor() != cor;
    }
}