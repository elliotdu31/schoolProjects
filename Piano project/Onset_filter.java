
import java.util.LinkedList;

public class Onset_filter {
	public final double G0 = 49.0;
	double[] frequencies;
	LinkedList<FourrierSample> fourrier;
	double time_window;
	double[] o;
	double threshold;
	double silence;

	double[] energy(FourrierSample f1) {

		double[] b = new double[94];
		for (int i = 0; i < b.length; i++) {
			b[i] = 0;
		}
		for (int l = 0; l < f1.data.length; l++) {
			int frequency = (l * f1.sampleRate) / f1.data.length;
			if (frequency > frequencies[0] && frequency < frequencies[1]) {
				b[0] = b[0] + Math
						.pow(f1.data[l] * (1 - (frequencies[1] - frequency) / (frequencies[1] - frequencies[0])), 2);
			}
			for (int i = 0; i < b.length - 1; i++) {
				b[i] = b[i]
						+ Math.pow(
								f1.data[l] * (1
										- (frequency - frequencies[i + 1]) / (frequencies[i + 2] - frequencies[i + 1])),
								2);
				b[i + 1] = b[i + 1]
						+ Math.pow(
								f1.data[l] * (1
										- (frequencies[i + 2] - frequency) / (frequencies[i + 2] - frequencies[i + 1])),
								2);

			}

			if (frequency > frequencies[94] && frequency < frequencies[95]) {
				b[93] = b[93] + Math
						.pow(f1.data[l] * (1 - (frequency - frequencies[94]) / (frequencies[95] - frequencies[94])), 2);
			}

		}
		for (int i = 0; i < b.length; i++) {
			b[i] = Math.sqrt(b[i]);
		}
		return b;
	}

	double[] derive(double[] b_t1, double[] b_t) {
		double[] c = new double[b_t.length];
		for (int i = 0; i < c.length; i++) {
			c[i] = (b_t[i] - b_t1[i]>0)? (b_t[i] - b_t1[i]): 0;
		}
		return c;
	}

	double sum(double[] b) {
		double s = 0;
		for (int i = 0; i < b.length; i++) {
			s += b[i];
		}
		return s;
	}

	

	double onset_fonction(double[] b_t1, double[] b_t) {
		double fonction = 0;
		double[] c = this.derive(b_t1, b_t);
		double a = this.sum(c);
		double s = this.sum(b_t);
		if (s > this.silence) {
			fonction = a / s;
			return fonction;

		}
		return fonction;
	}

	private double[] func() {
		double[] o = new double[3];
		FourrierSample[] f = new FourrierSample[4];
		double[][] b = new double[4][];
		for (int i = 0; i < f.length; i++) {
			f[3 - i] = this.fourrier.removeLast();
		}
		for (int i = 0; i < f.length; i++) {
			b[i] = this.energy(f[i]);
		}
		for (int i = 1; i < o.length; i++) {
			o[i] = this.onset_fonction(b[i], b[i+1]);
		}
		return o;
	}

	boolean is_attack() {
		return (this.o[1] > this.o[2]) && (this.o[1] > this.o[0]) && (this.o[1] > this.threshold);
	}

	public Onset_filter( LinkedList<FourrierSample> fourrier, double threshold, double silence) {
		double[] freq = new double[96];
		double multiplicator = Math.pow(2.0, 1.0 / 12.0);

		double fInitial = G0;

		for (int i = 0; i < freq.length; i++) {
			freq[i] = fInitial;
			fInitial *= multiplicator;
		}
		this.frequencies = freq;
		this.threshold = threshold;
		this.silence = silence;
		this.fourrier = fourrier;
		time_window = ((double) fourrier.getFirst().data.length) / fourrier.getFirst().sampleRate;
		this.o = this.func();

	}

	static boolean isattack(LinkedList<FourrierSample> fourrier) {
		Onset_filter o = new Onset_filter( fourrier, 0.5, 0.2);
		return o.is_attack();
	}
	// threshold=0.2;
}
