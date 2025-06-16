package model;

import util.ObservadorIF;
import util.ObservadoIF;
import java.util.ArrayList;
import java.util.List;

public class JogoXadrez implements ObservadoIF {
    private static JogoXadrez instancia = null;
    private Tabuleiro tabuleiro;
    private Cor jogadorDaVez;
    private Peca pecaSelecionada;
    private HistoricoJogadas historico;
    
    // Lista de observadores
    private List<ObservadorIF> observadores;

    private JogoXadrez() {
        tabuleiro = new Tabuleiro();
        jogadorDaVez = Cor.BRANCO;
        observadores = new ArrayList<>();
        historico = new HistoricoJogadas();
    }

    public static JogoXadrez getInstancia() {
        if (instancia == null) instancia = new JogoXadrez();
        return instancia;
    }
    
    public static JogoXadrez novaInstancia() {
        instancia = new JogoXadrez();
        return instancia;
    }
    
    // Implementação dos métodos do padrão Observer
    @Override
    public void add(ObservadorIF o) {
        if (!observadores.contains(o)) {
            observadores.add(o);
        }
    }
    
    @Override
    public void remove(ObservadorIF o) {
        observadores.remove(o);
    }
    
    @Override
    public int get(int i) {
        switch(i) {
            case 0: // Retorna código do jogador atual
                return jogadorDaVez == Cor.BRANCO ? 0 : 1;
            case 1: // Retorna se está em xeque
                return estaEmXeque(jogadorDaVez) ? 1 : 0;
            case 2: // Retorna se está em xeque-mate
                return estaEmXequeMate(jogadorDaVez) ? 1 : 0;
            case 3: // Retorna se está em empate
                return estaEmEmpate() ? 1 : 0;
            case 4: // Retorna número de jogadas
                return historico.getNumeroJogada();
            default:
                return -1;
        }
    }
    
    /**
     * Notifica todos os observadores sobre mudanças
     */
    private void notificarObservadores() {
        System.out.println("[DEBUG Observer] Notificando " + observadores.size() + " observadores");
        for (ObservadorIF obs : observadores) {
            System.out.println("[DEBUG Observer] Notificando: " + obs.getClass().getSimpleName());
            obs.notify(this);
        }
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
            // Notificar observadores após mudança
            notificarObservadores();
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
    
    //verifica se movimento deixa próprio rei em xeque
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
        
        // Determinar tipo de movimento
        String tipoMovimento = "normal";
        Peca pecaCapturada = tabuleiro.getPeca(linhaDestino, colunaDestino);
        boolean roquePequeno = false;
        
        // Verificar roque
        if (peca instanceof Rei && Math.abs(colunaDestino - colunaOrigem) == 2) {
            roquePequeno = colunaDestino > colunaOrigem;
            // Verificar se o roque é seguro (as casas que o rei atravessa não estão sob ataque)
            if (!movimentoSeguroParaRoque((Rei)peca, roquePequeno)) {
                return false;
            }
            
            tipoMovimento = "roque";
            // Executar roque
            MovimentoEspecial.executarRoque(tabuleiro, (Rei)peca, roquePequeno);
            
            // Adicionar ao histórico
            historico.adicionarJogada(new HistoricoJogadas.Jogada(
                linhaOrigem, colunaOrigem, linhaDestino, colunaDestino,
                peca, null, tipoMovimento
            ));
            
            alternarJogador();
            notificarObservadores();
            return true;
        }
        
        // Verificar en passant
        if (peca instanceof Peao && pecaCapturada == null && 
            Math.abs(colunaDestino - colunaOrigem) == 1) {
            if (MovimentoEspecial.podeEnPassant(tabuleiro, (Peao)peca, colunaDestino)) {
                tipoMovimento = "enpassant";
                pecaCapturada = tabuleiro.getPeca(linhaOrigem, colunaDestino);
                // Verificar se o en passant não deixa o rei em xeque
                if (!movimentoSeguro(linhaOrigem, colunaOrigem, linhaDestino, colunaDestino)) {
                    return false;
                }
                MovimentoEspecial.executarEnPassant(tabuleiro, (Peao)peca, colunaDestino);
                
                // Adicionar ao histórico
                historico.adicionarJogada(new HistoricoJogadas.Jogada(
                    linhaOrigem, colunaOrigem, linhaDestino, colunaDestino,
                    peca, pecaCapturada, tipoMovimento
                ));
                
                alternarJogador();
                notificarObservadores();
                return true;
            }
        }
        
        //Verificar se movimento deixa próprio rei em xeque
        if (!movimentoSeguro(linhaOrigem, colunaOrigem, linhaDestino, colunaDestino)) {
            return false;
        }
        
        // Realizar o movimento normal
        tabuleiro.moverPeca(peca, linhaDestino, colunaDestino);
        
        // Adicionar ao histórico
        historico.adicionarJogada(new HistoricoJogadas.Jogada(
            linhaOrigem, colunaOrigem, linhaDestino, colunaDestino,
            peca, pecaCapturada, tipoMovimento
        ));
        
        // Verificar promoção de peão
        if (peca instanceof Peao) {
            boolean chegouNoFim =
                (peca.getCor() == Cor.BRANCO && linhaDestino == 0) ||
                (peca.getCor() == Cor.PRETO && linhaDestino == 7);

            if (chegouNoFim) {
                tipoMovimento = "promocao";
                // Por enquanto, promove automaticamente para rainha
                Peca novaRainha = new Rainha(peca.getCor(), linhaDestino, colunaDestino);
                tabuleiro.setPeca(linhaDestino, colunaDestino, novaRainha);
            }
        }
        
        alternarJogador();
        
        // Resetar en passant após cada turno
        Peao.resetarEnPassant();
        
        // Notificar observadores após movimento
        notificarObservadores();
        
        return true;
    }
    
