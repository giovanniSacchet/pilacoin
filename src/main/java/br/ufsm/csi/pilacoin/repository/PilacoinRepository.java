package br.ufsm.csi.pilacoin.repository;

import br.ufsm.csi.pilacoin.model.Pilacoin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PilacoinRepository extends JpaRepository<Pilacoin, String>  {
    Optional<Pilacoin> findByNonce(String nonce);
    List<Pilacoin> findAllByStatus(String status);

}
