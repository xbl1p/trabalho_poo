package model;

/**
 * Classe para gerenciar movimentos especiais do xadrez
 * como Roque e En Passant
 */
public class MovimentoEspecial {
    
    /**
     * Verifica se o roque é possível
     */
	public static boolean podeRoque(Tabuleiro tabuleiro, Rei rei, boolean roquePequeno) {
	    int linha = rei.getLinha();
	    int colunaRei = rei.getColuna();
	    int colunaTorre = roquePequeno ? 7 : 0;

	    System.out.println("=== DEBUG GERAL DO ROQUE ===");
	    System.out.println("rei.jaMoveu(): " + rei.jaMoveu());

	    JogoXadrez jogo = JogoXadrez.getInstancia();
	    System.out.println("Está em xeque? " + jogo.estaEmXeque(rei.getCor()));
	    System.out.println("Verificando torre na coluna: " + colunaTorre);

	    Peca pecaTorre = tabuleiro.getPeca(linha, colunaTorre);
	    if (pecaTorre instanceof Torre) {
	        Torre torre = (Torre) pecaTorre;
	        System.out.println("torre.jaMoveu(): " + torre.jaMoveu());
	    } else {
	        System.out.println("Não é torre ou está ausente.");
	    }

	    System.out.println("[DEBUG Roque] Verificando roque " + (roquePequeno ? "pequeno" : "grande"));
        // Verificar se o caminho está livre
        int inicio = Math.min(colunaRei, colunaTorre) + 1;
        int fim = Math.max(colunaRei, colunaTorre);
        
        System.out.println("[DEBUG Roque] Verificando caminho livre de " + inicio + " até " + fim);
        
        for (int col = inicio; col < fim; col++) {
            if (tabuleiro.getPeca(linha, col) != null) {
                System.out.println("[DEBUG Roque] Caminho bloqueado em (" + linha + "," + col + ")");
                return false;
            }
        }
        
        // Verificar se o rei passa por casa atacada
        int direcao = roquePequeno ? 1 : -1;
        // Verificar as duas casas que o rei vai atravessar (não a origem, pois já está em xeque)
        for (int i = 1; i <= 2; i++) {
            int novaColuna = colunaRei + (i * direcao);
            if (novaColuna >= 0 && novaColuna <= 7) {
                if (!movimentoSeguroRei(tabuleiro, rei, linha, novaColuna)) {
                    System.out.println("[DEBUG Roque] Rei passaria por casa atacada em (" + linha + "," + novaColuna + ")");
                    return false;
                }
            }
        }
        
        System.out.println("[DEBUG Roque] Roque permitido!");
        return true;
    }
    
    /**
     * Executa o roque
     */
    public static void executarRoque(Tabuleiro tabuleiro, Rei rei, boolean roquePequeno) {
        int linha = rei.getLinha();
        int colunaRei = rei.getColuna();
        int colunaTorre = roquePequeno ? 7 : 0;
        int novaColunaRei = roquePequeno ? 6 : 2;
        int novaColunaTorre = roquePequeno ? 5 : 3;
        
        Torre torre = (Torre) tabuleiro.getPeca(linha, colunaTorre);
        
        // Mover rei
        tabuleiro.moverPeca(rei, linha, novaColunaRei);
        rei.marcarMovimento();
        
        // Mover torre
        tabuleiro.moverPeca(torre, linha, novaColunaTorre);
        torre.marcarMovimento();
    }
    
    /**
     * Verifica se o en passant é possível
     */
    public static boolean podeEnPassant(Tabuleiro tabuleiro, Peao peao, int colunaDestino) {
        int linha = peao.getLinha();
        Cor cor = peao.getCor();
        
        // Verificar se o peão está na linha correta
        if ((cor == Cor.BRANCO && linha != 3) || 
            (cor == Cor.PRETO && linha != 4)) {
            return false;
        }
        
        // Verificar se há um peão adversário ao lado
        Peca pecaLateral = tabuleiro.getPeca(linha, colunaDestino);
        if (pecaLateral == null || !(pecaLateral instanceof Peao) || pecaLateral.getCor() == cor) {
            return false;
        }
        
        Peao peaoAdversario = (Peao) pecaLateral;
        
        // Verificar se o peão adversário acabou de fazer movimento duplo
        return peaoAdversario.acabouDeFazerMovimentoDuplo();
    }
    
    /**
     * Executa o en passant
     */
    public static void executarEnPassant(Tabuleiro tabuleiro, Peao peao, int colunaDestino) {
        int linha = peao.getLinha();
        int novaLinha = peao.getCor() == Cor.BRANCO ? linha - 1 : linha + 1;
        
        // Capturar o peão adversário
        tabuleiro.setPeca(linha, colunaDestino, null);
        
        // Mover o peão
        tabuleiro.moverPeca(peao, novaLinha, colunaDestino);
    }
    
    private static boolean movimentoSeguroRei(Tabuleiro tabuleiro, Rei rei, int linha, int coluna) {
        // Verificar se a posição seria atacada por alguma peça adversária
        for (int l = 0; l < 8; l++) {
            for (int c = 0; c < 8; c++) {
                Peca peca = tabuleiro.getPeca(l, c);
                if (peca != null && peca.getCor() != rei.getCor()) {
                    if (peca.podeMoverPara(tabuleiro, linha, coluna)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
