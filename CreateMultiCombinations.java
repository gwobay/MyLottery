
import java.io.*;

import javax.swing.text.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

import java.util.HashMap;
import java.util.Vector;

public class CreateMultiCombinations extends JPanel implements ActionListener, DocumentListener
{
static JTextArea myTA=null;
int currentIndex;
Vector textFieldV=new Vector();
HashMap<String, JTextField> dataFields;
GetHistoryPrimeData dataBuilder;
Vector<Vector<int[]>> historyPrecditionList;
Vector<int[]> historyJackPots;
JPanel imagePanel;
BufferedImage myImage;
int outputCol;
int dataSetCount;
JPanel setJRadios(String panelPrompt, String radiosLabel, String command, ActionListener listener)
{
	JPanel aPanel=new JPanel();//new GridLayout(1, 0));
	aPanel.add(new JButton(panelPrompt));
	ButtonGroup connectGroup=new ButtonGroup();
	String[] flds=radiosLabel.split("><");
	String[] fldsCom=command.split("><");
	
	for (int i=0; i<flds.length; i++)
	{
		JRadioButton callB=new JRadioButton(flds[i]);
		callB.setActionCommand(fldsCom[i]);
		if (i==0)
		callB.setSelected(true);
		callB.addActionListener(listener);
		connectGroup.add(callB);
		aPanel.add(callB);
	}
	return aPanel;
}

JPanel setJText(String panelPrompt, int length, String iniValue, String command, ActionListener listener)
{
	JPanel aPanel=new JPanel(new GridLayout(1, 0));
	aPanel.add(new JLabel(panelPrompt));
	JTextField callB=new JTextField(length);
	textFieldV.add(callB);
	dataFields.put(command, callB);
	callB.setText(iniValue);
	callB.setActionCommand(command);
	callB.addActionListener(listener);
	//action will be fired only hit enter key; tab key won't
	aPanel.add(callB);
	return aPanel;
}

public CreateMultiCombinations()
{
	super(new  GridBagLayout());//GridLayout(0, 1));
	dataFields=new HashMap<String, JTextField> ();
	dataBuilder=null;
	JPanel infoPanel=new JPanel(new GridLayout(9, 0));
	//JPanel panel0=setJText("Set the SIM Number", 20, "987654321", null, this);
	JPanel panel1=setJRadios("number of mix:", "2><3><4", "c:2><c:3><c:4",this);
	JPanel panelm=setJRadios("combine Method", "1x0><1x1><2x1><2x2><3x1", "m:10><m:11><m:21><m:22><m:31", this);
	
	JPanel panel2=setJText("number of input sets:", 6, "0", "n:input_sets",this);
	
	//infoPanel.
		infoPanel.add(panel1);
		infoPanel.add(panelm);
		//infoPanel.
		infoPanel.add(panel2);
		
	 for (int i=1; i<7; i++){
		 JPanel panelX=setJText("Set "+i+":", 6, "0", "n"+i+":data_set"+i,this);
		 infoPanel.add(panelX);
	 }
	//add(panel2);
	currentIndex=60;
	
	JButton paneConfirm=new JButton("START BUILD");
	paneConfirm.addActionListener(this);
	paneConfirm.setActionCommand("s:99");
	infoPanel.add(paneConfirm);
	
	GridBagConstraints c1 = new GridBagConstraints();
	c1.gridwidth = GridBagConstraints.REMAINDER;
    add(infoPanel, c1);
    
    
	imagePanel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(myImage, 0, 0, null);
        }
    };
    /*
    JScrollPane aScrol=new JScrollPane(imagePanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    
    c.fill = GridBagConstraints.BOTH;
	c.weightx = 1.0;
	c.weighty = 1.0;
    //add(imagePanel);
    add(aScrol, c);
    */
//following code are always put at the end
   
	JPanel aPanel=new JPanel(new GridBagLayout());

	JTextArea aTxtA=new JTextArea(60, 40);
	
	myTA=aTxtA;
	textFieldV.add(aTxtA); //must be the last element of this V
	aTxtA.setEditable(false);
	aTxtA.getDocument().putProperty(Document.TitleProperty, "MY-CONSOLE");
	aTxtA.getDocument().addDocumentListener(this);
	JScrollPane aScrol=new JScrollPane(aTxtA, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                       JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

	GridBagConstraints c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;

	//c.fill = GridBagConstraints.HORIZONTAL;
	//aPanel.add(aTxtA, c);

	c.fill = GridBagConstraints.BOTH;
	c.weightx = 1.0;
	c.weighty = 1.0;
	aPanel.add(aScrol, c);

	add(aPanel);//this must be the last one
	
}


static void updateTA(DocumentEvent e)
{
String thisDoc=(String)(e.getDocument().getProperty(Document.TitleProperty));
String myDoc=(String)(myTA.getDocument().getProperty(Document.TitleProperty));
//System.out.println("Got signal from "+thisDoc);
	if (thisDoc.compareTo(myDoc) == 0)
	{
		myTA.setCaretPosition(myTA.getDocument().getLength());
	}
}

public void insertUpdate(DocumentEvent e)
{
	updateTA(e);
}

public void removeUpdate(DocumentEvent e)
{
	updateTA(e);
}

public void changedUpdate(DocumentEvent e)
{
	updateTA(e);
}

public void actionPerformed(ActionEvent e)
{
boolean ready2Fire=false;
String which1=e.getActionCommand();
String[] flds=which1.split(":");
//System.out.println("Got command : "+which1);
char prediction_type='P';
	switch (flds[0].charAt(0))
	{
	//case 'n':simCard=flds[1];break;
	case 'c':if (flds[1].charAt(0)=='P') currentIndex--;
					else currentIndex++ ;
			JTextField jFtxt=(JTextField)(dataFields.get("h:forday"));
			jFtxt.setText(""+currentIndex);
				break;
	case 'a':prediction_type=flds[1].charAt(0);break;
	case 'h':
		String cmdTxt=((JTextField)dataFields.get(which1)).getText();
		currentIndex = Integer.parseInt(cmdTxt);
	               break;
	
	default:break;
	}

	if (dataBuilder==null) {
		dataBuilder=new GetHistoryPrimeData();
	}
	if (prediction_type!='0')
	dataBuilder.prediction_type=prediction_type;
		historyPrecditionList=dataBuilder.getHistoryPredictionLists();
	
	if (historyPrecditionList != null && historyPrecditionList.size() >0){
		currentIndex= (historyPrecditionList.size()<currentIndex)?historyPrecditionList.size():currentIndex;
		myImage=dataBuilder.get1PageImage(currentIndex, historyPrecditionList.get(currentIndex));
		imagePanel.repaint();		
	}	 
}

static void createAndShowGUI()
{
JFrame.setDefaultLookAndFeelDecorated(true);
JFrame aFrame=new JFrame("Show Try History");
	aFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
JComponent newTry=new CreateMultiCombinations();
	newTry.setOpaque(true);
	aFrame.setContentPane(newTry);

	aFrame.pack();
	aFrame.setVisible(true);
}

public static void main(String[] args)
{
javax.swing.SwingUtilities.invokeLater(new Runnable(){
	public void run() {
		createAndShowGUI();
	}
	});
}


}
