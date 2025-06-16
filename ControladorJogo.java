package controller;

import model.*;
import view.*;
import javax.swing.*;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

/**
 * Controller consolidado que organiza a sequência de eventos
 * em uma partida de xadrez
 */
public class ControladorJogo {
	private List<int[]> movimentosPossiveis = new ArrayList<>();
    private JanelaPrincipal janelaPrincipal;
    private FachadaView fachadaView;
    private JogoXadrez jogoXadrez;
    
    // Estado da seleção
    private int linhaSelecionada = -1;
    private int colunaSelecionada = -1;
    
    public ControladorJogo(JanelaPrincipal janela) {
        this.janelaPrincipal = janela;
        this.fachadaView = new FachadaView(janela);
    }
    
    public void iniciarNovaPartida() {
        jogoXadrez = JogoXadrez.novaInstancia();
        
        // Registrar a fachada como observadora do modelo
        jogoXadrez.add(fachadaView);
        
        // Resetar seleção
        linhaSelecionada = -1;
        colunaSelecionada = -1;
        
        // Atualizar view
        fachadaView.atualizarTabuleiro();
    }
    
    public void setTabuleiroGrafico(TabuleiroGrafico tabuleiro) {
        fachadaView.setTabuleiroGrafico(tabuleiro);
    }
    
    public void processarClique(int linha, int coluna) {
        if (jogoXadrez == null) return;

        Peca peca = jogoXadrez.getTabuleiro().getPeca(linha, coluna);

        // ================================
        // Caso 1: Nenhuma peça selecionada
        // ================================
        if (linhaSelecionada == -1) {
            if (peca != null && peca.getCor() == jogoXadrez.getJogadorAtual()) {
                linhaSelecionada = linha;
                colunaSelecionada = coluna;

                fachadaView.selecionarCasa(linha, coluna);
                movimentosPossiveis = calcularMovimentosPossiveis(linha, coluna);
                fachadaView.mostrarMovimentosPossiveis(movimentosPossiveis);
            }
        } 
        // ====================================
        // Caso 2: Já existe uma peça selecionada
        // ====================================
        else {
            // Primeiro, verificar se clicou em uma casa de destino válida (ex: roque)
            if (movimentosPossiveis != null) {
                for (int[] mov : movimentosPossiveis) {
                    if (mov[0] == linha && mov[1] == coluna) {
                        boolean movimentoValido = jogoXadrez.moverPeca(linhaSelecionada, colunaSelecionada, linha, coluna);
                        if (movimentoValido) {
                            limparSelecao();
                            verificarXeque();
                            verificarFimDeJogo();
                        } else {
                            limparSelecao(); // segurança
                        }
                        return;
                    }
                }
            }

            // Clicou na mesma peça: cancelar seleção
            if (linha == linhaSelecionada && coluna == colunaSelecionada) {
                limparSelecao();
                return;
            }

            // Clicou em outra peça da mesma cor: trocar seleção
            Peca pecaDestino = jogoXadrez.getTabuleiro().getPeca(linha, coluna);
            if (pecaDestino != null && pecaDestino.getCor() == jogoXadrez.getJogadorAtual()) {
                linhaSelecionada = linha;
                colunaSelecionada = coluna;
                fachadaView.selecionarCasa(linha, coluna);
                movimentosPossiveis = calcularMovimentosPossiveis(linha, coluna);
                fachadaView.mostrarMovimentosPossiveis(movimentosPossiveis);
                return;
            }

            // Última tentativa: mover para casa não destacada
            boolean movimentoValido = jogoXadrez.moverPeca(linhaSelecionada, colunaSelecionada, linha, coluna);
            if (movimentoValido) {
                if (precisaPromocao(linha, coluna)) {
                    fachadaView.mostrarPromocaoPeao(linha, coluna);
                }
                limparSelecao();
                verificarXeque();
                verificarFimDeJogo();
            } else {
                limparSelecao();
            }
        }
    }

    
    private List<int[]> calcularMovimentosPossiveis(int linha, int coluna) {
        List<int[]> movimentos = new ArrayList<>();
        
        // Debug - verificar peça selecionada
        String infoPeca = obterInfoPeca(linha, coluna);
        System.out.println("\n[DEBUG ControladorJogo] ========================================");
        System.out.println("[DEBUG ControladorJogo] Calculando movimentos para: " + infoPeca + 
                          " em (" + linha + "," + coluna + ")");
        
        // Se for rei, verificar status de roque
        if (infoPeca != null && infoPeca.contains("king")) {
            Peca peca = jogoXadrez.getTabuleiro().getPeca(linha, coluna);
            if (peca instanceof Rei) {
                System.out.println("[DEBUG ControladorJogo] Rei - jaMoveu: " + ((Rei)peca).jaMoveu());
                System.out.println("[DEBUG ControladorJogo] Pode roque pequeno: " + 
                                 jogoXadrez.podeRoque(true));
                System.out.println("[DEBUG ControladorJogo] Pode roque grande: " + 
                                 jogoXadrez.podeRoque(false));
            }
        }
        
        // usar movimentoValido() em vez de método inexistente
        for (int l = 0; l < 8; l++) {
            for (int c = 0; c < 8; c++) {
                if (jogoXadrez.movimentoValido(linha, coluna, l, c)) {
                    movimentos.add(new int[]{l, c});
                    
                    // Debug para roque
                    if (infoPeca != null && infoPeca.contains("king") && 
                        Math.abs(c - coluna) == 2 && l == linha) {
                        System.out.println("[DEBUG ControladorJogo] *** ROQUE DETECTADO como movimento válido para (" + 
                                         l + "," + c + ") ***");
                    }
                }
            }
        }
        
        System.out.println("[DEBUG ControladorJogo] Total de movimentos possíveis: " + movimentos.size());
        
        // Debug - listar movimentos de roque especificamente
        if (infoPeca != null && infoPeca.contains("king")) {
            System.out.println("[DEBUG ControladorJogo] Movimentos de roque encontrados:");
            boolean encontrouRoque = false;
            for (int[] mov : movimentos) {
                if (Math.abs(mov[1] - coluna) == 2 && mov[0] == linha) {
                    System.out.println("[DEBUG ControladorJogo]   - Roque para: (" + 
                                     mov[0] + "," + mov[1] + ")");
                    encontrouRoque = true;
                }
            }
            if (!encontrouRoque) {
                System.out.println("[DEBUG ControladorJogo]   - Nenhum movimento de roque disponível");
            }
        }
        System.out.println("[DEBUG ControladorJogo] ========================================\n");
        
        return movimentos;
    }
    
