/**
 * 
 */
package com.sunrun.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
  * DomainData
  * @Description: TODO
  * @author Mr.Yuan
  * @email yuanyong@gzsunrun.cn
  * @date  2017年10月30日 下午2:27:32
  * @version 3.0
  */
@Setter
@Getter
@NoArgsConstructor
@ToString
public class IamValidateRespData implements Serializable{
	
	private static final long serialVersionUID = -4528281337892327214L;
	private Long user_id;
	private String user_name;
	private Integer domain_id;
	private String domain_name;
	private String session_id;
	private String session_name;
}
