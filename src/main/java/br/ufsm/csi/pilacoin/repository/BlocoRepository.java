package br.ufsm.csi.pilacoin.repository;

import br.ufsm.csi.pilacoin.model.Bloco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlocoRepository extends JpaRepository<Bloco, String> {
    Optional<Bloco> findByNonce(String nonce);
}
