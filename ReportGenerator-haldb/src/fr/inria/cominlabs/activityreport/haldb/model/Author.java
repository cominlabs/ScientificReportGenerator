package fr.inria.cominlabs.activityreport.haldb.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the Authors database table.
 * 
 */
@Entity
@Table(name="Authors")
public class Author implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private String authorID;

	private String authorForeName;

	private String authorSurName;

	//bi-directional many-to-many association to Document
	@ManyToMany
	@JoinTable(
		name="Documents_has_Authors"
		, joinColumns={
			@JoinColumn(name="authorID")
			}
		, inverseJoinColumns={
			@JoinColumn(name="documentID")
			}
		)
	private List<Document> documents;

	public Author() {
	}

	public String getAuthorID() {
		return this.authorID;
	}

	public void setAuthorID(String authorID) {
		this.authorID = authorID;
	}

	public String getAuthorForeName() {
		return this.authorForeName;
	}

	public void setAuthorForeName(String authorForeName) {
		this.authorForeName = authorForeName;
	}

	public String getAuthorSurName() {
		return this.authorSurName;
	}

	public void setAuthorSurName(String authorSurName) {
		this.authorSurName = authorSurName;
	}

	public List<Document> getDocuments() {
		return this.documents;
	}

	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}
	
	public void addDocuments(Document documents){
	    if (!this.documents.contains(documents)){
		this.documents.add( documents);
	    }
	    
	}
}