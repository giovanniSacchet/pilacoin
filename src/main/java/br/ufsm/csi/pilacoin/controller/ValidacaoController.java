package br.ufsm.csi.pilacoin.controller;

import br.ufsm.csi.pilacoin.service.MineracaoService;
import br.ufsm.csi.pilacoin.service.ValidacaoService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/validar")
@CrossOrigin
public class ValidacaoController {

    private final ValidacaoService validacaoService;
    private final MineracaoService mineracaoService;

    public ValidacaoController(ValidacaoService validacaoService, MineracaoService mineracaoService) {
        this.validacaoService = validacaoService;
        this.mineracaoService = mineracaoService;
    }
    @GetMapping("/pila")
    public String validarPilacoin() {
        this.mineracaoService.pararMineracaoPila();
        this.mineracaoService.pararMineracaoBloco();
        this.validacaoService.pararValidacaoBloco();
        this.validacaoService.iniciarValidacaoPila();
        return "*** INICIANDO VALIDAÇÃO DE PILACOIN ***";
    }
    @GetMapping("/pila/parar")
    public String pararValidacaoPila() {
        this.validacaoService.pararValidacaoPila();
        return "***** PARANDO VALIDAÇÃO PILA *****" ;
    }

    @GetMapping("/bloco")
    public String validarBloco() {
        this.mineracaoService.pararMineracaoPila();
        this.mineracaoService.pararMineracaoBloco();
        this.validacaoService.pararValidacaoPila();
        this.validacaoService.iniciarValidacaoBloco();
        return "*** INICIANDO VALIDAÇÃO DE BLOCO ***";
    }
    @GetMapping("/bloco/parar")
    public String pararValidacaoBloco() {
        this.validacaoService.pararValidacaoBloco();
        return "***** PARANDO VALIDAÇÃO BLOCO *****" ;
    }
}
