package com.hvisions.iot.utils;

public interface Description {
    /**
     * 获取描述对象的名称, 作为ID显示
     *
     * @return 名称
     */
    String getName();

    /**
     * 获取描述对象的标识名称，一般就是显示的部分，如中文的描述
     *
     * @return label的名称
     */
    String getLabel();

    /**
     * 获取描述对象的描述信息，一般就是单单的描述信息
     *
     * @return 描述的信息
     */
    default String getDesc() {
        return null;
    }

}
