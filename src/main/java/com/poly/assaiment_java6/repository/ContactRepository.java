package com.poly.assaiment_java6.repository;

import com.poly.assaiment_java6.entity.LienHe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<LienHe, Integer> {
}