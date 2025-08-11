import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;
import javax.swing.text.Document;

import java.awt.*;
import java.awt.event.*;

public class SudokuFrame extends JFrame {
	private JTextArea sourceTextArea;
	private JTextArea resultsTextArea;
	private JButton checkButton;
	private JCheckBox autoCheckBox;

	public SudokuFrame() {
		super("Sudoku Solver");
		super.setLayout(new BorderLayout(4,4));

		sourceTextArea = new JTextArea(15, 20);
		sourceTextArea.setBorder(new TitledBorder("Puzzle"));
		sourceTextArea.setLineWrap(true);
		resultsTextArea = new JTextArea(15, 20);
		resultsTextArea.setBorder(new TitledBorder("Solution"));
		resultsTextArea.setEditable(false);

		checkButton = new JButton("Check");
		autoCheckBox = new JCheckBox("Auto", true);

		checkButton.addActionListener(e -> checkSolution());

		Document doc = sourceTextArea.getDocument();
		doc.addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				if (autoCheckBox.isSelected()) {
					checkSolution();
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				if (autoCheckBox.isSelected()) {
					checkSolution();
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				if (autoCheckBox.isSelected()) {
					checkSolution();
				}
			}
		});

		setLayout(new BorderLayout(4, 4));
		add(sourceTextArea, BorderLayout.CENTER);
		add(resultsTextArea, BorderLayout.EAST);

		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
		controlPanel.add(checkButton);
		controlPanel.add(Box.createRigidArea(new Dimension(4, 0)));
		controlPanel.add(autoCheckBox);

		add(controlPanel, BorderLayout.SOUTH);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	private void checkSolution() {
		try {
			String puzzleText = sourceTextArea.getText();
			Sudoku sudoku = new Sudoku(puzzleText);
			int count = sudoku.solve();

            String result = sudoku.getSolutionText() +
                    "\nsolutions:" + count + "\n" +
                    "elapsed:" + sudoku.getElapsed() + "ms\n";

			resultsTextArea.setText(result);
		} catch (Exception ex) {
			resultsTextArea.setText("Parsing problem");
		}
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) { }

		SudokuFrame frame = new SudokuFrame();
	}
}