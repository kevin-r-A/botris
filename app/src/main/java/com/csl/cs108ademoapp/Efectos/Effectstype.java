package com.csl.cs108ademoapp.Efectos;


import com.csl.cs108ademoapp.TipoEfectos.BaseEffects;
import com.csl.cs108ademoapp.TipoEfectos.FadeIn;
import com.csl.cs108ademoapp.TipoEfectos.Fall;
import com.csl.cs108ademoapp.TipoEfectos.FlipH;
import com.csl.cs108ademoapp.TipoEfectos.FlipV;
import com.csl.cs108ademoapp.TipoEfectos.NewsPaper;
import com.csl.cs108ademoapp.TipoEfectos.RotateBottom;
import com.csl.cs108ademoapp.TipoEfectos.RotateLeft;
import com.csl.cs108ademoapp.TipoEfectos.Shake;
import com.csl.cs108ademoapp.TipoEfectos.SideFall;
import com.csl.cs108ademoapp.TipoEfectos.SlideBottom;
import com.csl.cs108ademoapp.TipoEfectos.SlideLeft;
import com.csl.cs108ademoapp.TipoEfectos.SlideRight;
import com.csl.cs108ademoapp.TipoEfectos.SlideTop;
import com.csl.cs108ademoapp.TipoEfectos.Slit;


public enum  Effectstype {

    Fadein(FadeIn.class),
    Slideleft(SlideLeft.class),
    Slidetop(SlideTop.class),
    SlideBottom(SlideBottom.class),
    Slideright(SlideRight.class),
    Fall(Fall.class),
    Newspager(NewsPaper.class),
    Fliph(FlipH.class),
    Flipv(FlipV.class),
    RotateBottom(RotateBottom.class),
    RotateLeft(RotateLeft.class),
    Slit(Slit.class),
    Shake(Shake.class),
    Sidefill(SideFall.class);
    private Class<? extends BaseEffects> effectsClazz;

    private Effectstype(Class<? extends BaseEffects> mclass) {
        effectsClazz = mclass;
    }

    public BaseEffects getAnimator() {
        BaseEffects bEffects=null;
        try {
            bEffects = effectsClazz.newInstance();
        } catch (ClassCastException e) {
            throw new Error("Can not init animatorClazz instance");
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            throw new Error("Can not init animatorClazz instance");
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            throw new Error("Can not init animatorClazz instance");
        }
        return bEffects;
    }
}
