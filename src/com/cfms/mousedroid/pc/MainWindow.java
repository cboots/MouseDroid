package com.cfms.mousedroid.pc;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.UUID;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JProgressBar;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MainWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 717984543671268522L;
	private JPanel contentPane;
	private ListeningThread mListeningThread;
	private JButton btnNewButton;
	private JProgressBar progressBar;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame = new MainWindow();
					frame.setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainWindow() {
		initComponents();

	}

	private void initComponents() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		btnNewButton = new JButton("Start Listening");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				do_btnNewButton_actionPerformed(arg0);
			}
		});
		contentPane.add(btnNewButton, BorderLayout.NORTH);

		progressBar = new JProgressBar();
		contentPane.add(progressBar, BorderLayout.SOUTH);
	}

	protected void do_btnNewButton_actionPerformed(ActionEvent arg0) {
		// TODO implement better threading
		if (mListeningThread == null) {
			mListeningThread = new ListeningThread();
			(new Thread(mListeningThread)).start();
			progressBar.setIndeterminate(true);
		}
	}
}
