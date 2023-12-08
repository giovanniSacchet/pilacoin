package br.ufsm.csi.pilacoin.controller;

import br.ufsm.csi.pilacoin.model.TransacaoUsuario;
import br.ufsm.csi.pilacoin.service.TransferirPilaService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/transacao")
@CrossOrigin
public class TransacaoController {
    private final TransferirPilaService transferirPilaService;

    public TransacaoController(TransferirPilaService transferirPilaService) {
        this.transferirPilaService = transferirPilaService;
    }

    @GetMapping("/pila")
    public String transferirPila(@RequestBody TransacaoUsuario transacaoUsuario) throws JsonProcessingException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        if (this.transferirPilaService.transferirPila(transacaoUsuario)) {
            return "***************** DEU BOM NA TRANSAÇÃO! *****************";
        }
        return "***************** USUARIO INDISPONIVEL OU NENHUM PILA DISPONIVEL PARA TRANSAÇÃO! *****************";
    }
}
