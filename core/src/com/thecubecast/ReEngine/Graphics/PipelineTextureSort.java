package com.thecubecast.ReEngine.Graphics;

import com.thecubecast.ReEngine.Graphics.RePipeTextureDrawable;
import com.thecubecast.ReEngine.Graphics.RePipeline;

import java.util.Comparator;

public class PipelineTextureSort implements Comparator<RePipeTextureDrawable> {
    @Override
    public int compare(RePipeTextureDrawable o1, RePipeTextureDrawable o2) {
        // entities ordered based on y-position
        return (Float.compare(o2.y, o1.y + o2.height));
    }
}
