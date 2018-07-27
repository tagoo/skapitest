package com.sunrun.service.impl;

import com.sunrun.exception.NotFindPropertyException;
import com.sunrun.exception.PropertyNameEmptyException;
import com.sunrun.service.SystemPropertyService;
import com.sunrun.entity.Property;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SystemPropertyServiceImplTest {
    @Autowired
    private SystemPropertyService systemPropertyService;
    @Test
    public void findPropertyByPropertyName() {
        Property propertyByPropertyName = systemPropertyService.findPropertyByPropertyName("2221");
        System.out.println(propertyByPropertyName);
        Assert.assertNotNull(propertyByPropertyName);
    }

    @Test
    public void getAllProperties() {
        List<Property> allProperties = systemPropertyService.getAllProperties();
        System.out.println(allProperties);
        Assert.assertFalse(allProperties.isEmpty());
    }

    @Test
    public void update() {
        try {
            Assert.assertTrue(systemPropertyService.update(new Property("xx","update")));
        } catch (PropertyNameEmptyException e) {
            e.printStackTrace();
        } catch (NotFindPropertyException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void save() {
        try {
            Assert.assertTrue(systemPropertyService.save(new Property("xx","save")));
        } catch (PropertyNameEmptyException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void delete() {
        try {
            Assert.assertTrue(systemPropertyService.delete("xx"));
        } catch (PropertyNameEmptyException e) {
            e.printStackTrace();
        } catch (NotFindPropertyException e) {
            e.printStackTrace();
        }
    }
}