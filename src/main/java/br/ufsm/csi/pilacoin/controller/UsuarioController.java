package br.ufsm.csi.pilacoin.controller;

import br.ufsm.csi.pilacoin.model.Usuario;
import br.ufsm.csi.pilacoin.repository.UsuarioRepository;
import br.ufsm.csi.pilacoin.service.UsuarioService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/usuario")
@CrossOrigin
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioRepository usuarioRepository, UsuarioService usuarioService) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
    }


    @GetMapping("/add")
    public String adicionarUsuario() throws Exception {
        List<Usuario> usuarios = this.usuarioRepository.findAll();
        if (!usuarios.isEmpty()) {
            return "***** APENAS 1 USUÁRIO PERMITIDO NO SISTEMA! *****";
        }

        this.usuarioService.gerarKeyPair();
        Usuario u = new Usuario();
        u.setNome("giovanni");
        this.usuarioRepository.save(u);

        return "***** USUÁRIO ADICIONADO! *****";
    }
}
