package pingping_java;
import java.util.Scanner;

public class PingPing_q4java{

    static class PingSend implements Runnable{
        private int n;

        public PingSend(int n){
            this.n = n;
        }

        @Override
        public void run() {
            for (int i=0; i < n; i++){
                System.out.print("Toma aí o Ping número: " + (i + 1) + "\n");
            }
        }
    }

    static class PingReceive implements Runnable{
        private int n;

        public PingReceive(int n){
            this.n = n;
        }

        @Override
        public void run() {
            for (int i=0; i < n; i++){
                System.out.print("Eita! Recebi o Ping número: " + (i+1) + "\n");
            }
        }
    }

    public static void main(String args[]){
        //receber inputs do usuario
        Scanner sc = new Scanner(System.in);
        System.out.print("Quantas mensagens PingPing você quer enviar?\n");
        int n = sc.nextInt(); //recebe o numero n de mensagens
        sc.close(); //encerra a classe de recebimentos de entrada

        Thread pingSend = new Thread(new PingSend(n)); //enviar as mensagens
        Thread pingReceive = new Thread(new PingReceive(n)); //receber as mensagens

        //inicia as threads
        pingSend.start();
        pingReceive.start();

        try{
            //waiting state
            pingSend.join();
            pingReceive.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }

        System.out.print("Fim da brincadeira PingPing");
    }

    
}