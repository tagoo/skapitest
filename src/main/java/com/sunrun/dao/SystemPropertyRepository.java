package com.sunrun.dao;

import com.sunrun.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemPropertyRepository extends JpaRepository<Property,String> {
}
