package fr.inria.cominlabs.activityreport.webapp.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean(name="myBean")
@RequestScoped
public class TestBean {
private String filename;

public String getFilename() {
	return this.filename;
}

public void setFilename(String filename) {
	this.filename = filename;
}

}
