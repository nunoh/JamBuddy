package pt.santos.nuno;

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
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
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

public class GUI implements WindowListener {

	private static final int DEFAULT_TABLE_ROWS = 4;

	// string arrays
	private final static String[] keys = { "C", "D", "E", "F", "G", "A", "B" };
	private final static String[] accidentals = { "b", " ", "#" };	
	private final static String iconsFolder = 	"icons";
	private final static int tableRowHeight = 40;

	// button icons	
	private final static String playIcon = 		iconsFolder + "\\play_small.png";
	private final static String pauseIcon = 	iconsFolder + "\\pause_small.png";
	private final static String stopIcon = 		iconsFolder + "\\stop_small.png";
	private final static String generateIcon = 	iconsFolder + "\\generate.png";
	private final static String saveIcon = 		iconsFolder + "\\save.png";
	private final static String exportIcon = 	iconsFolder + "\\export2.png";
	private final static String openIcon = 		iconsFolder + "\\open.png";
	private final static String addIcon = 		iconsFolder + "\\add.png";
	private final static String clearIcon = 	iconsFolder + "\\clear.png";

	private JFrame frame;
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

		SpinnerModel sm = new SpinnerNumberModel(Api.DEFAULT_SEQUENCE_BPM, 20, 200, 1);

		String patterns[] = new String[app.getPatterns().size()];
		for (int i = 0; i < app.getPatterns().size(); i++) {
			Pattern p = app.getPatterns().get(i);
			patterns[i] = p.getName();
		}

		// PLAY
		JButton btnPlay = new JButton("");
		btnPlay.setIcon(new ImageIcon(playIcon));
		btnPlay.setBounds(17, 21, 32, 32);
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
		btnPause.setBounds(70, 21, 32, 32);
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
		btnStop.setBounds(130, 21, 32, 32);
		frame.getContentPane().add(btnStop);

		// GENERATE
		JButton btnGenerate = new JButton("");
		btnGenerate.setIcon(new ImageIcon(generateIcon));
		btnGenerate.setBounds(465, 11, 50, 50);
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
		btnSave.setBounds(262, 11, 50, 50);
		frame.getContentPane().add(btnSave);

		btnExport.setBounds(322, 11, 50, 50);
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
		btnOpen.setBounds(202, 11, 50, 50);
		frame.getContentPane().add(btnOpen);

		// LABELS
		JLabel lblPlay = new JLabel("Play");
		lblPlay.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblPlay.setHorizontalAlignment(SwingConstants.CENTER);
		lblPlay.setBounds(10, 51, 46, 14);
		frame.getContentPane().add(lblPlay);

		JLabel lblPause = new JLabel("Pause");
		lblPause.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblPause.setHorizontalAlignment(SwingConstants.CENTER);
		lblPause.setBounds(63, 51, 46, 14);
		frame.getContentPane().add(lblPause);

		JLabel lblStop = new JLabel("Stop");
		lblStop.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblStop.setHorizontalAlignment(SwingConstants.CENTER);
		lblStop.setBounds(123, 51, 46, 14);
		frame.getContentPane().add(lblStop);

		JLabel lblGenerate = new JLabel("Generate");
		lblGenerate.setFont(new Font("Tahoma", Font.BOLD, 10));
		lblGenerate.setHorizontalAlignment(SwingConstants.CENTER);
		lblGenerate.setBounds(469, 61, 46, 14);
		frame.getContentPane().add(lblGenerate);

		JLabel lblSave = new JLabel("Save");
		lblSave.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblSave.setHorizontalAlignment(SwingConstants.CENTER);
		lblSave.setBounds(264, 64, 46, 14);
		frame.getContentPane().add(lblSave);

		JLabel lblExport = new JLabel("Export");
		lblExport.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblExport.setHorizontalAlignment(SwingConstants.CENTER);
		lblExport.setBounds(324, 64, 46, 14);
		frame.getContentPane().add(lblExport);

		JLabel lblOpen = new JLabel("Open");
		lblOpen.setHorizontalAlignment(SwingConstants.CENTER);
		lblOpen.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblOpen.setBounds(204, 64, 46, 14);
		frame.getContentPane().add(lblOpen);

