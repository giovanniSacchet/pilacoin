package br.ufsm.csi.pilacoin.controller;

import br.ufsm.csi.pilacoin.model.Pilacoin;
import br.ufsm.csi.pilacoin.model.TransferirPila;
import br.ufsm.csi.pilacoin.model.Usuario;
import br.ufsm.csi.pilacoin.repository.PilacoinRepository;
import br.ufsm.csi.pilacoin.repository.UsuarioRepository;
import br.ufsm.csi.pilacoin.shared.KeyUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/transacao")
@CrossOrigin
public class TransacaoController {

    private final PilacoinRepository pilacoinRepository;
    private final UsuarioRepository usuarioRepository;
    private final RabbitTemplate rabbitTemplate;

    public TransacaoController(PilacoinRepository pilacoinRepository, UsuarioRepository usuarioRepository, RabbitTemplate rabbitTemplate) {
        this.pilacoinRepository = pilacoinRepository;
        this.usuarioRepository = usuarioRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping("/pila")
    public void transferirPila() throws JsonProcessingException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        List<Pilacoin> pilasValidados = pilacoinRepository.findAllByStatus("VALIDO");
        List<Pilacoin> pilasDisponiveis = new ArrayList<>();

        for (Pilacoin pila : pilasValidados) { // Coloca os pilas validos e que n tenham sido transferidos
            if (pila.getTransacoes() == null || pila.getTransacoes().size() <= 1) {
                pilasDisponiveis.add(pila);
            }
        }

        // ***** PEGA USUARIO QUALQUER ******
        Optional<Usuario> usuario = this.usuarioRepository.findByNome("Casanova");
        if (usuario.isPresent()) {
            Pilacoin pila = pilasDisponiveis.get(0);

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

            System.out.println(objectMapper.writeValueAsString(transferencia));
            rabbitTemplate.convertAndSend("transferir-pila", objectMapper.writeValueAsString(transferencia));

            Pilacoin pilacoin = pilasDisponiveis.get(0);
            List<TransferirPila> transacoesPila = new ArrayList<>();
            transacoesPila.add(transferencia);
            pila.setTransacoes(transacoesPila);
            //this.pilacoinRepository.save(pila);
        }

    }
}
