package com.cfms.mousedroid.pc;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import com.cfms.mousedroid.pc.bluetooth.BluetoothServer;
import com.cfms.mousedroid.pc.bluetooth.BluetoothServer.BTEventListener;

public class MainWindow extends JFrame implements BTEventListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 717984543671268522L;
	private JPanel contentPane;
	private JButton btnNewButton;
	private JProgressBar progressBar;
	private JLabel lblStatus;
	private BluetoothServer mBTServer;

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
		
		lblStatus = new JLabel("No Connection");
		contentPane.add(lblStatus, BorderLayout.CENTER);
	}

	protected void do_btnNewButton_actionPerformed(ActionEvent arg0) {
		mBTServer = new BluetoothServer();
		mBTServer.setEventListener(this);
		mBTServer.start();
		
	}

	@Override
	public void onStateChanged(int oldState, int newState) {
		if(newState == BluetoothServer.STATE_LISTEN){
			lblStatus.setText("Waiting for Connection");
			progressBar.setIndeterminate(true);
		}else if(newState == BluetoothServer.STATE_CONNECTED){
			lblStatus.setText("Connected");
			progressBar.setIndeterminate(false);
		}else if(newState == BluetoothServer.STATE_NONE){
			lblStatus.setText("Server Down");
			progressBar.setIndeterminate(false);
		}
	}

	@Override
	public void onError(int errorCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBTMessageWritten(byte[] message, int length) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBTMessageRead(byte[] message, int length) {
		// TODO Auto-generated method stub
		
	}
	

	
}
