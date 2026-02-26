package com.poly.assaiment_java6.repository;

import com.poly.assaiment_java6.entity.DanhMuc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface DanhMucRepository extends JpaRepository<DanhMuc, Integer>{
}
