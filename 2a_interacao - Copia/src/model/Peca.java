package model;

public abstract class Peca {  // mudado para public
    protected int linha, coluna;
    protected Cor cor;

    public Peca(Cor cor, int linha, int coluna) {  // mudado para public
        this.cor = cor;
        this.linha = linha;
        this.coluna = coluna;
    }

    public Cor getCor() {  //  mudado para public
        return cor;
    }

    public int getLinha() {  // mudado para public
        return linha;
    }

    public int getColuna() {  // mudado para public
        return coluna;
    }

    public void setPosicao(int linha, int coluna) {  // mudado para public
        this.linha = linha;
        this.coluna = coluna;
    }

    public abstract boolean podeMoverPara(Tabuleiro tabuleiro, int linhaDestino, int colunaDestino);  // mudado para public
}