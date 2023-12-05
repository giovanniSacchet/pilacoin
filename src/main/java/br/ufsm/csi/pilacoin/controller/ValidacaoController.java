package br.ufsm.csi.pilacoin.controller;

import br.ufsm.csi.pilacoin.model.Usuario;
import br.ufsm.csi.pilacoin.repository.UsuarioRepository;
import br.ufsm.csi.pilacoin.service.MineracaoService;
import br.ufsm.csi.pilacoin.service.ValidacaoService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/validar")
@CrossOrigin
public class ValidacaoController {

    private final ValidacaoService validacaoService;
    private final UsuarioRepository usuarioRepository;

    public ValidacaoController(ValidacaoService validacaoService, UsuarioRepository usuarioRepository) {
        this.validacaoService = validacaoService;
        this.usuarioRepository = usuarioRepository;
    }
    @GetMapping("/pila")
    public String validarPilacoin() {
        String retorno = "*** INICIANDO VALIDAÇÃO DE PILACOIN ***";

        List<Usuario> usuarios = this.usuarioRepository.findAll();
        if (!usuarios.isEmpty()) {
            Usuario usuario = usuarios.get(0);
            System.out.println("\n\n***** USUARIO ENCONTRADO *****\n\t--- " + usuario.getNome() + " ---\n***** INICIANDO VALIDAÇÃO DE PILACOIN *****");
            this.validacaoService.pararValidacaoBloco();
            this.validacaoService.iniciarValidacaoPila();
        } else {
            System.out.println("\n\n----- NENHUM USUARIO ENCONTRADO -----");
            retorno = "***** CADASTRE UM USUARIO PRIMEIRO *****";
        }

        return retorno;
    }
    @GetMapping("/pila/parar")
    public String pararValidacaoPila() {
        this.validacaoService.pararValidacaoPila();
        return "***** PARANDO VALIDAÇÃO PILA *****" ;
    }

    @GetMapping("/bloco")
    public String validarBloco() {
        String retorno = "*** INICIANDO VALIDAÇÃO DE BLOCO ***";

        List<Usuario> usuarios = this.usuarioRepository.findAll();
        if (!usuarios.isEmpty()) {
            Usuario usuario = usuarios.get(0);
            System.out.println("\n\n***** USUARIO ENCONTRADO *****\n\t--- " + usuario.getNome() + " ---\n***** INICIANDO VALIDAÇÃO DE BLOCO *****");
            this.validacaoService.pararValidacaoPila();
            this.validacaoService.iniciarValidacaoBloco();
        } else {
            System.out.println("\n\n----- NENHUM USUARIO ENCONTRADO -----");
            retorno = "***** CADASTRE UM USUARIO PRIMEIRO *****";
        }

        return retorno;
    }
    @GetMapping("/bloco/parar")
    public String pararValidacaoBloco() {
        this.validacaoService.pararValidacaoBloco();
        return "***** PARANDO VALIDAÇÃO BLOCO *****" ;
    }
}
