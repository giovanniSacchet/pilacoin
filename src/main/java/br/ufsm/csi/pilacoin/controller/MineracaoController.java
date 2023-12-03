package br.ufsm.csi.pilacoin.controller;

import br.ufsm.csi.pilacoin.model.Usuario;
import br.ufsm.csi.pilacoin.repository.UsuarioRepository;
import br.ufsm.csi.pilacoin.service.MineracaoService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/mineracao")
@CrossOrigin
public class MineracaoController {

    private final UsuarioRepository usuarioRepository;
    private final MineracaoService mineracaoService;
    public boolean minerando = true;

    public MineracaoController(UsuarioRepository usuarioRepository, MineracaoService mineracaoService) {
        this.mineracaoService = mineracaoService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/iniciar")
    public String iniciar() {
        String retorno = "*** INICIANDO MINERAÇÃO ***";

        List<Usuario> usuarios = this.usuarioRepository.findAll();
        if (!usuarios.isEmpty()) {
            Usuario usuario = usuarios.get(0);
            System.out.println("\n\n***** USUARIO ENCONTRADO *****\n\t--- " + usuario.getNome() + " ---\n***** INICIANDO MINERAÇÃO *****");
            this.mineracaoService.run();
        } else {
            System.out.println("\n\n----- NENHUM USUARIO ENCONTRADO -----");
            retorno = "***** CADASTRE UM USUARIO PRIMEIRO *****";
        }

        return retorno;
    }

    @GetMapping("/trocar_status")
    public boolean minerar() {
        if(minerando) {
            System.out.println("\n\n*** PARANDO MINERAÇÃO ***");
            mineracaoService.pararMineracao();
        } else {
            System.out.println("\n\n*** VOLTANDO MINERAÇÃO ***");
            mineracaoService.iniciarMineracao();
        }
        minerando = !minerando;
        return minerando;
    }




}
