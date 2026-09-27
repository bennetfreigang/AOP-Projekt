package model.board;

import model.tiles.Tile;
import model.tiles.TileColor;
import model.tiles.TileSymbol;

import java.util.*;

/**
 * Finds the move that scores the most points with a given hand, by trying every legal move.
 */
public class BestMoveFinder {

    private final Map<Position, Tile> placedTiles;
    private final Set<Map<Position, Tile>> visited = new HashSet<>();
    private Move best;

    public BestMoveFinder(Map<Position, Tile> placedTiles) {
        this.placedTiles = placedTiles;
    }

    public static Move findBestMove(Map<Position, Tile> placedTiles, List<Tile> hand) {
        BestMoveFinder finder = new BestMoveFinder(placedTiles);
        finder.search(new HashMap<>(), new ArrayList<>(hand));
        return finder.best;
    }

    private void search(Map<Position, Tile> pending, List<Tile> remaining) {
        List<Position> candidates = pending.isEmpty() ? findAnchors() : findRunEnds(pending);

        for (Position position : candidates) {
            Set<TileKind> triedKinds = new HashSet<>();

            for (int i = 0; i < remaining.size(); i++) {
                Tile tile = remaining.get(i);
                if (!triedKinds.add(new TileKind(tile.getColor(), tile.getSymbol()))) continue;

                pending.put(position, tile);
                if (visited.add(new HashMap<>(pending)) && PlacementValidator.checkPendingTilePlacement(placedTiles, pending).isLegal()) {
                    offer(pending);

                    List<Tile> rest = new ArrayList<>(remaining);
                    rest.remove(i);
                    search(pending, rest);
                }
                pending.remove(position);
            }
        }
    }

    private void offer(Map<Position, Tile> pending) {
        int score = ScoreCalculator.calculatePendingTilesScore(placedTiles, pending);

        boolean isBetter = best == null
                || score > best.score()
                || (score == best.score() && pending.size() > best.tiles().size());

        if (isBetter) best = new Move(pending, score);
    }

    private List<Position> findAnchors() {
        if (placedTiles.isEmpty()) return List.of(new Position(0, 0));

        Set<Position> anchors = new HashSet<>();
        for (Position position : placedTiles.keySet()) {
            for (Direction direction : Direction.values()) {
                Position neighbor = position.neighbor(direction);
                if (!placedTiles.containsKey(neighbor)) anchors.add(neighbor);
            }
        }
        return new ArrayList<>(anchors);
    }

    private List<Position> findRunEnds(Map<Position, Tile> pending) {
        List<Position> ends = new ArrayList<>();

        if (pending.size() == 1) {
            Position only = pending.keySet().iterator().next();
            for (Direction direction : Direction.values()) {
                ends.add(only.neighbor(direction));
            }
            return ends;
        }

        boolean sameRow = pending.keySet().stream().map(Position::y).distinct().count() == 1;
        LineOrientation orientation = sameRow ? LineOrientation.HORIZONTAL : LineOrientation.VERTICAL;

        Position first = null;
        Position last = null;
        for (Position position : pending.keySet()) {
            int extent = orientation.extentCoordinate.applyAsInt(position);
            if (first == null || extent < orientation.extentCoordinate.applyAsInt(first)) first = position;
            if (last == null || extent > orientation.extentCoordinate.applyAsInt(last)) last = position;
        }

        ends.add(first.neighbor(orientation.backwardDirection));
        ends.add(last.neighbor(orientation.forwardDirection));
        return ends;
    }

    private record TileKind(TileColor color, TileSymbol symbol) {}
}
