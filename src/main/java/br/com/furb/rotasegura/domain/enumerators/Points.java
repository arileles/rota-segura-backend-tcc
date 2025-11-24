package br.com.furb.rotasegura.domain.enumerators;

import java.util.List;

public enum Points {
    LEVEL1(1, 1),
    LEVEL2(2, 5),
    LEVEL3(3, 10),
    LEVEL4(4, 15),
    LEVEL5(5, 20);
    private int level;

    private int count;

    Points(int level, int count) {
        this.level = level;
        this.count = count;
    }

    public int getLevel() {
        return level;
    }

    public int getCount() {
        return count;
    }

    public static List<Points> getSortedLevels() {
        return List.of(LEVEL5, LEVEL4, LEVEL3, LEVEL2, LEVEL1);
    }
}
