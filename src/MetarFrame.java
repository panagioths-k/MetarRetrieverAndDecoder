import java.awt.*;

import javax.swing.*;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.MalformedInputException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.border.EmptyBorder;

public class MetarFrame extends JFrame implements Serializable{

	private JPanel contentPane;
	private JTextArea MetarText;
	private JTextArea DecodedText; 
	private ArrayList<City> cityList;
	private  CityManager citym;
	
	private JList<City> stationList ;// carries all the Cities
    private	JTextArea ThermArea ;
    
  
    private ArrayList<City> ThermList;
    private Map<String, String> terminologyMap ;
	
    private String Totaltmp ="";
	/**
	 * Create the frame.
	 */
	public MetarFrame(CityManager cm) {	
		super("Metar Retriever and Decoder");
		citym=cm;
		cityList = new ArrayList<>();
		
		
		ThermList = new ArrayList<City>();
		
		Data();    
		
		terminologyMap = new LinkedHashMap<String, String>();
		TermynologyMap();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(780, 600);
		this.setResizable(false);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton RetrieveButton = new JButton("Retrieve Metar Info");
		RetrieveButton.addActionListener(new ButtonListener());
		
	//	RetrieveButton.addActionListener(new ActionListener() ;
		
		RetrieveButton.setToolTipText("Click a station and press this button.");
		RetrieveButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		RetrieveButton.setBounds(20, 62, 303, 20);
		contentPane.add(RetrieveButton);
		
		JLabel lblRetrieveMetar = new JLabel("Retrieve Metar");
		lblRetrieveMetar.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblRetrieveMetar.setBounds(0, 0, 86, 20);
		contentPane.add(lblRetrieveMetar);
		
		JLabel lblSelectAStation = new JLabel("Select a station and click Retrieve");
		lblSelectAStation.setBounds(10, 31, 264, 20);
		contentPane.add(lblSelectAStation);
		
		JLabel lblAvailableMetarStatios = new JLabel("Available METAR stations:");
		lblAvailableMetarStatios.setBounds(10, 93, 151, 20);
		contentPane.add(lblAvailableMetarStatios);
		
		JLabel lblLowerTempretureCities = new JLabel("Lower Temprature cities:");
		lblLowerTempretureCities.setBounds(189, 93, 147, 20);
		contentPane.add(lblLowerTempretureCities);
		
		JLabel lblDecodedMetar = new JLabel("Decoded Metar");
		lblDecodedMetar.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblDecodedMetar.setBounds(10, 293, 112, 14);
		contentPane.add(lblDecodedMetar);
		                       //***** LISTS *****
		
		
		//Add the Cities to the stationlist//
		MyListModel listModel = new MyListModel();// MyListModel
	
		
		//***** stationList with JScrollpane *****
		JScrollPane stationListscrollPane = new JScrollPane();
		stationListscrollPane.setBounds(10, 121, 151, 167);
		contentPane.add(stationListscrollPane);
		
		stationList = new JList<City>(listModel.getDefList(cityList));
		stationListscrollPane.setViewportView(stationList);
		stationList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		stationList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		stationList.setVisibleRowCount(-1);
	
		//***** Text Areas *****
		MetarText = new JTextArea();
		MetarText.setFont(new Font("Tahoma", Font.PLAIN, 11));
		MetarText.setBounds(360, 11, 394, 277);
		MetarText.setEditable(false);
		contentPane.add(MetarText);
		
		DecodedText = new JTextArea();
		DecodedText.setBounds(10, 318, 744, 244);
		contentPane.add(DecodedText);
		
		ThermArea = new JTextArea();
		ThermArea.setBounds(189, 121, 161, 167);
	//	ThermArea.setEditable(false);
		contentPane.add(ThermArea);
	
		this.setVisible(true);
	}
	
