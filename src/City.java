import java.io.Serializable;
import java.util.Comparator;


public class City implements Comparable<City>, Serializable {
	private String name;
	private String code;
	private String therm;
	
	public City(String nm, String cd){// Constructor #1
		name = nm;
		code = cd;
		therm = "Not";
	
	}
	public City(){      //Constructor #2 empty!
		
	}
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	public String toString(){
		return name+"-"+code;
	}
	
	@Override
	public int compareTo(City ct) {
		if(this.getTherm()=="Not" || ct.getTherm()=="Not"){
			if(this.getName().compareTo(ct.getName()) <0)    
				return -1;
			else if(this.getName().compareTo(ct.getName()) ==0)
				return 0;
			else 
				return 1;  
		}
	
		if(this.getTherm().compareTo(ct.getTherm()) <0)    
			return -1;
		else if(this.getTherm().compareTo(ct.getTherm()) ==0)
			return 1;                       //1 INSTEAD OF 0         //BIGGER
		else 
			return 1;  
	}

	/**@Override
	public int compareTo(City ct) {
	if(this.getName().compareTo(ct.getName()) <0)    
			return -1;
		else if(this.getName().compareTo(ct.getName()) ==0)
			return 0;
		else 
			return 1;  

	}  **/
	
	public String getTherm() {
		return therm;
	}
	public void setTherm(String therm) {
		this.therm = therm;
	}
}