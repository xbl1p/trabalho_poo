package view;

import javax.swing.SwingUtilities;

public class roda {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> { // invokeLater (boa pratica)
            JanelaPrincipal janela = new JanelaPrincipal();
            janela.setVisible(true); // Exibe a janela
        });
    }
}
