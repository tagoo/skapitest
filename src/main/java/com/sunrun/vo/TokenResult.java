/**
 * 
 */
package com.sunrun.vo;

/**
  * TokenResult
  * @Description: TODO
  * @author Mr.Yuan
  * @email yuanyong@gzsunrun.cn
  * @date  2017年10月24日 下午4:01:02
  * @version 3.0
  */
public class TokenResult {
	
	private String access_token;
	private String token_type;
	private Integer expires_in;
	private String refresh_token;
	private String scope;
	
	public TokenResult() {
		super();
	}
	public String getAccess_token() {
		return access_token;
	}
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	public String getToken_type() {
		return token_type;
	}
	public void setToken_type(String token_type) {
		this.token_type = token_type;
	}
	public Integer getExpires_in() {
		return expires_in;
	}
	public void setExpires_in(Integer expires_in) {
		this.expires_in = expires_in;
	}
	public String getRefresh_token() {
		return refresh_token;
	}
	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	@Override
	public String toString() {
		return "TokenResult [access_token=" + access_token + ", token_type=" + token_type + ", expires_in=" + expires_in
				+ ", refresh_token=" + refresh_token + ", scope=" + scope + "]";
	}
	
}