    //Verifica se movimento é seguro (não deixa próprio rei em xeque)
    private boolean movimentoSeguro(int linhaOrigem, int colunaOrigem, int linhaDestino, int colunaDestino) {
        System.out.println("[DEBUG movimentoSeguro] Verificando movimento de (" + linhaOrigem + "," + 
                         colunaOrigem + ") para (" + linhaDestino + "," + colunaDestino + ")");
        
        Peca peca = tabuleiro.getPeca(linhaOrigem, colunaOrigem);
        Peca pecaCapturada = tabuleiro.getPeca(linhaDestino, colunaDestino);
        
        // Verificar se é roque
        boolean isRoque = false;
        Torre torre = null;
        int colunaTorreOriginal = -1;
        int colunaTorreDestino = -1;
        boolean reiJaMoveuAntes = false;
        boolean torreJaMoveuAntes = false;
        
        if (peca instanceof Rei && Math.abs(colunaDestino - colunaOrigem) == 2) {
            System.out.println("[DEBUG movimentoSeguro] Detectado movimento de roque");
            isRoque = true;
            // Determinar posições da torre
            boolean roquePequeno = colunaDestino > colunaOrigem;
            colunaTorreOriginal = roquePequeno ? 7 : 0;
            colunaTorreDestino = roquePequeno ? 5 : 3;
            torre = (Torre) tabuleiro.getPeca(linhaOrigem, colunaTorreOriginal);
            
            if (torre != null) {
                System.out.println("[DEBUG movimentoSeguro] Torre encontrada em (" + linhaOrigem + 
                                 "," + colunaTorreOriginal + ")");
                // Salvar estados originais
                reiJaMoveuAntes = ((Rei) peca).jaMoveu();
                torreJaMoveuAntes = torre.jaMoveu();
            } else {
                System.out.println("[DEBUG movimentoSeguro] Torre não encontrada!");
                return false;
            }
        }
        
        // Caso especial para en passant
        boolean enPassant = false;
        int linhaCaptura = -1, colunaCaptura = -1;
        if (peca instanceof Peao && pecaCapturada == null && 
            Math.abs(colunaDestino - colunaOrigem) == 1) {
            
            if (MovimentoEspecial.podeEnPassant(tabuleiro, (Peao)peca, colunaDestino)) {
                enPassant = true;
                linhaCaptura = linhaOrigem; // Linha do peão capturado
                colunaCaptura = colunaDestino; // Coluna do peão capturado
                pecaCapturada = tabuleiro.getPeca(linhaCaptura, colunaCaptura);
            }
        }
        
        // Salvar estado do peão (se aplicável)
        boolean primeiroMovimentoAntes = false;
        if (peca instanceof Peao) {
            primeiroMovimentoAntes = ((Peao) peca).isPrimeiroMovimento();
        }
        
        // Simular movimento
        tabuleiro.moverPeca(peca, linhaDestino, colunaDestino);
        
        // Se for roque, mover também a torre
        if (isRoque && torre != null) {
            System.out.println("[DEBUG movimentoSeguro] Simulando movimento da torre");
            tabuleiro.moverPeca(torre, linhaOrigem, colunaTorreDestino);
        }
        
        // Se for en passant, remover o peão capturado
        if (enPassant) {
            tabuleiro.setPeca(linhaCaptura, colunaCaptura, null);
        }
        
        // Verificar se próprio rei ficou em xeque
        boolean reiEmXeque = estaEmXeque(jogadorDaVez);
        System.out.println("[DEBUG movimentoSeguro] Rei em xeque após movimento: " + reiEmXeque);
        
        // Desfazer movimento
        tabuleiro.moverPeca(peca, linhaOrigem, colunaOrigem);
        tabuleiro.setPeca(linhaDestino, colunaDestino, pecaCapturada);
        
        // Se foi roque, desfazer movimento da torre
        if (isRoque && torre != null) {
            System.out.println("[DEBUG movimentoSeguro] Desfazendo movimento da torre");
            tabuleiro.moverPeca(torre, linhaOrigem, colunaTorreOriginal);
            
            // Restaurar estados originais do rei e torre
            if (!reiJaMoveuAntes) {
                ((Rei) peca).setPrimeiroMovimento(true);
            }
            if (!torreJaMoveuAntes) {
                torre.setPrimeiroMovimento(true);
            }
        }
        
        // Se foi en passant, recolocar o peão capturado
        if (enPassant) {
            tabuleiro.setPeca(linhaCaptura, colunaCaptura, pecaCapturada);
        }
        
        // Restaurar estado do peão
        if (peca instanceof Peao) {
            ((Peao) peca).setPrimeiroMovimento(primeiroMovimentoAntes);
        }
        
        System.out.println("[DEBUG movimentoSeguro] Movimento seguro: " + !reiEmXeque);
        return !reiEmXeque;
    }
    
