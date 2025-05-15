package test;

import model.JogoXadrez;

public class TesteJogo {
    public static void main(String[] args) {
        JogoXadrez jogo = JogoXadrez.getInstancia();

        System.out.println("Selecionando peão branco na linha 6, coluna 0:");
        if (jogo.selecionaPeca(6, 0)) {
            System.out.println("Peça selecionada.");
            if (jogo.selecionaCasa(4, 0)) {
                System.out.println("Movimento realizado!");
            } else {
                System.out.println("Movimento inválido.");
            }
        } else {
            System.out.println("Peça inválida ou não pertence ao jogador da vez.");
        }
    }
}
