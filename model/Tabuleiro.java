package model;

class Tabuleiro {
    private Peca[][] casas;

    Tabuleiro() {
        casas = new Peca[8][8];
        inicializar();
    }

    void inicializar() {
        for (int i = 0; i < 8; i++) {
            casas[1][i] = new Peao(Cor.PRETO, 1, i);
            casas[6][i] = new Peao(Cor.BRANCO, 6, i);
        }
        casas[0][0] = new Torre(Cor.PRETO, 0, 0);
        casas[0][7] = new Torre(Cor.PRETO, 0, 7);
        casas[7][0] = new Torre(Cor.BRANCO, 7, 0);
        casas[7][7] = new Torre(Cor.BRANCO, 7, 7);

        casas[0][1] = new Cavalo(Cor.PRETO, 0, 1);
        casas[0][6] = new Cavalo(Cor.PRETO, 0, 6);
        casas[7][1] = new Cavalo(Cor.BRANCO, 7, 1);
        casas[7][6] = new Cavalo(Cor.BRANCO, 7, 6);

        casas[0][2] = new Bispo(Cor.PRETO, 0, 2);
        casas[0][5] = new Bispo(Cor.PRETO, 0, 5);
        casas[7][2] = new Bispo(Cor.BRANCO, 7, 2);
        casas[7][5] = new Bispo(Cor.BRANCO, 7, 5);

        casas[0][3] = new Rainha(Cor.PRETO, 0, 3);
        casas[7][3] = new Rainha(Cor.BRANCO, 7, 3);
        casas[0][4] = new Rei(Cor.PRETO, 0, 4);
        casas[7][4] = new Rei(Cor.BRANCO, 7, 4);
    }

    Peca getPeca(int linha, int coluna) {
        if (linha < 0 || linha > 7 || coluna < 0 || coluna > 7) return null;
        return casas[linha][coluna];
    }

    void moverPeca(Peca p, int linha, int coluna) {
        casas[p.getLinha()][p.getColuna()] = null;
        p.setPosicao(linha, coluna);
        casas[linha][coluna] = p;
    }
}
