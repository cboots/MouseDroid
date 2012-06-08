package com.cfms.mousedroid.pc;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import com.cfms.android.mousedroid.BTProtocol.KeyEventType;
import com.cfms.android.mousedroid.BTProtocol.MouseButton;
import com.cfms.android.mousedroid.BTProtocol.MouseButtonEvent;
import com.cfms.mousedroid.pc.CommandProcessor.CommandEventListener;

public class MainWindow extends JFrame implements CommandEventListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 717984543671268522L;
	private JPanel contentPane;
	private JButton btnNewButton;
	private JProgressBar progressBar;
	private JLabel lblStatus;
	
	private CommandProcessor mCmdProcessor;
	
	/**
	 * Create the frame.
	 */
	public MainWindow(CommandProcessor cmdProcessor) {
		mCmdProcessor = cmdProcessor;
		mCmdProcessor.setEventListener(this);
		
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
				
			}
		});
		contentPane.add(btnNewButton, BorderLayout.NORTH);

		progressBar = new JProgressBar();
		contentPane.add(progressBar, BorderLayout.SOUTH);
		
		lblStatus = new JLabel("No Connection");
		contentPane.add(lblStatus, BorderLayout.CENTER);
	}

	@Override
	public void onStateChanged(int oldState, int newState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(int errorCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseMove(int dx, int dy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseWheelEvent(int ticks) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseButtonEvent(MouseButton MB, MouseButtonEvent MBE) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onKeyEvent(int keyCode, KeyEventType keyEventType) {
		// TODO Auto-generated method stub
		
	}
	
}
