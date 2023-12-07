package br.ufsm.csi.pilacoin.service;

import br.ufsm.csi.pilacoin.model.Pilacoin;
import br.ufsm.csi.pilacoin.model.TransferirPila;
import br.ufsm.csi.pilacoin.model.Usuario;
import br.ufsm.csi.pilacoin.repository.PilacoinRepository;
import br.ufsm.csi.pilacoin.repository.TransferirPilaRepository;
import br.ufsm.csi.pilacoin.repository.UsuarioRepository;
import br.ufsm.csi.pilacoin.shared.KeyUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
public class TransferirPilaService {

    private final PilacoinRepository pilacoinRepository;
    private final TransferirPilaRepository transferirPilaRepository;
    private final UsuarioRepository usuarioRepository;
    private final RabbitTemplate rabbitTemplate;

    public TransferirPilaService(PilacoinRepository pilacoinRepository, TransferirPilaRepository transferirPilaRepository, RabbitTemplate rabbitTemplate, UsuarioRepository usuarioRepository) {
        this.pilacoinRepository = pilacoinRepository;
        this.transferirPilaRepository = transferirPilaRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.usuarioRepository = usuarioRepository;

    }

    public boolean transferirPila() throws JsonProcessingException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        List<Pilacoin> pilasValidados = pilacoinRepository.findAllByStatus("VALIDO");
        List<Pilacoin> pilasDisponiveis = new ArrayList<>();
        List<TransferirPila> transacoesPila;

        for (Pilacoin pila : pilasValidados) {
            transacoesPila = this.transferirPilaRepository.findAllByNoncePila(pila.getNonce());
            if (!transacoesPila.isEmpty()) {
                pila.setTransacoes(transacoesPila);
            }
            if (Arrays.equals(pila.getChaveCriador(), KeyUtil.publicKey.getEncoded()) && pila.getTransacoes() == null || pila.getTransacoes().size() <= 1) {
                pilasDisponiveis.add(pila);
            }
        }

        // ***** PEGA USUARIO QUALQUER ******
        Optional<Usuario> usuario = this.usuarioRepository.findByNome("Casanova");

        if (usuario.isPresent() && !pilasDisponiveis.isEmpty()) {

            TransferirPila transferencia = TransferirPila.builder().
                    chaveUsuarioOrigem(KeyUtil.publicKey.getEncoded()).
                    chaveUsuarioDestino(usuario.get().getChavePublica()).
                    nomeUsuarioOrigem("gxs").
                    nomeUsuarioDestino(usuario.get().getNome()).
                    dataTransacao(new Date()).
                    noncePila(pilasDisponiveis.get(0).getNonce()).
                    build();

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String str = objectMapper.writeValueAsString(transferencia);

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, KeyUtil.privateKey);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] assinatura = md.digest(str.getBytes(StandardCharsets.UTF_8));

            transferencia.setAssinatura(cipher.doFinal(assinatura));

            //System.out.println(objectMapper.writeValueAsString(transferencia));
            rabbitTemplate.convertAndSend("transferir-pila", objectMapper.writeValueAsString(transferencia));

            this.transferirPilaRepository.save(transferencia);
            return true;
        }
        return false;
    }


}
