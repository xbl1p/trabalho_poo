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
/**
 * TabuleiroGrafico herda de JPanel apenas para ter acesso ao contexto Graphics2D.
 * Nenhum componente Swing é usado na implementação - todo desenho é feito
 * diretamente via métodos fill(), draw() e drawImage() conforme especificado.
 */
public class TabuleiroGrafico extends JPanel implements MouseListener {
    private static final long serialVersionUID = 1L;
    
    private static final int TAMANHO_CASA = 80;
    private static final int TAMANHO_TABULEIRO = 8;
    private static final Color COR_CLARA = new Color(240, 217, 181);
    private static final Color COR_ESCURA = new Color(181, 136, 99);
    private static final Color COR_SELECAO = new Color(255, 255, 0, 100);
    private static final Color COR_MOVIMENTO_POSSIVEL = new Color(0, 255, 0, 80);
    private static final Color COR_XEQUE = new Color(255, 0, 0, 100);
    
    private ControladorJogo controlador;
    private Map<String, BufferedImage> imagensPecas;
    private int linhaSelecionada = -1;
    private int colunaSelecionada = -1;
    private List<int[]> movimentosPossiveis;
    private boolean reiEmXeque = false;
    private int linhaReiXeque = -1;
    private int colunaReiXeque = -1;
    
    // Mapeamento  para imagens Cyan/Purple
    private static final Map<String, String> MAPEAMENTO_PECAS = new HashMap<>();
    static {
        // Mapeamento: tipo_da_peça -> letra_do_arquivo
        MAPEAMENTO_PECAS.put("king", "K");     // Rei
        MAPEAMENTO_PECAS.put("queen", "Q");    // Rainha  
        MAPEAMENTO_PECAS.put("rook", "R");     // Torre
        MAPEAMENTO_PECAS.put("bishop", "B");   // Bispo
        MAPEAMENTO_PECAS.put("knight", "N");   // Cavalo 
        MAPEAMENTO_PECAS.put("pawn", "P");     // Peão
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
    
   
    private void carregarImagens() {
        String[] cores = {"Cyan", "Purple"};  
        String[] tiposPecas = {"king", "queen", "rook", "bishop", "knight", "pawn"};
        
        System.out.println(" Carregando imagens das peças...");
        
        for (String cor : cores) {
            for (String tipo : tiposPecas) {
                try {
                    
                    String letraPeca = MAPEAMENTO_PECAS.get(tipo);
                    String nomeArquivo = "images/" + cor + letraPeca + ".png";
                    File arquivo = new File(nomeArquivo);
                    
                    if (arquivo.exists()) {
                        BufferedImage img = ImageIO.read(arquivo);
                        
                        // Redimensionar imagem para caber na casa
                        Image imgRedimensionada = img.getScaledInstance(
                            TAMANHO_CASA - 10, TAMANHO_CASA - 10, 
                            Image.SCALE_SMOOTH
                        );
                        
                        // Converter de volta para BufferedImage
                        BufferedImage imgFinal = new BufferedImage(
                            TAMANHO_CASA - 10, TAMANHO_CASA - 10,
                            BufferedImage.TYPE_INT_ARGB
                        );
                        Graphics2D g2d = imgFinal.createGraphics();
                        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
                                           RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                        g2d.drawImage(imgRedimensionada, 0, 0, null);
                        g2d.dispose();
                        
                        //  Mapear para o formato esperado pelo ControladorJogo
                        String corPadrao = cor.equals("Cyan") ? "white" : "black";
                        String chaveImagem = corPadrao + "_" + tipo;
                        imagensPecas.put(chaveImagem, imgFinal);
                        
                        System.out.println("OK Carregada: " + nomeArquivo + " -> " + chaveImagem);
                    } else {
                        System.err.println("X Arquivo não encontrado: " + nomeArquivo);
                    }
                } catch (IOException e) {
                    System.err.println("X Erro ao carregar " + cor + " " + tipo + ": " + e.getMessage());
                }
            }
        }
        
        System.out.println(" Carregamento concluído. " + imagensPecas.size() + " imagens carregadas.");
        
        //  Verificar se todas as imagens necessárias foram carregadas
        verificarImagensCarregadas();
    }
    
    // Método para verificar se todas as imagens foram carregadas
    private void verificarImagensCarregadas() {
        String[] cores = {"white", "black"};
        String[] tipos = {"king", "queen", "rook", "bishop", "knight", "pawn"};
        
        boolean todasCarregadas = true;
        for (String cor : cores) {
            for (String tipo : tipos) {
                String chave = cor + "_" + tipo;
                if (!imagensPecas.containsKey(chave)) {
                    System.err.println("!! Imagem faltando: " + chave);
                    todasCarregadas = false;
                }
            }
        }
        
        if (todasCarregadas) {
            System.out.println("OK Todas as imagens foram carregadas com sucesso!");
        } else {
            System.out.println("!! Algumas imagens não foram encontradas. Símbolos Unicode serão usados como fallback.");
        }
    }
    
    private void configurarMenuContexto() {
        JPopupMenu menuContexto = new JPopupMenu();
        
        JMenuItem itemSalvar = new JMenuItem("Salvar Partida");
        itemSalvar.addActionListener(e -> {
            if (controlador != null) {
                controlador.salvarPartida();
            }
        });
        menuContexto.add(itemSalvar);
        
        //  tratamento do menu de contexto
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    mostrarMenuContexto(e, menuContexto);
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    mostrarMenuContexto(e, menuContexto);
                }
            }
            
