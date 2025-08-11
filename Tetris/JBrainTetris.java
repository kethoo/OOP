import javax.swing.*;
import java.awt.*;

public class JBrainTetris extends JTetris {

    private Brain brain;
    private Brain.Move move;
    private JCheckBox mode;
    private JSlider adv;
    private JLabel advStatus;

    JBrainTetris(int pixels) {
        super(pixels);
        brain = new DefaultBrain();
    }

    @Override
    public JComponent createControlPanel() {
        JPanel p = (JPanel)super.createControlPanel();
        JPanel l = new JPanel();
        advStatus = new JLabel("ok");
        l.add(advStatus);
        l.add(new JLabel("Adversary:"));
        adv = new JSlider(0, 100, 0);
        adv.setPreferredSize(new Dimension(100,15));
        l.add(adv);
        p.add(l);
        mode = new JCheckBox("Brain active");
        p.add(mode);
        return p;
    }

    @Override
    public void tick(int action) {
        if (mode.isSelected() && action == DOWN) {
            tickHelper();
        }
        super.tick(action);
    }

    private void tickHelper() {
        board.undo();
        move = brain.bestMove(board, currentPiece, board.getHeight(), move);
        if(!move.piece.equals(currentPiece)) {
            super.tick(ROTATE);
        }
        else if (move.x < currentX) {
            super.tick(LEFT);
        }
        else if(move.x > currentX){
            super.tick(RIGHT);
        }
    }

    @Override
    public Piece pickNextPiece() {
        int sliderValue = adv.getValue();

        if (random.nextInt(100) < sliderValue) {
            advStatus.setText("*ok*");
            return pickWorstPiece();
        } else {
            advStatus.setText("ok");
            return super.pickNextPiece();
        }
    }

    private Piece pickWorstPiece() {
        Piece worstPiece = null;
        double worstScore = -1;

        board.commit();

        for (Piece piece : pieces) {
            Brain.Move nextMove = brain.bestMove(board, piece, board.getHeight(), null);

            if (nextMove != null && (worstPiece == null || nextMove.score > worstScore)) {
                worstPiece = piece;
                worstScore = nextMove.score;
            }
        }

        if (worstPiece == null) {
            return super.pickNextPiece();
        }

        return worstPiece;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }
        JBrainTetris tetris = new JBrainTetris(16);
        JFrame frame = JTetris.createFrame(tetris);
        frame.setVisible(true);
    }
}