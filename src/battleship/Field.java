package battleship;

/**
 * Created by spasynkov on 29.11.2015.
 */
class Field {
    private final char filledCell = '8';
    private final char emptyCell = ' ';
    private final char[][] field = new char[10][10];
    public Field() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                field[i][j] = '-';
            }
        }
    }

    public String getFieldLine(int index) {
        StringBuilder sb = new StringBuilder("|");
        for (int i = 0; i < field[index].length; i++) {
            sb.append(" ").append(field[index][i]).append(" ");
        }
        return sb.append("|").toString();
    }

    public void clearField() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (field[i][j] != filledCell) field[i][j] = emptyCell;
            }
        }
    }

    public void drawField() {
        System.out.println("   --------------------------------");
        for (int i = 9; i >= 0; i--) {
            if (i != 9) System.out.print(" ");
            System.out.print((i + 1) + " |");
            for (int j = 0; j < 10; j++) {
                System.out.print(" " + field[i][j] + " ");
            }
            System.out.print("|");
            System.out.println();
        }
        System.out.println("   --------------------------------");
        System.out.println("     A  B  C  D  E  F  G  H  I  J");
    }

    public boolean putShip(int decks, String sStart, String sEnd) throws ShipPlacementException {
        int[] start = getCoordinatesFromString(sStart);
        int[] end = getCoordinatesFromString(sEnd);
        if (start == null || end == null) return false;
        int startX = start[0];
        int startY = start[1];
        int endX = end[0];
        int endY = end[1];

        if (Math.abs(startX - endX) != decks ^ Math.abs(startY - endY) != decks) {
            throw new ShipPlacementException("Wrong size");
        }

        boolean xDirection = false;
        boolean yDirection = false;
        if (startX == endX) yDirection = true;
        if (startY == endY) xDirection = true;

        if (xDirection && yDirection) throw new ShipPlacementException("Not single-deck");
        else if (!xDirection && !yDirection) throw new ShipPlacementException("Wrong placement");
        else {
            int startCell;
            int endCell;

            if (xDirection) {
                startCell = Math.min(startX, endX);
                endCell = startCell + decks;
            } else {
                startCell = Math.min(startY, endY);
                endCell = startCell + decks;
            }

            // Checking before filling field
            for (int i = startCell; i < endCell; i++) {
                if (xDirection) checkIfCloseByXY(startY, i);
                else checkIfCloseByXY(i, startX);
            }
            // Filling field with dots and 8's
            for (int i = startCell; i < endCell; i++) {
                if (xDirection) {
                    this.field[startY][i] = filledCell;
                    surroundWithDots(startY, i);
                } else {
                    this.field[i][startX] = filledCell;
                    surroundWithDots(i, startX);
                }
            }
        }
        return true;
    }

    public boolean putShip(String coordinate) throws ShipPlacementException {
        int[] start = getCoordinatesFromString(coordinate);
        if (start == null) return false;
        checkIfCloseByXY(start[1], start[0]);
        this.field[start[1]][start[0]] = filledCell;
        surroundWithDots(start[1], start[0]);
        return true;
    }

    private void checkIfCloseByXY(int y, int x) throws ShipPlacementException {
        ShipPlacementException e = new ShipPlacementException("Too close to other ships");
        if (x > 0) {
            if (y - 1 >= 0 && this.field[y - 1][x - 1] == filledCell) throw e;
            if (this.field[y][x - 1] == filledCell) throw e;
            if (y + 1 < 10 && this.field[y + 1][x - 1] == filledCell) throw e;
        }
        if (x < 9) {
            if (y - 1 >= 0 && this.field[y - 1][x + 1] == filledCell) throw e;
            if (this.field[y][x + 1] == filledCell) throw e;
            if (y + 1 < 10 && this.field[y + 1][x + 1] == filledCell) throw e;
        }
        if (y - 1 >= 0 && this.field[y - 1][x] == filledCell) throw e;
        if (this.field[y][x] == filledCell) throw new ShipPlacementException("Cell isn't empty");
        if (y + 1 < 10 && this.field[y + 1][x] == filledCell) throw e;
    }

    private void surroundWithDots(int y, int x) {
        if (x > 0) {
            if (y - 1 >= 0 && this.field[y - 1][x - 1] != filledCell) this.field[y - 1][x - 1] = '.';
            if (this.field[y][x - 1] != filledCell) this.field[y][x - 1] = '.';
            if (y + 1 < 10 && this.field[y + 1][x - 1] != filledCell) this.field[y + 1][x - 1] = '.';
        }
        if (x < 9) {
            if (y - 1 >= 0 && this.field[y - 1][x + 1] != filledCell) this.field[y - 1][x + 1] = '.';
            if (this.field[y][x + 1] != filledCell) this.field[y][x + 1] = '.';
            if (y + 1 < 10 && this.field[y + 1][x + 1] != filledCell) this.field[y + 1][x + 1] = '.';
        }
        if (y - 1 >= 0 && this.field[y - 1][x] != filledCell) this.field[y - 1][x] = '.';
        if (y + 1 < 10 && this.field[y + 1][x] != filledCell) this.field[y + 1][x] = '.';

    }

    private int[] getCoordinatesFromString(String s) throws ShipPlacementException {
        int[] result;
        if (s.length() == 2 || s.length() == 3) {
            try {
                char first = s.toUpperCase().charAt(0);
                int second = Integer.parseInt(s.substring(1));
                if (first >= 'A' && first <= 'J' &&
                        second > 0 && second < 11) {
                    result = new int[]{first - 'A', second - 1};
                } else throw new ShipPlacementException("Coordinates out of range");
            } catch (NumberFormatException e) {
                throw new ShipPlacementException("Number format failed");
            }
        } else throw new ShipPlacementException("String too long");
        return result;
    }
}
