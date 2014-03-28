package haui.app.fm;

import haui.components.JExDialog;
import haui.components.ProxyAuthorizationPanel;
import haui.components.ProxyPropertiesPanel;
import haui.components.VerticalFlowLayout;
import haui.io.FileConnector;
import haui.io.FileInterface.FileInterface;
import haui.util.AppProperties;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

/**
 * Module: PropertyDialog.java<br>
 * $Source: $
 * <p>
 * Description: Properties dialog.<br>
 * </p>
 * <p>
 * Created: 14.06.2002 by AE
 * </p>
 * <p>
 * Modification:<br>
 * $Log: $
 * </p>
 * <p>
 * 
 * @author Andreas Eisenhauer
 *         </p>
 *         <p>
 * @version v1.0, 2002; $Revision: $<br>
 *          $Header: $
 *          </p>
 *          <p>
 * @since JDK1.2
 *        </p>
 */
public class PropertyDialog extends JExDialog {
	private static final long serialVersionUID = 2507403332032087597L;

	// GUI member variables
	JPanel m_jPanelBase = new JPanel();
	BorderLayout m_borderLayoutBase = new BorderLayout();
	JButton m_jButtonOk = new JButton();
	JPanel m_jPanelButtons = new JPanel();
	JButton m_jButtonCancel = new JButton();
	TitledBorder titledBorder1;
	TitledBorder titledBorder2;
	TitledBorder titledBorder3;
	TitledBorder titledBorder4;
	JTabbedPane m_jTabbedPaneBase = new JTabbedPane();
	ProxyPropertiesPanel m_jPanelProxy = new ProxyPropertiesPanel();
	JPanel m_jPanelProperties = new JPanel();
	JPanel m_jPanelProps = new JPanel();
	JLabel m_jLabelTitle = new JLabel();
	BorderLayout m_borderLayoutProperties = new BorderLayout();
	VerticalFlowLayout m_verticalFlowLayoutProps = new VerticalFlowLayout();
	JPanel m_jPanelTempdir = new JPanel();
	FlowLayout m_flowLayoutTempdir = new FlowLayout();
	JTextField m_jTextFieldTmpDir = new JTextField();
	JLabel m_jLabelTmpDir = new JLabel();
	JPanel m_jPanelExtPath = new JPanel();
	FlowLayout m_flowLayoutExtPath = new FlowLayout();
	JTextField m_jTextFieldExtPath = new JTextField();
	JLabel m_jLabelExtPath = new JLabel();
	JButton m_jButtonTmpBrowse = new JButton();
	JButton m_jButtonExtpBrowse = new JButton();

	// member variables
	String m_strTmpDir = "";
	String m_strExtPath = "";
	AppProperties m_appProps;
	JPanel m_jPanelSearchDepth = new JPanel();
	JTextField m_jTextFieldSearchDepth = new JTextField();
	JLabel m_jLabelSearchDepth = new JLabel();
	FlowLayout m_flowLayoutSearchDepth = new FlowLayout();

