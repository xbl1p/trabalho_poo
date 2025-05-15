package model;

abstract class Peca {
    protected int linha, coluna;
    protected Cor cor;

    Peca(Cor cor, int linha, int coluna) {
        this.cor = cor;
        this.linha = linha;
        this.coluna = coluna;
    }

    Cor getCor() {
        return cor;
    }

    int getLinha() {
        return linha;
    }

    int getColuna() {
        return coluna;
    }

    void setPosicao(int linha, int coluna) {
        this.linha = linha;
        this.coluna = coluna;
    }

    abstract boolean podeMoverPara(Tabuleiro tabuleiro, int linhaDestino, int colunaDestino);
}
