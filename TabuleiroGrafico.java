package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import controller.ControladorJogo;
import util.ObservadorIF;
import util.ObservadoIF;

public class TabuleiroGrafico extends JPanel implements MouseListener, ObservadorIF {
    private static final long serialVersionUID = 1L;
    
    private static final int TAMANHO_CASA = 80;
    private static final int TAMANHO_TABULEIRO = 8;
    private static final Color COR_CLARA = new Color(240, 217, 181);
    private static final Color COR_ESCURA = new Color(181, 136, 99);
    private static final Color COR_SELECAO = new Color(255, 255, 0, 100);
    private static final Color COR_MOVIMENTO_POSSIVEL = new Color(0, 255, 0, 80);
    private static final Color COR_XEQUE = new Color(255, 0, 0, 100);
    private static final Color COR_ROQUE = new Color(0, 0, 255, 120); // Nova cor para roque
    
    private ControladorJogo controlador;
    private Map<String, BufferedImage> imagensPecas;
    private int linhaSelecionada = -1;
    private int colunaSelecionada = -1;
    private List<int[]> movimentosPossiveis;
    private boolean reiEmXeque = false;
    private int linhaReiXeque = -1;
    private int colunaReiXeque = -1;
    
    // Mapeamento para imagens Cyan/Purple
    private static final Map<String, String> MAPEAMENTO_PECAS = new HashMap<>();
    static {
        MAPEAMENTO_PECAS.put("king", "K");
        MAPEAMENTO_PECAS.put("queen", "Q");  
        MAPEAMENTO_PECAS.put("rook", "R");
        MAPEAMENTO_PECAS.put("bishop", "B");
        MAPEAMENTO_PECAS.put("knight", "N"); 
        MAPEAMENTO_PECAS.put("pawn", "P");
    }
    
    public TabuleiroGrafico(ControladorJogo controlador) {
        this.controlador = controlador;
        this.imagensPecas = new HashMap<>();
        this.movimentosPossiveis = new ArrayList<>();
        
        setPreferredSize(new Dimension(
            TAMANHO_CASA * TAMANHO_TABULEIRO,
            TAMANHO_CASA * TAMANHO_TABULEIRO
        ));
        
        addMouseListener(this);
        setBackground(Color.WHITE);
        
        carregarImagens();
        configurarMenuContexto();
    }
    
    @Override
    public void notify(ObservadoIF o) {
        System.out.println("[DEBUG TabuleiroGrafico] Recebeu notificação do modelo");
        atualizar();
    }
   
    private void carregarImagens() {
        String[] cores = {"Cyan", "Purple"};  
        String[] tiposPecas = {"king", "queen", "rook", "bishop", "knight", "pawn"};
        
        for (String cor : cores) {
            for (String tipo : tiposPecas) {
                try {
                    String letraPeca = MAPEAMENTO_PECAS.get(tipo);
                    String nomeArquivo = "src/resources/images/" + cor + letraPeca + ".png";
                    File arquivo = new File(nomeArquivo);
                    
                    if (arquivo.exists()) {
                        BufferedImage img = ImageIO.read(arquivo);
                        
                        // Redimensionar imagem
                        Image imgRedimensionada = img.getScaledInstance(
                            TAMANHO_CASA - 10, TAMANHO_CASA - 10, 
                            Image.SCALE_SMOOTH
                        );
                        
                        BufferedImage imgFinal = new BufferedImage(
                            TAMANHO_CASA - 10, TAMANHO_CASA - 10,
                            BufferedImage.TYPE_INT_ARGB
                        );
                        Graphics2D g2d = imgFinal.createGraphics();
                        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
                                           RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                        g2d.drawImage(imgRedimensionada, 0, 0, null);
                        g2d.dispose();
                        
                        String corPadrao = cor.equals("Cyan") ? "white" : "black";
                        String chaveImagem = corPadrao + "_" + tipo;
                        imagensPecas.put(chaveImagem, imgFinal);
                        
                    } else {
                        System.err.println("❌ Arquivo não encontrado: " + nomeArquivo);
                    }
                } catch (IOException e) {
                    System.err.println("❌ Erro ao carregar " + cor + " " + tipo + ": " + e.getMessage());
                }
            }
        }
    }
    
