package br.ufsm.csi.pilacoin.shared;

import lombok.Data;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Data
public class KeyUtil {

    public static PrivateKey privateKey;
    public static PublicKey publicKey;

    public static void carregarChavePrivada() throws Exception {
        byte[] chavePrivadaBytes = Files.readAllBytes(Paths.get("chave_privada.pem"));
        PKCS8EncodedKeySpec chavePrivadaSpec = new PKCS8EncodedKeySpec(chavePrivadaBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        privateKey = keyFactory.generatePrivate(chavePrivadaSpec);
        System.out.println("\n\n***** CHAVE PRIVADA *****\n\t" + privateKey);
    }

    public static void carregarChavePublica() throws Exception {
        byte[] chavePublicaBytes = Files.readAllBytes(Paths.get("chave_publica.pem"));
        X509EncodedKeySpec chavePublicaSpec = new X509EncodedKeySpec(chavePublicaBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        publicKey = keyFactory.generatePublic(chavePublicaSpec);
        System.out.println("\n\n***** CHAVE PUBLICA *****\n\t" + publicKey);
    }

}
