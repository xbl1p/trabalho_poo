package model;

class Torre extends Peca {

    Torre(Cor cor, int linha, int coluna) {
        super(cor, linha, coluna);
    }

    @Override
    boolean podeMoverPara(Tabuleiro tabuleiro, int l, int c) {
        if (linha != l && coluna != c) return false;

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