	public PropertyDialog(Component frame, String title, boolean modal,
			AppProperties appProps) {
		super(null, title, modal, FileManager.APPNAME);
		setFrame(frame);
		m_appProps = appProps;
		m_jPanelProxy = new ProxyPropertiesPanel(appProps);
		try {
			jbInit();
			pack();
			getRootPane().setDefaultButton(m_jButtonOk);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		setResizable(false);
		_init();
		LisAction actionlis = new LisAction();
		m_jButtonOk.addActionListener(actionlis);
		m_jButtonCancel.addActionListener(actionlis);
		m_jButtonTmpBrowse.addActionListener(actionlis);
		m_jButtonExtpBrowse.addActionListener(actionlis);
	}

	public PropertyDialog() {
		this(null, "", false, null);
	}

	void jbInit() throws Exception {
		titledBorder1 = new TitledBorder("");
		titledBorder2 = new TitledBorder("");
		titledBorder3 = new TitledBorder("");
		titledBorder4 = new TitledBorder("");
		m_jPanelBase.setLayout(m_borderLayoutBase);
		m_jButtonOk.setActionCommand("Ok");
		m_jButtonOk.setText("Ok");
		m_jButtonCancel.setActionCommand("Cancel");
		m_jButtonCancel.setText("Cancel");
		m_jButtonTmpBrowse.setActionCommand("tmpbrowse");
		m_jButtonTmpBrowse.setText("Browse");
		m_jButtonExtpBrowse.setActionCommand("extpbrowse");
		m_jButtonExtpBrowse.setText("Browse");
		m_jPanelProxy.setBorder(titledBorder3);
		m_jPanelProps.setLayout(m_verticalFlowLayoutProps);
		m_jLabelTitle.setFont(new java.awt.Font("Dialog", 1, 12));
		m_jLabelTitle.setText("Properties");
		m_jPanelProperties.setLayout(m_borderLayoutProperties);
		m_jPanelProps.setBorder(null);
		m_jPanelProperties.setBorder(titledBorder4);
		m_jPanelTempdir.setLayout(m_flowLayoutTempdir);
		m_jTextFieldTmpDir.setMinimumSize(new Dimension(200, 21));
		m_jTextFieldTmpDir.setPreferredSize(new Dimension(200, 21));
		m_jTextFieldTmpDir.setToolTipText("Directory for temporary files");
		m_jLabelTmpDir.setRequestFocusEnabled(true);
		m_jLabelTmpDir.setToolTipText("Directory for temporary files");
		m_jLabelTmpDir.setText("Temp Directory:");
		m_jPanelTempdir.setAlignmentX((float) 0.5);
		m_flowLayoutTempdir.setAlignment(FlowLayout.LEFT);
		m_jPanelExtPath.setAlignmentX((float) 0.5);
		m_jPanelExtPath.setLayout(m_flowLayoutExtPath);
		m_flowLayoutExtPath.setAlignment(FlowLayout.LEFT);
		m_jTextFieldExtPath.setMinimumSize(new Dimension(200, 21));
		m_jTextFieldExtPath.setOpaque(true);
		m_jTextFieldExtPath.setPreferredSize(new Dimension(200, 21));
		m_jTextFieldExtPath
				.setToolTipText("Additional path to search commands");
		m_jLabelExtPath.setToolTipText("Additional path to search commands");
		m_jLabelExtPath.setText("Extended Path: ");
		m_jPanelSearchDepth.setLayout(m_flowLayoutSearchDepth);
		m_jTextFieldSearchDepth.setMaximumSize(new Dimension(21, 21));
		m_jTextFieldSearchDepth.setMinimumSize(new Dimension(21, 21));
		m_jTextFieldSearchDepth.setPreferredSize(new Dimension(21, 21));
		m_jTextFieldSearchDepth.setToolTipText("Extended path search depth");
		m_jLabelSearchDepth.setToolTipText("Extended path search depth");
		m_jLabelSearchDepth.setText("Search Depth:  ");
		m_flowLayoutSearchDepth.setAlignment(FlowLayout.LEFT);
		getContentPane().add(m_jPanelBase);
		m_jPanelBase.add(m_jPanelButtons, BorderLayout.SOUTH);
		m_jPanelButtons.add(m_jButtonOk, null);
		m_jPanelButtons.add(m_jButtonCancel, null);
		m_jPanelBase.add(m_jTabbedPaneBase, BorderLayout.CENTER);
		m_jTabbedPaneBase.add(m_jPanelProperties, "General");
		m_jTabbedPaneBase.add(m_jPanelProxy, "Proxy");
		m_jPanelExtPath.add(m_jLabelExtPath, null);
		m_jPanelExtPath.add(m_jTextFieldExtPath, null);
		m_jPanelExtPath.add(m_jButtonExtpBrowse, null);
		m_jPanelSearchDepth.add(m_jLabelSearchDepth, null);
		m_jPanelSearchDepth.add(m_jTextFieldSearchDepth, null);
		m_jPanelProps.add(m_jLabelTitle, null);
		m_jPanelProps.add(m_jPanelTempdir, null);
		m_jPanelProps.add(m_jPanelExtPath, null);
		m_jPanelProps.add(m_jPanelSearchDepth, null);
		m_jPanelProperties.add(m_jPanelProps, BorderLayout.NORTH);
		m_jPanelTempdir.add(m_jLabelTmpDir, null);
		m_jPanelTempdir.add(m_jTextFieldTmpDir, null);
		m_jPanelTempdir.add(m_jButtonTmpBrowse, null);
	}

	public void _init() {
		if (m_appProps != null) {
			m_strTmpDir = m_appProps.getProperty(FileInterface.TEMPDIR);
			m_strExtPath = m_appProps.getProperty(FileInterface.EXTENDEDPATH);
			m_jTextFieldSearchDepth.setText(String.valueOf(FileConnector
					.getSearchDepth()));
			FileConnector.setTempDirectory(m_strTmpDir);
			FileConnector.setExtPath(m_strExtPath);
		}

		m_jTextFieldTmpDir.setText(m_strTmpDir);
		m_jTextFieldExtPath.setText(m_strExtPath);
		m_jPanelProxy._init();
	}

	public void _save() {
		m_strTmpDir = m_jTextFieldTmpDir.getText();
		m_strExtPath = m_jTextFieldExtPath.getText();

		if (m_appProps != null) {
			if (m_strTmpDir == null || m_strTmpDir.equals("")) {
				m_appProps.remove(FileInterface.TEMPDIR);
				FileConnector.setTempDirectory(null);
			} else
				m_appProps.setProperty(FileInterface.TEMPDIR, m_strTmpDir);
			if (m_strExtPath == null || m_strExtPath.equals("")) {
				m_appProps.remove(FileInterface.EXTENDEDPATH);
				FileConnector.setExtPath(null);
			} else
				m_appProps
						.setProperty(FileInterface.EXTENDEDPATH, m_strExtPath);
			int iOld = FileConnector.getSearchDepth();
			try {
				int i = Integer.parseInt(m_jTextFieldSearchDepth.getText());
				FileConnector.setSearchDepth(i);
				m_appProps.setProperty(FileInterface.SEARCHDEPTH,
						new Integer(i));
			} catch (NumberFormatException nfex) {
				nfex.printStackTrace();
				m_appProps.setProperty(FileInterface.SEARCHDEPTH, new Integer(
						iOld));
			}
		}
		m_jPanelProxy._save();
	}

	class LisAction implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			String cmd = event.getActionCommand();
			if (cmd == "Ok")
				m_jButtonOk_actionPerformed(event);
			else if (cmd == "Cancel")
				setVisible(false);
			else if (cmd == "tmpbrowse") {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (m_strTmpDir != null && !m_strTmpDir.equals("")) {
					File f = new File(m_strTmpDir);
					if (f.exists()) {
						File fPar = f.getParentFile();
						if (fPar != null && fPar.exists())
							chooser.setCurrentDirectory(fPar);
					}
				}
				int returnVal = chooser.showOpenDialog(getParent());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					if (chooser.getSelectedFile() != null
							&& chooser.getSelectedFile().getAbsolutePath() != null)
						m_jTextFieldTmpDir.setText(chooser.getSelectedFile()
								.getAbsolutePath());
				}
			} else if (cmd == "extpbrowse") {
				boolean blNew = true;
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (m_strExtPath != null && !m_strExtPath.equals("")) {
					blNew = false;
					File f = new File(m_strExtPath);
					if (f.exists()) {
						File fPar = f.getParentFile();
						if (fPar != null && fPar.exists())
							chooser.setCurrentDirectory(fPar);
					}
				}
				int returnVal = chooser.showOpenDialog(getParent());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					if (chooser.getSelectedFile() != null
							&& chooser.getSelectedFile().getAbsolutePath() != null) {
						String str = chooser.getSelectedFile()
								.getAbsolutePath();
						if (!blNew)
							str = m_strExtPath + ";" + str;
						m_jTextFieldExtPath.setText(str);
					}
				}
			}
		}
	}

	public void setVisible(boolean b) {
		if (b) {
			m_jPanelProxy._init();
			m_jPanelProxy.getAuthPanel().setRequestText(
					ProxyAuthorizationPanel.HEAD);
			pack();
		}
		super.setVisible(b);
		m_jPanelProxy.requestFocus();
	}

	void m_jButtonOk_actionPerformed(ActionEvent e) {
		_save();
		setVisible(false);
	}
}
