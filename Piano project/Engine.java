import java.util.LinkedList;

public class Engine {

	PCPcanvas pcpCanvas1;
	PCPcanvas pcpCanvas2;
	OscilloscopeCanvas oCanvas;
	FourierCanvas fCanvas;
	SheetCanvas sCanvas;

	LinkedList<SoundSample> soundQueue;
	LinkedList<SoundSample> mergeQueue;
	int maxMergeSize = 32;
	boolean merging;
	
	final int maxQueueSize = 43;
	final int sampleRate = 44100;
	final int readerLength = 2048;

	SoundSample testSample;
	int testSize = 400;
	int testPos = 0;

	// =====================================================================================
	Engine() {
		double[] dataSound = new double[testSize];
		testSample = new SoundSample(4, dataSound);

		soundQueue = new LinkedList<SoundSample>();
		mergeQueue = new LinkedList<SoundSample>();

		// Création des canvas
		pcpCanvas1 = new PCPcanvas();
		pcpCanvas2 = new PCPcanvas();
		oCanvas = new OscilloscopeCanvas(true, 8.0);
		fCanvas = new FourierCanvas();
		sCanvas = new SheetCanvas(40);

		// Création des fenètres
		new Window(pcpCanvas1, "PCP 1", 520, 350, 100, 10, true, false);
		new Window(pcpCanvas2, "PCP 2", 520, 350, 500, 10, true, false);
		new Window(fCanvas, "Fourier", 520, 350, 630, 10);
		new Window(oCanvas, "Signal", 300, 200, 100, 370);
		new Window(sCanvas, "Sheet", 600, 300, 200, 370);
		new Window(oCanvas, "Signal", 3000, 200, 100, 370);

		// Reglage du son
		SoundSample.init(sampleRate);
	}

	// =====================================================================================

	public void start() {
		while (main()) {

		}
	}

	// =====================================================================================
	boolean previousPick = false;
	long lastPickTime;
	private boolean main() { // renvoyer true pour continuer la boucle
		boolean picked = false;

		// ====================Gestion Son========================================
		//SoundSample soundSample = SoundSample.capture(readerLength, 100000);
		SoundSample soundSample = SoundSample.capture(readerLength);
		
		
		 // soundSample.gaussienner(0.2); soundSample =
		 soundSample.lowPass(5000); 
		 for(int i = 0; i < 0 ; i++) 
		 	soundSample = soundSample.lowPass(5000); 
		 for(int i = 0; i < 0 ; i++) 
		 	soundSample =  soundSample.HighPass(50);
		 

		soundQueue.addLast(soundSample);
		if (soundQueue.size() > maxQueueSize) {
			soundQueue.removeFirst();

			if (EnergyBeatDetector.isattack(soundQueue))
				picked = true;

			if (previousPick == true && picked == false) {
				picked = true;
				previousPick = false;
			} else {
				previousPick = picked;
				picked = false;

			}

			if (testPos < testSize) {

				testSample.data[testPos] = (picked) ? -1 : 0;
				testPos++;
			} else {

				for (int i = 1; i < testSize - 1; i++) {
					testSample.data[i] = testSample.data[i + 1];

				}
				testSample.data[testSize - 1] = (picked) ? -1 : 0;

			}
		}

		// ====================Fourrier===========================================
		//FourrierSample fourierSample = new FourrierSample(soundSample);
		// fourrierSample1.cut(0, 100);
		// fourrierSample1.logFilter();
		if (picked && merging) {
			merging = false;
			SoundSample mergedSound = SoundSample.merge(mergeQueue);
			//mergedSound.play();
			FourrierSample fourierSample = new FourrierSample(mergedSound);
			fCanvas.setData(fourierSample);
			pitchDetected(fourierSample.HPCP(), lastPickTime);
			lastPickTime = System.currentTimeMillis();
			mergeQueue = new LinkedList<SoundSample>();
			mergeQueue.addLast(soundSample);
			
		}
		else if(merging) {
			mergeQueue.addLast(soundSample);
			if(mergeQueue.size() > maxMergeSize) {
				merging = false;
				SoundSample mergedSound = SoundSample.merge(mergeQueue);
				//mergedSound.play();
				FourrierSample fourierSample = new FourrierSample(mergedSound);
				fCanvas.setData(fourierSample);
				
				pitchDetected(fourierSample.HPCP(), lastPickTime); // A terminer
				mergeQueue = new LinkedList<SoundSample>();
			}
		
				
		}
		else if(picked) {
			merging = true;
			mergeQueue.addLast(soundSample);
			lastPickTime = System.currentTimeMillis();
		}
		
		

		
		
		

		

		// =====================Merdouilles==========================================

		// PCP
		//PCP pcp = new PCP(fourierSample);

		// Affichage
		// pcpCanvas.setData(pcp.descripteur);

		// pcpCanvas.setData(pickQueue);
		//oCanvas.setData(testSample);
		oCanvas.setData(soundSample);
		//fCanvas.setData(fourierSample);

		//pcp.compute();
		//sCanvas.setData(pcp.toBool());

		loopIteration ();
		return true;
	}

	
	void pitchDetected(double[] accord, long t) {
		//code de Xavier et Toan
		pcpCanvas1.setData(accord);
		pcpCanvas2.setData(PCPtoAccord(accord));
	}
	void loopIteration () {
		//code de Xavier et Toan
	}
	
	public double[] PCPtoAccord(double[] PCP) {
		double[] accord = new double[12];
		for(int i = 0; i < 12; i++)
			accord[i] = PCP[i];
		double variance = 0.0;
		double moyenne = 1.0/12.0;
		for(int i = 0; i < 12; i++) 
			variance += Math.pow(accord[i] - moyenne, 2);
		variance *= moyenne;
		for(int i = 0; i < 12; i++)
			if(accord[i] < moyenne + variance*3)
				accord[i] = 0;
		return accord;
	}
	
	double booleaniser(double x) {
		if (x > 0.2)
			return 1.0;
		else
			return 0.0;
	}
	// =====================================================================================

	public static void main(String[] args) {
		Engine engine = new Engine();
		engine.start();
	}

}
