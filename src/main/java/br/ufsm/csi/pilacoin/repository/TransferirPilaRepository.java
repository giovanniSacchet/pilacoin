package br.ufsm.csi.pilacoin.repository;

import br.ufsm.csi.pilacoin.model.TransferirPila;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransferirPilaRepository extends JpaRepository<TransferirPila, String> {

    List<TransferirPila> findAllByNoncePila(String s);
}
