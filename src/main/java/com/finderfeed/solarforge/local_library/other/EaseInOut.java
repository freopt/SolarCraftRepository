package com.finderfeed.solarforge.local_library.other;

import com.finderfeed.solarforge.local_library.helpers.FinderfeedMathHelper;
import net.minecraft.util.Mth;

public class EaseInOut extends InterpolatedValue implements CanTick{


    private final double modifier;
    public EaseInOut(double start,double end, double duration,double modifier){
        super(start,end,duration);
        this.start = start;
        this.duration = duration;
        this.end = end;
        this.modifier = modifier;
    }


    public double getValue(){
        double time = FinderfeedMathHelper.clamp(start,ticker,duration);
        double easein= FinderfeedMathHelper.SQUARE.apply(time/duration);
        double easeout = FinderfeedMathHelper.FLIP.apply(Math.pow(FinderfeedMathHelper.FLIP.apply(time/duration),modifier));
        return Mth.lerp(Mth.lerp(time/duration,easein,easeout),start,end);
    }


    @Override
    public void tick(){
        if (this.ticker + 1 <= duration) {
            this.ticker += 1;
        }
    }

    public void tickBackwards(){
        if (this.ticker -1 >= 0) {
            this.ticker -= 1;
        }
    }

    public void reset(){
        this.ticker = 0;
    }
}