    private void configurarMenuContexto() {
        // ... (código existente)
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        desenharTabuleiro(g2d);
        
        if (reiEmXeque) {
            destacarXeque(g2d);
        }
        
        if (linhaSelecionada >= 0 && colunaSelecionada >= 0) {
            destacarCasaSelecionada(g2d);
        }
        
        if (movimentosPossiveis != null && !movimentosPossiveis.isEmpty()) {
            destacarMovimentosPossiveis(g2d);
        }
        
        desenharPecas(g2d);
    }
    
    private void desenharTabuleiro(Graphics2D g2d) {
        for (int linha = 0; linha < TAMANHO_TABULEIRO; linha++) {
            for (int coluna = 0; coluna < TAMANHO_TABULEIRO; coluna++) {
                Color cor = (linha + coluna) % 2 == 0 ? COR_CLARA : COR_ESCURA;
                g2d.setColor(cor);
                
                int x = coluna * TAMANHO_CASA;
                int y = linha * TAMANHO_CASA;
                g2d.fillRect(x, y, TAMANHO_CASA, TAMANHO_CASA);
                
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRect(x, y, TAMANHO_CASA, TAMANHO_CASA);
            }
        }
    }
    
    private void destacarCasaSelecionada(Graphics2D g2d) {
        g2d.setColor(COR_SELECAO);
        g2d.fillRect(
            colunaSelecionada * TAMANHO_CASA,
            linhaSelecionada * TAMANHO_CASA,
            TAMANHO_CASA,
            TAMANHO_CASA
        );
    }
    
    private void destacarMovimentosPossiveis(Graphics2D g2d) {
        System.out.println("\n[DEBUG TabuleiroGrafico] ========================================");
        System.out.println("[DEBUG TabuleiroGrafico] Destacando " + movimentosPossiveis.size() + " movimentos");
        
        // Debug - verificar peça selecionada
        if (linhaSelecionada >= 0 && colunaSelecionada >= 0) {
            String infoPeca = controlador.obterInfoPeca(linhaSelecionada, colunaSelecionada);
            System.out.println("[DEBUG TabuleiroGrafico] Peça selecionada: " + infoPeca + 
                             " em (" + linhaSelecionada + "," + colunaSelecionada + ")");
        }
        
        int roqueCount = 0;
        for (int[] pos : movimentosPossiveis) {
            int linha = pos[0];
            int coluna = pos[1];
            
            // Verificar se é um movimento de roque (rei movendo 2 casas horizontalmente)
            if (isRoque(linha, coluna)) {
                System.out.println("[DEBUG TabuleiroGrafico] ROQUE DETECTADO para (" + linha + "," + coluna + ")!");
                destacarRoque(g2d, linha, coluna);
                roqueCount++;
            }
            // Verificar se é captura
            else if (controlador != null && controlador.temPecaAdversaria(linha, coluna)) {
                destacarCaptura(g2d, linha, coluna);
            }
            // Movimento normal
            else {
                destacarMovimentoNormal(g2d, linha, coluna);
            }
        }
        
        System.out.println("[DEBUG TabuleiroGrafico] Total de roques destacados: " + roqueCount);
        System.out.println("[DEBUG TabuleiroGrafico] ========================================\n");
    }
    
    private boolean isRoque(int linhaDestino, int colunaDestino) {
        if (linhaSelecionada == -1 || colunaSelecionada == -1 || controlador == null) {
            return false;
        }
        
        // Obter informação da peça selecionada
        String infoPeca = controlador.obterInfoPeca(linhaSelecionada, colunaSelecionada);
        
        // Verificar se é um rei
        if (infoPeca == null || !infoPeca.contains("king")) {
            return false;
        }
        
        // Calcular diferenças
        int diffLinha = Math.abs(linhaDestino - linhaSelecionada);
        int diffColuna = Math.abs(colunaDestino - colunaSelecionada);
        
        // É roque apenas se: movimento horizontal (mesma linha) e 2 casas
        boolean resultado = diffLinha == 0 && diffColuna == 2;
        
        if (resultado) {
            System.out.println("[DEBUG isRoque] ROQUE CONFIRMADO - destino: (" + 
                             linhaDestino + "," + colunaDestino + ")");
        }
        
        return resultado;
    }
    
