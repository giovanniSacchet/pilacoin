package br.ufsm.csi.pilacoin.controller;

import br.ufsm.csi.pilacoin.service.MineracaoService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/minerar")
@CrossOrigin
public class MineracaoController {

    private final MineracaoService mineracaoService;

    public MineracaoController(MineracaoService mineracaoService) {
        this.mineracaoService = mineracaoService;
    }

    @GetMapping("/pila")
    public String minerarPilacoin() {
        String retorno = "*** INICIANDO MINERAÇÃO DE PILACOIN ***";
        this.mineracaoService.minerarPilacoin();
        return retorno;
    }

    @GetMapping("/bloco")
    public String minerarBloco() {
        String retorno = "*** INICIANDO MINERAÇÃO DE BLOCOS ***";
        this.mineracaoService.iniciarMineracaoBloco();
        return retorno;
    }

    @GetMapping("/pila/parar")
    public String pararMineracaoPila() {
        mineracaoService.pararMineracaoPila();
        return "***** PARANDO MINERAÇÃO PILA *****" ;
    }

    @GetMapping("/bloco/parar")
    public String pararMineracaoBloco() {
        mineracaoService.pararMineracaoBloco();
        return "***** PARANDO MINERAÇÃO BLOCO *****" ;
    }

}
