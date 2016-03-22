import java.util.*;

import javax.swing.*;


public class MyListModel extends AbstractListModel<City> implements Comparator<City>{

	private DefaultListModel<City> DefList;


	public MyListModel() {   // Constructor #1 Empty
		DefList = new DefaultListModel<City>();
		
		}
	
	
	/** Gets an ArrayList<City> 
	 * add the cities in TreeSet 
	 * and returns the cities sorted
	 */
	public DefaultListModel<City> getDefList(ArrayList<City> arrlist) { 
		TreeSet<City> TreeList = new TreeSet<City>(arrlist);
		for(City ct: TreeList){
			DefList.addElement(ct);
		}
	return DefList;
	}

	public void setDefList(DefaultListModel<City> defList) {
		DefList = defList;
	}

	public void addElement(City city){  //These 2 addElement are completely different methods from different Classes! 
		DefList.addElement(city);   
	}

	@Override
	public City getElementAt(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return DefList.getSize();
	}

	@Override
	public int compare(City ct1, City ct2) {
		return 0;
			}

}