    private void limparSelecao() {
        linhaSelecionada = -1;
        colunaSelecionada = -1;
        movimentosPossiveis = new ArrayList<>();
        fachadaView.limparSelecao();
    }
    
    private boolean precisaPromocao(int linha, int coluna) {
        Peca peca = jogoXadrez.getTabuleiro().getPeca(linha, coluna);
        
        if (peca instanceof Peao) {
            // Peão branco na linha 0 ou peão preto na linha 7
            return (peca.getCor() == Cor.BRANCO && linha == 0) ||
                   (peca.getCor() == Cor.PRETO && linha == 7);
        }
        
        return false;
    }
    
    public void promoverPeao(int linha, int coluna, String escolha) {
        Peca novaPeca = null;
        Cor cor = jogoXadrez.getTabuleiro().getPeca(linha, coluna).getCor();
        
        switch (escolha) {
            case "Rainha":
                novaPeca = new Rainha(cor, linha, coluna);
                break;
            case "Torre":
                novaPeca = new Torre(cor, linha, coluna);
                break;
            case "Bispo":
                novaPeca = new Bispo(cor, linha, coluna);
                break;
            case "Cavalo":
                novaPeca = new Cavalo(cor, linha, coluna);
                break;
        }
        
        if (novaPeca != null) {
            jogoXadrez.getTabuleiro().setPeca(linha, coluna, novaPeca);
            // Forçar atualização da view
            jogoXadrez.sincronizarView();
        }
    }
    
    private void verificarXeque() {
        if (jogoXadrez.estaEmXeque(jogoXadrez.getJogadorAtual())) {
            // Encontrar posição do rei
            int[] posRei = encontrarRei(jogoXadrez.getJogadorAtual());
            if (posRei != null) {
                fachadaView.destacarReiEmXeque(posRei[0], posRei[1]);
            }
        } else {
            fachadaView.limparXeque();
        }
    }
    