            private void mostrarMenuContexto(MouseEvent e, JPopupMenu menu) {
                // Verificar se o clique foi dentro do tabuleiro
                int coluna = e.getX() / TAMANHO_CASA;
                int linha = e.getY() / TAMANHO_CASA;
                
                if (linha >= 0 && linha < TAMANHO_TABULEIRO && 
                    coluna >= 0 && coluna < TAMANHO_TABULEIRO) {
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        //  Melhor qualidade de renderização
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                            RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        // Desenhar tabuleiro
        desenharTabuleiro(g2d);
        
        // Destacar rei em xeque
        if (reiEmXeque) {
            destacarXeque(g2d);
        }
        
        // Destacar casa selecionada
        if (linhaSelecionada >= 0 && colunaSelecionada >= 0) {
            destacarCasaSelecionada(g2d);
        }
        
        // Destacar movimentos possíveis
        if (movimentosPossiveis != null && !movimentosPossiveis.isEmpty()) {
            destacarMovimentosPossiveis(g2d);
        }
        
        // Desenhar peças
        desenharPecas(g2d);
    }
    
    private void desenharTabuleiro(Graphics2D g2d) {
        for (int linha = 0; linha < TAMANHO_TABULEIRO; linha++) {
            for (int coluna = 0; coluna < TAMANHO_TABULEIRO; coluna++) {
                // Alternar cores
                Color cor = (linha + coluna) % 2 == 0 ? COR_CLARA : COR_ESCURA;
                g2d.setColor(cor);
                
                // Desenhar casa
                int x = coluna * TAMANHO_CASA;
                int y = linha * TAMANHO_CASA;
                g2d.fillRect(x, y, TAMANHO_CASA, TAMANHO_CASA);
                
                // Desenhar borda
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
        for (int[] pos : movimentosPossiveis) {
            int linha = pos[0];
            int coluna = pos[1];
            
            //  Verificação de segurança
            if (controlador != null && controlador.temPecaAdversaria(linha, coluna)) {
                // Desenhar borda vermelha para captura
                g2d.setColor(new Color(255, 0, 0, 120));
                g2d.setStroke(new BasicStroke(4));
                g2d.drawRect(
                    coluna * TAMANHO_CASA + 2,
                    linha * TAMANHO_CASA + 2,
                    TAMANHO_CASA - 4,
                    TAMANHO_CASA - 4
                );
            } else {
                // Desenhar círculo verde para movimento
                g2d.setColor(COR_MOVIMENTO_POSSIVEL);
                int centroX = coluna * TAMANHO_CASA + TAMANHO_CASA / 2;
                int centroY = linha * TAMANHO_CASA + TAMANHO_CASA / 2;
                g2d.fillOval(centroX - 15, centroY - 15, 30, 30);
            }
        }
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
        //  Verificação de segurança do controlador
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
                        // Se não tiver imagem, desenhar texto
                        desenharPecaTexto(g2d, linha, coluna, infoPeca);
                    }
                }
            }
        }
    }
    
    //  Melhor fallback para símbolos Unicode
    private void desenharPecaTexto(Graphics2D g2d, int linha, int coluna, String info) {
        try {
            String[] partes = info.split("_");
            if (partes.length != 2) return;
            
            String cor = partes[0];
            String tipo = partes[1];
            
            // Configurar fonte
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
            
            // Desenhar símbolo colorido
            g2d.setColor(cor.equals("white") ? Color.WHITE : new Color(75, 0, 130)); // Branco ou Roxo
            g2d.drawString(simbolo, x, y);
            
        } catch (Exception e) {
            // Fallback final - desenhar um quadrado colorido
            g2d.setColor(info.startsWith("white") ? Color.LIGHT_GRAY : Color.DARK_GRAY);
            g2d.fillRect(
                coluna * TAMANHO_CASA + 20, 
                linha * TAMANHO_CASA + 20,
                TAMANHO_CASA - 40, 
                TAMANHO_CASA - 40
            );
        }
    }
    
    //  Símbolos Unicode 
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
        // Verificação de segurança
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
        //  Verificação de null
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
        //  Verificação de segurança do controlador
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
        
        // Mostrar popup na posição do peão
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