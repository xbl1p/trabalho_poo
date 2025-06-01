// ===================== ControladorJogo.java CORRIGIDO =====================
package controller;

import model.*;
import view.*;
import javax.swing.*;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class ControladorJogo {
    private JanelaPrincipal janelaPrincipal;
    private TabuleiroGrafico tabuleiroGrafico;
    private JogoXadrez jogoXadrez;
    
    // Estado da seleção
    private int linhaSelecionada = -1;
    private int colunaSelecionada = -1;
    
    public ControladorJogo(JanelaPrincipal janela) {
        this.janelaPrincipal = janela;
    }
    
    public void iniciarNovaPartida() {
        jogoXadrez = JogoXadrez.novaInstancia();
        
        // Resetar seleção
        linhaSelecionada = -1;
        colunaSelecionada = -1;
        
        // Se já tem tabuleiro gráfico, atualizar
        if (tabuleiroGrafico != null) {
            tabuleiroGrafico.atualizar();
        }
    }
    
    public void setTabuleiroGrafico(TabuleiroGrafico tabuleiro) {
        this.tabuleiroGrafico = tabuleiro;
    }
    
    public void processarClique(int linha, int coluna) {
        if (jogoXadrez == null) return;
        
        // Se não tem peça selecionada
        if (linhaSelecionada == -1) {
            // Verificar se há peça na posição clicada
            Peca peca = jogoXadrez.getTabuleiro().getPeca(linha, coluna);
            
            if (peca != null && peca.getCor() == jogoXadrez.getJogadorAtual()) {
                // Selecionar peça
                linhaSelecionada = linha;
                colunaSelecionada = coluna;
                
                // Mostrar casa selecionada
                tabuleiroGrafico.selecionarCasa(linha, coluna);
                
                // Calcular e mostrar movimentos possíveis
                List<int[]> movimentos = calcularMovimentosPossiveis(linha, coluna);
                tabuleiroGrafico.mostrarMovimentosPossiveis(movimentos);
            }
        } else {
            // Já tem peça selecionada - tentar mover
            
            // Verificar se clicou na mesma peça (deselecionar)
            if (linha == linhaSelecionada && coluna == colunaSelecionada) {
                limparSelecao();
                return;
            }
            
            // Verificar se clicou em outra peça do mesmo jogador
            Peca pecaDestino = jogoXadrez.getTabuleiro().getPeca(linha, coluna);
            if (pecaDestino != null && pecaDestino.getCor() == jogoXadrez.getJogadorAtual()) {
                // Selecionar nova peça
                linhaSelecionada = linha;
                colunaSelecionada = coluna;
                tabuleiroGrafico.selecionarCasa(linha, coluna);
                
                List<int[]> movimentos = calcularMovimentosPossiveis(linha, coluna);
                tabuleiroGrafico.mostrarMovimentosPossiveis(movimentos);
                return;
            }
            
            // Tentar fazer o movimento
            boolean movimentoValido = jogoXadrez.moverPeca(
                linhaSelecionada, colunaSelecionada, linha, coluna
            );
            
            if (movimentoValido) {
                // Verificar promoção de peão
                if (precisaPromocao(linha, coluna)) {
                    tabuleiroGrafico.mostrarPromocaoPeao(linha, coluna);
                }
                
                // Limpar seleção
                limparSelecao();
                
                // Atualizar tabuleiro
                tabuleiroGrafico.atualizar();
                
                // Verificar xeque
                verificarXeque();
                
                // Verificar fim de jogo
                verificarFimDeJogo();
            } else {
                // Movimento inválido - limpar seleção
                limparSelecao();
            }
        }
    }
    
    private List<int[]> calcularMovimentosPossiveis(int linha, int coluna) {
        List<int[]> movimentos = new ArrayList<>();
        
        // usar movimentoValido() em vez de método inexistente
        for (int l = 0; l < 8; l++) {
            for (int c = 0; c < 8; c++) {
                if (jogoXadrez.movimentoValido(linha, coluna, l, c)) {
                    movimentos.add(new int[]{l, c});
                }
            }
        }
        
        return movimentos;
    }
    
    private void limparSelecao() {
        linhaSelecionada = -1;
        colunaSelecionada = -1;
        tabuleiroGrafico.limparSelecao();
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
            tabuleiroGrafico.atualizar();
        }
    }
    
    private void verificarXeque() {
        if (jogoXadrez.estaEmXeque(jogoXadrez.getJogadorAtual())) {
            // Encontrar posição do rei
            int[] posRei = encontrarRei(jogoXadrez.getJogadorAtual());
            if (posRei != null) {
                tabuleiroGrafico.destacarReiEmXeque(posRei[0], posRei[1]);
            }
        } else {
            tabuleiroGrafico.limparXeque();
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
            janelaPrincipal.mostrarFimDeJogo("Xeque-mate! Vitória das peças " + vencedor);
        } else if (jogoXadrez.estaEmEmpate()) {
            janelaPrincipal.mostrarFimDeJogo("Empate! O jogador não pode fazer movimentos legais.");
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
    
    public void requestNewGame() {
        int resposta = JOptionPane.showConfirmDialog(janelaPrincipal,
            "Deseja iniciar uma nova partida?",
            "Nova Partida",
            JOptionPane.YES_NO_OPTION);
            
        if (resposta == JOptionPane.YES_OPTION) {
            iniciarNovaPartida();
            janelaPrincipal.mostrarTabuleiro();
        }
    }
}