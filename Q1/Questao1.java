import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Aeroporto {
    private int pistas;
    private List<Aviao> filaAvioes;
    private long tempoPistaOcupada = 500;
    private Lock lock = new ReentrantLock();
    private Condition isPistaLivre = lock.newCondition();


    public Aeroporto(int pistas) {
        this.pistas = pistas;
        this.filaAvioes = new ArrayList<>();
    }

    public void programarVooAviao(Aviao aviao) {
        lock.lock();
        filaAvioes.add(aviao);
        Collections.sort(filaAvioes);
        lock.unlock();
    }

    public void acessarPista() {
        try {
            lock.lock();
            while (pistas == 0) {
                try {
                    isPistaLivre.await();
                } catch (InterruptedException e) {}
            }

            pistas--;
            Aviao proximoAviao = filaAvioes.remove(0);
            if (proximoAviao != null) {
                proximoAviao.executarAcaoAviao(System.currentTimeMillis());
                Thread.sleep(tempoPistaOcupada);

                pistas++;
                isPistaLivre.signalAll();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }
}

class Aviao implements Runnable, Comparable<Aviao> {
    private Aeroporto aeroporto;
    public int id;
    public long horaPrevistaFornecida;
    public long horaAcaoExecutada;
    public Boolean isDecolou;
    public String tipoVoo;
    public String horaPrevistaFornecidaFormatada;
    public String horaAcaoExecutadaFormatada;

    public Aviao(Aeroporto aeroporto, int id, long horaPrevistaFornecida, boolean isDecolou) {
        this.aeroporto = aeroporto;
        this.id = id;
        this.horaPrevistaFornecida = horaPrevistaFornecida;
        this.isDecolou = isDecolou;
        this.tipoVoo = isDecolou ? "Decolagem" : "Aterrissagem";
        this.horaPrevistaFornecidaFormatada = String.format("%1$tH:%1$tM:%1$tS", this.horaPrevistaFornecida);
    }

    public int compareTo(Aviao aviao) {
        return Long.compare(this.horaPrevistaFornecida, aviao.horaPrevistaFornecida);
    }

    
    public void executarAcaoAviao(long horaAcaoExecutada) {
        this.horaAcaoExecutada = horaAcaoExecutada;
        this.horaAcaoExecutadaFormatada = String.format("%1$tH:%1$tM:%1$tS", this.horaAcaoExecutada);

        long atrasoEmMilissegundos = this.horaAcaoExecutada - this.horaPrevistaFornecida;
        String atrasoFormatado = String.format("%d:%02d:%02d",
                (atrasoEmMilissegundos / 3600000),
                (atrasoEmMilissegundos / 60000) % 60,
                (atrasoEmMilissegundos / 1000) % 60);

        System.out.printf("%s do Avião id %d registrada!%n", this.tipoVoo, this.id);
        System.out.printf("Horário esperado de saída: %s%n", this.horaPrevistaFornecidaFormatada);
        System.out.printf("Horário real de saída: %s%n", this.horaAcaoExecutadaFormatada);
        System.out.println("Atraso: " + atrasoFormatado);
    }

    @Override
    public void run() {
        try {
            aeroporto.programarVooAviao(this);

            long horaAtual = System.currentTimeMillis();
            if (this.horaPrevistaFornecida > horaAtual) {
                Thread.sleep(this.horaPrevistaFornecida - horaAtual);
            }

            aeroporto.acessarPista();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

public class Questao1{
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        long startTime = System.currentTimeMillis();

        System.out.print("Quantidade de aviões esperando para decolar: ");
        int quantidadeAvioesEsperandoSair = sc.nextInt();

        System.out.print("Quantidade de aviões que irão aterrissar: ");
        int quantidadeAvioesVaoChegar = sc.nextInt();

        System.out.print("Número de pistas disponíveis: ");
        int numeroPistasDisponiveis = sc.nextInt();
        sc.close();

        Aeroporto aeroporto = new Aeroporto(numeroPistasDisponiveis);
        Thread[] threadsAvioes = new Thread[quantidadeAvioesEsperandoSair + quantidadeAvioesVaoChegar];

        Random random = new Random();

        
        for (int i = 0; i < quantidadeAvioesEsperandoSair; i++) {
            long horaPrevistaFornecidaSaidaAviao = System.currentTimeMillis() + 1000 + (Math.abs(random.nextLong() % 10000));
            threadsAvioes[i] = new Thread(new Aviao(aeroporto, i, horaPrevistaFornecidaSaidaAviao, true));
        }

        
        for (int i = 0; i < quantidadeAvioesVaoChegar; i++) {
            long horaPrevistaFornecidaSaidaAviao = System.currentTimeMillis() + 1000 + (Math.abs(random.nextLong() % 10000));
            threadsAvioes[quantidadeAvioesEsperandoSair + i] = new Thread(new Aviao(aeroporto, quantidadeAvioesEsperandoSair + i + 1, horaPrevistaFornecidaSaidaAviao, false));
        }

        
        for (int i = 0; i < quantidadeAvioesEsperandoSair + quantidadeAvioesVaoChegar; i++) {
            threadsAvioes[i].start();
        }

        
        for (int i = 0; i < quantidadeAvioesEsperandoSair + quantidadeAvioesVaoChegar; i++) {
            try {
                threadsAvioes[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        long endTime = System.currentTimeMillis();
        long tempoExecucao = endTime - startTime;
        String tempoFormatado = String.format("%d:%02d:%02d",
                (tempoExecucao / 3600000),
                (tempoExecucao / 60000) % 60,
                (tempoExecucao / 1000) % 60);
        System.out.println("Tempo de execução: " + tempoFormatado);
    }
}