    private int[] encontrarRei(Cor cor) {
        Tabuleiro tab = jogoXadrez.getTabuleiro();
        for (int linha = 0; linha < 8; linha++) {
            for (int coluna = 0; coluna < 8; coluna++) {
                Peca peca = tab.getPeca(linha, coluna);
                if (peca instanceof Rei && peca.getCor() == cor) {
                    return new int[]{linha, coluna};
                }
            }
        }
        return null;
    }
    
    private void verificarFimDeJogo() {
        if (jogoXadrez.estaEmXequeMate(jogoXadrez.getJogadorAtual())) {
            String vencedor = jogoXadrez.getJogadorAtual() == Cor.BRANCO ? "Pretas" : "Brancas";
            fachadaView.mostrarFimDeJogo("Xeque-mate! Vitória das peças " + vencedor);
        } else if (jogoXadrez.estaEmEmpate()) {
            fachadaView.mostrarFimDeJogo("Empate! O jogador não pode fazer movimentos legais.");
        }
    }
    
    public String obterInfoPeca(int linha, int coluna) {
        if (jogoXadrez == null) return null;
        
        Peca peca = jogoXadrez.getTabuleiro().getPeca(linha, coluna);
        if (peca == null) return null;
        
        // Retornar no formato esperado pelas imagens: "cor_tipo"
        String cor = peca.getCor() == Cor.BRANCO ? "white" : "black";
        String tipo = "";
        
        if (peca instanceof Rei) tipo = "king";
        else if (peca instanceof Rainha) tipo = "queen";
        else if (peca instanceof Torre) tipo = "rook";
        else if (peca instanceof Bispo) tipo = "bishop";
        else if (peca instanceof Cavalo) tipo = "knight";
        else if (peca instanceof Peao) tipo = "pawn";
        
        return cor + "_" + tipo;
    }
    
    public boolean temPecaAdversaria(int linha, int coluna) {
        if (jogoXadrez == null) return false;
        
        Peca peca = jogoXadrez.getTabuleiro().getPeca(linha, coluna);
        return peca != null && peca.getCor() != jogoXadrez.getJogadorAtual();
    }
    
    public void salvarPartida() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar Partida");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Arquivos de texto", "txt"));
        
        int resultado = fileChooser.showSaveDialog(janelaPrincipal);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File arquivo = fileChooser.getSelectedFile();
            if (!arquivo.getName().endsWith(".txt")) {
                arquivo = new File(arquivo.getAbsolutePath() + ".txt");
            }
            
            //DE ACORDO COM O PDF -->  funcionalidade será implementada na 4ª iteração
            JOptionPane.showMessageDialog(janelaPrincipal, 
                "Partida será salva em: " + arquivo.getAbsolutePath() + 
                "\n(Implementação completa na 4ª iteração)");
        }
    }
    
    public void encerrarPartida() {
        int resposta = JOptionPane.showConfirmDialog(janelaPrincipal,
            "Deseja encerrar a partida sem salvar?",
            "Encerrar Partida",
            JOptionPane.YES_NO_OPTION);
            
        if (resposta == JOptionPane.YES_OPTION) {
            fachadaView.mostrarTelaInicial();
        }
    }
    
    public void requestNewGame() {
        int resposta = JOptionPane.showConfirmDialog(janelaPrincipal,
            "Deseja iniciar uma nova partida?",
            "Nova Partida",
            JOptionPane.YES_NO_OPTION);
            
        if (resposta == JOptionPane.YES_OPTION) {
            iniciarNovaPartida();
            fachadaView.mostrarTabuleiro();
        }
    }
    
    // Métodos adicionais para o PainelInformacoes
    public Cor getJogadorAtual() {
        return jogoXadrez != null ? jogoXadrez.getJogadorAtual() : null;
    }
    
    public int getNumeroJogada() {
        return jogoXadrez != null ? jogoXadrez.getHistorico().getNumeroJogada() : 1;
    }
    
    public String getHistoricoPGN() {
        return jogoXadrez != null ? jogoXadrez.getPGN() : "";
    }
    
    public String getStatusJogo() {
        if (jogoXadrez == null) return null;
        
        if (jogoXadrez.estaEmXequeMate(jogoXadrez.getJogadorAtual())) {
            return "XEQUE-MATE!";
        } else if (jogoXadrez.estaEmXeque(jogoXadrez.getJogadorAtual())) {
            return "XEQUE!";
        } else if (jogoXadrez.estaEmEmpate()) {
            return "EMPATE!";
        }
        
        return null;
    }
}
