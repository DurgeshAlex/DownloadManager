package downloder;

public class main {
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		// System.out.print(stream);
		Download downloadThread1 = new Download(
				"https://dandeacon.com/mp3/07%20acorn%20master/06_Breast_Cake_Penis_Sleeve.mp3",null);
		//Download downloadThread2 = new Download(
		//		"http://dl8.heyserver.in/serial/Riverdale/S01/480p/Riverdale.S01E10.480p.HDTV.x264.mkv");
		
		downloadThread1.start();
		
		//downloadThread2.start();
		
		
		

	}

}
