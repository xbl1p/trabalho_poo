package model;

public class JogoXadrez {
    private static JogoXadrez instancia = null;
    private Tabuleiro tabuleiro;
    private Cor jogadorDaVez;
    private Peca pecaSelecionada;

    private JogoXadrez() {
        tabuleiro = new Tabuleiro();
        jogadorDaVez = Cor.BRANCO;
    }

    public static JogoXadrez getInstancia() {
        if (instancia == null) instancia = new JogoXadrez();
        return instancia;
    }
    
    public static JogoXadrez novaInstancia() {
        instancia = new JogoXadrez();
        return instancia;
    }

    public boolean selecionaPeca(int linha, int coluna) {
        Peca p = tabuleiro.getPeca(linha, coluna);
        if (p != null && p.getCor() == jogadorDaVez) {
            pecaSelecionada = p;
            return true;
        }
        return false;
    }

    public boolean selecionaCasa(int linha, int coluna) {
        if (pecaSelecionada == null) return false;
        boolean podeMover = pecaSelecionada.podeMoverPara(tabuleiro, linha, coluna);
        if (podeMover) {
            tabuleiro.moverPeca(pecaSelecionada, linha, coluna);
            
         // Verifica promoção
            if (pecaSelecionada instanceof Peao) {
                boolean chegouNoFim =
                    (pecaSelecionada.getCor() == Cor.BRANCO && linha == 0) ||
                    (pecaSelecionada.getCor() == Cor.PRETO && linha == 7);

                if (chegouNoFim) {
                    // substitui diretamente por rainha
                    Peca novaRainha = new Rainha(pecaSelecionada.getCor(), linha, coluna);
                    tabuleiro.setPeca(linha, coluna, novaRainha);
                }
            }
            
            alternarJogador();
        }
        pecaSelecionada = null;
        return podeMover;
    }

    private void alternarJogador() {
        jogadorDaVez = (jogadorDaVez == Cor.BRANCO) ? Cor.PRETO : Cor.BRANCO;
    }
    
    public Tabuleiro getTabuleiro() {
        return tabuleiro;
    }
    
    public Cor getJogadorAtual() {
        return jogadorDaVez;
    }
    
    public boolean moverPeca(int linhaOrigem, int colunaOrigem, int linhaDestino, int colunaDestino) {
        Peca peca = tabuleiro.getPeca(linhaOrigem, colunaOrigem);
        
        // Verificar se é uma peça do jogador atual
        if (peca == null || peca.getCor() != jogadorDaVez) {
            return false;
        }
        
        // Verificar se o movimento é válido
        if (!peca.podeMoverPara(tabuleiro, linhaDestino, colunaDestino)) {
            return false;
        }
        
        // Realizar o movimento
        tabuleiro.moverPeca(peca, linhaDestino, colunaDestino);
        
        // Verificar promoção de peão
        if (peca instanceof Peao) {
            boolean chegouNoFim =
                (peca.getCor() == Cor.BRANCO && linhaDestino == 0) ||
                (peca.getCor() == Cor.PRETO && linhaDestino == 7);

            if (chegouNoFim) {
                // Por enquanto, promove automaticamente para rainha
                Peca novaRainha = new Rainha(peca.getCor(), linhaDestino, colunaDestino);
                tabuleiro.setPeca(linhaDestino, colunaDestino, novaRainha);
            }
        }
        
        alternarJogador();
        return true;
    }
    
    public boolean movimentoValido(int linhaOrigem, int colunaOrigem, int linhaDestino, int colunaDestino) {
        Peca peca = tabuleiro.getPeca(linhaOrigem, colunaOrigem);
        
        if (peca == null || peca.getCor() != jogadorDaVez) {
            return false;
        }
        
        return peca.podeMoverPara(tabuleiro, linhaDestino, colunaDestino);
    }
    
    public boolean estaEmXeque(Cor cor) {
        // Encontrar o rei da cor especificada
        int[] posicaoRei = encontrarRei(cor);
        if (posicaoRei == null) return false;
        
        // Verificar se alguma peça adversária pode atacar o rei
        for (int linha = 0; linha < 8; linha++) {
            for (int coluna = 0; coluna < 8; coluna++) {
                Peca peca = tabuleiro.getPeca(linha, coluna);
                if (peca != null && peca.getCor() != cor) {
                    if (peca.podeMoverPara(tabuleiro, posicaoRei[0], posicaoRei[1])) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public boolean estaEmXequeMate(Cor cor) {
        if (!estaEmXeque(cor)) return false;
        
        // Verificar se existe algum movimento legal que tire o xeque
        for (int linha = 0; linha < 8; linha++) {
            for (int coluna = 0; coluna < 8; coluna++) {
                Peca peca = tabuleiro.getPeca(linha, coluna);
                if (peca != null && peca.getCor() == cor) {
                    // Testar todos os movimentos possíveis desta peça
                    for (int novaLinha = 0; novaLinha < 8; novaLinha++) {
                        for (int novaColuna = 0; novaColuna < 8; novaColuna++) {
                            if (peca.podeMoverPara(tabuleiro, novaLinha, novaColuna)) {
                                // Simular movimento
                                Peca pecaCapturada = tabuleiro.getPeca(novaLinha, novaColuna);
                                int linhaOriginal = peca.getLinha();
                                int colunaOriginal = peca.getColuna();
                                
                                tabuleiro.moverPeca(peca, novaLinha, novaColuna);
                                
                                boolean aindaEmXeque = estaEmXeque(cor);
                                
                                // Desfazer movimento
                                tabuleiro.moverPeca(peca, linhaOriginal, colunaOriginal);
                                tabuleiro.setPeca(novaLinha, novaColuna, pecaCapturada);
                                
                                if (!aindaEmXeque) {
                                    return false; // Existe um movimento legal
                                }
                            }
                        }
                    }
                }
            }
        }
        return true; // Não existe movimento legal - é xeque-mate
    }
    
    public boolean estaEmEmpate() {
        if (estaEmXeque(jogadorDaVez)) return false; // Se está em xeque, não é empate
        
        // Verificar se o jogador atual tem movimentos legais
        for (int linha = 0; linha < 8; linha++) {
            for (int coluna = 0; coluna < 8; coluna++) {
                Peca peca = tabuleiro.getPeca(linha, coluna);
                if (peca != null && peca.getCor() == jogadorDaVez) {
                    for (int novaLinha = 0; novaLinha < 8; novaLinha++) {
                        for (int novaColuna = 0; novaColuna < 8; novaColuna++) {
                            if (peca.podeMoverPara(tabuleiro, novaLinha, novaColuna)) {
                                return false; // Tem movimento legal
                            }
                        }
                    }
                }
            }
        }
        return true; // Não tem movimentos legais - é empate
    }
    
    private int[] encontrarRei(Cor cor) {
        for (int linha = 0; linha < 8; linha++) {
            for (int coluna = 0; coluna < 8; coluna++) {
                Peca peca = tabuleiro.getPeca(linha, coluna);
                if (peca instanceof Rei && peca.getCor() == cor) {
                    return new int[]{linha, coluna};
                }
            }
        }
        return null;
    }
}