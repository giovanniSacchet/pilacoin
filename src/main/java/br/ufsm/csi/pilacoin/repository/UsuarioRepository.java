package br.ufsm.csi.pilacoin.repository;

import br.ufsm.csi.pilacoin.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    Optional<Usuario> findByNome(String nome);
}
