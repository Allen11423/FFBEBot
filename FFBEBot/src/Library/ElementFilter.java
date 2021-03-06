package Library;

import java.util.ArrayList;

import org.jsoup.nodes.Element;
/**
 * Very naive filter for Elements
 * Will break on stuff being named "null"
 */
public class ElementFilter {
	private String tag="null";
	private ArrayList<String> atrs=new ArrayList<String>();
	private String text="null";
	private String id="null";
	//not sure why this one is a constructor.
	public ElementFilter(){
	}
	//stuff match on the tagname, text and the id of the element
	public ElementFilter(String tagname){
		tag=tagname;
	}
	public ElementFilter(String tagname,String text){
		tag=tagname;
		this.text=text;
	}
	public ElementFilter(String tagname, String text,String id){
		this.id=id;
	}
	//only returns true if all the properties match
	public boolean filtered(Element ele){
		if(!(tag.equals("null"))){
			if(ele.getElementsByTag(tag).size()==0){
				return false;
			}
		}
		if(!(text.equals("null"))){
			if(!ele.text().equals(text)){
				return false;
			}
		}
		if(!(id.equals("null"))){
			if(!ele.attr("id").equals(id)){
				return false;
			}
		}
		if(atrs.size()>0){
			for(String s:atrs){
				if(!ele.hasAttr(s)){
					return false;
				}
			}
		}
		return true;
	}
	
}
