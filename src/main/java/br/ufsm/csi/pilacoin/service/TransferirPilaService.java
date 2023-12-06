package br.ufsm.csi.pilacoin.service;

import org.springframework.stereotype.Service;

@Service
public class TransferirPilaService {

    /*try {
      transaction.setChaveUsuarioOrigem(cryptoUtil.generateKeys().getPublic().getEncoded());
      transaction.setDataTransacao(new Date());

      ObjectMapper mapper = new ObjectMapper();
      String transactionStr = mapper.writeValueAsString(transaction);

      Cipher cipher = Cipher.getInstance("RSA");
      cipher.init(Cipher.ENCRYPT_MODE, cryptoUtil.generateKeys().getPrivate());

      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] signature = cipher.doFinal(md.digest(transactionStr.getBytes(StandardCharsets.UTF_8)));

      Base64.getEncoder().encodeToString(signature);
      transaction.setAssinatura(signature);

      System.out.println("\n\n[TRANSFER DESTINATION]: " + transaction.getNomeUsuarioDestino());

      rabbitTemplate.convertAndSend(transactionQueue, mapper.writeValueAsString(transaction));

      deleteByNonce(transaction.getNoncePila());
    } catch (Exception e) {
      e.printStackTrace();
    }*/


}
