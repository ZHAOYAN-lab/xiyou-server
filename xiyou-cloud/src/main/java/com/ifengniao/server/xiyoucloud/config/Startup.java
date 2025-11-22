package com.ifengniao.server.xiyoucloud.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
public class Startup implements CommandLineRunner {

//    @Autowired
//    private TestService testService;

    //    @Autowired
//    private FenceService fenceService;
//    @Autowired
//    private BeaconEntityRepository beaconEntityRepository;
//    @Autowired
//    private LocationObjectEntityRepository locationObjectEntityRepository;
//
//    @Autowired
//    private Influx2Sample influx2Sample;
//    @Autowired
//    private InfluxDBClient influxDBClient;

    @Override
    public void run(String... args) throws Exception {

//        MailService.sendMail("wangyanan@ifengniao.com", "你好，测试", "你好，测试");
//        System.out.println("");
//        MqttHandler.sendMqtt("123");
//        testService.registerByTag("test");

//        String jdbcUrl = "jdbc:TAOS://10.8.1.78:6030?user=root&password=taosdata";
//        Properties connProps = new Properties();
//        connProps.setProperty(TSDBDriver.PROPERTY_KEY_CHARSET, "UTF-8");
//        connProps.setProperty(TSDBDriver.PROPERTY_KEY_LOCALE, "en_US.UTF-8");
//        connProps.setProperty(TSDBDriver.PROPERTY_KEY_TIME_ZONE, "UTC-8");
//        Connection conn = DriverManager.getConnection(jdbcUrl, connProps);
//        System.out.println("Connected");
//        conn.close();

//        fenceEntityRepository.save(new FenceEntity(null, "测试", (short) Constants.WARN_TYPE_FENCE_ENTER, true, new MapEntity(6)));

//        var aaa = fenceService.findTest();
//        var str = JsonUtil.toJson(aaa);
//        var bbb = JsonUtil.fromJsonToList(str, FenceDTO.class);
//        System.out.println(str);
//        var list = fenceService.findAllByContainsPoint(1,2, 5.47);
//        var list1 = fenceService.findAllByContainsPoint(6,2, 5.47);
//        System.out.println(list);
//        var list = beaconEntityRepository.findAll();
//        System.out.println("123");
//        list = beaconEntityRepository.saveAll(list.stream().map(b -> b.setBeaconLastFenceIds(new Integer[]{})).toList());
//        System.out.println("456");
//        var locationObjectEntities = locationObjectEntityRepository.countLocationObjectGroupByLocationObjectType();
//        System.out.println(locationObjectEntities);

//        influx2Sample.writeByPOJO(WritePrecision.NS, new Temperature("location1", 1.1D, "测试", Instant.now()));
//
//        influx2Sample.writeByPoint("temperature", System.currentTimeMillis(), Map.of("location", "location2"), Map.of("value", 2.2D, "value1", "测试"));
////
//        try {
//            var param = new HashMap<String, String>();
//            param.put("mapId", null);
//            param.put("loId", null);
//            var list = influx2Sample.query("beacon_history", param, null, null, null, "3s");
//            System.out.println(list);
//            var map = list.stream().collect(Collectors.groupingBy(m -> m.get("loId").toString()));
//            System.out.println(map);
//        } catch (Exception e) {
//            log.error("失败：", e);
//        }

//        StringBuilder flux = new StringBuilder("from(bucket:\"xiyou\") ");
//        flux.append(" |> range(start: 0) ");
//        flux.append(" |> filter(fn: (r) => ");
//        flux.append(" r._measurement == \"beacon_history\" ");
//        flux.append(" and r._field != \"z\" ");
//        flux.append(" )");
//        flux.append(" |> last() ");
////        flux.append(" |> sort(desc: true, columns: [\"_time\"]) ");
////        flux.append(" |> limit(n: 100, offset: 100) ");
////        flux.append(" |> window(every: 3s) ");
////        flux.append(" |> first() ");
////        flux.append(" |> duplicate(column: \"_stop\", as: \"_time\") ");
////        flux.append(" |> window(every: inf) ");
////        flux.append(" |> drop(columns: [\"_measurement\", \"mapId\"]) ");
//        List<FluxTable> tables = influxDBClient.getQueryApi().query(flux.toString());
//        for (FluxTable fluxTable : tables) {
//            for (FluxRecord fluxRecord : fluxTable.getRecords()) {
//                System.out.println(fluxRecord.getValues());
//            }
//        }

    }

//    @NoArgsConstructor
//    @AllArgsConstructor
//    @Data
//    @Accessors(chain = true)
//    @Measurement(name = "temperature")
//    private static class Temperature {
//        @Column(tag = true)
//        String location;
//        @Column
//        Double value;
//        @Column
//        String value1;
//        @Column(timestamp = true)
//        Instant time;
//    }

}
