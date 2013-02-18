package fr.inria.cominlabs.activityreport.handler;

import java.util.HashMap;

public class Editors {
HashMap<String,String> list_editors;
Editors(){
	list_editors =new HashMap<String,String> ();
	list_editors.put("acm", "AcmURLHandler");
	list_editors.put("springer", "SpringerNewURLHandler");
	
}

public String getEditor(String url){
	return null;
}
}
