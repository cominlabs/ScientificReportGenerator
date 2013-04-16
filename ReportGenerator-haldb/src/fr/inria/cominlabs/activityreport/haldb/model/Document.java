package fr.inria.cominlabs.activityreport.haldb.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the Documents database table.
 * 
 */
@Entity
@Table(name="Documents")
public class Document implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private String documentID;

	@Lob
	private String documentAbstract;

	@Lob
	private String documentContent;

	@Lob
	private String documentTitle;

	private Integer yearPub;

	//bi-directional many-to-many association to Author
	@ManyToMany(mappedBy="documents")
	private List<Author> authors;

	//bi-directional many-to-one association to Category
	@ManyToOne
	@JoinColumn(name="categoryID")
	private Category category;

	public Document() {
	}

	public String getDocumentID() {
		return this.documentID;
	}

	public void setDocumentID(String documentID) {
		this.documentID = documentID;
	}

	public String getDocumentAbstract() {
		return this.documentAbstract;
	}

	public void setDocumentAbstract(String documentAbstract) {
		this.documentAbstract = documentAbstract;
	}

	public String getDocumentContent() {
		return this.documentContent;
	}

	public void setDocumentContent(String documentContent) {
		this.documentContent = documentContent;
	}

	public String getDocumentTitle() {
		return this.documentTitle;
	}

	public void setDocumentTitle(String documentTitle) {
		this.documentTitle = documentTitle;
	}

	public Integer getYearPub() {
		return this.yearPub;
	}

	public void setYearPub(Integer yearPub) {
		this.yearPub = yearPub;
	}

	public List<Author> getAuthors() {
		return this.authors;
	}

	public void setAuthors(List<Author> authors) {
		this.authors = authors;
	}

	public Category getCategory() {
		return this.category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

}