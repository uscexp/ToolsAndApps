package haui.app.dbtreetableview;

import java.awt.*;
import javax.swing.*;

/**
 *
 *		Module:					OueryTextArea.java<br>
 *										$Source: C:\\WIN32APP\\RCS\\C\\Dev\\source\\Bean\\DbTreeTableView\\OueryTextArea.java,v $
 *<p>
 *		Description:    TextArea.<br>
 *</p><p>
 *		Created:				28.09.2000	by	AE
 *</p><p>
 *		@history				26.09.2000 - 05.10.2000	by	AE: Created.<br>
 *</p><p>
 *		Modification:<br>
 *		$Log: OueryTextArea.java,v $
 *
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 2000; $Revision:  $<br>
 *										$Header: C:\\WIN32APP\\RCS\\C\\Dev\\source\\Bean\\DbTreeTableView\\OueryTextArea.java,v 1.1 2002-01-15 11:14:25+01 t026843 Exp t026843 $
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class QueryTextArea
  extends JPanel
{
  BorderLayout m_borderLayoutBase = new BorderLayout();
  JScrollPane m_jScrollPaneMain = new JScrollPane();
  JTextArea m_jEditorPaneMain = new JTextArea();

  public QueryTextArea()
  {
    try
    {
      jbInit();
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
  }

  void jbInit() throws Exception
  {
    this.setLayout(m_borderLayoutBase);
    this.add(m_jScrollPaneMain, BorderLayout.CENTER);
    m_jScrollPaneMain.getViewport().add(m_jEditorPaneMain, null);
  }

  public void setText( String str)
  {
    m_jEditorPaneMain.setText( str);
  }

  public String getText()
  {
    return m_jEditorPaneMain.getText();
  }

  public void replaceSelection( String str)
  {
    m_jEditorPaneMain.replaceSelection( str);
  }

  public int getCaretPosition()
  {
    return m_jEditorPaneMain.getCaretPosition();
  }

  public void cut()
  {
    m_jEditorPaneMain.cut();
  }

  public void paste()
  {
    m_jEditorPaneMain.paste();
  }

  public void copy()
  {
    m_jEditorPaneMain.copy();
  }

  public void undo()
  {
  }
}
