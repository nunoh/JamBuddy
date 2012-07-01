package pt.santos.nuno;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class GUI implements WindowListener {

	private JFrame frame;

	// string arrays
	private final static String[] keys = { "C", "D", "E", "F", "G", "A", "B" };
	private final static String[] accidentals = { "b", " ", "#" };
	private final static String[] patterns = { "Up", "Down", "Freestylo" };

	// the textfields
	private JTextField tf1, tf2, tf3, tf4, tf5, tf6, tf7, tf8, tf9, tf10, tf11, tf12, tf13, tf14, tf15, tf16;  

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

	private static Api app;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();					
//					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					window.frame.setVisible(true);

					app = new Api();
					app.loadXML();
					app.openMidiDevice();

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
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JPanel panelToolbar = new JPanel();
		panelToolbar.setBounds(0, 0, 171, 89);
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

		spnBPM = new JSpinner();
		panelToolbar.add(spnBPM, "6, 4, 5, 1, left, top");
		spnBPM.setValue(120);

		// PATTERN
		lblPattern = new JLabel("Pattern");
		panelToolbar.add(lblPattern, "2, 6, right, default");

		cbbPatterns = new JComboBox(patterns);
		panelToolbar.add(cbbPatterns, "6, 6, 5, 1, left, top");

		// panel chords
		JPanel panelChords = new JPanel();
		panelChords.setBounds(67, 151, 300, 100);
		frame.getContentPane().add(panelChords);
		panelChords.setLayout(new GridLayout(4, 4, 1, 1));

		tf1 = new JTextField();
		tf1.setHorizontalAlignment(SwingConstants.CENTER);
		tf1.setColumns(10);
		panelChords.add(tf1);

		tf2 = new JTextField();
		tf2.setHorizontalAlignment(SwingConstants.CENTER);
		tf2.setColumns(10);
		panelChords.add(tf2);

		tf3 = new JTextField();
		tf3.setHorizontalAlignment(SwingConstants.CENTER);
		tf3.setColumns(10);
		panelChords.add(tf3);

		tf4 = new JTextField();
		tf4.setHorizontalAlignment(SwingConstants.CENTER);
		tf4.setColumns(2);
		panelChords.add(tf4);

		tf5 = new JTextField();
		tf5.setHorizontalAlignment(SwingConstants.CENTER);
		tf5.setColumns(10);
		panelChords.add(tf5);

		tf6 = new JTextField();
		tf6.setHorizontalAlignment(SwingConstants.CENTER);
		tf6.setColumns(10);
		panelChords.add(tf6);

		tf7 = new JTextField();
		tf7.setHorizontalAlignment(SwingConstants.CENTER);
		tf7.setColumns(10);
		panelChords.add(tf7);

		tf8 = new JTextField();
		tf8.setHorizontalAlignment(SwingConstants.CENTER);
		tf8.setColumns(10);
		panelChords.add(tf8);

		tf9 = new JTextField();
		tf9.setHorizontalAlignment(SwingConstants.CENTER);
		tf9.setColumns(10);
		panelChords.add(tf9);

		tf10 = new JTextField();
		tf10.setHorizontalAlignment(SwingConstants.CENTER);
		tf10.setColumns(10);
		panelChords.add(tf10);

		tf11 = new JTextField();
		tf11.setHorizontalAlignment(SwingConstants.CENTER);
		tf11.setColumns(10);
		panelChords.add(tf11);

		tf12 = new JTextField();
		tf12.setHorizontalAlignment(SwingConstants.CENTER);
		tf12.setColumns(10);
		panelChords.add(tf12);

		tf13 = new JTextField();
		tf13.setHorizontalAlignment(SwingConstants.CENTER);
		tf13.setColumns(10);
		panelChords.add(tf13);

		tf14 = new JTextField();
		tf14.setHorizontalAlignment(SwingConstants.CENTER);
		tf14.setColumns(10);
		panelChords.add(tf14);

		tf15 = new JTextField();
		tf15.setHorizontalAlignment(SwingConstants.CENTER);
		tf15.setColumns(2);
		panelChords.add(tf15);

		tf16 = new JTextField();
		tf16.setHorizontalAlignment(SwingConstants.CENTER);
		tf16.setColumns(2);
		panelChords.add(tf16);

		JButton btnPlay = new JButton("Play");
		btnPlay.setBounds(182, 0, 68, 23);
		frame.getContentPane().add(btnPlay);

		JButton btnExportMidi = new JButton("Export MIDI");
		btnExportMidi.setBounds(280, 34, 107, 23);
		frame.getContentPane().add(btnExportMidi);

		JButton btnSave = new JButton("Save");
		btnSave.setBounds(181, 66, 89, 23);
		frame.getContentPane().add(btnSave);

		JSlider slider = new JSlider();
		slider.setBounds(224, 89, 200, 23);
		frame.getContentPane().add(slider);

		JLabel lblDensidadeRtmica = new JLabel("Rhytmic Density");
		lblDensidadeRtmica.setBounds(118, 98, 96, 14);
		frame.getContentPane().add(lblDensidadeRtmica);

		JLabel lblHarmonicComplexity = new JLabel("Harmonic Complexity");
		lblHarmonicComplexity.setBounds(90, 123, 124, 14);
		frame.getContentPane().add(lblHarmonicComplexity);

		JSlider slider_1 = new JSlider();
		slider_1.setBounds(224, 123, 200, 23);
		frame.getContentPane().add(slider_1);
		
		JButton btnGenerate = new JButton("Generate");
		btnGenerate.setBounds(181, 34, 89, 23);
		frame.getContentPane().add(btnGenerate);
		
		JButton btnStop = new JButton("Stop");
		btnStop.setBounds(334, 0, 68, 23);
		frame.getContentPane().add(btnStop);
		
		JButton btnPause = new JButton("Pause");
		btnPause.setBounds(252, 0, 73, 23);
		frame.getContentPane().add(btnPause);


		btnPlay.addActionListener(new PlayListener());			
	}

	class PlayListener implements ActionListener {		

		public void actionPerformed(ActionEvent e) {

			String t = tf1.getText();

			if (t.equals("")) {
				tf1.setBackground(Color.WHITE);
			}

			else {
				try {
					Chord c = new Chord(t);
					tf1.setBackground(Color.WHITE);

					Progression prog = new Progression();
					prog.addChord(c);
					prog.setPattern(new Pattern("up"));
					
					int bpm = (Integer) spnBPM.getValue();
					app.setBPM(bpm);
					
					app.setProgression(prog);
					app.playProgression();

				}
				catch (Exception exc) {
					exc.printStackTrace();
					System.out.println("wrong");
					tf1.setBackground(Color.red);
				}
			}
		}
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
