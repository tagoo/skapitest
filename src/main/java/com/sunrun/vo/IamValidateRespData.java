/**
 * 
 */
package com.sunrun.vo;

import java.io.Serializable;

/**
  * DomainData
  * @Description: TODO
  * @author Mr.Yuan
  * @email yuanyong@gzsunrun.cn
  * @date  2017年10月30日 下午2:27:32
  * @version 3.0
  */
public class IamValidateRespData implements Serializable{
	
	private static final long serialVersionUID = -4528281337892327214L;
	private Long user_id;
	private String user_name;
	private Integer domain_id;
	private String domain_name;
	private String session_id;
	private String session_name;

	
	public IamValidateRespData() {
		super();
	}
	
	public Long getUser_id() {
		return user_id;
	}
	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public Integer getDomain_id() {
		return domain_id;
	}
	public void setDomain_id(Integer domain_id) {
		this.domain_id = domain_id;
	}
	public String getDomain_name() {
		return domain_name;
	}
	public void setDomain_name(String domain_name) {
		this.domain_name = domain_name;
	}
	public String getSession_id() {
		return session_id;
	}
	public void setSession_id(String session_id) {
		this.session_id = session_id;
	}
	public String getSession_name() {
		return session_name;
	}
	public void setSession_name(String session_name) {
		this.session_name = session_name;
	}
	@Override
	public String toString() {
		return "IamValidateRespData [user_id=" + user_id + ", user_name=" + user_name + ", domain_id=" + domain_id
				+ ", domain_name=" + domain_name + ", session_id=" + session_id + ", session_name=" + session_name
				+ "]";
	}
	
	
}