		// TABLE
		table = new JTable();
		tableModel = new DefaultTableModel(new String[]{"", "", "", ""}, DEFAULT_TABLE_ROWS);
		table.setModel(tableModel);
		table.setTableHeader(null);
		table.setRowHeight(tableRowHeight);

		// SCROLL PANE
		scrollPane = new JScrollPane();
		scrollPane.setBounds(50, 160, 465, 214);
		scrollPane.setViewportView(table);
		frame.getContentPane().add(scrollPane);				

		JButton btnAdd = new JButton("");
		btnAdd.setIcon(new ImageIcon(addIcon));
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tableModel.addRow(new String[]{"", "", "", ""});
			}
		});
		btnAdd.setBounds(50, 385, 32, 32);
		frame.getContentPane().add(btnAdd);

		JButton btnClear = new JButton("");
		btnClear.setIcon(new ImageIcon(clearIcon));
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//				for (int i = 0; i < tableModel.getRowCount(); i++)
				//					for (int j = 0; j < tableModel.getColumnCount(); j++)
				//						tableModel.setValueAt(null, i, j);
				int rows;
				while ( (rows = tableModel.getRowCount()) > 4) {
					tableModel.removeRow(rows-1);
				}
			}
		});
		btnClear.setBounds(103, 385, 32, 32);
		frame.getContentPane().add(btnClear);

		JLabel lblAddBar = new JLabel("Add Bars");
		lblAddBar.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblAddBar.setHorizontalAlignment(SwingConstants.CENTER);
		lblAddBar.setBounds(43, 417, 46, 14);
		frame.getContentPane().add(lblAddBar);

		JLabel lblClear = new JLabel("Clear");
		lblClear.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblClear.setHorizontalAlignment(SwingConstants.CENTER);
		lblClear.setBounds(96, 417, 46, 14);
		frame.getContentPane().add(lblClear);

		// KEY
		lblKey = new JLabel("Key");
		lblKey.setBounds(50, 95, 32, 14);
		frame.getContentPane().add(lblKey);

		cbbKeys = new JComboBox(keys);
		cbbKeys.setBounds(103, 92, 39, 20);
		frame.getContentPane().add(cbbKeys);
		cbbKeys.setSelectedIndex(0);

		cbbAccidentals = new JComboBox(accidentals);
		cbbAccidentals.setBounds(143, 92, 32, 20);
		frame.getContentPane().add(cbbAccidentals);
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
		spnBPM = new JSpinner(sm);
		spnBPM.setBounds(103, 117, 46, 20);
		frame.getContentPane().add(spnBPM);

		// BPM
		lblBpm = new JLabel("BPM");
		lblBpm.setBounds(50, 120, 32, 14);
		frame.getContentPane().add(lblBpm);

		// PATTERN
		lblPattern = new JLabel("Pattern");
		lblPattern.setBounds(213, 95, 67, 14);
		frame.getContentPane().add(lblPattern);

		JLabel lblLoopCount = new JLabel("Loop");
		lblLoopCount.setBounds(213, 120, 67, 14);
		frame.getContentPane().add(lblLoopCount);

		// LOOP
		cbLoop = new JCheckBox("Forever");
		cbLoop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (cbLoop.isSelected())
					spnLoopCount.setEnabled(false);
				else
					spnLoopCount.setEnabled(true);
			}
		});
		cbLoop.setBounds(319, 116, 75, 23);
		frame.getContentPane().add(cbLoop);

		cbbPatterns = new JComboBox(patterns);
		cbbPatterns.setBounds(271, 93, 81, 18);
		frame.getContentPane().add(cbbPatterns);

		SpinnerModel sm2 = new SpinnerNumberModel(0, 0, 99, 1);
		spnLoopCount = new JSpinner(sm2);
		spnLoopCount.setBounds(271, 117, 41, 20);
		frame.getContentPane().add(spnLoopCount);
		cbbPatterns.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox)e.getSource();
				String sPattern = (String)cb.getSelectedItem();
				Api.pattern = new Pattern(sPattern);
				System.out.println("pattern now is " + sPattern);
			}		
		});
		cbbKeys.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox)e.getSource();
				String sKey = (String)cb.getSelectedItem();
				int iKey = Note.getPitchClass(Note.getMidiValue(sKey));
				app.key = iKey;
				System.out.println("key now is " + app.key);
			}
		});

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
