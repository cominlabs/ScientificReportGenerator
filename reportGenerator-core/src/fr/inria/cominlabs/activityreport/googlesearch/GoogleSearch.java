package fr.inria.cominlabs.activityreport.googlesearch;
	import java.io.IOException;
	import java.io.InputStreamReader;
	import java.io.Reader;
	import java.net.URL;
	import java.net.URLEncoder;
	import java.util.List;
	import com.google.gson.Gson;
	
	
	/**
	 * @author william Kokou D�dzo�
	 *
	 */
	
	
	
	public class GoogleSearch {
		
		/**
		 *  This method returns results of given Google search query.
		 *  @param query is object of type java.io.String representing the query.
		 *  @return Object representing the results of the query.
		 */
		
		public static GoogleResults googleSearchResults(String query) throws IOException{
			String address = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=";
			 query = query +" filetype:pdf";
			String charset = "UTF-8";
			URL url = new URL(address + URLEncoder.encode(query, charset));
			Reader reader = new InputStreamReader(url.openStream(), charset);
			GoogleResults results = new Gson().fromJson(reader, GoogleResults.class);
			return results;
		}
	}
	 
	
	/**
	 *  This class represents Google search results.
	 *
	 */
	class GoogleResults{
	 
	    private ResponseData responseData;
	    public ResponseData getResponseData() { return responseData; }
	    public void setResponseData(ResponseData responseData) { this.responseData = responseData; }
	    public String toString() { return "ResponseData[" + responseData + "]"; }
	 
	    static class ResponseData {
	        private List<Result> results;
	        public List<Result> getResults() { return results; }
	        public void setResults(List<Result> results) { this.results = results; }
	        public String toString() { return "Results[" + results + "]"; }
	    }
	 
	    static class Result {
	        private String url;
	        private String title;
	        public String getUrl() { return url; }
	        public String getTitle() { return title; }
	        public void setUrl(String url) { this.url = url; }
	        public void setTitle(String title) { this.title = title; }
	        public String toString() { return "Result[url:" + url +",title:" + title + "]"; }
	    }
	

}