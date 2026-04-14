package solitaire;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GameRecorder {

    private static final String FILE_PATH = "game_record.txt";
    private BufferedWriter writer;
    private boolean recording = false;

    // Start a new recording — writes header with size and type
    public void startRecording(int size, BoardType type) {
        try {
            writer = new BufferedWriter(new FileWriter(FILE_PATH));
            writer.write(size + "," + type.name());
            writer.newLine();
            recording = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Record a regular move
    public void recordMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (!recording) return;
        try {
            writer.write(fromRow + "," + fromCol + "," + toRow + "," + toCol);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Record a randomize event — saves full board snapshot
    public void recordRandomize(Board board) {
        if (!recording) return;
        try {
            writer.write("RANDOMIZE");
            writer.newLine();
            int size = board.getSize();
            StringBuilder sb = new StringBuilder();
            for (int r = 0; r < size; r++) {
                for (int c = 0; c < size; c++) {
                    sb.append(board.getState(r, c).name());
                    if (c < size - 1) sb.append(",");
                }
                if (r < size - 1) sb.append(";");
            }
            writer.write(sb.toString());
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Stop recording and close the file
    public void stopRecording() {
        if (!recording) return;
        try {
            if (writer != null) writer.close();
            recording = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isRecording() {
        return recording;
    }

    // Read the recorded file and return all lines
    public List<String> loadRecording() {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }
}