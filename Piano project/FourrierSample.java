
public class FourrierSample {
	final int sampleRate;
	double[] data;

	FourrierSample(int sampleRate, double[] data) {
		this.sampleRate = sampleRate;
		this.data = data;
	}

	FourrierSample(SoundSample soundSample) {
		this.sampleRate = soundSample.sampleRate;
		this.data = FFT.fft(soundSample.data);
		//this.data = FastFourierTransform.ComputeDft(soundSample.data);
	}

	// ========================================================================================================================

	private static final double E = 1.5;

	double[] PCP() {
		double[] descripteur = new double[12];
		for (int l = 1; l < this.data.length / 2; l++) {
			int k = M(l, this.data.length, this.sampleRate);
			descripteur[k] += Math.pow(this.data[l], E);
		}
		double s = 0;
		for (int l = 0; l < 12; l++) {
			s += descripteur[l];
		}
		for (int l = 0; l < 12; l++) {
			descripteur[l] = descripteur[l] / s;
		}
		return descripteur;
	}

	private static int M(int l, int n, int fs) {
		if (l == 0)
			return -1;
		else {
			int t = ((int) Math.round(12 * Math.log((((double) (fs * l)) / (double) n) / 261.63) / Math.log(2.0))) % 12;
			if (t < 0)
				return t + 12;
			return t;
		}

	}

	// ========================================================================================================================

	FourrierSample traiter() {
		double[] s = new double[data.length];
		double min = Double.MAX_VALUE;
		int iMin = (int) (100.0 / (double) sampleRate * (double) data.length);
		int iMax = (int) (5000.0 / (double) sampleRate * (double) data.length);
		for (int i = iMin; i < iMax; i++) {
			s[i] = data[i];
			if (data[i] < min)
				min = data[i];
		}
		/*
		 * System.out.println("sum = " + medium + " iMax - iMin =" +
		 * (double)(iMax - iMin)); medium /= (double)(iMax - iMin);
		 * System.out.println("Min = " + min + " Medium = " + medium); for (int
		 * i = iMin; i < iMax; i++) { s[i] = s[i] - medium; if (s[i] < 0.0) s[i]
		 * = 0.0; }
		 */
		return new FourrierSample(sampleRate, s);
	}
	
	void logFilter() {
		for(int i = 0; i < data.length; i++)
			data[i] *= Math.log(i);
	}


	double[] HPCP() {
		double[] descripteur = new double[12];
		for (int l = 1; l < this.data.length / 2; l++) {
			double t = 12 * Math.log((((double) (sampleRate * l)) / (double) data.length) / 261.63) / Math.log(2.0);
			int k = (int) Math.round(t);
			while (k >= 12) {
				t -= 12.0;
				k -= 12;
			}
			while (k < 0) {
				t += 12.0;
				k += 12;
			}

			double d = Math.abs((t - k) * 10); //Réglable

			if (d <= 1.0)
				descripteur[k] += Math.pow(Math.cos(Math.PI / 2 * d), 2) * Math.pow(this.data[l], E);
			
		}
		double s = 0;
		for (int l = 0; l < 12; l++)
			s += descripteur[l];
		for (int l = 0; l < 12; l++)
			descripteur[l] = descripteur[l] / s;
		return descripteur;

	}


	double[] clavier() {
		double[] descripteur = new double[88];
		double constante = Math.pow(2.0, 1.0/12.0);
		double iMin = data.length*32.7032/this.sampleRate;
		double iMax = iMin*constante;
		double sum = 0;
		for(int i = 0; i < descripteur.length; i++) {
			
			for(int k = (int)iMin; k < (int)iMax; k++) 
				descripteur[i] += this.data[k]/k;
			sum +=descripteur[i];
			iMin = iMax;
			iMax *= constante;
		}
		for (int l = 0; l < descripteur.length; l++) //normalisation
			descripteur[l] = descripteur[l] * 5 / sum;
		
		return descripteur;
	}
	// ========================================================================================================================
	
	double[] cutDescriptor(int fmin, int fmax, int n){
		double[] descripteur = new double[n];
		double a = Math.pow((double)fmax/(double)fmin, 1.0/(double)n);
		double iMin = (double)data.length*fmin/(double)this.sampleRate;
		double iMax = iMin*a;
		double sum = 0;
		for(int i = 0; i < n; i++) {
			for(int k = (int)iMin; k < (int)iMax; k++) 
				descripteur[i] += this.data[k]/k;
			sum +=descripteur[i];
			iMin = iMax;
			iMax *= a;
		}
		for (int l = 0; l < descripteur.length; l++) //normalisation
			descripteur[l] = descripteur[l]*10.0/ sum;
		return descripteur;
	}
	
	double[] cutDescriptor2(int fstart, int n) {
		double[] descripteur = new double[n];
		double a = Math.pow(2, 1.0/12.0);
		double sum = 0.0;
		double fmin = (double)fstart/Math.pow(2, 1.0/24.0);
		double fmax = fmin*a;
		for(int i = 0; i < n; i++) {
			for(int k = (int)(data.length*fmin/sampleRate); k < (int)(data.length*fmax/sampleRate); k++) 
				descripteur[i] += Math.pow(this.data[k], 2);
			sum += descripteur[i];
			fmin = fmax;
			fmax *= a;
		}
		
		for (int l = 0; l < descripteur.length; l++) //normalisation
			descripteur[l] = descripteur[l]*10.0/ sum;
		return descripteur;
		
	}
	// ========================================================================================================================
	void cut(int fmin, int fmax) {
		for (int i = (int) ((double) fmin / (double) sampleRate * (double) data.length); i < (int) ((double) fmax
				/ (double) sampleRate * (double) data.length); i++)
			data[i] = 0;
	}

}
