package io.fraud.database.dao;

import com.qaguild.dbeaver.repositories.Repository;
import io.fraud.database.model.Deal;

import java.util.List;

public interface DealDao extends Repository<Integer, Deal> {

//    Deal findById(int id);

    List<Deal> findByCurrency(String currency);

    List<Deal> findBySource(String source);
}
