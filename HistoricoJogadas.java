package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe para manter o histórico de jogadas
 * Útil para desfazer movimentos, salvar partidas e notação
 */
public class HistoricoJogadas {
    
    public static class Jogada {
        public final int linhaOrigem;
        public final int colunaOrigem;
        public final int linhaDestino;
        public final int colunaDestino;
        public final Peca pecaMovida;
        public final Peca pecaCapturada;
        public final String tipoMovimento; // "normal", "roque", "enpassant", "promocao"
        public final long timestamp;
        
        public Jogada(int linhaOrigem, int colunaOrigem, int linhaDestino, 
                     int colunaDestino, Peca pecaMovida, Peca pecaCapturada, 
                     String tipoMovimento) {
            this.linhaOrigem = linhaOrigem;
            this.colunaOrigem = colunaOrigem;
            this.linhaDestino = linhaDestino;
            this.colunaDestino = colunaDestino;
            this.pecaMovida = pecaMovida;
            this.pecaCapturada = pecaCapturada;
            this.tipoMovimento = tipoMovimento;
            this.timestamp = System.currentTimeMillis();
        }
        
        /**
         * Converte a jogada para notação algébrica
         */
        public String toNotacaoAlgebrica() {
            StringBuilder notacao = new StringBuilder();
            
            // Tipo da peça
            if (!(pecaMovida instanceof Peao)) {
                if (pecaMovida instanceof Rei) notacao.append("R");
                else if (pecaMovida instanceof Rainha) notacao.append("D");
                else if (pecaMovida instanceof Torre) notacao.append("T");
                else if (pecaMovida instanceof Bispo) notacao.append("B");
                else if (pecaMovida instanceof Cavalo) notacao.append("C");
            }
            
            // Casa de origem (simplificada)
            char colunaChar = (char) ('a' + colunaOrigem);
            int linhaNum = 8 - linhaOrigem;
            
            // Captura
            if (pecaCapturada != null) {
                if (pecaMovida instanceof Peao) {
                    notacao.append(colunaChar);
                }
                notacao.append("x");
            }
            
            // Casa de destino
            char colunaDestChar = (char) ('a' + colunaDestino);
            int linhaDestNum = 8 - linhaDestino;
            notacao.append(colunaDestChar).append(linhaDestNum);
            
            // Movimentos especiais
            if (tipoMovimento.equals("roque")) {
                if (colunaDestino > colunaOrigem) {
                    return "O-O"; // Roque pequeno
                } else {
                    return "O-O-O"; // Roque grande
                }
            }
            
            if (tipoMovimento.equals("enpassant")) {
                notacao.append(" e.p.");
            }
            
            return notacao.toString();
        }
    }
    
    private List<Jogada> historico;
    private int numeroJogada;
    
    public HistoricoJogadas() {
        this.historico = new ArrayList<>();
        this.numeroJogada = 1;
    }
    
    public void adicionarJogada(Jogada jogada) {
        historico.add(jogada);
        // Incrementar número da jogada a cada duas jogadas (branca + preta)
        if (historico.size() % 2 == 0) {
            numeroJogada++;
        }
    }
    
    public List<Jogada> getHistorico() {
        return new ArrayList<>(historico);
    }
    
    public Jogada getUltimaJogada() {
        if (historico.isEmpty()) return null;
        return historico.get(historico.size() - 1);
    }
    
    public int getNumeroJogada() {
        return numeroJogada;
    }
    
    public void limpar() {
        historico.clear();
        numeroJogada = 1;
    }
    
    /**
     * Gera a notação PGN da partida
     */
    public String toPGN() {
        StringBuilder pgn = new StringBuilder();
        
        for (int i = 0; i < historico.size(); i++) {
            if (i % 2 == 0) {
                pgn.append((i / 2 + 1)).append(". ");
            }
            pgn.append(historico.get(i).toNotacaoAlgebrica()).append(" ");
        }
        
        return pgn.toString().trim();
    }
}
