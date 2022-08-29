package com.example.demo.src.feed.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@AllArgsConstructor
public class GetBrandRes implements Comparable<GetBrandRes> {
    private String name;
    private String brandImgUrl;
    private Integer productCount;

    @Override
    public int compareTo(@NotNull GetBrandRes o) {
        if (o.productCount < productCount) {
            return 1;
        } else if (o.productCount > productCount) {
            return -1;
        }
        return 0;
    }
}
