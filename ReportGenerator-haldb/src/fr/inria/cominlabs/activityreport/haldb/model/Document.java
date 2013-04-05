package fr.inria.cominlabs.activityreport.haldb.model;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;
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

	private BigInteger authorID;

	@Temporal(TemporalType.DATE)
	private Date datePub;

	@Lob
	private String documentAbstract;

	@Lob
	private String documentContent;

	private String documentTitle;

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

	public BigInteger getAuthorID() {
		return this.authorID;
	}

	public void setAuthorID(BigInteger authorID) {
		this.authorID = authorID;
	}

	public Date getDatePub() {
		return this.datePub;
	}

	public void setDatePub(Date datePub) {
		this.datePub = datePub;
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