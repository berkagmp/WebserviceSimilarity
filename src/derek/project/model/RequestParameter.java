package derek.project.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "REQUEST_PARAMETER")
public class RequestParameter {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "req_pid")
	private Long reqPid;
	private String param;
	private String description;

	public Long getReqPid() {
		return reqPid;
	}

	public void setReqPid(Long reqPid) {
		this.reqPid = reqPid;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
