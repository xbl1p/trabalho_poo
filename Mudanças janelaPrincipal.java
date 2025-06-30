// Mudanças JanelaPrincipal.java, substituir carregarPartida

private void carregarPartida() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Carregar Partida");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Arquivos de texto", "txt"));
        
        int resultado = fileChooser.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            try {
                File arquivo = fileChooser.getSelectedFile();
                
                // Ler conteúdo do arquivo
                StringBuilder conteudo = new StringBuilder();
                try (java.io.BufferedReader reader = new java.io.BufferedReader(
                        new java.io.FileReader(arquivo))) {
                    String linha;
                    while ((linha = reader.readLine()) != null) {
                        conteudo.append(linha).append(" ");
                    }
                }
                
                String pgn = conteudo.toString().trim();
                
                if (pgn.isEmpty()) {
                    // Arquivo vazio = criar partida na posição inicial
                    if (controlador != null) {
                        controlador.iniciarNovaPartida();
                        JOptionPane.showMessageDialog(this,
                            "Partida carregada na posição inicial.",
                            "Carregamento Concluído",
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // IMPORTANTE: Mostrar o tabuleiro
                        mostrarTabuleiro();
                    }
                    return;
                }

                // Iniciar nova partida
                if (controlador != null) {
                    controlador.iniciarNovaPartida();
                    
                    // Aplicar movimentos do PGN
                    boolean sucesso = controlador.aplicarMovimentosPGN(pgn);
                    
                    if (sucesso) {
                        JOptionPane.showMessageDialog(this,
                            "Partida carregada com sucesso!\n" + 
                            "Arquivo: " + arquivo.getName(),
                            "Carregamento Concluído",
                            JOptionPane.INFORMATION_MESSAGE);
                            
                        System.out.println("[DEBUG] Partida carregada de: " + arquivo.getAbsolutePath());
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Erro ao processar alguns movimentos.\n" +
                            "A partida pode estar incompleta.",
                            "Aviso",
                            JOptionPane.WARNING_MESSAGE);
                    }
                    
                    // IMPORTANTE: Mostrar o tabuleiro após carregar
                    mostrarTabuleiro();
                }
                
            } catch (java.io.IOException e) {
                JOptionPane.showMessageDialog(this,
                    "Erro ao ler o arquivo:\n" + e.getMessage(),
                    "Erro de Leitura",
                    JOptionPane.ERROR_MESSAGE);
                    
                System.err.println("[ERRO] Erro ao ler arquivo: " + e.getMessage());
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Erro inesperado ao carregar partida:\n" + e.getMessage(),
                    "Erro no Carregamento",
                    JOptionPane.ERROR_MESSAGE);
                    
                System.err.println("[ERRO] Erro inesperado: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
