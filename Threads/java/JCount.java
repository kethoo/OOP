import javax.swing.*;
import java.awt.*;

import static javax.swing.SwingUtilities.invokeLater;

public class JCount extends JPanel {
	private final JTextField textField;
	private final JLabel countLabel;
	private final JButton startButton;
	private final JButton stopButton;
	private WorkerThread currentWorker;

	public JCount() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));

		textField = new JTextField("100000000", 10);
		textField.setMaximumSize(new Dimension(100, 30));
		countLabel = new JLabel("0");
		startButton = new JButton("Start");
		stopButton = new JButton("Stop");
		stopButton.setEnabled(false);

		controlPanel.add(textField);
		controlPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		controlPanel.add(countLabel);
		controlPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		controlPanel.add(startButton);
		controlPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		controlPanel.add(stopButton);

		add(controlPanel);
		add(Box.createRigidArea(new Dimension(0, 40)));

		startButton.addActionListener(e -> startCounting());

		stopButton.addActionListener(e -> stopCounting());
	}

	private void startCounting() {
		stopCounting();

		int maxCount;
		try {
			maxCount = Integer.parseInt(textField.getText());
		} catch (NumberFormatException e) {
			maxCount = 100000000;
			textField.setText(String.valueOf(maxCount));
		}

		countLabel.setText("0");
		startButton.setEnabled(false);
		stopButton.setEnabled(true);

		currentWorker = new WorkerThread(maxCount);
		currentWorker.start();
	}

	private void stopCounting() {
		if (currentWorker != null && currentWorker.isAlive()) {
			currentWorker.interrupt();
			currentWorker = null;
		}
		startButton.setEnabled(true);
		stopButton.setEnabled(false);
	}

	private class WorkerThread extends Thread {
		private final int maxCount;

		public WorkerThread(int maxCount) {
			this.maxCount = maxCount;
		}

		@Override
		public void run() {
			try {
				for (int i = 0; i <= maxCount; i++) {
					if (isInterrupted()) {
						return;
					}

					if (i % 10000 == 0) {
						final int count = i;
						invokeLater(() -> countLabel.setText(String.valueOf(count)));
						Thread.sleep(100);
					}
				}
			} catch (InterruptedException e) {
				return;
			} finally {
				invokeLater(() -> {
					startButton.setEnabled(true);
					stopButton.setEnabled(false);
				});
			}
		}
	}

	public static void main(String[] args) {
		invokeLater(() -> {
			JFrame frame = new JFrame("The Count");
			frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			frame.add(new JCount());
			frame.add(new JCount());
			frame.add(new JCount());
			frame.add(new JCount());

			frame.pack();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
		});
	}
}