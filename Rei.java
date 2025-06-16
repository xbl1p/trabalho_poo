package model;

public class Rei extends Peca {
    private boolean primeiroMovimento = true;

    public Rei(Cor cor, int linha, int coluna) {
        super(cor, linha, coluna);
    }

    @Override
    public boolean podeMoverPara(Tabuleiro tabuleiro, int l, int c) {
        if (l < 0 || l > 7 || c < 0 || c > 7) return false; 

        int dx = Math.abs(linha - l);
        int dy = Math.abs(coluna - c);
        
        // Movimento normal do rei (uma casa em qualquer direção)
        if (dx <= 1 && dy <= 1) {
            Peca destino = tabuleiro.getPeca(l, c);
            boolean podeIr = destino == null || destino.getCor() != cor;
            if (podeIr) {
                System.out.println("[DEBUG Rei] Movimento normal permitido para (" + l + "," + c + ")");
            }
            return podeIr;
        }
        
        // Verificar roque
        if (dx == 0 && Math.abs(dy) == 2 && !jaMoveu()) {
            System.out.println("[DEBUG Rei] Verificando roque - jaMoveu: " + jaMoveu());
            // Roque pequeno (rei move 2 casas para direita)
            if (c > coluna) {
            	boolean podeRoquePequeno = MovimentoEspecial.podeRoque(tabuleiro, this, true);
                System.out.println("[DEBUG Rei] Roque pequeno: " + podeRoquePequeno);
                return podeRoquePequeno;
            }
            // Roque grande (rei move 2 casas para esquerda)
            else {
            	boolean podeRoqueGrande = MovimentoEspecial.podeRoque(tabuleiro, this, false);
                System.out.println("[DEBUG Rei] Roque grande: " + podeRoqueGrande);
                return podeRoqueGrande;
            }
        }
        
        return false;
    }
    
    @Override
    public void setPosicao(int linha, int coluna) {
        super.setPosicao(linha, coluna);
        primeiroMovimento = false;
    }
    
    public boolean jaMoveu() {
        return !primeiroMovimento;
    }
    
    public void marcarMovimento() {
        primeiroMovimento = false;
    }
    
    // MÉTODO ADICIONADO
    public void setPrimeiroMovimento(boolean valor) {
        this.primeiroMovimento = valor;
    }
}