	public class ButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			
			if(stationList.getSelectedValue()== null){
				JOptionPane.showMessageDialog(null, "Please choose a city first!");
			}
			else{
				City ct = new City();       // Get the city the user has chosen!
				ct = stationList.getSelectedValue();
				// ***** Reading form URL *****
				try{
					URL metarUrl = new URL("ftp://tgftp.nws.noaa.gov/data/observations/metar/stations/"+ct.getCode()+".TXT") ;
					//	InputStream is = metarUrl.openStream();
					//	Scanner metarScanner = new Scanner(is);
					BufferedReader in = new BufferedReader(new InputStreamReader(metarUrl.openStream()));
					String MetarLine1 = in.readLine(); //Date and Time
					String MetarLine2 =in.readLine(); // Decoded Matar 
				
					
					//***** DecodedText *****
					String TotalStr = "Location-"+ct;  //Location
					
					StringTokenizer st = new StringTokenizer(MetarLine2, " ");
					
					
					                     //***** Metar *****
					MetarText.setText(MetarLine1 +"\n" +MetarLine2);//Metar goes here
					String sMetar =st.nextToken() ;
					sMetar = st.nextToken();


					TotalStr +="\nDay of Month: "+MetarLine2.substring(5, 7); //Day of Month
					TotalStr += "\nTime: "+ MetarLine2.substring(7, 9) +":"+MetarLine2.substring(9,11); //Time
					TotalStr += " "+ terminologyMap.get(MetarLine2.substring(11, 12));                //Time 2
				
					while(st.hasMoreTokens() ){
						sMetar = st.nextToken();
						
						if(sMetar.length()==7){  //WIND (has length 7)
							if(terminologyMap.get(sMetar.substring(0, 3)) == null  ){ 
								TotalStr += "\nWind: True Direction = " +sMetar.substring(0, 3)+ " degrees, " +
										"Speed: "+sMetar.substring(3, 5)+ " "+terminologyMap.get(sMetar.substring(5)); 
							}
							else {   //incase Wind is Variable in Direction
								TotalStr += "\nWind: " +terminologyMap.get(sMetar.substring(0, 3))+ ", Speed: "+MetarLine2.substring(3, 5)+
										" " +terminologyMap.get(MetarLine2.substring(5)); 
							}
						}
						else if(sMetar.contains("NOSIG")){ //NOSIG
							TotalStr += "\nNo significant changes expected in the near future.";
						}
						else if(terminologyMap.get(sMetar.substring(0, 3)) !=null){ //CLOUDS
							TotalStr += "\nClouds: " +terminologyMap.get(sMetar.substring(0,3)) +
									" at " +sMetar.substring(3)+ "00 above aerodrome level";
						}
						else if( sMetar.indexOf("/")!= -1){   //TEMPERATURE AND DEWPOINT  has "/" )
							String TempStr = sMetar.substring(0, 2);
							ct.setTherm(TempStr);               //add the therm in the city
							
							TotalStr += "\nTemperature: " +sMetar.substring(0, 2)+ " degrees Celcius";
							TotalStr += "\nDewPoint: " +sMetar.substring(3)+ " degrees Celsius";
						}
						
						else if(sMetar.substring(0, 1).equals("Q") && sMetar.length()==5 ){//QNH (5 length and starts with "Q"
							TotalStr += "\nQNH (Sea-level pressure): " +sMetar.substring(1)+ " hPa";
						}
						else if(terminologyMap.get(sMetar) != null){  //WEATHER (may not exists)
							TotalStr += "\nWeather: " +terminologyMap.get(sMetar);
						}
						
						
						                                                                            //VISIBILITY has lenght 4,5,6
						else if(sMetar.length()==4 || (sMetar.length()==5 && terminologyMap.get(sMetar.substring(5)) !=null) || 
					          (sMetar.length()==6 && terminologyMap.get(sMetar.substring(5)) !=null) ){ 

							if(sMetar.indexOf("9999")!=-1){ //it is 9999
								TotalStr += "\nVisibility: Clear";
							}else
							{
								TotalStr += "\nVisibility: " +sMetar.substring(0, 4)+ " m ";

								if(sMetar.length()==5){                  //One Direction: N, E, S, W
									TotalStr += " direction "	+terminologyMap.get(sMetar.substring(4)) ;
								}
								else if (sMetar.length()==6){          //Two Directions: SE, SW, NW, NE
									TotalStr += " direction "	+terminologyMap.get(sMetar.substring(4)) ;
								}
							}


						}
							
						
						DecodedText.setText(TotalStr);

					}
					//***** substring ( , ]  

					
					
					//***** Temperature Cities *****
					ThermList.add(ct);
					
					MyListModel ThermModel= new MyListModel() ;
					DefaultListModel<City> defTemp = new DefaultListModel<City>();
					
					defTemp = ThermModel.getDefList(ThermList);
					
				
					
					for(int i=0; i<defTemp.size(); i++){
						Totaltmp += defTemp.get(i).toString() +" " +defTemp.get(i).getTherm() +" C\n" ;
						
					}
					ThermArea.setText("");
					ThermArea.setText( Totaltmp );
			
					Totaltmp="";
					
				}
				
				
				catch(NoSuchElementException nsuchelex){
				JOptionPane.showMessageDialog(null, "No More Tokens found!");
				}
				
				catch(Exception ex) { //GENERAL CATCH EXCEPTION             Warning Message
					JOptionPane.showMessageDialog(null,"METAR Info not available for the selected city","Error, No data", JOptionPane.ERROR_MESSAGE);
				}
				//Finished reading URL
     /** Date , not necessary...
				DateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss");//arrange the format!
				java.util.Date date = new java.util.Date();    //create the date ;
	**/			
				
				
				
				
				
			}
			
		}
		
	}
	
	
	/**
      If stations.ser exists --> Read it
      else 
         create it and add the cities
	 **/
	public void Data(){
		File Dir = new File("stations.ser");
		if(!Dir.exists()) {
			System.out.println("File Doesnt Exists and got created");
		
			/** Create Cities **/
			
			cityList.add(new City("Thessaloniki","LGTS"));
			cityList.add(new City("Athens","LGAV"));
			cityList.add(new City("Larisa","LGLR"));
			cityList.add(new City("Volos","LGBL"));
			cityList.add(new City("Kavala","LGKV"));
			cityList.add(new City("Kalamata","LGKL"));
			cityList.add(new City("Alexandroupolis","LGAL"));
			cityList.add(new City("Corfu","LGKR"));
			cityList.add(new City("Araxos","LGRX"));
			cityList.add(new City("Chania","LGSA"));
			cityList.add(new City("Chios","LGHI"));
			cityList.add(new City("Heraklion","LGIR"));
			cityList.add(new City("Karpathos","LGKP"));
			cityList.add(new City("Kos","LGKO"));
			cityList.add(new City("Mikonos","LGMK"));
			cityList.add(new City("Mytilene","LGMT"));
			cityList.add(new City("Preveza","LGPZ"));
			cityList.add(new City("Samos","LGSM"));
			cityList.add(new City("Skiathos","LGSK"));
			cityList.add(new City("Rhodes","LGRP"));
			cityList.add(new City("Zakynthos","LGZA"));
			cityList.add(new City("Argostolion","LGKF"));
			cityList.add(new City("Ioannina","LGIO"));
			cityList.add(new City("Naxos","LGNX"));
			cityList.add(new City("Sitia","LGST"));
			cityList.add(new City("Ermoupoli","LGSO"));
			cityList.add(new City("Kithira","LGKC"));
			cityList.add(new City("Fry (Kasos)","LGKS"));
			cityList.add(new City("Kozani","LGKZ"));
			cityList.add(new City("Moudhros (Limnos)","LGLM"));
			cityList.add(new City("Milos ","LGML"));
			cityList.add(new City("Paros","LGPA"));
			cityList.add(new City("Skiros","LGSY"));
			cityList.add(new City("Kastoria","LGKA"));
			cityList.add(new City("Sparta","LGSP"));
			cityList.add(new City("Kalymnos","LGKY"));
	
			citym.setCityList(cityList);
			/** Save the Cities **/
			try{	
				FileOutputStream fos = new FileOutputStream("stations.ser");
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(citym);//save the PRIVATE properties   //WRITE in the file everything we he done so far
				oos.flush();   //If the program crashes that data won't be lost. Cause an immediate write to disk from the buffer
				oos.close();
				fos.close();                   //this. = QManager from SaveFrame
			}  
			catch(Exception ioe) { ioe.printStackTrace(); }

		}
		else{   //stations.ser already exists
			try {

				FileInputStream fileIn = new FileInputStream(Dir);
				ObjectInputStream in = new ObjectInputStream(fileIn);
				citym = ( (CityManager) in.readObject()); 
				cityList = citym.getCityList();    //if the stations.ser exists, get the ArrayList<City>
				in.close();
				fileIn.close();
				
				for(City ct: cityList){ //As the file gets readed again , the Cities STILL HAVE the temperatures
					ct.setTherm("Not");   //so here the temps get deleted
				}
				System.out.println("Serializable File Got Read");
			}
			catch(Exception ex) {
				System.out.println("File not found!");
			}
		}
	
	}
	public void TermynologyMap(){
//		Map<String, String> terminologyMap = new LinkedHashMap<String, String>();
		terminologyMap.put("ACC","altocumulus castellanus");
		terminologyMap.put("ACSL","altocumulus standing lenticular cloud");
		terminologyMap.put("AO1","automated station without precipitation discriminator");
		terminologyMap.put("APCH","approach");
		terminologyMap.put("APRX","approximately");
		terminologyMap.put("AUTO","fully automated report");
		terminologyMap.put("BC","patches of");
		terminologyMap.put("BL","blowing");
		terminologyMap.put("C","center");
		terminologyMap.put("CB","cumulonimbus cloud");
		terminologyMap.put("CC","cloud-cloud lightning");
		terminologyMap.put("cd","candela");
		terminologyMap.put("CHI","cloud-height indicator");
		terminologyMap.put("CIG","ceiling");
		terminologyMap.put("CONS","continuous");
		terminologyMap.put("DS","duststorm");
		terminologyMap.put("DSNT","distant");
		terminologyMap.put("DVR","dispatch visual range");
		terminologyMap.put("FC","funnel cloud");
		terminologyMap.put("FG","fog");
		terminologyMap.put("FIRST","first observation after a break in coverage at manual station");
		terminologyMap.put("FROIN","Frost On The Indicator");
		terminologyMap.put("FU","smoke");
		terminologyMap.put("GR","hail");
		terminologyMap.put("HLSTO","hailstone");
		terminologyMap.put("IC","ice crystals");
		terminologyMap.put("INCRG","increasing");
		terminologyMap.put("KT","KNOTS");
		terminologyMap.put("LAST","last observation before a break in coverage at a manual station");
		terminologyMap.put("LTG","lightning");
		terminologyMap.put("METAR","routine weather report provided at fixed intervals");
		terminologyMap.put("MT", "mountains");
		terminologyMap.put("N/A", "not applicable");
		terminologyMap.put("NE", "NorthEast");
		terminologyMap.put("E", "East");
		terminologyMap.put("W", "West");
		terminologyMap.put("N", "North");
		terminologyMap.put("NW", "NorthWest");
		terminologyMap.put("S", "South");
		terminologyMap.put("SW", "SouthWest");
		terminologyMap.put("SE", "SouthEast");
		terminologyMap.put("NOSPECI", "no SPECI reports are taken at the station");
		terminologyMap.put("OCNL", "occasional");
		terminologyMap.put("OHD", "overhead");
		terminologyMap.put("OVR", "over");
		terminologyMap.put("PCPN", "precipitation");
		terminologyMap.put("PL", "ice pellets");
		terminologyMap.put("PO", "dust/sand whirls");
		terminologyMap.put("PRES", "Atmospheric pressure");
		terminologyMap.put("PRESRR", "pressure rising rapidly");
		terminologyMap.put("PY", "spray");
		terminologyMap.put("RA", "rain");
		terminologyMap.put("RV", "reportable value");
		terminologyMap.put("RVRNO", "RVR system values not available");
		terminologyMap.put("SCSL", "stratocumulus standing lenticular cloud");
		terminologyMap.put("SG", "snow grains");
		terminologyMap.put("SKC", "sky clear");
		terminologyMap.put("SLPNO", "sea-level pressure not available");
		terminologyMap.put("SN", "snow");
		terminologyMap.put("SP", "snow pellets");
		terminologyMap.put("SQ", "squalls");
		terminologyMap.put("STN", "station");
		terminologyMap.put("TCU", "towering cumulus");
		terminologyMap.put("TSNO", "thunderstorm information not available");
		terminologyMap.put("UTC", "Coordinated Universal Time");
		terminologyMap.put("VA", "volcanic ash");
		terminologyMap.put("VIS", "visibility");
		terminologyMap.put("VR", "visual range");
		terminologyMap.put("VV", "vertical visibility");
		terminologyMap.put("WG/SO", "Working Group for Surface Observations");
		terminologyMap.put("WND", "wind");
		terminologyMap.put("WSHFT", "wind shift");
		terminologyMap.put("ALP", "airport location point");
		terminologyMap.put("ACFT MSHP", "aircraft mishap");
		terminologyMap.put("APRNT", "apparent");
		terminologyMap.put("ATCT", "airport traffic control tower");
		terminologyMap.put("B", "began");
		terminologyMap.put("BKN", "broken (5-7/8ths of the sky covered with cloud)");
		terminologyMap.put("BR", "mist");
		terminologyMap.put("CA", "cloud-air lightning");
		terminologyMap.put("CBMAM", "cumulonimbus mammatus cloud");
		terminologyMap.put("CCSL", "cirrocumulus standing lenticular cloud");
		terminologyMap.put("CG", "cloud-ground lightning");
		terminologyMap.put("CHINO", "sky condition at secondary location not available");
		terminologyMap.put("CLR", "clear sky");
		terminologyMap.put("COR", "correction to a previously disseminated observation");
		terminologyMap.put("DR", "low drifting");
		terminologyMap.put("DSIPTG", "dissipating");
		terminologyMap.put("DU", "widespread dust");
		terminologyMap.put("DZ", "drizzle");
		terminologyMap.put("FEW", "few clouds (1-2/8ths of the sky covered with cloud)");
		terminologyMap.put("FIBI", "filed but impracticable to transmit");
		terminologyMap.put("FROPA", "frontal passage");
		terminologyMap.put("FRQ", "frequent");
		terminologyMap.put("FZ", "freezing");
		terminologyMap.put("-","light");
		terminologyMap.put("+","strong");
		terminologyMap.put("/","visual range data follows; separator between temp and dew point data.");
		terminologyMap.put("G","gust");
		terminologyMap.put("GS","small hail and/or snow pellets");
		terminologyMap.put("HZ","haze");
		terminologyMap.put("ICAO","International Civil Aviation Organization");
		terminologyMap.put("INTMT","intermittent");
		terminologyMap.put("L","left (with reference to runway designation)");
		terminologyMap.put("LST","Local Standard Time");
		terminologyMap.put("LWR","lower");
		terminologyMap.put("max","maximum");
		terminologyMap.put("MI","shallow");
		terminologyMap.put("MOV","moved/moving/movement");
		terminologyMap.put("NCDC","National Climatic Data Center");
		terminologyMap.put("NOS","National Ocean Survey");
		terminologyMap.put("NOTAM","Notice to Airmen");
		terminologyMap.put("NWS","National Weather Service");
		terminologyMap.put("OFCM","Office of the Federal Coordinator for Meteorology");
		terminologyMap.put("OVC","overcast (8/8ths of the sky covered with cloud)");
		terminologyMap.put("P","indicates greater than the highest reportable value");
		terminologyMap.put("PK WND","peak wind");
		terminologyMap.put("PNO","precipitation amount not available");
		terminologyMap.put("PR","partial");
		terminologyMap.put("PRESFR","pressure falling rapidly");
		terminologyMap.put("PWINO","precipitation identifier sensor not available");
		terminologyMap.put("R","runway");
		terminologyMap.put("RTD","Routine Delayed (late) observation");
		terminologyMap.put("RVR","Runway visual range");
		terminologyMap.put("RY","runway");
		terminologyMap.put("SA","sand");
		terminologyMap.put("SCT","scattered (3-4/8ths of the sky covered with cloud)");
		terminologyMap.put("SFC","surface (i.e. ground level)");
		terminologyMap.put("SH","showers of");
		terminologyMap.put("SLP","sea-level pressure");
		terminologyMap.put("SM","statute miles");
		terminologyMap.put("SNINCR","snow increasing rapidly");
		terminologyMap.put("SPECI","an unscheduled report taken when certain criteria have been met");
		terminologyMap.put("SS","sandstorm");
		terminologyMap.put("TS","thunderstorm");
		terminologyMap.put("TWR","tower");
		terminologyMap.put("UP","unknown precipitation");
		terminologyMap.put("V","variable");
		terminologyMap.put("VC","in the vicinity");
		terminologyMap.put("VISNO","visibility at secondary location not available");
		terminologyMap.put("VRB","variable");
		terminologyMap.put("WMO","World Meteorological Organization");
		terminologyMap.put("WS","wind shear");
		terminologyMap.put("Z","Zulu, i.e., Coordinated Universal Time");
	}
}

