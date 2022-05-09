package skipbo.client;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.*;
import java.util.Objects;

public class DragDropTest extends JFrame implements DropTargetListener, DragGestureListener {

    JTextPane draggedPane;

    ImageIcon card1 = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("R1.png")));
    ImageIcon card2 = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("R2.png")));
    ImageIcon card3 = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("R3.png")));


    public static void main(String[] args) {

        JFrame frame = new DragDropTest();
        frame.setVisible(true);
    }

    /**
     * Class for testing drag and drop but not really working as wished (yet).
     */
    DragDropTest() {
        setTitle("DragDrop Test");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(100, 100, dim.width, dim.height);
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel();
        setContentPane(contentPane);

/*

        //drag card
        JTextPane textPane = new JTextPane();
        textPane.setBounds(500, 100, card1.getIconWidth(), card1.getIconHeight());
        textPane.insertIcon(card1);
        textPane.setEditable(false);
        textPane.setDragEnabled(true);
        contentPane.add(textPane);
        DragSource dragSource = new DragSource();
        DragGestureRecognizer dragGestureRecognizer = dragSource.createDefaultDragGestureRecognizer(textPane, DnDConstants.ACTION_MOVE, this);

        //drop card not draggable
        JTextPane textPane2 = new JTextPane();
        textPane2.setBounds(700, 100, card2.getIconWidth(), card2.getIconHeight());
        textPane2.insertIcon(card2);
        textPane2.setEditable(false);
        contentPane.add(textPane2);
        DropTarget dropTarget = new DropTarget(textPane2, this);
        textPane2.setDragEnabled(false);
*/

        //scale image
        Image image = card1.getImage().getScaledInstance(100, 145, Image.SCALE_DEFAULT);
        ImageIcon scaledIcon = new ImageIcon(image);

        JTextPane hand = new JTextPane();
        hand.setBounds(500, 500, scaledIcon.getIconWidth(), scaledIcon.getIconHeight());
        hand.setPreferredSize(new Dimension(scaledIcon.getIconWidth(), scaledIcon.getIconHeight()));
        hand.insertIcon(scaledIcon);
        hand.setEditable(false);
        contentPane.add(hand);
        hand.setDragEnabled(true);
        DragSource handSource = new DragSource();
        DragGestureRecognizer handRecognizer = handSource.createDefaultDragGestureRecognizer(hand, DnDConstants.ACTION_MOVE, this);

        //Discard
        JTextPane discard = new JTextPane();
        discard.setBounds(700, 500, card1.getIconWidth(), card1.getIconHeight());
        discard.setPreferredSize(new Dimension(card1.getIconWidth(), card1.getIconHeight()));
        discard.setEditable(false);
        discard.setBackground(Color.GRAY);
        contentPane.add(discard);
        discard.setDragEnabled(true);
        DragSource discardSource = new DragSource();
        DragGestureRecognizer discardRecognizer = discardSource.createDefaultDragGestureRecognizer(discard, DnDConstants.ACTION_MOVE, this);
        DropTarget discardTarget = new DropTarget(discard, this);

        //build
        JTextPane build = new JTextPane();
        build.setBounds(900, 500, card2.getIconWidth(), card2.getIconHeight());
        build.insertIcon(card2);
        build.setEditable(false);
        contentPane.add(build);
        DropTarget buildTarget = new DropTarget(build, this);
        build.setDragEnabled(false);

    }


    //Dropping
    @Override
    public void dragEnter(DropTargetDragEvent dropTargetDragEvent) {
        // clientLog.debug("drag Enter");
    }

    @Override
    public void dragOver(DropTargetDragEvent dropTargetDragEvent) {

    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dropTargetDragEvent) {

    }

    @Override
    public void dragExit(DropTargetEvent dropTargetEvent) {
        //clientLog.debug("drag Exit");
    }

    @Override
    public void drop(DropTargetDropEvent dropTargetDropEvent) {
        //clientLog.debug("Drop event occurred");
        DropTarget target = (DropTarget) dropTargetDropEvent.getSource();
        JTextPane pane = (JTextPane) target.getComponent();
        draggedPane.setText("");
        draggedPane.insertIcon(card3);
        pane.setText("");
        pane.insertIcon(card1);
    }


    //Dragging
    @Override
    public void dragGestureRecognized(DragGestureEvent dragGestureEvent) {
        //clientLog.debug("drag gesture recognized");
        draggedPane = (JTextPane) dragGestureEvent.getComponent();

    }


}
