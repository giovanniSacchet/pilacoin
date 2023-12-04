package br.ufsm.csi.pilacoin.service;

import br.ufsm.csi.pilacoin.model.Bloco;
import br.ufsm.csi.pilacoin.model.Pilacoin;
import br.ufsm.csi.pilacoin.model.Transacao;
import br.ufsm.csi.pilacoin.repository.BlocoRepository;
import br.ufsm.csi.pilacoin.repository.PilacoinRepository;
import br.ufsm.csi.pilacoin.repository.TransacaoRepository;
import br.ufsm.csi.pilacoin.shared.KeyUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.SneakyThrows;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;
import java.util.Random;

@Service
public class MineracaoService {

    private final RabbitTemplate rabbitTemplate;
    private final PilacoinRepository pilacoinRepository;
    private final BlocoRepository blocoRepository;
    private final TransacaoRepository transacaoRepository;
    private final DificuldadeService dificuldadeService;
    private volatile boolean minerandoPila = true;
    private volatile boolean minerandoBloco = false;
    public MineracaoService(RabbitTemplate rabbitTemplate,
                            PilacoinRepository pilacoinRepository,
                            DificuldadeService dificuldadeService,
                            BlocoRepository blocoRepository,
                            TransacaoRepository transacaoRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.pilacoinRepository = pilacoinRepository;
        this.dificuldadeService = dificuldadeService;
        this.blocoRepository = blocoRepository;
        this.transacaoRepository = transacaoRepository;
    }

    public static String getNonce(){
        Random nonce = new Random();
        byte[] bytes = new byte[256/8];
        nonce.nextBytes(bytes);
        return new BigInteger(bytes).abs().toString();
    }

    @SneakyThrows
    public void minerarPilacoin() {
        Pilacoin pila = Pilacoin.builder().
                chaveCriador(KeyUtil.publicKey.getEncoded()).
                dataCriacao(new Date()).
                nomeCriador("giovanni").build();
        ObjectMapper obj = new ObjectMapper();
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        BigInteger hash;
        int tentativa = 0;
        while (true){
            if (this.dificuldadeService.getDificuldadeAtual() != null) {
                tentativa++;
                if(!minerandoPila){
                    while (!minerandoPila){}
                }
                pila.setNonce(getNonce());
                hash = new BigInteger(md.digest(obj.writeValueAsString(pila).getBytes(StandardCharsets.UTF_8))).abs();
                if (hash.compareTo(this.dificuldadeService.getDificuldadeAtual()) < 0){
                    System.out.println("\n\n****** PILA MINERADO ****** \n\tTentativas: " + tentativa);
                    tentativa = 0;
                    rabbitTemplate.convertAndSend("pila-minerado", obj.writeValueAsString(pila));
                    pilacoinRepository.save(Pilacoin.builder().
                            nonce(pila.getNonce()).
                            dataCriacao(new Date()).
                            chaveCriador(KeyUtil.publicKey.getEncoded()).
                            nomeCriador("giovanni").
                            build());
                }
            }
        }
    }

    @SneakyThrows
    @RabbitListener(queues = "descobre-bloco")
    public void minerarBloco(@Payload String blocoStr) {
        if (!this.minerandoBloco) {
            rabbitTemplate.convertAndSend("descobre-bloco", blocoStr);
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Bloco bloco = objectMapper.readValue(blocoStr, Bloco.class);
        bloco.setNomeUsuarioMinerador("giovanni");
        bloco.setChaveUsuarioMinerador(KeyUtil.publicKey.getEncoded());
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        BigInteger hash;
        String blocoJson = "";

        while(true){
            bloco.setNonce(getNonce());
            objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            blocoJson = objectMapper.writeValueAsString(bloco);
            hash = new BigInteger(md.digest(blocoJson.getBytes(StandardCharsets.UTF_8))).abs();

            if (this.dificuldadeService.getDificuldadeAtual() != null) {
                if (hash.compareTo(this.dificuldadeService.getDificuldadeAtual()) < 0){
                    System.out.println(hash);
                    System.out.println("dificuldade" + this.dificuldadeService.getDificuldadeAtual());
                    System.out.println(blocoJson);
                    System.out.println("Numero do bloco: "+bloco.getNumeroBloco());
                    rabbitTemplate.convertAndSend("bloco-minerado", blocoJson);

                    for (Transacao t : bloco.getTransacoes()) {
                        t.setBloco(bloco);
                        transacaoRepository.save(t);
                    }
                    blocoRepository.save(bloco);

                    return;
                }
            }
        }
    }

    @RabbitListener(queues = "report")
    public void teste(@Payload String teste) {
        System.out.println(teste);
    }

    @RabbitListener(queues = "giovanni")
    public void mensagens(@Payload String msg){
        System.out.println("-=+=".repeat(10)+"\n"+msg+"\n"+"-=+=".repeat(10));
    }

    /*@SneakyThrows
    @RabbitListener(queues = "bloco-minerado")
    public void blocoMinerado(@Payload String blocoStr) {
        System.out.println("ESSE AQUI\n "+blocoStr);
    }*/

    public void pararMineracaoPila(){
        minerandoPila = false;
    }

    public void iniciarMineracaoBloco(){
        minerandoBloco = true;
    }

    public void pararMineracaoBloco(){
        minerandoBloco = false;
    }
}
