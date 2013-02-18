package fr.inria.cominlabs.activityreport.model;

import java.util.List;

public class Article {
	
	private int year;
	private String title;
	private String  summary;
	private String url;
	private List<Author> authors;
	public int getYear() {
		return this.year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getTitle() {
		return this.title;
	}
	public String getUrl() {
		return this.url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSummary() {
		return this.summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public List<Author> getAuthors() {
		return this.authors;
	}
	public void setAuthors(List<Author> authors) {
		this.authors = authors;
	}
	
	public void addAuthor(Author author){
		authors.add(author);
	}
	
	public void deleteAuthor(Author author){
		authors.remove(author);
	}
	

}
