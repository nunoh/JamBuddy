package pt.santos.nuno;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequencer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class GUI implements WindowListener {

	private static final int DEFAULT_TABLE_ROWS = 4;
	// string arrays
	private final static String[] keys = { "C", "D", "E", "F", "G", "A", "B" };
	private final static String[] accidentals = { "b", " ", "#" };	
	private final static String iconsFolder = 	"icons";
	private final static int tableRowHeight = 40;
	private final static String[] tableColumns = {"Foo", "Bar"};

	// button icons	
	private final static String playIcon = 		iconsFolder + "\\play2.png";
	private final static String pauseIcon = 	iconsFolder + "\\pause2.png";
	private final static String stopIcon = 		iconsFolder + "\\stop.png";
	private final static String generateIcon = 	iconsFolder + "\\generate.png";
	private final static String saveIcon = 		iconsFolder + "\\save.png";
	private final static String exportIcon = 	iconsFolder + "\\export2.png";
	private final static String openIcon = 		iconsFolder + "\\open.png";

	private JFrame frame;
	private JTextField tfs[];
	private JSpinner spnBPM;
	private JSpinner spnLoopCount; 
	private JComboBox cbbPatterns;
	private JComboBox cbbKeys;
	private JComboBox cbbAccidentals;
	private JCheckBox cbLoop;
	private JLabel lblKey;
	private JLabel lblBpm;
	private JLabel lblPattern;
	private JScrollPane scrollPane; 
	private JFileChooser fileChooser;
	private JFileChooser fcOpen;
	private JTable table;
	private DefaultTableModel tableModel;

	Progression prog;
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
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					window.frame.setVisible(true);


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
		panelToolbar.setBounds(374, 116, 171, 192);
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
		cbbKeys.setSelectedIndex(0);
		cbbKeys.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox)e.getSource();
				String sKey = (String)cb.getSelectedItem();
				int iKey = Note.getPitchClass(Note.getMidiValue(sKey));
				app.key = iKey;
				System.out.println("key now is " + app.key);
			}
		});
		panelToolbar.add(cbbKeys, "6, 2, 3, 1, fill, center");

		cbbAccidentals = new JComboBox(accidentals);
		cbbAccidentals.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox)e.getSource();
				String sKey = (String)cb.getSelectedItem();
				if (sKey.equals("b")) { 
					app.key--;
				}
				else if (sKey.equals("#")) {
					app.key++;
				}
				System.out.println("key now is " + app.key);
			}
		});
		cbbAccidentals.setSelectedIndex(1);
		panelToolbar.add(cbbAccidentals, "9, 2, 2, 1, fill, center");

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
				Api.pattern = new Pattern(sPattern);
				System.out.println("pattern now is " + sPattern);
			}		
		});

		// LOOP
		cbLoop = new JCheckBox("Loop");
		panelToolbar.add(cbLoop, "6, 8, 5, 1, left, default");

		JLabel lblLoopCount = new JLabel("Loop Count");
		panelToolbar.add(lblLoopCount, "2, 10, 7, 1, center, default");

		spnLoopCount = new JSpinner();
		panelToolbar.add(spnLoopCount, "10, 10");

		tfs = new JTextField[20];

		//		for (int i = 0; i < 16; i++) {
		//			JTextField tf = new JTextField();
		//			tf.setHorizontalAlignment(SwingConstants.CENTER);
		//			tfs[i] = tf;
		//		}

		//		//TODO
		//		for (int i = 0; i < tfs.length; i++) {
		//			JTextField tf = tfs[i];
		//			if (tf != null)
		//				panelChords.add(tf);
		//		}

		// PLAY
		JButton btnPlay = new JButton("");
		btnPlay.setIcon(new ImageIcon(playIcon));
		btnPlay.setBounds(52, 21, 50, 50);
		frame.getContentPane().add(btnPlay);
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				

				if (paused) {
					Api.sequencer.start();
					paused = false;
					return;
				}

				buildProgression();

				// bpms
				int bpm = (Integer) spnBPM.getValue();
				app.setBPM(bpm);

				// loop count
				if (cbLoop.isSelected()) {
					app.sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
				}

				else {
					int iLoopCount = (Integer) spnLoopCount.getValue(); 
					app.sequencer.setLoopCount(iLoopCount);
				}

				// actually play
				app.playProgression();
			}
		});

		// EXPORT
		fileChooser = new JFileChooser();
		JButton btnExport = new JButton("");
		btnExport.setIcon(new ImageIcon(exportIcon));
		btnExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (!containsChords()) {
					JOptionPane.showMessageDialog(frame, "Nothing to export...");	
					return;
				}

				int rval = fileChooser.showSaveDialog(frame);
				if (rval == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();				
					try {
						buildProgression();
						MidiSystem.write(app.sequence, 1, file);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		// SAVE
		JButton btnSave = new JButton("");
		btnSave.setIcon(new ImageIcon(saveIcon));
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if (!containsChords()) {
					JOptionPane.showMessageDialog(frame, "nothing to save...");
					return;
				}

				ArrayList<String> chords = new ArrayList<String>();
				for (int i = 0; i < tableModel.getRowCount(); i++) {
					for (int j = 0; j < tableModel.getColumnCount(); j++) {
						String s = (String) tableModel.getValueAt(i, j);
						if (s != null) {
							chords.add(s);
						}
					}
				}
				
				int rval = fileChooser.showSaveDialog(frame);
				if (rval == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();								
					app.saveSong(chords, file);
				}
			}			
		});

		// PAUSE
		JButton btnPause = new JButton("");
		btnPause.setIcon(new ImageIcon(pauseIcon));
		btnPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (Api.sequencer.isOpen()) {
					paused = true;
					Api.sequencer.stop();
				}
			}
		});
		btnPause.setBounds(112, 21, 50, 50);
		frame.getContentPane().add(btnPause);

		// STOP
		JButton btnStop = new JButton("");
		btnStop.setIcon(new ImageIcon(stopIcon));
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (Api.sequencer.isOpen()) {
					Api.sequencer.stop();
				}
			}
		});
		btnStop.setBounds(172, 21, 50, 50);
		frame.getContentPane().add(btnStop);

		// GENERATE
		JButton btnGenerate = new JButton("");
		btnGenerate.setIcon(new ImageIcon(generateIcon));
		btnGenerate.setBounds(232, 21, 50, 50);
		btnGenerate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int rows = 2;
				int cols = 4;
				for (int i = 0; i < rows; i++) {
					for (int j = 0; j < cols; j++) {					
						String next = app.markov.getNext();
						Chord c = app.getMarkovChord(next);
						int p = c.getRoot();
						String let = Note.getLetter(p);
						String sChord = let + c.getDef();
						tableModel.setValueAt(sChord, i, j);
					}
				}
			}						 
		});
		frame.getContentPane().add(btnGenerate);
		btnSave.setBounds(292, 21, 50, 50);
		frame.getContentPane().add(btnSave);

		btnExport.setBounds(352, 21, 50, 50);
		frame.getContentPane().add(btnExport);

		// OPEN
		JButton btnOpen = new JButton("");
		btnOpen.setIcon(new ImageIcon(openIcon));
		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg) {
				int rval = fileChooser.showOpenDialog(frame);
				if (rval == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();								
					loadSong(file);
				}
			}

			private void loadSong(File file) {
				try {
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					dbf.setIgnoringElementContentWhitespace(true);
					DocumentBuilder db = dbf.newDocumentBuilder();			
					Document dom = db.parse(file);
					dom.getDocumentElement().normalize();

					NodeList songs = dom.getElementsByTagName("song");
					Element elem = (Element) songs.item(0);
					String name = elem.getAttribute("name");				
					String key = elem.getAttribute("key");
					String sProg = elem.getTextContent();
					sProg = sProg.substring(sProg.indexOf(Api.CHORDS_DELIMITER), sProg.lastIndexOf(Api.CHORDS_DELIMITER)+1);

					System.out.println(sProg);

					String tokens[] = sProg.split("\\" + Api.CHORDS_DELIMITER);
					
					for (int i = 1; i < tokens.length; i++) {
						String token = tokens[i].trim();
						setChord(i-1, token);
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}

			private void setChord(int i, String s) {
				int row = i / tableModel.getColumnCount();
				int col = i % tableModel.getRowCount();
				tableModel.setValueAt(s, row, col);
			}
		});
		btnOpen.setBounds(412, 21, 50, 50);
		frame.getContentPane().add(btnOpen);


		// RTHYTMIC
		JSlider slRhytmic = new JSlider();
		slRhytmic.setBounds(273, 362, 200, 23);
		frame.getContentPane().add(slRhytmic);

		JLabel lblRhytmic = new JLabel("Rhytmic Density");
		lblRhytmic.setBounds(167, 371, 96, 14);
		frame.getContentPane().add(lblRhytmic);

		// HARMONIC
		JLabel lblHarmonicComplexity = new JLabel("Harmonic Complexity");
		lblHarmonicComplexity.setBounds(139, 417, 124, 14);
		frame.getContentPane().add(lblHarmonicComplexity);

		JSlider slHarmonic = new JSlider();
		slHarmonic.setBounds(273, 408, 200, 23);
		frame.getContentPane().add(slHarmonic);

		JSlider slider = new JSlider();
		slider.setOrientation(SwingConstants.VERTICAL);
		slider.setBounds(490, 285, 55, 146);
		frame.getContentPane().add(slider);

		// LABELS
		JLabel lblPlay = new JLabel("Play");
		lblPlay.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblPlay.setHorizontalAlignment(SwingConstants.CENTER);
		lblPlay.setBounds(54, 74, 46, 14);
		frame.getContentPane().add(lblPlay);

		JLabel lblPause = new JLabel("Pause");
		lblPause.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblPause.setHorizontalAlignment(SwingConstants.CENTER);
		lblPause.setBounds(114, 74, 46, 14);
		frame.getContentPane().add(lblPause);

		JLabel lblStop = new JLabel("Stop");
		lblStop.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblStop.setHorizontalAlignment(SwingConstants.CENTER);
		lblStop.setBounds(174, 74, 46, 14);
		frame.getContentPane().add(lblStop);

		JLabel lblGenerate = new JLabel("Generate");
		lblGenerate.setFont(new Font("Tahoma", Font.BOLD, 10));
		lblGenerate.setHorizontalAlignment(SwingConstants.CENTER);
		lblGenerate.setBounds(234, 74, 46, 14);
		frame.getContentPane().add(lblGenerate);

		JLabel lblSave = new JLabel("Save");
		lblSave.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblSave.setHorizontalAlignment(SwingConstants.CENTER);
		lblSave.setBounds(294, 74, 46, 14);
		frame.getContentPane().add(lblSave);

		JLabel lblExport = new JLabel("Export");
		lblExport.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblExport.setHorizontalAlignment(SwingConstants.CENTER);
		lblExport.setBounds(354, 74, 46, 14);
		frame.getContentPane().add(lblExport);

		JLabel lblOpen = new JLabel("Open");
		lblOpen.setHorizontalAlignment(SwingConstants.CENTER);
		lblOpen.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblOpen.setBounds(414, 74, 46, 14);
		frame.getContentPane().add(lblOpen);

		// TABLE
		table = new JTable();
		tableModel = new DefaultTableModel(new String[]{"", "", "", ""}, DEFAULT_TABLE_ROWS);
		table.setModel(tableModel);
		table.setTableHeader(null);
		table.setRowHeight(tableRowHeight);

		// SCROLL PANE
		scrollPane = new JScrollPane();
		scrollPane.setBounds(48, 110, 317, 198);
		scrollPane.setViewportView(table);
		frame.getContentPane().add(scrollPane);				

		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tableModel.addRow(new String[]{"", "", "", ""});
			}
		});
		btnAdd.setBounds(10, 334, 89, 23);
		frame.getContentPane().add(btnAdd);
		
		JButton btnClear = new JButton("Clear");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < tableModel.getRowCount(); i++)
					for (int j = 0; j < tableModel.getColumnCount(); j++)
						tableModel.setValueAt(null, i, j);
			}
		});
		btnClear.setBounds(11, 362, 89, 23);
		frame.getContentPane().add(btnClear);

	}

	public void buildProgression() {

		prog = new Progression();	

		for (int i = 0; i < tableModel.getRowCount(); i++) {
			for (int j = 0; j < tableModel.getColumnCount(); j++) {
				String s = (String) tableModel.getValueAt(i, j);
				if (s != null) {					
					try {
						Chord c = new Chord(s);					
						prog.addChord(c);
					}
					catch (Exception exc) {
						exc.printStackTrace();
					}
				}
			}
		}

		app.setProgression(prog);
	}

	public boolean containsChords() {
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			for (int j = 0; j < tableModel.getColumnCount(); j++) {
				String s = (String) tableModel.getValueAt(i, j);
				if (s != null)
					return true;
			}
		}
		return false;
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
