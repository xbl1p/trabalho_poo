package model;

class Peao extends Peca {

    Peao(Cor cor, int linha, int coluna) {
        super(cor, linha, coluna);
    }

    @Override
    boolean podeMoverPara(Tabuleiro tabuleiro, int l, int c) {
    	if (l < 0 || l > 7 || c < 0 || c > 7) return false; 

        int direcao = (cor == Cor.BRANCO) ? -1 : 1;
        boolean casaLivre = tabuleiro.getPeca(l, c) == null;

        if (c == coluna && casaLivre) {
            if (l == linha + direcao) return true;
            if ((linha == 1 && cor == Cor.PRETO || linha == 6 && cor == Cor.BRANCO)
                && l == linha + 2 * direcao && tabuleiro.getPeca(linha + direcao, c) == null) {
                return true;
            }
        }

        if (Math.abs(c - coluna) == 1 && l == linha + direcao) {
            Peca oponente = tabuleiro.getPeca(l, c);
            return oponente != null && oponente.getCor() != cor;
        }

        return false;
    }
}
