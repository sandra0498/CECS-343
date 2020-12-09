package miniplayertest;
//
import java.net.URL;

public class BasicPlayer {
	private static int Status;
	private static double Gain;

	public static final int UNKNOWN = 0;
	public static final int OPENED  = 1;
	public static final int PLAYING = 2;
	public static final int STOPPED = 3;

	public BasicPlayer(){
		Status = UNKNOWN;
		Gain = 0.0;
		System.out.println("MBP - Creating BasicPlayer object with status UNKNOWN");


	}
	public void open(URL url) throws BasicPlayerException{

		System.out.println("Opening URL file:"+url);
		Status = OPENED;
	}

	public void play()throws BasicPlayerException{
		System.out.println("MBP- Playing...");
		Status = PLAYING;
	}

	public void resume()throws BasicPlayerException{
		System.out.println("MBP - Resuming playback");
		Status = PLAYING;
	}

	public void stop()throws BasicPlayerException{
		System.out.println("MBP - Stopping play");
		Status = STOPPED;
	}

	public void setGain(double gain)throws BasicPlayerException{
		Gain = gain;
		System.out.println("MBP - Setting gain to "+ Gain );


	}

	public int getStatus(){
		System.out.println("MBP - Getting status - status is " + Status);
		return Status;
	}

}
