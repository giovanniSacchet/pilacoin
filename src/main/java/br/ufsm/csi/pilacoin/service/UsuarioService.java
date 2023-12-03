package br.ufsm.csi.pilacoin.service;

import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
@Service
public class UsuarioService {

    private void salvarChavePrivada(PrivateKey chavePrivada, String nomeArquivo) throws Exception {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(chavePrivada.getEncoded());
        try (FileOutputStream fos = new FileOutputStream(nomeArquivo)) {
            fos.write(pkcs8EncodedKeySpec.getEncoded());
        }
    }

    private static void salvarChavePublica(PublicKey chavePublica, String nomeArquivo) throws Exception {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(chavePublica.getEncoded());
        try (FileOutputStream fos = new FileOutputStream(nomeArquivo)) {
            fos.write(x509EncodedKeySpec.getEncoded());
        }
    }

    public void gerarKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        PublicKey chavePublica = keyPair.getPublic();
        PrivateKey chavePrivada = keyPair.getPrivate();

        salvarChavePrivada(chavePrivada, "chave_privada.pem");

        salvarChavePublica(chavePublica, "chave_publica.pem");
    }
}
