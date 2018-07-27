package com.sunrun.service.impl;

import com.sunrun.dao.SystemPropertyRepository;
import com.sunrun.exception.NotFindPropertyException;
import com.sunrun.exception.PropertyNameEmptyException;
import com.sunrun.service.SystemPropertyService;
import com.sunrun.support.iam.SystemPropertyInfo;
import com.sunrun.utils.RestApiUtil;
import com.sunrun.entity.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SystemPropertyServiceImpl implements SystemPropertyService {
    @Resource
    private RestApiUtil restApiUtil;
    @Autowired
    private SystemPropertyRepository systemPropertyRepository;

    @Override
    public Property findPropertyByPropertyName(String propertyName) {
        if (hasNotInit()) {
            return systemPropertyRepository.findById(propertyName).get();
        }
        return restApiUtil.getSystemProperty(propertyName);
    }

    private boolean hasNotInit() {
        return SystemPropertyInfo.getProperties().get(SystemPropertyInfo.OPENFIRE_FIRST_INIT) == null;
    }

    @Override
    public List<Property> getAllProperties() {
        if (hasNotInit()) {
            return systemPropertyRepository.findAll();
        }
        return restApiUtil.getAllSystemProperties();
    }

    @Override
    public boolean update(Property property) throws PropertyNameEmptyException, NotFindPropertyException {
        if (property == null || property.getKey() == null) {
            throw new PropertyNameEmptyException();
        }
        if (hasNotInit()) {
            if (!systemPropertyRepository.existsById(property.getKey())) {
                throw new NotFindPropertyException();
            }
            return systemPropertyRepository.save(property) != null;
        } else {
            Property systemProperty = restApiUtil.getSystemProperty(property.getKey());
            if (systemProperty == null) {
                throw new NotFindPropertyException();
            }
            return restApiUtil.updateSystemProperty(property);
        }
    }

    @Override
    public boolean save(Property property) throws PropertyNameEmptyException {
        if (property == null || property.getKey() == null) {
            throw new PropertyNameEmptyException();
        }
        if (hasNotInit()) {
            return systemPropertyRepository.save(property) != null;
        }
        return restApiUtil.createSystemProperty(property);
    }

    @Override
    public boolean delete(String propertyName) throws PropertyNameEmptyException, NotFindPropertyException {
        if (propertyName == null ) {
            throw new PropertyNameEmptyException();
        }
        if (hasNotInit()) {
            if (systemPropertyRepository.existsById(propertyName)){
                systemPropertyRepository.deleteById(propertyName);
                return true;
            }
            throw new NotFindPropertyException();
        }
        Property systemProperty = restApiUtil.getSystemProperty(propertyName);
        if (systemProperty == null) {
            throw new NotFindPropertyException();
        }
        return restApiUtil.deleteSystemProperty(propertyName);
    }

    @Override
    public void saveAll(List<Property> properties) {
        if (hasNotInit()) {
            systemPropertyRepository.saveAll(properties);
        } else {
            properties.forEach(p -> restApiUtil.createSystemProperty(p));
        }
    }
}
