import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;


public class CityManager implements Serializable {
	private ArrayList<City> cityList;

	public CityManager() {
	
	}

	public ArrayList<City> getCityList() {
		return cityList;
	}

	public void setCityList(ArrayList<City> cityList) {
		this.cityList = cityList;
	}


			
		

}

