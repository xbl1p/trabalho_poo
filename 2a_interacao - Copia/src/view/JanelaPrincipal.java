package view;

import javax.swing.*;
import java.awt.*;
import controller.ControladorJogo;

public class JanelaPrincipal extends JFrame {
    private static final long serialVersionUID = 1L;
    private ControladorJogo controlador;
    private TabuleiroGrafico tabuleiroGrafico;
    
    public JanelaPrincipal() {
        super("Jogo de Xadrez - INF1636");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Criar controlador
        controlador = new ControladorJogo(this);
        
        // Mostrar tela inicial
        mostrarTelaInicial();
        
        // Centralizar janela
        setLocationRelativeTo(null);
    }
    
    private void mostrarTelaInicial() {
        // Painel principal
        JPanel painelInicial = new JPanel();
        painelInicial.setLayout(new BorderLayout());
        painelInicial.setBackground(new Color(50, 50, 50));
        
        // Painel central com botões
        JPanel painelCentral = new JPanel();
        painelCentral.setLayout(new GridBagLayout());
        painelCentral.setOpaque(false);
        
        // Título
        JLabel titulo = new JLabel("JOGO DE XADREZ");
        titulo.setFont(new Font("Arial", Font.BOLD, 36));
        titulo.setForeground(Color.WHITE);
        
        // Botões
        JButton btnNovaPartida = criarBotao("Nova Partida");
        JButton btnCarregar = criarBotao("Carregar Partida");
        
        // Ações dos botões
        btnNovaPartida.addActionListener(e -> iniciarNovaPartida());
        btnCarregar.addActionListener(e -> carregarPartida());
        
        // Layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 0, 40, 0);
        painelCentral.add(titulo, gbc);
        
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 10, 0);
        painelCentral.add(btnNovaPartida, gbc);
        
        gbc.gridy = 2;
        painelCentral.add(btnCarregar, gbc);
        
        painelInicial.add(painelCentral, BorderLayout.CENTER);
        
        // Definir conteúdo
        setContentPane(painelInicial);
        
        // Adicionado pack para ajustar ao conteúdo
        pack();
    }
    
    private JButton criarBotao(String texto) {
        JButton botao = new JButton(texto);
        botao.setPreferredSize(new Dimension(200, 50));
        botao.setFont(new Font("Arial", Font.PLAIN, 18));
        botao.setFocusPainted(false);
        return botao;
    }
    
    private void iniciarNovaPartida() {
        controlador.iniciarNovaPartida();
        mostrarTabuleiro();
    }
    
    private void carregarPartida() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Carregar Partida");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Arquivos de texto", "txt"));
        
        int resultado = fileChooser.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(this, 
                "Funcionalidade será implementada na 4ª iteração",
                "Aviso", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    public void mostrarTabuleiro() {
        // Criar tabuleiro gráfico
        tabuleiroGrafico = new TabuleiroGrafico(controlador);
        
        // Conectar o tabuleiro ao controlador
        controlador.setTabuleiroGrafico(tabuleiroGrafico);
        
        // Container com margens para dimensionamento correto
        JPanel container = new JPanel(new BorderLayout());
        container.setBorder(BorderFactory.createEmptyBorder(30, 45, 30, 45));
        container.add(tabuleiroGrafico, BorderLayout.CENTER);
        
        // DEBUG: Verificar dimensões
        System.out.println("[DEBUG] Dimensões do Tabuleiro: " + tabuleiroGrafico.getPreferredSize());
        System.out.println("[DEBUG] Dimensões do Container: " + container.getPreferredSize());
        
        // Definir como conteúdo da janela
        setContentPane(container);
        
        //  Forçar redimensionamento
        setResizable(true); // Temporariamente habilitar redimensionamento
        pack();             // Recalcular tamanho
        setResizable(false);// Voltar ao estado original
        
        // Atualizar a interface
        revalidate();
        repaint();
        
        // Centralização aprimorada
        setLocationRelativeTo(null);
        
        // DEBUG: Verificar tamanho final da janela
        System.out.println("[DEBUG] Tamanho final da janela: " + getSize());
    }
    
    public void mostrarFimDeJogo(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, 
            "Fim de Jogo", JOptionPane.INFORMATION_MESSAGE);
        mostrarTelaInicial();
    }
    
    public TabuleiroGrafico getTabuleiroGrafico() {
        return tabuleiroGrafico;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JanelaPrincipal janela = new JanelaPrincipal();
            janela.setVisible(true);
        });
    }
}