    private void destacarRoque(Graphics2D g2d, int linha, int coluna) {
        System.out.println("[DEBUG destacarRoque] Desenhando roque em (" + linha + "," + coluna + ")");
        
        // Destaque especial para roque (retângulo azul grosso)
        g2d.setColor(COR_ROQUE);
        g2d.setStroke(new BasicStroke(6)); // Linha mais grossa
        
        // Desenhar borda azul
        g2d.drawRect(
            coluna * TAMANHO_CASA + 3,
            linha * TAMANHO_CASA + 3,
            TAMANHO_CASA - 6,
            TAMANHO_CASA - 6
        );
        
        // Preencher com azul semi-transparente
        g2d.fillRect(
            coluna * TAMANHO_CASA + 5,
            linha * TAMANHO_CASA + 5,
            TAMANHO_CASA - 10,
            TAMANHO_CASA - 10
        );
        
        // Adicionar texto "Roque" em branco para contraste
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Centralizar texto
        FontMetrics fm = g2d.getFontMetrics();
        String texto = "ROQUE";
        int textoX = coluna * TAMANHO_CASA + (TAMANHO_CASA - fm.stringWidth(texto)) / 2;
        int textoY = linha * TAMANHO_CASA + TAMANHO_CASA - 10;
        
        // Desenhar sombra do texto
        g2d.setColor(Color.BLACK);
        g2d.drawString(texto, textoX + 1, textoY + 1);
        
        // Desenhar texto
        g2d.setColor(Color.WHITE);
        g2d.drawString(texto, textoX, textoY);
    }
    
    private void destacarCaptura(Graphics2D g2d, int linha, int coluna) {
        g2d.setColor(new Color(255, 0, 0, 120));
        g2d.setStroke(new BasicStroke(4));
        g2d.drawRect(
            coluna * TAMANHO_CASA + 2,
            linha * TAMANHO_CASA + 2,
            TAMANHO_CASA - 4,
            TAMANHO_CASA - 4
        );
    }
    
    private void destacarMovimentoNormal(Graphics2D g2d, int linha, int coluna) {
        g2d.setColor(COR_MOVIMENTO_POSSIVEL);
        int centroX = coluna * TAMANHO_CASA + TAMANHO_CASA / 2;
        int centroY = linha * TAMANHO_CASA + TAMANHO_CASA / 2;
        g2d.fillOval(centroX - 15, centroY - 15, 30, 30);
    }
    
    private void destacarXeque(Graphics2D g2d) {
        g2d.setColor(COR_XEQUE);
        g2d.fillRect(
            colunaReiXeque * TAMANHO_CASA,
            linhaReiXeque * TAMANHO_CASA,
            TAMANHO_CASA,
            TAMANHO_CASA
        );
    }
    
    private void desenharPecas(Graphics2D g2d) {
        if (controlador == null) return;
        
        for (int linha = 0; linha < TAMANHO_TABULEIRO; linha++) {
            for (int coluna = 0; coluna < TAMANHO_TABULEIRO; coluna++) {
                String infoPeca = controlador.obterInfoPeca(linha, coluna);
                
                if (infoPeca != null && !infoPeca.isEmpty()) {
                    BufferedImage imgPeca = imagensPecas.get(infoPeca);
                    
                    if (imgPeca != null) {
                        int x = coluna * TAMANHO_CASA + 5;
                        int y = linha * TAMANHO_CASA + 5;
                        g2d.drawImage(imgPeca, x, y, null);
                    } else {
                        desenharPecaTexto(g2d, linha, coluna, infoPeca);
                    }
                }
            }
        }
    }
    
