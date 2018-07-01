package com.sunrun.utils;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Properties;

/**
  * IDGenerator
  * @Description: 用系统时间生成主键ID
  * @author Mr.Yuan
  * @email yuanyong@gzsunrun.cn
  * @date  2017年5月10日 上午9:28:03
  * @version 3.0
  */
public final class IDGenerator implements Configurable,IdentifierGenerator {
	private static long workerId;
	private static long datacenterId;
	private static final SnowflakeIdWorker snowflakeIdWorker;
	static {
		snowflakeIdWorker = new SnowflakeIdWorker(workerId,datacenterId);
	}
	public IDGenerator() {}

	@Override
	public void configure(Type type, Properties properties, ServiceRegistry serviceRegistry) throws MappingException {
		this.workerId = Long.parseLong(properties.getProperty("workerId","0"));
		this.datacenterId = Long.parseLong(properties.getProperty("datacenterId","0"));
	}

	@Override
	public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws HibernateException {
		return  getLongId();
	}

	public static final SnowflakeIdWorker getInstance(){
		return snowflakeIdWorker;
	}
	public static synchronized Long getLongId(){
		return getInstance().nextId();
	}
}
