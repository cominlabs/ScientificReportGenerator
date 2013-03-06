package fr.inria.cominlabs.activityreport.model;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean(name="authorBean")
@RequestScoped
public class Author {
	
	private String firstLastName;

	public String getFirstLastName() {
		return this.firstLastName;
	}

	public void setFirstLastName(String firstLastName) {
		this.firstLastName = firstLastName;
	}


}
