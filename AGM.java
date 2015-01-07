import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class AGM {

	/**
	 * @param args
	 * @throws FileNotFoundException
	 */
	
	static String aisFilename;
	static String gpsFilename;
	static String outputFilename;
	static ArrayList<String> aisData = new ArrayList<String>();
	static ArrayList<String> gpsData = new ArrayList<String>();
	private static BufferedReader ais;
	private static BufferedReader gps;
	
	public static void main(String[] args) throws IOException {
		// get filename
		System.out.println("START PROGRAM");
		getFilename();
		// get file data
		System.out.println("READ FILES");
		getFile();
		// replace space to comma
		System.out.println("Replace space to comma");
		replaceComma();
		// replace GPS Time
		System.out.println("replace GPS Time in AIS DATA");
		replace();
		
		System.out.println("Finish");
	}
	
	private static void replace() throws IOException {
		FileWriter fw = new FileWriter(new File(outputFilename), true);
		BufferedWriter bw = new BufferedWriter(fw);
		int npercent = 0;
		int bpercent = 0;
		for(int i = 0; i < aisData.size(); i++) {
			String aisLine = aisData.get(i);
			String[] aisArray = aisLine.split(",");
			if(aisArray.length > 4) {
				if(aisArray[5].equals("$GPRMC") || aisArray[5].equals("$GPGGA"))
					aisArray = replaceTime(aisArray);
				for(int i1 = 5; i1 < aisArray.length; i1++){
					bw.write(aisArray[i1]);
					if(i1 < aisArray.length - 1)
						bw.write(",");
				}
				bw.newLine();
				npercent = percent(i, aisData.size());
				if(npercent != bpercent) {
					System.out.println(npercent + "% Finished");
					bpercent = npercent;
				}
			}
		}
		bw.close();
	}

	private static int percent(int i, int size) {
		int per = (int)((double)i / (double)size * 100.0);
		return per;
	}

	private static String[] replaceTime(String[] aisArray) {
		for(int i = 0; i < gpsData.size(); i++) {
			String gpsLine = gpsData.get(i);
			String[] gpsArray = gpsLine.split(",");
			if(gpsArray.length > 4) {
				String[] aisTime = aisArray[3].split(":");
				String[] gpsTime = gpsArray[3].split(":");
				aisTime[2] = aisTime[2].substring(0, 2);
				gpsTime[2] = gpsTime[2].substring(0, 2);
				if(gpsArray[5].equals("$GPRMC") || gpsArray[5].equals("$GPGGA")) {
					if(aisArray[4].equals(gpsArray[4])) {
						if(aisArray[1].equals(gpsArray[1])) {
							if(aisArray[2].equals(gpsArray[2])) {
								if(aisTime[0].equals(gpsTime[0])) {
									if(aisTime[1].equals(gpsTime[1])) {
										if(aisTime[2].equals(gpsTime[2])) {
											aisArray[6] = gpsArray[6];
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return aisArray;
	}
	
	private static void replaceComma() {
		for(int i = 0; i < aisData.size(); i++) {
			String line = aisData.get(i);
			line = line.replaceAll(" ", ",");
			aisData.set(i, line);
		}
		
		for(int i = 0; i < gpsData.size(); i++) {
			String line = gpsData.get(i);
			line = line.replaceAll(" ", ",");
			gpsData.set(i, line);
		}
	}
	
	private static void getFile() throws IOException {
		ais = new BufferedReader(new FileReader(aisFilename));
		gps = new BufferedReader(new FileReader(gpsFilename));
		
		String line;
		while((line = ais.readLine()) != null) {
			aisData.add(line);
		}
		
		while((line = gps.readLine()) != null) {
			gpsData.add(line);
		}
	}
	
	private static void getFilename() throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.print("Please input AIS Data filename : ");
		aisFilename = in.readLine();
		
		System.out.print("Please input GPS Data filename : ");
		gpsFilename = in.readLine();
		
		System.out.print("Please output filename : ");
		outputFilename = in.readLine();
	}
}