    private void desenharPecaTexto(Graphics2D g2d, int linha, int coluna, String info) {
        try {
            String[] partes = info.split("_");
            if (partes.length != 2) return;
            
            String cor = partes[0];
            String tipo = partes[1];
            
            g2d.setFont(new Font("Arial Unicode MS", Font.BOLD, 30));
            
            String simbolo = obterSimboloPeca(tipo, cor.equals("white"));
            
            int x = coluna * TAMANHO_CASA + TAMANHO_CASA / 2 - 15;
            int y = linha * TAMANHO_CASA + TAMANHO_CASA / 2 + 10;
            
            // Desenhar contorno preto
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx != 0 || dy != 0) {
                        g2d.drawString(simbolo, x + dx, y + dy);
                    }
                }
            }
            
            g2d.setColor(cor.equals("white") ? Color.WHITE : new Color(75, 0, 130));
            g2d.drawString(simbolo, x, y);
            
        } catch (Exception e) {
            g2d.setColor(info.startsWith("white") ? Color.LIGHT_GRAY : Color.DARK_GRAY);
            g2d.fillRect(
                coluna * TAMANHO_CASA + 20, 
                linha * TAMANHO_CASA + 20,
                TAMANHO_CASA - 40, 
                TAMANHO_CASA - 40
            );
        }
    }
    
    private String obterSimboloPeca(String tipo, boolean branca) {
        switch (tipo) {
            case "king":   return branca ? "♔" : "♚";
            case "queen":  return branca ? "♕" : "♛";
            case "rook":   return branca ? "♖" : "♜";
            case "bishop": return branca ? "♗" : "♝";
            case "knight": return branca ? "♘" : "♞";
            case "pawn":   return branca ? "♙" : "♟";
            default:       return "?";
        }
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        if (controlador == null) return;
        
        if (SwingUtilities.isLeftMouseButton(e)) {
            int coluna = e.getX() / TAMANHO_CASA;
            int linha = e.getY() / TAMANHO_CASA;
            
            if (linha >= 0 && linha < TAMANHO_TABULEIRO && 
                coluna >= 0 && coluna < TAMANHO_TABULEIRO) {
                controlador.processarClique(linha, coluna);
            }
        }
    }
    
    // Métodos para atualizar o estado visual
    public void selecionarCasa(int linha, int coluna) {
        this.linhaSelecionada = linha;
        this.colunaSelecionada = coluna;
        repaint();
    }
    
    public void mostrarMovimentosPossiveis(List<int[]> movimentos) {
        System.out.println("[DEBUG mostrarMovimentosPossiveis] Recebendo " + 
                         (movimentos != null ? movimentos.size() : 0) + " movimentos");
        
        if (movimentos != null) {
            this.movimentosPossiveis = new ArrayList<>(movimentos);
        } else {
            this.movimentosPossiveis = new ArrayList<>();
        }
        repaint();
    }
    
    public void limparSelecao() {
        this.linhaSelecionada = -1;
        this.colunaSelecionada = -1;
        if (this.movimentosPossiveis != null) {
            this.movimentosPossiveis.clear();
        }
        repaint();
    }
    
    public void destacarReiEmXeque(int linha, int coluna) {
        this.reiEmXeque = true;
        this.linhaReiXeque = linha;
        this.colunaReiXeque = coluna;
        repaint();
    }
    
    public void limparXeque() {
        this.reiEmXeque = false;
        repaint();
    }
    
    public void mostrarPromocaoPeao(int linha, int coluna) {
        if (controlador == null) return;
        
        String[] opcoes = {"Rainha", "Torre", "Bispo", "Cavalo"};
        
        JPopupMenu popup = new JPopupMenu();
        for (String opcao : opcoes) {
            JMenuItem item = new JMenuItem(opcao);
            item.addActionListener(e -> {
                controlador.promoverPeao(linha, coluna, opcao);
                popup.setVisible(false);
            });
            popup.add(item);
        }
        
        int x = coluna * TAMANHO_CASA;
        int y = linha * TAMANHO_CASA;
        popup.show(this, x, y);
    }
    
    public void atualizar() {
        repaint();
    }
    
    // Métodos não utilizados do MouseListener
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}
