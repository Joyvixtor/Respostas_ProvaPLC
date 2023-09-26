package pingpong_java;
import java.util.Scanner;

public class PingPong_q4java {

    static class PingSend implements Runnable {
        private int n;
        private Object lock;

        public PingSend(int n, Object lock) {
            this.n = n;
            this.lock = lock;
        }

        @Override
        public void run() {
            for (int i = 0; i < n; i++) {
                synchronized (lock) {
                    System.out.print("Toma aí o Ping número: " + (i + 1) + "\n");
                    lock.notify(); // Notifica a outra thread
                    try {
                        if (i < n - 1) {
                            lock.wait(); // Aguarda a outra thread
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    static class PingReceive implements Runnable {
        private int n;
        private Object lock;

        public PingReceive(int n, Object lock) {
            this.n = n;
            this.lock = lock;
        }

        @Override
        public void run() {
            for (int i = 0; i < n; i++) {
                synchronized (lock) {
                    System.out.print("Eita! Recebi o Pong número: " + (i + 1) + "\n");
                    lock.notify(); // Notifica a outra thread
                    try {
                        if (i < n - 1) {
                            lock.wait(); // Aguarda a outra thread
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String args[]) {
        // Receber inputs do usuário
        Scanner sc = new Scanner(System.in);
        System.out.print("Quantas mensagens PingPong você quer enviar?\n");
        int n = sc.nextInt(); // Recebe o numero n de mensagens
        sc.close(); // Encerra a classe de recebimentos de entrada

        Object lock = new Object(); // Objeto de sincronização

        Thread pingSend = new Thread(new PingSend(n, lock)); // Enviar as mensagens
        Thread pingReceive = new Thread(new PingReceive(n, lock)); // Receber as mensagens

        // Inicia as threads
        pingSend.start();
        pingReceive.start();

        try {
            // Espera que as threads terminem
            pingSend.join();
            pingReceive.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.print("Fim da brincadeira PingPong");
    }
}
