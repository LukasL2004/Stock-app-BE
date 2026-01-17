package com.Calculator.Stock.Repository;

import com.Calculator.Stock.Entity.AuditLog;
import com.Calculator.Stock.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AuditLogRespository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findAllByUser(User user);

}
