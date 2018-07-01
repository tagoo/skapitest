/**
 * 
 */
package com.sunrun.vo;

import java.util.List;



/**
  * DomainResult
  * @Description: TODO
  * @author Mr.Yuan
  * @email yuanyong@gzsunrun.cn
  * @date  2017年10月18日 上午9:35:59
  * @version 3.0
  */
public class DomainResult {
	
	private List<DomainVo> domains ;

	public DomainResult() {
		super();
	}

	public List<DomainVo> getDomains() {
		return domains;
	}

	public void setDomains(List<DomainVo> domains) {
		this.domains = domains;
	}
	
}
