package br.ufsm.csi.pilacoin.repository;

import br.ufsm.csi.pilacoin.model.TransferirPila;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferirPilaRepository extends JpaRepository<TransferirPila, String> {
}
