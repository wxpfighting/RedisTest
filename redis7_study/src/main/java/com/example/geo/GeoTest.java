package com.example.geo;

import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

@Component
public class GeoTest {
    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    private static final String geoKey = "geo1";

    public void add(){
        HashMap<Object, Point> map = new HashMap<>();
        map.put("天安门",new Point(116.48431816068796,39.90304647716258));
        map.put("故宫",new Point(116.50413979705331,39.89514194606241));
        redisTemplate.opsForGeo().add(geoKey,map);
    }

    public Point position(){
        List<Point> position = redisTemplate.opsForGeo().position(geoKey, "天安门");
        return position.get(0);
    }

    public String getHash(){
        List<String> hashList = redisTemplate.opsForGeo().hash(geoKey, "天安门");
        return hashList.get(0);
    }

    public Distance getDistance(){
        Distance distance = redisTemplate.opsForGeo().distance(geoKey, "天安门", "故宫");
        return distance;
    }

    public GeoResults<RedisGeoCommands.GeoLocation<Object>> getRadius(){
        Point point = new Point(116.4995301140001, 39.89773543165026);
        Circle circle = new Circle(point, Metrics.KILOMETERS.getMultiplier());
        RedisGeoCommands.GeoRadiusCommandArgs geoRadiusCommandArgs = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeDistance().includeCoordinates().sortAscending();
        GeoResults<RedisGeoCommands.GeoLocation<Object>> radius = redisTemplate.opsForGeo().radius(geoKey, circle, geoRadiusCommandArgs);
        return radius;
    }

    public GeoResults<RedisGeoCommands.GeoLocation<Object>> getRadiusByMember(){
        RedisGeoCommands.GeoRadiusCommandArgs geoRadiusCommandArgs = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeDistance().includeCoordinates().sortAscending();
        GeoResults<RedisGeoCommands.GeoLocation<Object>> radius = redisTemplate.opsForGeo().radius(geoKey, "天安门",new Distance(233,Metrics.KILOMETERS),geoRadiusCommandArgs);
        return radius;
    }
}
