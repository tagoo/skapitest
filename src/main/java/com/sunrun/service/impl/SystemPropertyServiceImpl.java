package com.sunrun.service.impl;

import com.sunrun.exception.NotFindPropertyException;
import com.sunrun.exception.PropertyNameEmptyException;
import com.sunrun.service.SystemPropertyService;
import com.sunrun.utils.RestApiUtil;
import com.sunrun.utils.helper.Property;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SystemPropertyServiceImpl implements SystemPropertyService {
    @Resource
    private RestApiUtil restApiUtil;

    @Override
    public Property findPropertyByPropertyName(String propertyName) {
        return restApiUtil.getSystemProperty(propertyName);
    }

    @Override
    public List<Property> getAllProperties() {
        return restApiUtil.getAllSystemProperties();
    }

    @Override
    public boolean update(Property property) throws PropertyNameEmptyException, NotFindPropertyException {
        if (property == null || property.getKey() == null) {
            throw new PropertyNameEmptyException();
        }
        Property systemProperty = restApiUtil.getSystemProperty(property.getKey());
        if (systemProperty == null) {
            throw new NotFindPropertyException();
        }
        return restApiUtil.updateSystemProperty(property);
    }

    @Override
    public boolean save(Property property) throws PropertyNameEmptyException {
        if (property == null || property.getKey() == null) {
            throw new PropertyNameEmptyException();
        }
        return restApiUtil.createSystemProperty(property);
    }

    @Override
    public boolean delete(String propertyName) throws PropertyNameEmptyException, NotFindPropertyException {
        if (propertyName == null ) {
            throw new PropertyNameEmptyException();
        }
        Property systemProperty = restApiUtil.getSystemProperty(propertyName);
        if (systemProperty == null) {
            throw new NotFindPropertyException();
        }
        return restApiUtil.deleteSystemProperty(propertyName);
    }
}
