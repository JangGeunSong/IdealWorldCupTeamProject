import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.*;

public class MainGamePanel extends JPanel{
    private EntryPanel leftPanel, rightPanel;
    //
    private JButton btnH;
    //
    private ButtonListener btnL;
    //
    private PanelClickDetect dteP;
    //
    private Timer aniTimerLeft, resetTimerLeft;
    private Timer aniTimerRight, resetTimerRight;
    //
    private String Type;
    //
    private int nRound, nElem, nWinner, nNextMatch, rBound, signal;
    //
    private EntryComponent initialComponent;
    private EntryComponent[] eTree;
    private EntryPanel[] Tree;
    //
    private UserDefinedHistoryFrame UDHF;
    private FILEDB imString;
    private PrimaryPanel parentPanel;
    private EndingPanel EDP;


    public MainGamePanel(String Type, int Round, PrimaryPanel parent) throws SQLException {
        int i = 0, j = 0;
        this.setBackground(Color.WHITE);
        this.setPreferredSize(new Dimension(1440,900));
        this.setLayout(null);

        dteP = new PanelClickDetect();

        this.Type = Type;
        this.nRound = Round;
        this.nElem = (this.nRound * 2) - 1;
        this.nNextMatch = nElem;
        this.rBound = 1;
        this.signal = 0;
        this.parentPanel = parent;
        this.initialComponent = new EntryComponent("Images/QuestionMark/a.jpg","Images/QuestionMark/a.jpg","", 0);

        if(Type == "여자") {
        	imString = new FILEDB("WOMAN", nRound);
        }
        else if(Type == "남자"){
        	imString = new FILEDB("MAN", nRound);
        }

        eTree = new EntryComponent[nElem + 1];
        Tree = new EntryPanel[nElem + 1];

        for (i = nElem; i > 0; i--){
            if(i > nElem/2) {
                eTree[i] = imString.getRandomImgList()[j];
                j++;
            }
            else{
                eTree[i] = this.initialComponent;
            }
        }

        for(i = nElem; i > 0; i--) {
            Tree[i] = new EntryPanel(10,60,eTree[i]);
        }

        for(i = 1; i < nElem + 1; i++){
            Tree[i].addMouseListener(dteP);
        }

        UDHF = new UserDefinedHistoryFrame(nElem + 1, eTree);
        UDHF.setTitle("현재상황");
        UDHF.setVisible(false);


        leftPanel = Tree[nNextMatch - 1];
        leftPanel.setPtX(10);
        leftPanel.setPtY(60);
        leftPanel.setLocation(leftPanel.getPtX(),leftPanel.getPtY());
        leftPanel.repaint();
        this.add(leftPanel);
        //leftPanel.addMouseListener(dteP);

        rightPanel = Tree[nNextMatch];
        rightPanel.setPtX(720);
        rightPanel.setPtY(60);
        rightPanel.setLocation(rightPanel.getPtX(),rightPanel.getPtY());
        rightPanel.repaint();
        this.add(rightPanel);
        //rightPanel.addMouseListener(dteP);

        btnH = new JButton("HISTORY");
        btnH.setBounds(1200,10,200,40);
        btnH.setFont(new Font("Consolas", Font.BOLD, 30));
        btnH.setForeground(Color.WHITE);
        btnH.setBackground(Color.BLACK);
        this.add(btnH);

        btnL = new ButtonListener();
        btnH.addActionListener(btnL);
        
        
        resetTimerRight = new Timer(2500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent a){
                resetTimerRight.start();
                if((nNextMatch)/2 == 0){
                    signal = 1;
                }
                reset(nNextMatch - 1, nNextMatch, signal, eTree[1]);
                resetTimerRight.stop();
            }
        });
        
        resetTimerLeft = new Timer(2500, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                resetTimerLeft.start();
                if((nNextMatch)/2 == 0){
                    signal = 1;
                }
                reset(nNextMatch - 1, nNextMatch, signal, eTree[1]);
                resetTimerLeft.stop();
            }
        });
        
        //make Two reset Timer left and right side.
        
        aniTimerLeft = new Timer(1, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                aniTimerLeft.start();
                if(leftPanel.getPtX() < 400){
                    leftPanel.setPtX(leftPanel.getPtX() + leftPanel.getVelocity());
                    leftPanel.setLocation(leftPanel.getPtX(), leftPanel.getPtY());
                    leftPanel.repaint();
                    aniTimerLeft.setRepeats(true);
                }
                else if(leftPanel.getPtX() >= 400){
                    resetTimerLeft.start();
                    aniTimerLeft.stop();
                }
            }
        });
        
        aniTimerRight = new Timer(1, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                aniTimerRight.start();
                if(rightPanel.getPtX() > 400){
                    rightPanel.setPtX(rightPanel.getPtX() - rightPanel.getVelocity());
                    rightPanel.setLocation(rightPanel.getPtX(), rightPanel.getPtY());
                    rightPanel.repaint();
                    aniTimerLeft.setRepeats(true);
                }
                else if(rightPanel.getPtX() <= 400){
                    resetTimerRight.start();
                    aniTimerRight.stop();
                }
            }
        });
        //make Two animation Timer left and right side.

    }

    public void reset(int nLeft, int nRight, int signal, EntryComponent E) {
        if(signal == 0) {
            leftPanel.setVisible(false);
            rightPanel.setVisible(false);
            leftPanel = null;
            leftPanel = Tree[nLeft];
            leftPanel.setPtX(10);
            leftPanel.setPtY(60);
            leftPanel.setLocation(leftPanel.getPtX(), leftPanel.getPtY());
            //leftPanel.addMouseListener(dteP);
            this.add(leftPanel);
            leftPanel.setVisible(true);
            rightPanel = null;
            rightPanel = Tree[nRight];
            rightPanel.setPtX(720);
            rightPanel.setPtY(60);
            rightPanel.setLocation(rightPanel.getPtX(), rightPanel.getPtY());
            //rightPanel.addMouseListener(dteP);
            this.add(rightPanel);
            rightPanel.setVisible(true);
            this.repaint();
        }
        else{
        	this.parentPanel.disableMGP();
        	this.parentPanel.addEDP(E, imString, parentPanel);
        }
    }

    private class ButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            Object obj = e.getSource();
            if(obj == btnH){
            	new UserDefinedHistoryFrame(nElem + 1, eTree);
            }
        }
    }

    private class PanelClickDetect implements MouseListener{
        @Override
        public void mouseClicked(MouseEvent e){
            Object obj = e.getSource();
            if(obj == rightPanel){
            	if( !aniTimerRight.isRunning() && !resetTimerRight.isRunning()) {
            	
            			nWinner = nNextMatch;
            			Tree[(nNextMatch)/2] = rightPanel;
                    	eTree[(nNextMatch)/2] = rightPanel.getEntryComponent();
                    	UDHF.sendInformation(nNextMatch/2, rightPanel.getEntryComponent());
                    	nNextMatch = nNextMatch - 2;
                    	rightPanel.setVelocity(8);
                    	leftPanel.setVisible(false);
                    	//Tree.get(nNextMatch/2).add(Tree.get(nWinner));
                    	aniTimerRight.start();
            		}
            	//if - else
            }//if-else
            else if(obj == leftPanel){
            	if( !aniTimerLeft.isRunning() && !resetTimerLeft.isRunning()) {
            			nWinner = nNextMatch + 1;
            			Tree[(nNextMatch)/2] = leftPanel;
                        eTree[(nNextMatch)/2] = leftPanel.getEntryComponent();
                        UDHF.sendInformation(nNextMatch/2, leftPanel.getEntryComponent());
                        nNextMatch = nNextMatch - 2;
                        leftPanel.setVelocity(8);
                        rightPanel.setVisible(false);
                        //Tree.get(nNextMatch/2).add(Tree.get(nWinner));
                        aniTimerLeft.start();
            		}
            		
            	//if - else
            }//if-else
        }//mouseClicked()
        @Override
        public void mousePressed(MouseEvent e){}
        @Override
        public void mouseReleased(MouseEvent e){}
        @Override
        public void mouseEntered(MouseEvent e){}
        @Override
        public void mouseExited(MouseEvent e){}
    }
}
