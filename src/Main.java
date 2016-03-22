import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;


public class Main implements Serializable {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		CityManager CM =  new CityManager();
		new MetarFrame(CM);
	
	}
}

