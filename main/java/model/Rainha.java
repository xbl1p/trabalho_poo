package model;

class Rainha extends Peca {

    Rainha(Cor cor, int linha, int coluna) {
        super(cor, linha, coluna);
    }

    @Override
    boolean podeMoverPara(Tabuleiro tabuleiro, int l, int c) {
    	if (l < 0 || l > 7 || c < 0 || c > 7) return false; 
        
        int dx = Math.abs(l - linha);
        int dy = Math.abs(c - coluna);
        if (dx != 0 && dy != 0 && dx != dy) return false;

        int deltaL = Integer.compare(l, linha);
        int deltaC = Integer.compare(c, coluna);
        int i = linha + deltaL, j = coluna + deltaC;

        while (i != l || j != c) {
            if (tabuleiro.getPeca(i, j) != null) return false;
            i += deltaL;
            j += deltaC;
        }

        Peca destino = tabuleiro.getPeca(l, c);
        return destino == null || destino.getCor() != cor;
    }
}
