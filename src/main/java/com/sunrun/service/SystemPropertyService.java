package com.sunrun.service;

import com.sunrun.exception.NotFindPropertyException;
import com.sunrun.exception.PropertyNameEmptyException;
import com.sunrun.utils.helper.Property;

import java.util.List;

public interface SystemPropertyService {

    Property findPropertyByPropertyName(String propertyName);

    List<Property> getAllProperties();

    boolean update(Property property) throws PropertyNameEmptyException, NotFindPropertyException;

    boolean save(Property property) throws PropertyNameEmptyException;

    boolean delete(String propertyName) throws PropertyNameEmptyException, NotFindPropertyException;
}
