package com.thecubecast.ReEngine.Graphics;

import com.thecubecast.ReEngine.Graphics.RePipeTextureDrawable;
import com.thecubecast.ReEngine.Graphics.RePipeline;

import java.util.Comparator;

public class RePipeLayerSort implements Comparator<RePipeLayer> {
    @Override
    public int compare(RePipeLayer o1, RePipeLayer o2) {
        // entities ordered based on y-position
        return (Float.compare(o1.zLayer, o2.zLayer));
    }
}
