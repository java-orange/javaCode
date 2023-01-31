package com.hvisions.iot.utils;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class BaseInfo {
    private String name;
    private String label;
    private String desc;

    public static <T extends Description> List<BaseInfo> valuesFrom(List<T> descriptions) {
        return descriptions.stream()
                .map(desc -> new BaseInfo(desc.getName(), desc.getLabel(), desc.getDesc()))
                .collect(Collectors.toList());
    }

    public static <T extends Description> List<BaseInfo> enumInfo(Class<T> enumClass) {
        List<BaseInfo> baseInfos = new LinkedList<>();
        for (T t : enumClass.getEnumConstants()) {
            BaseInfo info = new BaseInfo();
            info.setLabel(t.getLabel());
            info.setName(t.getName());
            info.setDesc(t.getDesc());

            baseInfos.add(info);
        }

        return baseInfos;
    }

    public static <T> List<String> enumNames(Class<T> enumClass) {
        List<String> names = new LinkedList<>();

        for (T t : enumClass.getEnumConstants()) {
            names.add(t.toString());
        }

        return names;
    }

    public BaseInfo() {

    }

    public BaseInfo(String name, String label) {
        this.name = name;
        this.label = label;
    }

    public BaseInfo(String name, String label, String desc) {
        this.name = name;
        this.label = label;
        this.desc = desc;
    }
}
