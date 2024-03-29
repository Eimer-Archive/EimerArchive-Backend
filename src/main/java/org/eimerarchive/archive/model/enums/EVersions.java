package org.eimerarchive.archive.model.enums;

import java.util.ArrayList;
import java.util.List;

public enum EVersions {
    V1_19_2("1.19.2"),
    V1_19_1("1.19.1"),
    V1_19("1.19"),
    V1_18_2("1.18.2"),
    V1_18_1("1.18.1"),
    V1_18("1.18"),
    V1_17_1("1.17.1"),
    V1_17("1.17"),
    V1_16_5("1.16.5"),
    V1_16_4("1.16.4"),
    V1_16_3("1.16.3"),
    V1_16_2("1.16.2"),
    V1_16_1("1.16.1"),
    V1_16("1.16"),
    V1_15_2("1.15.2"),
    V1_15_1("1.15.1"),
    V1_15("1.15"),
    V1_14_4("1.14.4"),
    V1_14_3("1.14.3"),
    V1_14_2("1.14.2"),
    V1_14_1("1.14.1"),
    V1_14("1.14"),
    V1_13_2("1.13.2"),
    V1_13_1("1.13.1"),
    V1_13("1.13"),
    V1_12_2("1.12.2"),
    V1_12_1("1.12.1"),
    V1_12("1.12"),
    V1_11_2("1.11.2"),
    V1_11_1("1.11.1"),
    V1_11("1.11"),
    V1_10_2("1.10.2"),
    V1_10_1("1.10.1"),
    V1_10("1.10"),
    V1_9_2("1.9.2"),
    V1_9_1("1.9.1"),
    V1_9("1.9"),
    V1_8_9("1.8.9"),
    V1_8_8("1.8.8"),
    V1_8_7("1.8.7"),
    V1_8_6("1.8.6"),
    V1_8_5("1.8.5"),
    V1_8_4("1.8.4"),
    V1_8_3("1.8.3"),
    V1_8_2("1.8.2"),
    V1_8_1("1.8.1"),
    V1_8("1.8"),
    V1_7_10("1.7.10"),
    V1_7_9("1.7.9"),
    V1_7_8("1.7.8"),
    V1_7_7("1.7.7"),
    V1_7_6("1.7.6"),
    V1_7_5("1.7.5"),
    V1_7_4("1.7.4"),
    V1_7_3("1.7.3"),
    V1_7_2("1.7."),
    V1_7_1("1.7.1"),
    V1_7("1.7"),
    V1_6_4("1.6.4"),
    V1_6_3("1.6.3"),
    V1_6_2("1.6.2"),
    V1_6_1("1.6.1"),
    V1_6("1.6"),
    V1_5_2("1.5.2"),
    V1_5_1("1.5.1"),
    V1_5("1.5"),
    V1_4_6("1.4.6"),
    V1_4_5("1.4.5"),
    V1_4_4("1.4.4"),
    V1_4_3("1.4.3"),
    V1_4_2("1.4.2"),
    V1_4_1("1.4.1"),
    V1_4("1.4"),
    V1_3_2("1.3.2"),
    V1_3_1("1.3.1"),
    V1_3("1.3"),
    V1_2_5("1.2.5"),
    V1_2_4("1.2.4"),
    V1_2_3("1.2.3"),
    V1_2_2("1.2.2"),
    V1_2_1("1.2.1"),
    V1_2("1.2"),
    V1_1("1.1"),
    V1_0("1.0"),

    B1_7_3("b1.7.3"),
    B1_7_2("b1.7.2"),
    B1_7_1("b1.7.1");

    public final String version;

    EVersions(String version) {
        this.version = version;
    }

    public static EVersions fromString(String version) {
        for (EVersions v : EVersions.values()) {
            if (v.version.equals(version)) {
                return v;
            }
        }
        return null;
    }

    // Switch the enum to its string value list
    public static List<String> toStringArray(List<EVersions> list) {
        List<String> stringList = new ArrayList<>();
        for (EVersions v : list) {
            stringList.add(v.version);
        }
        return stringList;
    }
}