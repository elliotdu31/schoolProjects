import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class SoundSample {
	public final int sampleRate;
	public double[] data;

	SoundSample(int sampleRate, byte[] data) {
		this.sampleRate = sampleRate;
		this.data = new double[data.length];
		for (int i = 0; i < data.length; i++)
			this.data[i] = (double) data[i];
	}

	SoundSample(int sampleRate, double[] data) {
		this.sampleRate = sampleRate;
		this.data = data;
	}

	SoundSample(int sampleRate, int length, byte[] data) {
		this.sampleRate = sampleRate;
		this.data = new double[length];
		for (int i = 0; i < length; i++)
			this.data[i] = (double) data[i];
	}
	
	static SoundSample merge(SoundSample sound1, SoundSample sound2) {
		if(sound1 == null)
			return null;
		if(sound2 == null)
			return sound1;
		if(sound1.sampleRate != sound2.sampleRate)
			return null;
		double[] data = new double[sound1.data.length + sound2.data.length];
		for(int i = 0; i < sound1.data.length; i++)
			data[i] = sound1.data[i];
		for(int i = 0; i < sound2.data.length; i++)
			data[sound1.data.length + i] = sound2.data[i];
		return new SoundSample(sound1.sampleRate, data);
	}
	
	static SoundSample merge(LinkedList<SoundSample> sounds) {
		int size = 0;
		for(SoundSample s : sounds)
			size += s.data.length;
		double[] data = new double[size];
		int i = 0;
		for(SoundSample s : sounds) {
			for(int j = 0; j < s.data.length; j++)
				data[i+j] = s.data[j];
			i += s.data.length;
		}
		return new SoundSample(sounds.getLast().sampleRate, data);
	}

	//==================================
	
	void gaussienner(double variance) {
		double moyenne = this.data.length*0.5;
		double diviseur = 2/(variance*moyenne*moyenne);
		for(int i = 0; i < this.data.length; i++) 
			this.data[i] *= Math.exp(-(i-moyenne)*(i-moyenne)*diviseur);
		
	}

	// ========================================================================================================================

	public boolean print(String file, String description) {
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
			out.println(description);
			out.println(this.data.length);
			out.println(this.sampleRate);
			for (int i = 0; i < data.length; i++) {
				out.print(this.data[i] + " ");
			}
			out.println();
			out.close();
			return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	static HashMap<String, LinkedList<SoundSample>> read(String file) {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			HashMap<String, LinkedList<SoundSample>> map = new HashMap<String, LinkedList<SoundSample>>();
			
			String description = in.readLine();
			while (description != null) {
				
				if (!map.containsKey(description))
					map.put(description, new LinkedList<SoundSample>());
				double[] data = new double[Integer.parseInt(in.readLine())];
				int sampleRate = Integer.parseInt(in.readLine());
				
				String[] x = in.readLine().split(" ");
				
				for (int i = 0; i < x.length; i++)
					data[i] = Double.parseDouble(x[i]);
				
				SoundSample s = new SoundSample(sampleRate, data);
				map.get(description).add(s);
				description = in.readLine();
				
			}
			in.close();
			return map;

		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	static LinkedList<Pair<SoundSample, String>> readList(String file) {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			LinkedList<Pair<SoundSample, String>> l = new LinkedList<Pair<SoundSample, String>>();
			
			String description = in.readLine();
			while (description != null) {
				
				double[] data = new double[Integer.parseInt(in.readLine())];
				int sampleRate = Integer.parseInt(in.readLine());
				
				String[] x = in.readLine().split(" ");
				
				for (int i = 0; i < x.length; i++)
					data[i] = Double.parseDouble(x[i]);
				
				l.add(new Pair<SoundSample, String>(new SoundSample(sampleRate, data), description));
				description = in.readLine();
				
			}
			in.close();
			return l;

		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
	}

	// ========================================================================================================================
	// audio
	// ========================================================================================================================

	private static TargetDataLine microphone;
	private static SourceDataLine audioLine;
	private static int audioSampleRate;
	private static byte[] buffer;

	public static void init(int sampleRate) {
		// Reglage du son
		audioSampleRate = sampleRate;
		AudioFormat format = new AudioFormat((float) sampleRate, 16, 1, true, true);
		DataLine.Info infoOut = new DataLine.Info(SourceDataLine.class, format);
		DataLine.Info infoIn = new DataLine.Info(TargetDataLine.class, format);

		if (!AudioSystem.isLineSupported(infoIn))
			System.out.println("Line not supported");
		try {
			microphone = (TargetDataLine) AudioSystem.getLine(infoIn);
			microphone.open(format);

			audioLine = (SourceDataLine) AudioSystem.getLine(infoOut);
			audioLine.open(format);

		} catch (LineUnavailableException e) {
			;
			e.printStackTrace();
		}

		audioLine.start();
		microphone.start();
		buffer = new byte[microphone.getBufferSize()];
	}
	
	public static SoundSample capture(int readerLength) {
		int numBytesRead = microphone.read(buffer, 0, readerLength);
		double[] data = new double[numBytesRead/2];
		ByteBuffer bb = ByteBuffer.wrap(buffer);
		for(int i = 0; i < data.length; i++) {
			//data[i] = sample.data[2*i]*256 + sample.data[2*i+1];
			data[i] = (double) bb.getShort();
		}
		return new SoundSample(audioSampleRate, data);
	}
	
	public static SoundSample capture(int readerLength, int threshold) {
		double sum = 0.0;
		int numBytesRead = microphone.read(buffer, 0, readerLength);
		double[] data = new double[numBytesRead/2];
		ByteBuffer bb = ByteBuffer.wrap(buffer);
		for(int i = 0; i < data.length; i++) {
			//data[i] = (double)buffer[2*i+1]*256 + (double)buffer[2*i];
			data[i] = (double) bb.getShort();
			sum += Math.pow(data[i], 2);
		}
		if(sum < threshold)
			data = new double[numBytesRead/2];
		return new SoundSample(audioSampleRate, data);
	}

	public void play() {
		byte[] s = new byte[this.data.length*2];
		for (int i = 0; i < this.data.length; i++) {
			s[2*i+1] = (byte)( (short)this.data[i] & 0xff );
			s[2*i] = (byte)( ((short)this.data[i] >> 8) & 0xff );
		}
		audioLine.write(s, 0, s.length);
	}
	// ========================================================================================================================

	static SoundSample soundGenerator(LinkedList<Integer> fToAdd, int length, int sampleRate, boolean D) {
		if (D) {
			double[] buffer = new double[length];
			for (double f : fToAdd)
				for (int j = 0; j < length; j++)
					buffer[j] += Math.sin(j * 2.0 * 3.14 * f / (double) sampleRate) * 10.0;
			return new SoundSample(sampleRate, buffer);
		} else {
			byte[] buffer = new byte[length];
			for (double f : fToAdd)
				for (int j = 0; j < length; j++)
					buffer[j] += (byte) (Math.sin(j * 2.0 * 3.14 * f / (double) sampleRate) * 30.0 / fToAdd.size());
			return new SoundSample(sampleRate, buffer);
		}
	}

	
	public SoundSample MovingAverage(int n) {
		if (n < 1)
			n = 1;
		double[] s = new double[data.length];
		for (int i = 0; i < n; i++) {
			s[i] = data[i];
			s[data.length - i + -1] = data[data.length - i - 1];
		}
		for (int i = n; i < data.length - n; i++) {
			double m = 0.0;
			for (int k = -n; k <= n; k++)
				m += data[i + k];
			if (n > 0)
				m /= (double) n;
			s[i] = m;
		}
		return new SoundSample(this.sampleRate, s);
	}

	public SoundSample SavitzkyGolay(int n) { // n = [2, 4] sinon MovingAverage
		if (n < 2 || n > 4)
			return MovingAverage(n);

		double[] s = new double[data.length];
		for (int i = 0; i < n; i++) {
			s[i] = (double) data[i];
			s[data.length - i - 1] = (double) data[data.length - i - 1];
		}
		if (n == 2) {
			double normalisation = 1.0 / 35.0;
			for (int i = 3; i < data.length - 2; i++)
				s[i] = (-30 * data[i - 2] + 12 * data[i - 1] + 17 * data[i] + 12 * data[i + 1] - 3 * data[i + 2])
						* normalisation;
		}
		if (n == 3) {
			double normalisation = 1.0 / 21.0;
			for (int i = 3; i < data.length - 3; i++)
				s[i] = (-2 * data[i - 3] + 3 * data[i - 2] + 6 * data[i - 1] + 7 * data[i] + 6 * data[i + 1]
						+ 3 * data[i + 2] - 2 * data[i + 3]) * normalisation;
		}
		if (n == 4) {
			double normalisation = 1.0 / 231.0;
			for (int i = 4; i < data.length - 4; i++)
				s[i] = (-21 * data[i - 4] + 14 * data[i - 3] + 39 * data[i - 2] + 54 * data[i - 1] + 59 * data[i]
						+ 54 * data[i + 1] + 39 * data[i + 2] + 14 * data[i + 3] - 21 * data[i - 4]) * normalisation;
		}
		return new SoundSample(this.sampleRate, s);
	}

	public SoundSample SavitzkyGolayBis(int n) { // n = [3, 4] sinon moving
													// average
		if (n < 3 || n > 4)
			return MovingAverage(n);

		double[] s = new double[data.length];
		for (int i = 0; i < n; i++) {
			s[i] = (double) data[i];
			s[data.length - i - 1] = data[data.length - i - 1];
		}
		if (n == 3) {
			double normalisation = 1.0 / 231.0;
			for (int i = 3; i < data.length - 3; i++)
				s[i] = (5 * data[i - 3] - 30 * data[i - 2] + 75 * data[i - 1] + 131 * data[i] + 75 * data[i + 1]
						- 30 * data[i + 2] + 5 * data[i + 3]) * normalisation;
		}
		if (n == 4) {
			double normalisation = 1.0 / 429.0;
			for (int i = 4; i < data.length - 4; i++)
				s[i] = (15 * data[i - 4] - 55 * data[i - 3] + 30 * data[i - 2] + 135 * data[i - 1] + 179 * data[i]
						+ 135 * data[i + 1] + 30 * data[i + 2] - 55 * data[i + 3] + 15 * data[i - 4]) * normalisation;
		}
		return new SoundSample(this.sampleRate, s);
	}

	public SoundSample HighPass(int fc) {
		double[] newData = new double[this.data.length];
		double a = 1.0/(1.0 + 2*Math.PI*(double)fc/(double)this.sampleRate);
		newData[0] = data[0];
		for(int i = 1; i < this.data.length; i++)
			newData[i] = a*(newData[i-1] + this.data[i]-this.data[i-1]);
		return new SoundSample(this.sampleRate, newData);
	}
	
	public SoundSample lowPass(int fc) {
		double[] newData = new double[this.data.length];
		double a = 1.0/(1.0 + (double)this.sampleRate/(2*Math.PI*(double)fc));
		newData[0] = data[0];
		for(int i = 1; i < this.data.length; i++)
			newData[i] = newData[i-1] + a*(this.data[i]-newData[i-1]);
		return new SoundSample(this.sampleRate, newData);
	}
	
}
