package com.ifengniao.server.xiyoucloud.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.locationtech.jts.geom.Coordinate;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class Pxy {
    public double x;
    public double y;

    public static List<Pxy> covert(Coordinate[] list) {
        List<Pxy> result = new ArrayList<>();
        if (list != null) {
            for (var source : list) {
                result.add(new Pxy(source));
            }
        }
        return result;
    }

    public static Coordinate[] covertToCoordinateArray(List<Pxy> list) {
        Coordinate[] result = new Coordinate[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i).toCoordinate();
        }
        return result;
    }

    public Coordinate toCoordinate() {
        return new Coordinate(x, y);
    }

    public Pxy(Coordinate coordinate) {
        this.x = coordinate.getX();
        this.y = coordinate.getY();
    }
}