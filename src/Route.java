import java.util.ArrayList;
public class Route {
    public ArrayList<Integer[]> coords = new ArrayList<>();
    public int superpowers;

    public Route(int superpowers) {
        this.superpowers = superpowers;
    }

    public Route(ArrayList<Integer[]> route, int superpowers) {
        this.coords = new ArrayList<>(route);
        this.superpowers = superpowers;
    }

    public Route add(Integer[] coord) {
        coords.add(coord);
        return this;
    }

    public Route addUsePower(Integer[] coord) {
        if (superpowers <= 0) {
            throw new IllegalArgumentException("Cannot use addUsePower. Have no superpowers");
        }
        coords.add(coord);
        superpowers--;
        return this;
    }

    public boolean setOnPath(Maze maze) {
        try {
            for (Integer[] coord : coords) {
                maze.getRoom(coord[0], coord[1]).onPath = true;
            }
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

}
