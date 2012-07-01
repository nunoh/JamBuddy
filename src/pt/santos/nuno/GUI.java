package pt.santos.nuno;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import javax.swing.JSeparator;
import java.awt.Font;

public class GUI implements WindowListener {

	private JFrame frame;

	// string arrays
	private final static String[] keys = { "C", "D", "E", "F", "G", "A", "B" };
	private final static String[] accidentals = { "b", " ", "#" };

	JTextField tfs[];

	// spinners
	private JSpinner spnBPM;

	// comboboxes
	private JComboBox cbbPatterns;
	private JComboBox cbbKeys;
	private JComboBox cbbAccidentals;

	// labels
	private JLabel lblKey;
	private JLabel lblBpm;
	private JLabel lblPattern;

	// panels
	private JPanel panelChords;

	private boolean paused = false;

	private static Api app;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {					
					app = new Api();
					app.loadXML();
					app.loadSongs();
					app.openMidiDevice();

					GUI window = new GUI();
					window.frame.setVisible(true);
					//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public GUI() {
		initialize();
	}

	public void start() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		frame.setVisible(true);
	}

	private void initialize() {

		frame = new JFrame();
		frame.setTitle("Jazz Markov Generator");
		frame.setBounds(100, 100, 587, 480);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JPanel panelToolbar = new JPanel();
		panelToolbar.setBounds(0, 0, 171, 192);
		frame.getContentPane().add(panelToolbar);
		panelToolbar.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("47px"),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("1px"),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("32px"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("33px"),},
				new RowSpec[] {
				FormFactory.LINE_GAP_ROWSPEC,
				RowSpec.decode("23px"),
				FormFactory.LINE_GAP_ROWSPEC,
				RowSpec.decode("20px"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));

		// KEY
		lblKey = new JLabel("Key");
		panelToolbar.add(lblKey, "2, 2, right, default");

		cbbKeys = new JComboBox(keys);
		panelToolbar.add(cbbKeys, "6, 2, 3, 1, fill, center");

		cbbAccidentals = new JComboBox(accidentals);
		panelToolbar.add(cbbAccidentals, "10, 2, fill, center");

		// BPM
		lblBpm = new JLabel("BPM");
		panelToolbar.add(lblBpm, "2, 4, right, default");

		SpinnerModel sm = new SpinnerNumberModel(Api.DEFAULT_SEQUENCE_BPM, 20, 200, 1);
		spnBPM = new JSpinner(sm);
		panelToolbar.add(spnBPM, "6, 4, 4, 1, fill, top");

		// PATTERN
		lblPattern = new JLabel("Pattern");
		panelToolbar.add(lblPattern, "2, 6, right, default");

		String patterns[] = new String[app.getPatterns().size()];
		for (int i = 0; i < app.getPatterns().size(); i++) {
			Pattern p = app.getPatterns().get(i);
			patterns[i] = p.getName();
		}

		cbbPatterns = new JComboBox(patterns);
		panelToolbar.add(cbbPatterns, "6, 6, 5, 1, left, top");
		cbbPatterns.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox)e.getSource();
				String sPattern = (String)cb.getSelectedItem();
				app.setPattern(new Pattern(sPattern)); 
			}		
		});

		// LOOP
		JCheckBox chckbxLoop = new JCheckBox("Loop");
		panelToolbar.add(chckbxLoop, "6, 8, 5, 1, left, default");
		
		JLabel lblLoopCount = new JLabel("Loop Count");
		panelToolbar.add(lblLoopCount, "2, 10, 7, 1, center, default");
		
		JSpinner spinner = new JSpinner();
		panelToolbar.add(spinner, "10, 10");

		// CHORDS
		JPanel panelChords = new JPanel();
		panelChords.setBounds(118, 249, 300, 100);
		frame.getContentPane().add(panelChords);
		panelChords.setLayout(new GridLayout(4, 4, 1, 1));

		tfs = new JTextField[20];

		for (int i = 0; i < 16; i++) {
			JTextField tf = new JTextField();
			tf.setHorizontalAlignment(SwingConstants.CENTER);
			tfs[i] = tf;
		}

		for (int i = 0; i < tfs.length; i++) {
			JTextField tf = tfs[i];
			if (tf != null)
				panelChords.add(tf);
		}

		// PLAY
		JButton btnPlay = new JButton("");
		btnPlay.setIcon(new ImageIcon("icons\\play.png"));
		btnPlay.setBounds(181, 21, 50, 50);
		frame.getContentPane().add(btnPlay);
		btnPlay.addActionListener(new ActionListener() {
			Progression prog;
			public void actionPerformed(ActionEvent e) {
				if (paused) {
					Api.sequencer.start();
					return;
				}

				// build progression
				for (int i = 0; i < tfs.length; i++) {
					JTextField tf = tfs[i];
					if (tf != null) {
						handle(tf);
					}
				}

				// bpm
				int bpm = (Integer) spnBPM.getValue();
				app.setBPM(bpm);

				// play
				app.setProgression(prog);
				app.playProgression();		
			}
			
			public void handle(JTextField tf) {
				String txt = tf.getText();
				if (txt.equals("")) {
					tf.setBackground(Color.WHITE);
				}

				else {
					try {
						Chord c = new Chord(txt);					
						prog.addChord(c);
						System.out.println(c);
						tf.setBackground(Color.WHITE);
					}
					catch (Exception exc) {
						exc.printStackTrace();
						tf.setBackground(Color.red);
					}
				}
			}
		});
		
		

	// EXPORT
	JButton btnExportMidi = new JButton("");
	btnExportMidi.setIcon(new ImageIcon("icons\\export.png"));
	btnExportMidi.setBounds(481, 21, 50, 50);
	frame.getContentPane().add(btnExportMidi);

	// SAVE
	JButton btnSave = new JButton("");
	btnSave.setIcon(new ImageIcon("icons\\save.png"));
	btnSave.setBounds(361, 21, 50, 50);
	frame.getContentPane().add(btnSave);

	// RTHYTMIC
	JSlider slRhytmic = new JSlider();
	slRhytmic.setBounds(315, 105, 200, 23);
	frame.getContentPane().add(slRhytmic);

	JLabel lblRhytmic = new JLabel("Rhytmic Density");
	lblRhytmic.setBounds(209, 114, 96, 14);
	frame.getContentPane().add(lblRhytmic);

	// HARMONIC
	JLabel lblHarmonicComplexity = new JLabel("Harmonic Complexity");
	lblHarmonicComplexity.setBounds(181, 160, 124, 14);
	frame.getContentPane().add(lblHarmonicComplexity);

	JSlider slHarmonic = new JSlider();
	slHarmonic.setBounds(315, 151, 200, 23);
	frame.getContentPane().add(slHarmonic);

	// GENERATE
	JButton btnGenerate = new JButton("");
	btnGenerate.setIcon(new ImageIcon("icons\\generate.png"));
	btnGenerate.setBounds(421, 21, 50, 50);
	btnGenerate.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			for (int i = 0; i < 4; i++) {
				String next = app.markov.getNext();
				System.out.println(next);
			}
		}
	});
	frame.getContentPane().add(btnGenerate);

	// STOP
	JButton btnStop = new JButton("");
	btnStop.setIcon(new ImageIcon("icons\\stop.png"));
	btnStop.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if (Api.sequencer.isOpen()) {
				Api.sequencer.stop();
			}
		}
	});
	btnStop.setBounds(301, 21, 50, 50);
	frame.getContentPane().add(btnStop);

	// PAUSE
	JButton btnPause = new JButton("");
	btnPause.setIcon(new ImageIcon("icons\\pause.png"));
	btnPause.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if (Api.sequencer.isOpen()) {
				paused = true;
				Api.sequencer.stop();
			}
		}
	});
	btnPause.setBounds(241, 21, 50, 50);
	frame.getContentPane().add(btnPause);
	
	JSeparator separator = new JSeparator();
	separator.setBounds(334, 21, 1, 2);
	frame.getContentPane().add(separator);
	
	JSlider slider = new JSlider();
	slider.setOrientation(SwingConstants.VERTICAL);
	slider.setBounds(23, 217, 55, 146);
	frame.getContentPane().add(slider);
	
	JLabel lblPlay = new JLabel("Play");
	lblPlay.setFont(new Font("Tahoma", Font.PLAIN, 10));
	lblPlay.setHorizontalAlignment(SwingConstants.CENTER);
	lblPlay.setBounds(183, 74, 46, 14);
	frame.getContentPane().add(lblPlay);
	
	JLabel lblPause = new JLabel("Pause");
	lblPause.setFont(new Font("Tahoma", Font.PLAIN, 10));
	lblPause.setHorizontalAlignment(SwingConstants.CENTER);
	lblPause.setBounds(243, 74, 46, 14);
	frame.getContentPane().add(lblPause);
	
	JLabel lblStop = new JLabel("Stop");
	lblStop.setFont(new Font("Tahoma", Font.PLAIN, 10));
	lblStop.setHorizontalAlignment(SwingConstants.CENTER);
	lblStop.setBounds(303, 74, 46, 14);
	frame.getContentPane().add(lblStop);
	
	JLabel lblSave = new JLabel("Save");
	lblSave.setFont(new Font("Tahoma", Font.PLAIN, 10));
	lblSave.setHorizontalAlignment(SwingConstants.CENTER);
	lblSave.setBounds(363, 74, 46, 14);
	frame.getContentPane().add(lblSave);
	
	JLabel lblGenerate = new JLabel("Generate");
	lblGenerate.setFont(new Font("Tahoma", Font.PLAIN, 10));
	lblGenerate.setHorizontalAlignment(SwingConstants.CENTER);
	lblGenerate.setBounds(423, 74, 46, 14);
	frame.getContentPane().add(lblGenerate);
	
	JLabel lblExport = new JLabel("Export");
	lblExport.setFont(new Font("Tahoma", Font.PLAIN, 10));
	lblExport.setHorizontalAlignment(SwingConstants.CENTER);
	lblExport.setBounds(483, 74, 46, 14);
	frame.getContentPane().add(lblExport);
	
	}

	public void windowClosing(WindowEvent e) {
		app.closeMidiDevice();
	}

	public void windowActivated(WindowEvent e) { }
	public void windowClosed(WindowEvent e) { }
	public void windowDeactivated(WindowEvent e) { }
	public void windowDeiconified(WindowEvent e) { }
	public void windowIconified(WindowEvent e) { }
	public void windowOpened(WindowEvent e) { }	
}