    //Verifica se movimento é seguro para o roque
    private boolean movimentoSeguroParaRoque(Rei rei, boolean roquePequeno) {
        int linha = rei.getLinha();
        int colunaRei = rei.getColuna();
        int direcao = roquePequeno ? 1 : -1;
        
        // Verificar as casas que o rei vai atravessar (não a origem, pois já foi verificada)
        for (int i = 1; i <= 2; i++) {
            int novaColuna = colunaRei + (i * direcao);
            if (novaColuna < 0 || novaColuna > 7) continue;
            
            // Verificar se a casa é atacada
            if (casaSobAtaque(linha, novaColuna, rei.getCor())) {
                return false;
            }
        }
        return true;
    }
    
    // Método auxiliar para verificar se uma casa está sob ataque
    private boolean casaSobAtaque(int linha, int coluna, Cor cor) {
        for (int l = 0; l < 8; l++) {
            for (int c = 0; c < 8; c++) {
                Peca peca = tabuleiro.getPeca(l, c);
                if (peca != null && peca.getCor() != cor) {
                    if (peca.podeMoverPara(tabuleiro, linha, coluna)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    // Agora inclui verificação de xeque
    public boolean movimentoValido(int linhaOrigem, int colunaOrigem, int linhaDestino, int colunaDestino) {
        System.out.println("[DEBUG movimentoValido] Verificando validade de (" + linhaOrigem + "," + 
                         colunaOrigem + ") para (" + linhaDestino + "," + colunaDestino + ")");
        
        Peca peca = tabuleiro.getPeca(linhaOrigem, colunaOrigem);
        
        if (peca == null || peca.getCor() != jogadorDaVez) {
            System.out.println("[DEBUG movimentoValido] Peça inválida ou cor errada");
            return false;
        }
        
        if (peca instanceof Rei && linhaOrigem == linhaDestino && Math.abs(colunaDestino - colunaOrigem) == 2) {
            boolean roquePequeno = colunaDestino > colunaOrigem;
            return MovimentoEspecial.podeRoque(tabuleiro, (Rei) peca, roquePequeno);
        }
        
        // Verificar se movimento básico é válido
        if (!peca.podeMoverPara(tabuleiro, linhaDestino, colunaDestino)) {
            System.out.println("[DEBUG movimentoValido] podeMoverPara retornou false");
            return false;
        }
        
        System.out.println("[DEBUG movimentoValido] podeMoverPara retornou true, verificando segurança");
        
        //Verificar se movimento é seguro (não deixa rei em xeque)
        return movimentoSeguro(linhaOrigem, colunaOrigem, linhaDestino, colunaDestino);
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
    
    //verifica se movimentos são realmente legais
    public boolean estaEmEmpate() {
        if (estaEmXeque(jogadorDaVez)) return false; // Se está em xeque, não é empate
        
        // Verifica se o jogador atual tem movimentos legais
        for (int linha = 0; linha < 8; linha++) {
            for (int coluna = 0; coluna < 8; coluna++) {
                Peca peca = tabuleiro.getPeca(linha, coluna);
                if (peca != null && peca.getCor() == jogadorDaVez) {
                    for (int novaLinha = 0; novaLinha < 8; novaLinha++) {
                        for (int novaColuna = 0; novaColuna < 8; novaColuna++) {
                            if (peca.podeMoverPara(tabuleiro, novaLinha, novaColuna)) {
                                // Simula movimento para verificar se é realmente legal
                                Peca pecaCapturada = tabuleiro.getPeca(novaLinha, novaColuna);
                                int linhaOriginal = peca.getLinha();
                                int colunaOriginal = peca.getColuna();
                                
                                tabuleiro.moverPeca(peca, novaLinha, novaColuna);
                                
                                boolean deixaReiEmXeque = estaEmXeque(jogadorDaVez);
                                
                                // Desfazer movimento
                                tabuleiro.moverPeca(peca, linhaOriginal, colunaOriginal);
                                tabuleiro.setPeca(novaLinha, novaColuna, pecaCapturada);
                                
                                if (!deixaReiEmXeque) {
                                    return false; // Tem movimento legal
                                }
                            }
                        }
                    }
                }
            }
        }
        return true; // Não tem movimentos legais(empate)
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
    
    /**
     * Força notificação dos observadores
     * Útil para sincronizar a View quando necessário
     */
    public void sincronizarView() {
        notificarObservadores();
    }
    
    /**
     * Retorna o histórico de jogadas
     */
    public HistoricoJogadas getHistorico() {
        return historico;
    }
    
    /**
     * Retorna a notação PGN da partida
     */
    public String getPGN() {
        return historico.toPGN();
    }
    
    /**
     * Verifica se pode fazer roque
     */
    public boolean podeRoque(boolean roquePequeno) {
        int[] posRei = encontrarRei(jogadorDaVez);
        if (posRei == null) return false;

        Peca peca = tabuleiro.getPeca(posRei[0], posRei[1]);
        if (!(peca instanceof Rei)) return false;

        return MovimentoEspecial.podeRoque(tabuleiro, (Rei)peca, roquePequeno);
    }
}
